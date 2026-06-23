package com.itau.transaction_authorizer.adapter.inbound.queue.event

data class CreateAccountEvent(
    val account: CreateAccountPayload
)

data class CreateAccountPayload(
    val id: String,
    val owner: String,
    val createdAt: String,
    val status: String
)

