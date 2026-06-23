package com.itau.transaction_authorizer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TransactionAuthorizerApplication

fun main(args: Array<String>) {
	runApplication<TransactionAuthorizerApplication>(*args)
}
