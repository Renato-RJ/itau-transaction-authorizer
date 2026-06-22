package com.itau.transaction_authorizer.domain.core.valueobject

import java.math.BigDecimal
import java.math.BigDecimal.ZERO

data class Money(
    val amount: BigDecimal = ZERO
) {
    init {
        require(amount >= ZERO) { "Valor não pode ser negativo: $amount" }
    }

    operator fun plus(other: Money): Money {
        return Money(amount = this.amount.add(other.amount))
    }

    operator fun minus(value: Money): Money {
        val result = this.amount.subtract(value.amount)
        require(!result.isNegative()) {
            "Subtração resulta em valor negativo: $amount - ${value.amount}"
        }
        return Money(amount = result)
    }

    fun isSufficient(required: Money): Boolean = this.amount >= required.amount

    companion object {
        fun zero(): Money = Money(amount = ZERO)
        
        fun of(amount: BigDecimal): Money = Money(amount = amount)
        
        fun of(amount: Double): Money = Money(amount = BigDecimal.valueOf(amount))
        
        fun of(amount: String): Money = Money(amount = BigDecimal(amount))
    }

    private fun BigDecimal.isNegative(): Boolean = this < ZERO
}
