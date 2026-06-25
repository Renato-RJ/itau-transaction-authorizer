package com.itau.transaction_authorizer.domain.service

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.exception.AccountAlreadyExistsException
import com.itau.transaction_authorizer.domain.port.outbound.AccountRepositoryPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal.ZERO
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AccountCreationServiceTests {

    @Mock
    private lateinit var accountRepository: AccountRepositoryPort

    private lateinit var service: AccountCreationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        service = AccountCreationService(accountRepository = accountRepository)
    }

    @Test
    fun `create creates account with zero BRL balance and saves it`() {
        val accountId = AccountId(value = "acc-123")
        val owner = AccountOwnerId(value = "owner-456")
        val createdAt = Instant.parse("2026-01-01T10:00:00Z")

        whenever(accountRepository.findById(accountId = accountId)).thenReturn(null)

        val account = service.create(
            accountId = accountId,
            owner = owner,
            createdAt = createdAt,
            status = "ENABLED"
        )

        val savedAccountCaptor = argumentCaptor<Account>()

        verify(accountRepository).findById(accountId = accountId)
        verify(accountRepository).save(account = savedAccountCaptor.capture())

        assertEquals(expected = accountId, actual = account.id)
        assertEquals(expected = owner, actual = account.owner)
        assertEquals(expected = ZERO, actual = account.balance.amount)
        assertEquals(expected = BRL, actual = account.balance.currency)
        assertEquals(expected = createdAt, actual = account.createdAt)
        assertEquals(expected = "ENABLED", actual = account.status)
        assertEquals(expected = account, actual = savedAccountCaptor.firstValue)
    }

    @Test
    fun `create throws when account already exists and does not save`() {
        val accountId = AccountId(value = "acc-999")
        val owner = AccountOwnerId(value = "owner-000")
        val createdAt = Instant.parse("2026-02-02T10:00:00Z")

        val existingAccount = Account(
            id = accountId,
            owner = owner,
            balance = Money.zero(currency = BRL),
            createdAt = createdAt,
            status = "ENABLED"
        )

        whenever(accountRepository.findById(accountId = accountId)).thenReturn(existingAccount)

        val exception = assertFailsWith<AccountAlreadyExistsException> {
            service.create(
                accountId = accountId,
                owner = owner,
                createdAt = createdAt,
                status = "ENABLED"
            )
        }

        assertEquals(expected = exception.message?.contains("acc-999"), actual = true)
        verify(accountRepository).findById(accountId = accountId)
        verify(accountRepository, never()).save(account = any<Account>())
    }
}
