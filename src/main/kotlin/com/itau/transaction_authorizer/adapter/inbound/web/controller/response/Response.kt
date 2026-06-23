package com.itau.transaction_authorizer.adapter.inbound.web.controller.response

import java.math.BigDecimal
import java.time.ZonedDateTime

data class Response(val transaction: Transaction, val account: Account){
    data class Transaction(val id: String, val type: String, val amount: CurrencyValue, val status: Status, val timestamp: ZonedDateTime)
    data class Account(val id: String, val balance: Balance)
    data class CurrencyValue(val value: BigDecimal, val currency: String)
    data class Balance(val amount: BigDecimal, val currency: String)
    enum class Status { SUCCEEDED, FAILED }
}
