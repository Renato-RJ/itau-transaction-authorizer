package com.itau.transaction_authorizer.domain.core.aggregate

import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.AUTHORIZED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.REJECTED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.CREDIT
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.DEBIT
import com.itau.transaction_authorizer.domain.exception.InvalidAmountException
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AccountTests {

    @Test
    fun `authorizeCredit creates AUTHORIZED transaction and increases balance`() {
        val account = Account(
            id = AccountId(value = "acc-123"),
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = "1000.00", currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )
        val amount = Money.of(amount = BigDecimal("100.00"), currency = BRL)

        val transaction = account.authorizeCredit(
            transactionId = "tx-001",
            accountId = account.id,
            amount = amount
        )

        assertEquals(expected = AUTHORIZED, actual = transaction.status)
        assertEquals(expected = CREDIT, actual = transaction.type)
        assertEquals(expected = BigDecimal("1100.00"), actual = account.balance.amount)
    }

    @Test
    fun `authorizeCredit with zero amount throws InvalidAmountException`() {
        val account = Account(
            id = AccountId(value = "acc-123"),
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = "1000.00", currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )
        val amount = Money.of(amount = "0", currency = BRL)

        assertFailsWith<InvalidAmountException> {
            account.authorizeCredit(
                transactionId = "tx-002",
                accountId = account.id,
                amount = amount
            )
        }
    }

    @Test
    fun `authorizeDebit creates AUTHORIZED transaction and decreases balance when sufficient funds`() {
        val account = Account(
            id = AccountId(value = "acc-123"),
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = BigDecimal("1000.00"), currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )
        val amount = Money.of(amount = "100.00", currency = BRL)

        val transaction = account.authorizeDebit(
            transactionId = "tx-003",
            accountId = account.id,
            amount = amount
        )

        assertEquals(expected = AUTHORIZED, actual = transaction.status)
        assertEquals(expected = DEBIT, actual = transaction.type)
        assertEquals(expected = BigDecimal("900.00"), actual = account.balance.amount)
    }

    @Test
    fun `authorizeDebit creates REJECTED transaction when insufficient balance`() {
        val account = Account(
            id = AccountId(value = "acc-123"),
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = "50.00", currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )
        val amount = Money.of(amount = "100.00", currency = BRL)

        val transaction = account.authorizeDebit(
            transactionId = "tx-004",
            accountId = account.id,
            amount = amount
        )

        assertEquals(expected = REJECTED, actual = transaction.status)
        assertNotNull(transaction.rejectionReason)
        assertEquals(expected = BigDecimal("50.00"), actual = account.balance.amount)
    }

    @Test
    fun `authorizeDebit with zero amount throws IllegalArgumentException`() {
        val account = Account(
            id = AccountId(value = "acc-123"),
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = "1000.00", currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )
        val amount = Money.of(amount = "0", currency = BRL)

        assertFailsWith<IllegalArgumentException> {
            account.authorizeDebit(
                transactionId = "tx-005",
                accountId = account.id,
                amount = amount
            )
        }
    }

    @Test
    fun `creating account with negative balance throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            Account(
                id = AccountId(value = "acc-123"),
                owner = AccountOwnerId(value = "owner-456"),
                balance = Money.of(amount = "-1000.00", currency = BRL),
                createdAt = Instant.now(),
                status = "ENABLED"
            )
        }
    }
}
