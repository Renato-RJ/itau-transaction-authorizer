package com.itau.transaction_authorizer.domain.exception

class AccountAlreadyExistsException(
    accountId: String
) : DomainException("Conta bancária já existe: $accountId")

