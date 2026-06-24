package com.itau.transaction_authorizer.adapter.outbound.persistence

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.exception.InsufficientBalanceException
import com.itau.transaction_authorizer.domain.port.outbound.AccountRepositoryPort
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.sql.ResultSet
import java.time.ZoneOffset.UTC

@Repository
class AccountRepositoryAdapter(
    private val jdbc: NamedParameterJdbcTemplate
) : AccountRepositoryPort {

    private val log = LoggerFactory.getLogger(AccountRepositoryAdapter::class.java)

    @Retry(name = "postgresRead")
    @CircuitBreaker(name = "postgresDb")
    @Transactional(readOnly = true)
    override fun findById(accountId: AccountId): Account? {
        val sql = """
            SELECT id, owner_id, balance_amount, balance_currency, created_at, status
            FROM accounts
            WHERE id = :id
        """

        val params = MapSqlParameterSource().addValue("id", accountId.value)

        val rows = jdbc.query(sql, params) { rs: ResultSet, _: Int ->
            mapRow(rs)
        }

        val result = rows.firstOrNull()
        log.info("db_result operation=findById accountId={} found={}", accountId.value, result != null)
        return result
    }

    @Retry(name = "postgresWrite")
    @CircuitBreaker(name = "postgresDb")
    @Transactional
    override fun save(account: Account) {
        val sql = """
            INSERT INTO accounts (id, owner_id, balance_amount, balance_currency, created_at, status)
            VALUES (:id, :ownerId, :balanceAmount, :balanceCurrency, :createdAt, :status)
        """

        val params = MapSqlParameterSource()
            .addValue("id", account.id.value)
            .addValue("ownerId", account.owner.value)
            .addValue("balanceAmount", account.balance.amount)
            .addValue("balanceCurrency", account.balance.currency.name)
            .addValue("createdAt", account.createdAt.atOffset(UTC))
            .addValue("status", account.status)

        jdbc.update(sql, params)
        log.info("db_insert_ok operation=saveAccount accountId={}", account.id.value)
    }

    @CircuitBreaker(name = "postgresDb")
    @Transactional
    override fun applyTransaction(accountId: AccountId, transaction: Transaction): BigDecimal {
        val sql = "SELECT * FROM process_account_transaction(:id, :amount, :currency, :type)"

        val params = MapSqlParameterSource()
            .addValue("id", accountId.value)
            .addValue("amount", transaction.amount.amount)
            .addValue("currency", transaction.amount.currency.name)
            .addValue("type", transaction.type.name)

        val result: BigDecimal

        try {
            result = jdbc.queryForObject(sql, params) { rs, _ -> rs.getBigDecimal(1) } ?: ZERO
        } catch (ex: DataAccessException) {
            val psqlException = ex.rootCause as? PSQLException

            if (psqlException?.serverErrorMessage?.message == "Insufficient funds") {
                val currentBalance =
                    psqlException.serverErrorMessage?.detail?.toBigDecimal() ?: ZERO

                log.warn(
                    "db_call_rejected operation=applyTransaction accountId={} transactionId={} reason=insufficient_funds available={}",
                    accountId.value,
                    transaction.id.value,
                    currentBalance
                )

                throw InsufficientBalanceException(
                    availableBalance = Money.of(amount = currentBalance, currency = BRL),
                    requestedAmount = transaction.amount,
                    transaction = transaction
                )
            }
            log.error(
                "db_call_error operation=applyTransaction accountId={} transactionId={} error={}",
                accountId.value,
                transaction.id.value,
                ex.message
            )
            throw ex
        }

        log.info(
            "db_call_ok operation=applyTransaction accountId={} transactionId={} newBalance={}",
            accountId.value,
            transaction.id.value,
            result
        )
        return result
    }

    private fun mapRow(rs: ResultSet): Account {
        val id = rs.getString("id")
        val ownerId = rs.getString("owner_id")
        val balanceAmount = rs.getBigDecimal("balance_amount")
        val balanceCurrency = rs.getString("balance_currency")
        val createdAt = rs.getTimestamp("created_at").toInstant()
        val status = rs.getString("status")

        return Account(
            id = AccountId(id),
            owner = AccountOwnerId(ownerId),
            balance = Money.of(balanceAmount, Currency.valueOf(balanceCurrency)),
            createdAt = createdAt,
            status = status
        )
    }
}
