package com.itau.transaction_authorizer.domain.core.valueobject

import java.util.UUID

data class TransactionId(
    val value: String = UUID.randomUUID().toString()
) {
    init {
        require(value.isNotBlank()) { "TransactionId não pode ser vazio" }
    }

    override fun toString(): String = value
}
