package com.itau.transaction_authorizer.domain.service

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.CREDIT
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.DEBIT
import com.itau.transaction_authorizer.domain.exception.AccountNotFoundException
import com.itau.transaction_authorizer.domain.exception.InvalidAmountException
import com.itau.transaction_authorizer.domain.port.inbound.TransactionAuthorizer
import com.itau.transaction_authorizer.domain.port.outbound.AccountRepositoryPort
import com.itau.transaction_authorizer.domain.port.outbound.TransactionRepositoryPort
import org.springframework.stereotype.Service
import java.math.BigDecimal.ZERO

@Service
class TransactionAuthorizationService(
    private val accountRepository: AccountRepositoryPort,
    private val transactionRepository: TransactionRepositoryPort
) : TransactionAuthorizer {
    /**
     * Autoriza uma transação (CREDIT ou DEBIT).
     *
     * @param accountId ID da conta
     * @param type Tipo de transação
     * @param amount Valor
     * @return Transaction autorizada ou rejeitada
     * @throws AccountNotFoundException se conta não existe
     * @throws InvalidAmountException se amount <= 0
     */
    override fun authorize(
        accountId: AccountId,
        type: TransactionType,
        amount: Money
    ): Transaction {
        return when (type) {
            CREDIT -> authorizeCredit(accountId = accountId, amount = amount)
            DEBIT -> authorizeDebit(accountId = accountId, amount = amount)
        }
    }

    private fun authorizeCredit(
        accountId: AccountId,
        amount: Money
    ): Transaction {
        validateAmount(amount = amount)
        val account = getAccount(accountId = accountId)

        val transaction = account.authorizeCredit(accountId = accountId, amount = amount)

        accountRepository.update(account = account)
        transactionRepository.save(transaction = transaction)

        return transaction
    }

    private fun authorizeDebit(
        accountId: AccountId,
        amount: Money
    ): Transaction {
        validateAmount(amount = amount)
        val account = getAccount(accountId = accountId)

        val transaction = account.authorizeDebit(accountId = accountId, amount = amount)

        accountRepository.update(account = account)
        transactionRepository.save(transaction = transaction)

        return transaction
    }

    private fun getAccount(accountId: AccountId): Account {
        return accountRepository.findById(accountId = accountId)
            ?: throw AccountNotFoundException(accountId = accountId.toString())
    }

    private fun validateAmount(amount: Money){
        if(amount.amount <= ZERO) throw InvalidAmountException(amount = amount.amount)
    }

}
