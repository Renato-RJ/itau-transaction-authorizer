package com.itau.transaction_authorizer.domain.core.aggregate

import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionId
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionStatus.PROCESSING
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.CREDIT
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.DEBIT
import com.itau.transaction_authorizer.domain.exception.InvalidAmountException
import java.math.BigDecimal.ZERO

class Account(
    val id: AccountId,
    val ownerId: AccountOwnerId,
    var balance: Money = Money.zero()
) {
    init {
        require(balance.amount >= ZERO) {
            "Saldo da conta não pode ser negativo: ${balance.amount}"
        }
    }

    /**
     * Autoriza uma transação de CRÉDITO (adição de fundos).
     *
     * @param ownerId ID do titular que solicitou a transação
     * @param amount Valor a ser creditado
     * @return Transaction autorizada
     * @throws InvalidAmountException se amount <= 0
     */
    fun authorizeCredit(
        ownerId: AccountOwnerId,
        amount: Money
    ): Transaction {
        if(amount.amount <= ZERO) throw InvalidAmountException(amount.amount)

        return Transaction(
            id = TransactionId(),
            accountOwnerId = ownerId,
            type = CREDIT,
            amount = amount,
            status = PROCESSING
        ).authorize()
            .also {
                this.balance += amount
            }
    }

    /**
     * Autoriza uma transação de DÉBITO (retirada de fundos).
     *
     * @param ownerId ID do titular que solicitou a transação
     * @param amount Valor a ser debitado
     * @return Transaction (pode ser AUTHORIZED ou REJECTED)
     * @throws IllegalArgumentException se amount <= 0
     */
    fun authorizeDebit(
        ownerId: AccountOwnerId,
        amount: Money
    ): Transaction {
        require(amount.amount > ZERO) {
            "Valor de débito deve ser maior que zero: ${amount.amount}"
        }

        val transaction = Transaction(
            id = TransactionId(),
            accountOwnerId = ownerId,
            type = DEBIT,
            amount = amount,
            status = PROCESSING
        )

        if (!balance.isSufficient(required = amount)) {
            return transaction.reject(
                reason = "Saldo insuficiente. Saldo disponível: ${balance.amount}, " +
                "Valor solicitado: ${amount.amount}"
            )
        }

        return transaction.authorize().also {
            this.balance -= amount
        }
    }
}
