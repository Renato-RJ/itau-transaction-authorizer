package com.itau.transaction_authorizer.domain.service

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.AUTHORIZED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.CREDIT
import com.itau.transaction_authorizer.domain.exception.AccountNotFoundException
import com.itau.transaction_authorizer.domain.exception.InvalidAmountException
import com.itau.transaction_authorizer.domain.port.outbound.AccountRepositoryPort
import com.itau.transaction_authorizer.domain.port.outbound.TransactionRepositoryPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

class TransactionAuthorizationServiceTests {

    @Mock
    private lateinit var accountRepository: AccountRepositoryPort

    @Mock
    private lateinit var transactionRepository: TransactionRepositoryPort

    private lateinit var service: TransactionAuthorizationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        service = TransactionAuthorizationService(accountRepository, transactionRepository)
    }

    @Test
    fun `authorize credit transaction successfully`() {
        val accountId = AccountId(value = "acc-123")
        val transactionId = "tx-001"
        val amount = Money.of(amount = "100.00", currency = BRL)

        val account = Account(
            id = accountId,
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = BigDecimal("500.00"), currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )

        whenever(accountRepository.findById(accountId)).thenReturn(account)
        whenever(accountRepository.applyTransaction(any<AccountId>(), any<Transaction>())).thenReturn(BigDecimal("600.00"))
        doNothing().whenever(transactionRepository).save(any<Transaction>())

        val (transaction, newBalance) = service.authorize(
            transactionId = transactionId,
            accountId = accountId,
            type = CREDIT,
            amount = amount
        )

        assertNotNull(actual = transaction)
        assertEquals(expected = AUTHORIZED, actual = transaction.status)
        assertEquals(expected = BigDecimal("600.00"), actual = newBalance.amount)
        verify(transactionRepository).save(any())
    }

    @Test
    fun `authorize debit transaction with sufficient balance`() {
        val accountId = AccountId(value = "acc-123")
        val transactionId = "tx-002"
        val amount = Money.of(amount = "100.00", currency = BRL)

        val account = Account(
            id = accountId,
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = "500.00", currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )

        whenever(accountRepository.findById(accountId)).thenReturn(account)
        whenever(accountRepository.applyTransaction(any<AccountId>(), any<Transaction>())).thenReturn(BigDecimal("400.00"))
        doNothing().whenever(transactionRepository).save(any<Transaction>())

        val (transaction, newBalance) = service.authorize(
            transactionId = transactionId,
            accountId = accountId,
            type = TransactionType.DEBIT,
            amount = amount
        )

        assertNotNull(actual = transaction)
        assertEquals(expected = AUTHORIZED, actual = transaction.status)
        assertEquals(expected = BigDecimal("400.00"), actual = newBalance.amount)
    }

    @Test
    fun `authorize throws when account not found`() {
        val accountId = AccountId(value = "non-existent")
        val amount = Money.of(amount = "100.00", currency = BRL)

        whenever(accountRepository.findById(accountId)).thenReturn(null)

        assertFailsWith<AccountNotFoundException> {
            service.authorize(
                transactionId = "tx-003",
                accountId = accountId,
                type = CREDIT,
                amount = amount
            )
        }
    }

    @Test
    fun `authorize throws when amount is zero`() {
        val accountId = AccountId(value = "acc-123")
        val amount = Money.of(amount = "0", currency = BRL)

        val account = Account(
            id = accountId,
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = "500.00", currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )

        whenever(accountRepository.findById(accountId)).thenReturn(account)

        assertFailsWith<InvalidAmountException> {
            service.authorize(
                transactionId = "tx-004",
                accountId = accountId,
                type = CREDIT,
                amount = amount
            )
        }
    }

    @Test
    fun `authorize saves transaction to repository`() {
        val accountId = AccountId(value = "acc-123")
        val transactionId = "tx-005"
        val amount = Money.of(amount = BigDecimal("100.00"), currency = BRL)

        val account = Account(
            id = accountId,
            owner = AccountOwnerId(value = "owner-456"),
            balance = Money.of(amount = BigDecimal("500.00"), currency = BRL),
            createdAt = Instant.now(),
            status = "ENABLED"
        )

        whenever(accountRepository.findById(accountId)).thenReturn(account)
        whenever(accountRepository.applyTransaction(any<AccountId>(), any<Transaction>())).thenReturn(BigDecimal("600.00"))
        doNothing().whenever(transactionRepository).save(any<Transaction>())

        service.authorize(
            transactionId = transactionId,
            accountId = accountId,
            type = CREDIT,
            amount = amount
        )

        verify(transactionRepository).save(any<Transaction>())
    }
}
