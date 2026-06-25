package com.itau.transaction_authorizer.adapter.inbound.web.controller.converter

import com.itau.transaction_authorizer.adapter.inbound.web.controller.request.AuthorizeTransactionRequest
import com.itau.transaction_authorizer.adapter.inbound.web.controller.response.Response
import com.itau.transaction_authorizer.adapter.inbound.web.controller.response.Response.Status.FAILED
import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.USD
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionId
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.AUTHORIZED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.REJECTED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.CREDIT
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.DEBIT
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.test.assertEquals

class TransactionConverterTests {

    @Test
    fun `toDomain converts request to account id money and transaction type with currency`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "owner-456",
            type = "CREDIT",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        val (accountId, transactionType, currency) = TransactionConverter.toDomain(request = request)
        val money = TransactionConverter.moneyFromRequest(amount = request.amount, currency = currency)

        assertInstanceOf<AccountId>(actualValue = accountId)
        assertInstanceOf<TransactionType>(actualValue = transactionType)
        assertInstanceOf<Currency>(actualValue = currency)
        assertInstanceOf<Money>(actualValue = money)
        assertEquals(expected = "acc-123", actual = accountId.value)
        assertEquals(expected = CREDIT, actual = transactionType)
        assertEquals(expected = 0, actual = money.amount.compareTo(BigDecimal("100.00")))
        assertEquals(expected = BRL, actual = money.currency)
    }

    @Test
    fun `toDomain handles DEBIT transaction type`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-789",
            accountOwnerId = "owner-999",
            type = "DEBIT",
            amount = BigDecimal("50.00"),
            currency = "USD"
        )

        val (_, transactionType, currency) = TransactionConverter.toDomain(request = request)

        assertEquals(expected = DEBIT, actual = transactionType)
        assertEquals(expected = USD, actual = currency)
    }

    @Test
    fun `toResponse converts authorized transaction to succeeded response`() {
        val timestamp = Instant.parse("2026-01-01T12:34:56.999Z")
        val transaction = Transaction(
            id = TransactionId(value = "txn-001"),
            accountId = AccountId(value = "acc-123"),
            type = CREDIT,
            amount = Money.of(amount = "100.00", currency = BRL),
            status = AUTHORIZED,
            timestamp = timestamp
        )
        val updatedBalance = Money.of(amount = "900.00", currency = BRL)

        val response = TransactionConverter.toResponse(
            transaction = Pair(
                first = transaction,
                second = updatedBalance
            )
        )

        assertEquals(expected = "txn-001", actual = response.transaction.id)
        assertEquals(expected = "CREDIT", actual = response.transaction.type)
        assertEquals(expected = BigDecimal("100.00"), actual = response.transaction.amount.value)
        assertEquals(expected = "BRL", actual = response.transaction.amount.currency)
        assertEquals(expected = Response.Status.SUCCEEDED, actual = response.transaction.status)
        assertEquals(
            expected = timestamp.atZone(ZoneOffset.of("-03:00")).truncatedTo(SECONDS),
            actual = response.transaction.timestamp
        )
        assertEquals(expected = "acc-123", actual = response.account.id)
        assertEquals(expected = updatedBalance.amount, actual = response.account.balance.amount)
        assertEquals(expected = "BRL", actual = response.account.balance.currency)
    }

    @Test
    fun `toResponse converts rejected transaction to failed response`() {
        val transaction = Transaction(
            id = TransactionId(value = "txn-002"),
            accountId = AccountId(value = "acc-987"),
            type = DEBIT,
            amount = Money.of(amount = "50.00", currency = USD),
            status = REJECTED,
            rejectionReason = "insufficient funds"
        )
        val updatedBalance = Money.of(amount = "20.00", currency = USD)

        val response = TransactionConverter.toResponse(transaction = Pair(first = transaction, second = updatedBalance))

        assertEquals(expected = FAILED, actual = response.transaction.status)
        assertEquals(expected = "USD", actual = response.account.balance.currency)
    }
}
