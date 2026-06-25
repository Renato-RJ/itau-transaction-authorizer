package com.itau.transaction_authorizer.domain.core.valueobject.factory

import com.itau.transaction_authorizer.domain.core.valueobject.Currency.BRL
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.EUR
import com.itau.transaction_authorizer.domain.core.valueobject.Currency.USD
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.CREDIT
import com.itau.transaction_authorizer.domain.core.valueobject.TransactionType.DEBIT
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals

class ValueObjectFactoryTests {

    @Test
    fun `createAccountId with valid value returns AccountId`() {
        val accountId = ValueObjectFactory.createAccountId(value = "acc-123")
        assertEquals(expected = "acc-123", actual = accountId.value)
    }

    @Test
    fun `createAccountId with blank value throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            ValueObjectFactory.createAccountId(value = "")
        }
    }

    @Test
    fun `createTransactionType with CREDIT returns CREDIT`() {
        val type = ValueObjectFactory.createTransactionType(value = "CREDIT")
        assertEquals(expected = CREDIT, actual = type)
    }

    @Test
    fun `createTransactionType with debit lowercase returns DEBIT`() {
        val type = ValueObjectFactory.createTransactionType(value = "debit")
        assertEquals(expected = DEBIT, actual = type)
    }

    @Test
    fun `createTransactionType with invalid value throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            ValueObjectFactory.createTransactionType(value = "INVALID")
        }
    }

    @Test
    fun `createCurrency with BRL returns BRL`() {
        val currency = ValueObjectFactory.createCurrency(value = "BRL")
        assertEquals(expected = BRL, actual = currency)
    }

    @Test
    fun `createCurrency with usd lowercase returns USD`() {
        val currency = ValueObjectFactory.createCurrency(value = "usd")
        assertEquals(expected = USD, actual = currency)
    }

    @Test
    fun `createCurrency with EUR returns EUR`() {
        val currency = ValueObjectFactory.createCurrency(value = "EUR")
        assertEquals(expected = EUR, actual = currency)
    }

    @Test
    fun `createCurrency with invalid value throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            ValueObjectFactory.createCurrency(value = "INVALID")
        }
    }

    @Test
    fun `createMoney creates Money with correct amount and currency`() {
        val money = ValueObjectFactory.createMoney(amount = BigDecimal("100.50"), currency = BRL)
        assertEquals(expected = BigDecimal("100.50"), actual = money.amount)
        assertEquals(expected = BRL, actual = money.currency)
    }
}
