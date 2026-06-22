package com.itau.transaction_authorizer.domain.core.entity

import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionId
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.AUTHORIZED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.REJECTED
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType
import java.math.BigDecimal.ZERO
import java.time.Instant
import java.time.Instant.now

data class Transaction(
    val id: TransactionId,
    val accountOwnerId: AccountOwnerId,
    val type: TransactionType,
    val amount: Money,
    val status: TransactionStatus,
    val timestamp: Instant = now(),
    val rejectionReason: String? = null
) {
    init {
        require(amount.amount > ZERO) {
            "Valor da transação deve ser maior que zero: ${amount.amount}"
        }

        if (status == REJECTED && rejectionReason == null) {
            throw IllegalArgumentException("Transação rejeitada deve ter motivo da rejeição")
        }
    }

    fun authorize(): Transaction {
        return this.copy(
            status = AUTHORIZED,
            rejectionReason = null
        )
    }

    fun reject(reason: String): Transaction {
        require(reason.isNotBlank()) { "Motivo da rejeição não pode ser vazio" }
        return this.copy(
            status = REJECTED,
            rejectionReason = reason
        )
    }
}
