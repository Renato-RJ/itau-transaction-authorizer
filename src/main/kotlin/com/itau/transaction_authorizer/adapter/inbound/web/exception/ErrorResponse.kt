package com.itau.transaction_authorizer.adapter.inbound.web.exception

data class ErrorResponse(
    val message: String,
    val code: String,
    val details: String? = null
)

