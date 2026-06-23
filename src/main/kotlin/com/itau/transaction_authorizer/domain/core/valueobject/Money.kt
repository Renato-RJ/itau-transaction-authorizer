package com.itau.transaction_authorizer.domain.core.valueobject

import java.math.BigDecimal
import java.math.BigDecimal.ZERO

data class Money(
    val amount: BigDecimal = ZERO,
    val currency: Currency
) {
    init {
        require(amount >= ZERO) { "Valor não pode ser negativo: $amount" }
    }

    operator fun plus(other: Money): Money {
        return Money(amount = this.amount.add(other.amount), currency = this.currency)
    }

    operator fun minus(value: Money): Money {
        val result = this.amount.subtract(value.amount)
        require(!result.isNegative()) {
            "Subtração resulta em valor negativo: $amount - ${value.amount}"
        }
        return Money(amount = result, currency = this.currency)
    }

    fun isSufficient(required: Money): Boolean = this.amount >= required.amount

    companion object {
        fun zero(currency: Currency): Money = Money(amount = ZERO, currency = currency)
        
        fun of(amount: BigDecimal, currency: Currency): Money = Money(amount = amount, currency = currency)
        
        fun of(amount: Double, currency: Currency): Money = Money(amount = BigDecimal.valueOf(amount), currency = currency)
        
        fun of(amount: String, currency: Currency): Money = Money(amount = BigDecimal(amount), currency = currency)
    }

    private fun BigDecimal.isNegative(): Boolean = this < ZERO
}
