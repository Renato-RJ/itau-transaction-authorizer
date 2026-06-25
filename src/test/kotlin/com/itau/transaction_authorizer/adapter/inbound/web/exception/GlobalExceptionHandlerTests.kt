package com.itau.transaction_authorizer.adapter.inbound.web.exception

import com.itau.transaction_authorizer.domain.exception.AccountNotFoundException
import com.itau.transaction_authorizer.domain.exception.InvalidAmountException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import java.math.BigDecimal
import kotlin.test.assertEquals

class GlobalExceptionHandlerTests {

    private lateinit var handler: GlobalExceptionHandler

    @BeforeEach
    fun setup() {
        handler = GlobalExceptionHandler()
    }

    @Test
    fun `handleDomainException with AccountNotFoundException returns NOT_FOUND`() {
        val ex = AccountNotFoundException("acc-123")
        val response = handler.handleDomainException(ex)

        assertEquals(expected = NOT_FOUND, actual = response.statusCode)
        val body = response.body as ErrorResponse
        assertEquals(expected = "ACCOUNT_NOT_FOUND", actual = body.code)
        assertEquals(expected = "Conta não encontrada", actual = body.message)
    }

    @Test
    fun `handleDomainException with InvalidAmountException returns UNPROCESSABLE_ENTITY`() {
        val ex = InvalidAmountException(BigDecimal("-10"))
        val response = handler.handleDomainException(ex)

        assertEquals(expected = UNPROCESSABLE_ENTITY, actual = response.statusCode)
        val body = response.body as ErrorResponse
        assertEquals(expected = "INVALID_AMOUNT", actual = body.code)
    }

    @Test
    fun `handleIllegalArgument returns BAD_REQUEST`() {
        val ex = IllegalArgumentException("Invalid data")
        val response = handler.handleIllegalArgument(ex)

        assertEquals(expected = BAD_REQUEST, actual = response.statusCode)
        val body = response.body as ErrorResponse
        assertEquals(expected = "INVALID_REQUEST", actual = body.code)
    }

    @Test
    fun `handleGenericException returns INTERNAL_SERVER_ERROR`() {
        val ex = Exception("Unexpected error")
        val response = handler.handleGenericException(ex)

        assertEquals(expected = INTERNAL_SERVER_ERROR, actual = response.statusCode)
        val body = response.body as ErrorResponse
        assertEquals(expected = "INTERNAL_SERVER_ERROR", actual = body.code)
    }
}
