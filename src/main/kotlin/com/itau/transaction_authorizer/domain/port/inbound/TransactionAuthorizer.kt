package com.itau.transaction_authorizer.domain.port.inbound

import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType

interface TransactionAuthorizer {
    /**
     * Autoriza uma transação
     *
     * @param accountId de uma conta
     * @param type da transação
     * @param amount da transação
     * @return Pair com a transação rejeitada ou aprovada e o saldo atualizado da conta
     */
    fun authorize(accountId: AccountId,
                  type: TransactionType,
                  amount: Money
    ): Pair<Transaction, Money>
}