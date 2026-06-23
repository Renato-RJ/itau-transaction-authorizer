package com.itau.transaction_authorizer.domain.core.valueobject.factory

import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Currency
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType
import java.math.BigDecimal

object ValueObjectFactory {

    fun createAccountId(value: String): AccountId {
        require(value.isNotBlank()) { "accountId não pode ser vazio" }
        return AccountId(value = value)
    }

    fun createTransactionType(value: String): TransactionType {
        return try {
            TransactionType.valueOf(value.uppercase())
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("Tipo de transação inválido: $value. Use CREDIT ou DEBIT")
        }
    }

    fun createMoney(amount: BigDecimal, currency: Currency): Money {
        return Money.of(amount = amount, currency = currency)
    }
}


