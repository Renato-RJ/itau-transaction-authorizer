package com.itau.transaction_authorizer.adapter.inbound.web.controller

import com.itau.transaction_authorizer.adapter.inbound.web.controller.converter.TransactionConverter
import com.itau.transaction_authorizer.adapter.inbound.web.controller.request.AuthorizeTransactionRequest
import com.itau.transaction_authorizer.adapter.inbound.web.controller.request.validator.AuthorizeTransactionRequestValidator
import com.itau.transaction_authorizer.adapter.inbound.web.controller.response.Response
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.port.inbound.TransactionAuthorizer
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionAuthorizerController(
    private val transactionAuthorizer: TransactionAuthorizer
) {

    @PostMapping("/transactions")
    fun authorize(@RequestBody request: AuthorizeTransactionRequest): ResponseEntity<Response> {
        AuthorizeTransactionRequestValidator.validate(request)

        val (accountId, transactionType) = TransactionConverter.toDomain(request)
        val amount = TransactionConverter.moneyFromRequest(amount = request.amount, currency = BRL)

        val transaction = transactionAuthorizer.authorize(
            accountId = accountId,
            type = transactionType,
            amount = amount
        )

        val response = TransactionConverter.toResponse(transaction)
        return ResponseEntity.status(CREATED).body(response)
    }
}



