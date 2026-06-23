package com.itau.transaction_authorizer.adapter.inbound.web.controller.response

import java.math.BigDecimal
import java.time.Instant

data class Response(val transaction: Transaction, val account: Account){
    data class Transaction(val id: String, val type: String, val amount: CurrencyAmount, val status: Status, val timestamp: Instant)
    data class Account(val id: String, val balance: CurrencyAmount)
    data class CurrencyAmount(val amount: BigDecimal, val currency: String)
    enum class Status { SUCCEEDED, FAILED }
}
