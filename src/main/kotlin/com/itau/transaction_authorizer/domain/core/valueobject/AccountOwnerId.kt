package com.itau.transaction_authorizer.domain.core.valueobject

data class AccountOwnerId(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "AccountOwnerId não pode ser vazio" }
    }

    override fun toString(): String = value
}
