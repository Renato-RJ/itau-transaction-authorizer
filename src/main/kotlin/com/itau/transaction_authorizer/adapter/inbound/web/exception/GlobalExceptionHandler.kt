package com.itau.transaction_authorizer.adapter.inbound.web.exception

import com.itau.transaction_authorizer.adapter.inbound.web.controller.converter.TransactionConverter
import com.itau.transaction_authorizer.domain.exception.AccountAlreadyExistsException
import com.itau.transaction_authorizer.domain.exception.AccountNotFoundException
import com.itau.transaction_authorizer.domain.exception.DomainException
import com.itau.transaction_authorizer.domain.exception.InsufficientBalanceException
import com.itau.transaction_authorizer.domain.exception.InvalidAmountException
import com.itau.transaction_authorizer.domain.exception.InvalidTransactionException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<Any> {
        val (httpStatus, response) = when (ex) {
            is AccountNotFoundException -> NOT_FOUND to ErrorResponse(message = "Conta não encontrada", code = "ACCOUNT_NOT_FOUND")
            is AccountAlreadyExistsException -> CONFLICT to ErrorResponse(message = "Conta já existente", code = "ACCOUNT_ALREADY_EXISTS")
            is InsufficientBalanceException -> OK to with(ex.transaction) { TransactionConverter.toResponse(transaction = Pair(this, ex.availableBalance)) }
            is InvalidAmountException -> UNPROCESSABLE_ENTITY to ErrorResponse(message = "Valor de transação inválido", code = "INVALID_AMOUNT")
            is InvalidTransactionException -> UNPROCESSABLE_ENTITY to ErrorResponse(message = "Transação inválida", code = "INVALID_TRANSACTION")
            else -> UNPROCESSABLE_ENTITY to ErrorResponse(message = "Erro ao processar transação", code = "DOMAIN_ERROR")
        }

        log.warn(
            "domain_exception traceId={} type={} code={} message={}",
            MDC.get("traceId"),
            ex::class.simpleName,
            if (response is ErrorResponse) response.code else "N/A",
            ex.message
        )

        return ResponseEntity.status(httpStatus).body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.error(
            "illegal_argument_exception traceId={} message={}",
            MDC.get("traceId"),
            ex.message
        )
        return ResponseEntity.status(BAD_REQUEST).body(
            ErrorResponse(message = "Dados da requisição inválidos", code = "INVALID_REQUEST")
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error(
            "unexpected_exception traceId={} type={} message={}",
            MDC.get("traceId"),
            ex::class.simpleName,
            ex.cause?.message?.let { it.substringAfter("Detail: ", it) }
        )
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
            ErrorResponse(message = "Erro interno do servidor", code = "INTERNAL_SERVER_ERROR")
        )
    }
}
