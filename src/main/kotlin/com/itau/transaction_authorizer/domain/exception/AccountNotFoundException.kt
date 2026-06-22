package com.itau.transaction_authorizer.domain.exception

class AccountNotFoundException(
    accountId: String
) : DomainException("Conta bancária não encontrada: $accountId")

