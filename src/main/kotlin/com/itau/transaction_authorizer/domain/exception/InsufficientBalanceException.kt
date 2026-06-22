package com.itau.transaction_authorizer.domain.exception

import com.itau.transaction_authorizer.domain.core.valueobject.Money

class InsufficientBalanceException(
    availableBalance: Money,
    requestedAmount: Money
) : DomainException(
    "Saldo insuficiente. " +
    "Saldo disponível: ${availableBalance.amount}, " +
    "valor necessário: ${requestedAmount.amount}"
)

