package com.itau.transaction_authorizer.domain.exception

import java.math.BigDecimal

class InvalidAmountException(
    amount: BigDecimal?,
    reason: String = "Valor inválido para transação"
) : DomainException("$reason: $amount")

