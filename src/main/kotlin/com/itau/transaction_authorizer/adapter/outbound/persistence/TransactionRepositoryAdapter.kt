package com.itau.transaction_authorizer.adapter.outbound.persistence

import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.port.outbound.TransactionRepositoryPort
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset.UTC

@Repository
class TransactionRepositoryAdapter(
    private val jdbc: NamedParameterJdbcTemplate
) : TransactionRepositoryPort {

    private val log = LoggerFactory.getLogger(TransactionRepositoryAdapter::class.java)

    @Retry(name = "postgresWrite")
    @CircuitBreaker(name = "postgresDb")
    @Transactional
    override fun save(transaction: Transaction) {
        val sql = """
            INSERT INTO transactions (id, account_id, type, amount, currency, status, transaction_timestamp, rejection_reason)
            VALUES (:id, :accountId, :type, :amount, :currency, :status, :timestamp, :rejectionReason)
        """

        val params = MapSqlParameterSource()
            .addValue("id", transaction.id.value)
            .addValue("accountId", transaction.accountId.value)
            .addValue("type", transaction.type.name)
            .addValue("amount", transaction.amount.amount)
            .addValue("currency", transaction.amount.currency.name)
            .addValue("status", transaction.status.name)
            .addValue("timestamp", transaction.timestamp.atOffset(UTC))
            .addValue("rejectionReason", transaction.rejectionReason)

        jdbc.update(sql, params)
    }
}
