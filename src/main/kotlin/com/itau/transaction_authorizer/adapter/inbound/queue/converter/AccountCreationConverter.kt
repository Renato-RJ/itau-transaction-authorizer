package com.itau.transaction_authorizer.adapter.inbound.queue.converter

import com.itau.transaction_authorizer.adapter.inbound.queue.event.CreateAccountEvent
import com.itau.transaction_authorizer.domain.core.valueobject.AccountId
import com.itau.transaction_authorizer.domain.core.valueobject.AccountOwnerId
import java.time.Instant

object AccountCreationConverter {
    fun toDomain(event: CreateAccountEvent): DomainAccount = with(event.account) {
        DomainAccount(
            accountId = AccountId(value = id),
            owner = AccountOwnerId(value = owner),
            createdAt = Instant.ofEpochSecond(createdAt.toLong()),
            status = status
        )
    }

    data class DomainAccount(
        val accountId: AccountId,
        val owner: AccountOwnerId,
        val createdAt: Instant,
        val status: String
    )
}

