package com.itau.transaction_authorizer.adapter.inbound.queue

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@Component
class ResilientSqsClient {

    @Retry(name = "sqsOperations")
    @CircuitBreaker(name = "sqsOperations")
    fun receiveMessages(
        sqsClient: SqsClient,
        receiveRequest: ReceiveMessageRequest
    ): List<Message> {
        return sqsClient.receiveMessage(receiveRequest).messages()
    }

    @Retry(name = "sqsOperations")
    @CircuitBreaker(name = "sqsOperations")
    fun deleteMessage(
        sqsClient: SqsClient,
        deleteRequest: DeleteMessageRequest
    ) {
        sqsClient.deleteMessage(deleteRequest)
    }
}
