package com.itau.transaction_authorizer.adapter.inbound.web.controller.request

import java.math.BigDecimal

data class AuthorizeTransactionRequest(
    val accountId: String,
    val accountOwnerId: String,
    val type: String,
    val amount: BigDecimal
)

