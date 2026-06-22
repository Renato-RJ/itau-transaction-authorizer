package com.itau.transaction_authorizer.domain.core.valueobject

data class AccountId(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "AccountId não pode ser vazio" }
    }

    override fun toString(): String = value
}
