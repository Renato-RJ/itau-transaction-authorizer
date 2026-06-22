package com.itau.transaction_authorizer.domain.port.outbound

import com.itau.transaction_authorizer.domain.core.entity.Transaction

interface TransactionRepositoryPort {
    /**
     * Salva uma transação.
     *
     * @param transaction Transação a ser salva
     */
    fun save(transaction: Transaction)
}

