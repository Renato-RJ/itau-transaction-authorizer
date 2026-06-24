package com.itau.transaction_authorizer.domain.port.outbound

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.exception.InsufficientBalanceException
import org.springframework.dao.DataAccessException
import java.math.BigDecimal

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
     * Processa uma transação bancária (débito ou crédito) de forma atômica,
     * delegando a validação e atualização de saldo à camada de persistência.
     *
     * A operação pode falhar caso a transação seja inválida ou não haja saldo suficiente.
     *
     * @param accountId ID da conta
     * @param transaction transação a ser aplicada (Debit/Credit)
     * @return BigDecimal referente ao saldo atualizado da conta
     * @throws DataAccessException caso ocorra algum erro de acesso a dados
     * @throws InsufficientBalanceException caso não haja saldo suficiente para a transação
     */
    fun applyTransaction(accountId: AccountId, transaction: Transaction): BigDecimal
}

