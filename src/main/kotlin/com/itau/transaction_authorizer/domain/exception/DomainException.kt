package com.itau.transaction_authorizer.domain.exception

abstract class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

