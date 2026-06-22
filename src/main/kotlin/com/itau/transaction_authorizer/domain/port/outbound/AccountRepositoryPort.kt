package com.itau.transaction_authorizer.domain.port.outbound

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId

interface AccountRepositoryPort {
    /**
     * Recupera uma conta pelo seu ID.
     *
     * @param accountId ID da conta
     * @return Account se encontrada, null caso contrário
     */
    fun findById(accountId: AccountId): Account?

    /**
     * Salva uma conta.
     *
     * @param account Conta a ser salva
     */
    fun save(account: Account)

    /**
     * Atualiza uma conta.
     *
     * @param account Conta a ser atualizada
     */
    fun update(account: Account)
}

