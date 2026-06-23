package com.itau.transaction_authorizer.adapter.inbound.web.exception

import com.itau.transaction_authorizer.domain.exception.AccountNotFoundException
import com.itau.transaction_authorizer.domain.exception.DomainException
import com.itau.transaction_authorizer.domain.exception.InvalidAmountException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFound(ex: AccountNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "Conta não encontrada",
            code = "ACCOUNT_NOT_FOUND",
            details = ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(InvalidAmountException::class)
    fun handleInvalidAmount(ex: InvalidAmountException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "Valor de transação inválido",
            code = "INVALID_AMOUNT",
            details = ex.message
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "Erro ao processar transação",
            code = "DOMAIN_ERROR",
            details = ex.message
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "Dados da requisição inválidos",
            code = "INVALID_REQUEST",
            details = ex.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "Erro interno do servidor",
            code = "INTERNAL_SERVER_ERROR"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}


