package com.itau.transaction_authorizer.domain.service

import com.itau.transaction_authorizer.domain.core.aggregate.Account
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import com.itau.transaction_authorizer.domain.core.valueobject.Money
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.port.inbound.AccountCreator
import com.itau.transaction_authorizer.domain.port.outbound.AccountRepositoryPort
import com.itau.transaction_authorizer.domain.exception.AccountAlreadyExistsException
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AccountCreationService(
    private val accountRepository: AccountRepositoryPort
) : AccountCreator {

    override fun create(accountId: AccountId, owner: AccountOwnerId, createdAt: Instant, status: String): Account {
        accountRepository.findById(accountId = accountId)?.let {
            throw AccountAlreadyExistsException(accountId = accountId.toString())
        }

        val account = Account(
            id = accountId,
            owner = owner,
            balance = Money.zero(currency = BRL),
            createdAt = createdAt,
            status = status
        )

        accountRepository.save(account = account)
        return account
    }
}

