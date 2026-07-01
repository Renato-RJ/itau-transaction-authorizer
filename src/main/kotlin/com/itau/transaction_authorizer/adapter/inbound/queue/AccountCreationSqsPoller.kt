package com.itau.transaction_authorizer.adapter.inbound.queue

import com.fasterxml.jackson.databind.ObjectMapper
import com.itau.transaction_authorizer.adapter.inbound.queue.converter.AccountCreationConverter
import com.itau.transaction_authorizer.adapter.inbound.queue.event.CreateAccountEvent
import com.itau.transaction_authorizer.domain.port.inbound.AccountCreator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.net.URI

@Component
class AccountCreationSqsPoller(
    private val objectMapper: ObjectMapper,
    private val accountCreator: AccountCreator,
    private val resilientSqsClient: ResilientSqsClient,
    @Value("\${aws.sqs.queue-url}")
    private val queueUrl: String,
    @Value("\${aws.sqs.endpoint}")
    private val endpoint: String,
    @Value("\${aws.region:sa-east-1}")
    private val region: String
) {
    private val log = LoggerFactory.getLogger(AccountCreationSqsPoller::class.java)

    private val sqsClient: SqsClient = run {
        val builder = SqsClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("x", "x")))
            .region(Region.of(region))

        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint))
        }

        builder.build()
    }

    @Scheduled(fixedDelayString = "5000", initialDelay = 5000)
    fun poll() {
        val receiveRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(3)
            .waitTimeSeconds(5)
            .build()

        val messages = try {
            resilientSqsClient.receiveMessages(sqsClient = sqsClient, receiveRequest = receiveRequest)
        } catch (e: Exception) {
            log.error("Error while polling SQS queue: {}", queueUrl, e)
            emptyList()
        }

        if (messages.isEmpty()) return

        val accountIdList: MutableList<String> = mutableListOf()

        for (msg in messages) {
            try {
                val event = objectMapper.readValue(msg.body(), CreateAccountEvent::class.java)
                val domain = AccountCreationConverter.toDomain(event)

                accountCreator.create(
                    accountId = domain.accountId,
                    owner = domain.owner,
                    createdAt = domain.createdAt,
                    status = domain.status
                )

                val deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())
                    .build()

                resilientSqsClient.deleteMessage(sqsClient = sqsClient, deleteRequest = deleteRequest)

                accountIdList.add(domain.accountId.value)
            } catch (e: Exception) {
                log.error("Failed to process message id={}, body={}", msg.messageId(), msg.body(), e)
            }
        }
        log.info(
            "account creation messages processed for {} accountIds={ {} }",
            accountIdList.size, accountIdList.joinToString(" - ")
        )
    }
}
