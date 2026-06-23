package com.itau.transaction_authorizer.adapter.inbound.web.controller.request.validator

import com.itau.transaction_authorizer.adapter.inbound.web.controller.request.AuthorizeTransactionRequest
import java.math.BigDecimal

object AuthorizeTransactionRequestValidator {

    fun validate(request: AuthorizeTransactionRequest) {
        validateAccountId(request.accountId)
        validateAccountOwnerId(request.accountOwnerId)
        validateTransactionType(request.type)
        validateAmount(request.amount)
    }

    private fun validateAccountId(accountId: String) {
        require(accountId.isNotBlank()) { "accountId não pode ser vazio" }
    }

    private fun validateAccountOwnerId(accountOwnerId: String) {
        require(accountOwnerId.isNotBlank()) { "accountOwnerId não pode ser vazio" }
    }

    private fun validateTransactionType(type: String) {
        require(type.isNotBlank()) { "type não pode ser vazio" }
        require(type.uppercase() in listOf("CREDIT", "DEBIT")) {
            "type deve ser CREDIT ou DEBIT, recebido: $type"
        }
    }

    private fun validateAmount(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "amount deve ser maior que zero, recebido: $amount" }
    }
}

