package com.itau.transaction_authorizer.domain.core.entity

import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionId
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.AUTHORIZED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.PROCESSING
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.REJECTED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.CREDIT
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.DEBIT
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TransactionTests {

    @Test
    fun `authorize transaction changes status to AUTHORIZED`() {
        val transaction = Transaction(
            id = TransactionId(value = "tx-001"),
            accountId = AccountId(value = "acc-123"),
            type = CREDIT,
            amount = Money.of(amount = "100.00", currency = BRL),
            status = PROCESSING
        )

        val authorized = transaction.authorize()

        assertEquals(expected = AUTHORIZED, actual = authorized.status)
        assertNull(actual = authorized.rejectionReason)
    }

    @Test
    fun `reject transaction changes status to REJECTED with reason`() {
        val transaction = Transaction(
            id = TransactionId(value = "tx-002"),
            accountId = AccountId(value = "acc-123"),
            type = DEBIT,
            amount = Money.of(amount = "100.00", currency =  BRL),
            status = PROCESSING
        )
        val reason = "Insufficient funds"

        val rejected = transaction.reject(reason)

        assertEquals(expected = REJECTED, actual = rejected.status)
        assertEquals(expected = reason, actual = rejected.rejectionReason)
    }

    @Test
    fun `reject throws when reason is blank`() {
        val transaction = Transaction(
            id = TransactionId(value = "tx-003"),
            accountId = AccountId(value = "acc-123"),
            type = CREDIT,
            amount = Money.of(amount = "100.00", currency = BRL),
            status = PROCESSING
        )

        assertFailsWith<IllegalArgumentException> {
            transaction.reject("")
        }
    }

    @Test
    fun `creating transaction with zero amount throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            Transaction(
                id = TransactionId(value = "tx-004"),
                accountId = AccountId(value = "acc-123"),
                type = CREDIT,
                amount = Money.of(amount = "0", currency = BRL),
                status = PROCESSING
            )
        }
    }

    @Test
    fun `creating transaction with negative amount throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            Transaction(
                id = TransactionId(value = "tx-005"),
                accountId = AccountId(value = "acc-123"),
                type = DEBIT,
                amount = Money.of(amount = "-50.00", currency = BRL),
                status = PROCESSING
            )
        }
    }

    @Test
    fun `creating rejected transaction without reason throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            Transaction(
                id = TransactionId(value = "tx-006"),
                accountId = AccountId(value = "acc-123"),
                type = CREDIT,
                amount = Money.of(amount = "100.00", currency = BRL),
                status = REJECTED,
                rejectionReason = null
            )
        }
    }
}
