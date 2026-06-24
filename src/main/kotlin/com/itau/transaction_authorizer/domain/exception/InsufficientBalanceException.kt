package com.itau.transaction_authorizer.domain.exception

import com.itau.transaction_authorizer.domain.core.entity.Transaction
import com.itau.transaction_authorizer.domain.core.valueobject.Money

class InsufficientBalanceException(
    val availableBalance: Money,
    val transaction: Transaction,
    requestedAmount: Money
) : DomainException(
    "Saldo insuficiente. " +
    "Saldo disponível: ${availableBalance.amount}, " +
    "valor necessário: ${requestedAmount.amount}"
)

