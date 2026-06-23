package com.itau.transaction_authorizer.adapter.inbound.web.controller.converter

import com.itau.transaction_authorizer.adapter.inbound.web.controller.request.AuthorizeTransactionRequest
import com.itau.transaction_authorizer.adapter.inbound.web.controller.response.Response
import com.itau.transaction_authorizer.adapter.inbound.web.controller.response.Response.Status.FAILED
import com.itau.transaction_authorizer.adapter.inbound.web.controller.response.Response.Status.SUCCEEDED
import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType
import com.itau.transaction_authorizer.domain.core.valueobject.factory.ValueObjectFactory
import java.math.BigDecimal

object TransactionConverter {

    fun toDomain(request: AuthorizeTransactionRequest): Pair<AccountId, TransactionType> {
        val accountId = ValueObjectFactory.createAccountId(value = request.accountId)
        val transactionType = ValueObjectFactory.createTransactionType(value = request.type)

        return Pair(accountId, transactionType)
    }

    fun toResponse(transaction: Transaction): Response {
        return with(transaction) {
            Response(
                transaction = Response.Transaction(
                    id = id.value,
                    type = type.name,
                    amount = Response.CurrencyAmount(
                        amount = amount.amount,
                        currency = amount.currency.name
                    ),
                    status = if (status.name == "APPROVED") SUCCEEDED else FAILED,
                    timestamp = timestamp,
                ),
                account = Response.Account(
                    id = accountId.value,
                    balance = Response.CurrencyAmount(
                        amount = amount.amount,
                        currency = amount.currency.name
                    )
                )
            )
        }
    }

    fun moneyFromRequest(amount: BigDecimal, currency: Currency): Money {
        return ValueObjectFactory.createMoney(amount = amount, currency = currency)
    }
}


