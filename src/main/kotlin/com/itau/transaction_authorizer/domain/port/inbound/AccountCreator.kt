package com.itau.transaction_authorizer.domain.port.inbound

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import java.time.Instant

interface AccountCreator {
    /**
     * Cria uma nova conta com saldo inicial igual a zero.
     *
     * @return Account criada
     */
    fun create(accountId: AccountId, owner: AccountOwnerId, createdAt: Instant, status: String): Account
}

