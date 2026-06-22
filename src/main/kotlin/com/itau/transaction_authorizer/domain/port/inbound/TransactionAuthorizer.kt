package com.itau.transaction_authorizer.domain.port.inbound

import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType

interface TransactionAuthorizer {
    /**
     * Autoriza uma transação
     *
     * @param accountId de uma conta
     * @param accountOwnerId de uma conta
     * @param type da transação
     * @param amount da transação
     * @return Transaction rejeitada ou aprovada
     */
    fun authorize(accountId: AccountId,
                  accountOwnerId: AccountOwnerId,
                  type: TransactionType,
                  amount: Money
    ): Transaction
}