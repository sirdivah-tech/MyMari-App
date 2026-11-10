package com.example

import com.example.data.model.FinanceConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBrandAndDeveloperConstants() {
        assertEquals("MyMari", FinanceConstants.APP_NAME)
        assertEquals("Divine Pezari", FinanceConstants.DEVELOPER_NAME)
        assertEquals("Keep Your Money in Mind.", FinanceConstants.TAGLINE)
    }

    @Test
    fun testPaymentMethodsIncludeMukuruAndEcoCash() {
        assertTrue(FinanceConstants.PAYMENT_METHODS.contains("Mukuru"))
        assertTrue(FinanceConstants.PAYMENT_METHODS.contains("EcoCash"))
        assertTrue(FinanceConstants.PAYMENT_METHODS.contains("InnBucks"))
        assertTrue(FinanceConstants.PAYMENT_METHODS.contains("OneMoney"))
    }

    @Test
    fun testExpenseCategoriesExist() {
        assertTrue(FinanceConstants.EXPENSE_CATEGORIES.contains("Groceries"))
        assertTrue(FinanceConstants.EXPENSE_CATEGORIES.contains("Canteen and food"))
        assertTrue(FinanceConstants.EXPENSE_CATEGORIES.contains("Vegetables and fresh produce"))
        assertTrue(FinanceConstants.EXPENSE_CATEGORIES.contains("Airtime and data"))
    }

    @Test
    fun testQuickPresetsAreFastAndValid() {
        assertTrue(FinanceConstants.QUICK_PRESETS.isNotEmpty())
        FinanceConstants.QUICK_PRESETS.forEach { preset ->
            assertTrue(preset.amount > 0)
            assertNotNull(preset.title)
        }
    }
}
