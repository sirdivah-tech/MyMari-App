package com.example

import com.example.data.model.FinanceConstants
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.ThemeMode
import com.example.ui.i18n.stringsForLanguage
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

    @Test
    fun testThemeModeDefaultsToSystem() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromKey(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromKey("unknown"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromKey("light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromKey("dark"))
    }

    @Test
    fun testLanguagesAndTranslationsComplete() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(null))
        assertEquals(AppLanguage.SHONA, AppLanguage.fromCode("sn"))
        assertEquals(AppLanguage.NDEBELE, AppLanguage.fromCode("nd"))

        AppLanguage.entries.forEach { lang ->
            val s = stringsForLanguage(lang)
            assertTrue(s.motivationalMessages.isNotEmpty())
            FinanceConstants.EXPENSE_CATEGORIES.forEach { cat ->
                assertTrue("Missing $cat for $lang", s.categoryNames.containsKey(cat))
            }
            FinanceConstants.INCOME_SOURCES.forEach { src ->
                assertTrue("Missing $src for $lang", s.sourceNames.containsKey(src))
            }
            FinanceConstants.PAYMENT_METHODS.forEach { method ->
                assertTrue("Missing $method for $lang", s.paymentMethodNames.containsKey(method))
            }
            FinanceConstants.QUICK_PRESETS.forEach { preset ->
                assertTrue("Missing ${preset.title} for $lang", s.presetTitles.containsKey(preset.title))
            }
        }
    }
}
