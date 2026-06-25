package com.itau.transaction_authorizer.domain.core.valueobject

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransactionIdTests {

    @Test
    fun `creating TransactionId with explicit value uses provided value`() {
        val id = "tx-123"
        val transactionId = TransactionId(value = id)
        assertEquals(expected = id, actual = transactionId.value)
    }

    @Test
    fun `creating TransactionId without value generates UUID`() {
        val transactionId = TransactionId()
        assertInstanceOf<UUID>(actualValue = UUID.fromString(transactionId.value))
    }

    @Test
    fun `TransactionId throws when value is blank`() {
        assertFailsWith<IllegalArgumentException> {
            TransactionId(value = "")
        }
    }

    @Test
    fun `TransactionId toString returns value`() {
        val id = "tx-456"
        val transactionId = TransactionId(value = id)
        assertEquals(expected = id, actual = transactionId.toString())
    }
}

