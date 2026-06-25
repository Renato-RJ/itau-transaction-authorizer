package com.itau.transaction_authorizer.adapter.inbound.web.controller.request.validator

import com.itau.transaction_authorizer.adapter.inbound.web.controller.request.AuthorizeTransactionRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import kotlin.test.assertFailsWith

class AuthorizeTransactionRequestValidatorTests {

    @Test
    fun `validate accepts valid request`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "owner-456",
            type = "CREDIT",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        assertDoesNotThrow {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate throws when accountId is empty`() {
        val request = AuthorizeTransactionRequest(
            accountId = "",
            accountOwnerId = "owner-456",
            type = "CREDIT",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        assertFailsWith<IllegalArgumentException> {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate throws when accountOwnerId is empty`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "",
            type = "CREDIT",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        assertFailsWith<IllegalArgumentException> {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate throws when type is invalid`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "owner-456",
            type = "INVALID",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        assertFailsWith<IllegalArgumentException> {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate throws when amount is zero`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "owner-456",
            type = "CREDIT",
            amount = ZERO,
            currency = "BRL"
        )

        assertFailsWith<IllegalArgumentException> {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate throws when amount is negative`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "owner-456",
            type = "CREDIT",
            amount = BigDecimal("-50.00"),
            currency = "BRL"
        )

        assertFailsWith<IllegalArgumentException> {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate throws when currency is invalid`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "owner-456",
            type = "CREDIT",
            amount = BigDecimal("100.00"),
            currency = "INVALID"
        )

        assertFailsWith<IllegalArgumentException> {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate accepts DEBIT type`() {
        val request = AuthorizeTransactionRequest(
            accountId = "acc-123",
            accountOwnerId = "owner-456",
            type = "DEBIT",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        assertDoesNotThrow {
            AuthorizeTransactionRequestValidator.validate(request)
        }
    }

    @Test
    fun `validate accepts all valid currencies`() {
        for (currency in listOf("BRL", "USD", "EUR")) {
            val request = AuthorizeTransactionRequest(
                accountId = "acc-123",
                accountOwnerId = "owner-456",
                type = "CREDIT",
                amount = BigDecimal("100.00"),
                currency = currency
            )
            assertDoesNotThrow {
                AuthorizeTransactionRequestValidator.validate(request)
            }
        }
    }
}
