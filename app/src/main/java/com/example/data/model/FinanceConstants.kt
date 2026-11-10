package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object FinanceConstants {
    const val APP_NAME = "MyMari"
    const val DEVELOPER_NAME = "Divine Pezari"
    const val TAGLINE = "Keep Your Money in Mind."
    const val BRAND_MESSAGE =
        "Financial freedom comes with financial responsibility. MyMari helps you understand your spending, take control of your money and save towards a better tomorrow."

    val INCOME_SOURCES = listOf(
        "Parents or guardians",
        "Personal savings",
        "Part-time employment",
        "Scholarships or allowances",
        "Business income",
        "Other"
    )

    val PAYMENT_METHODS = listOf(
        "Mukuru",
        "EcoCash",
        "InnBucks",
        "OneMoney",
        "Bank transfer",
        "Cash",
        "Other"
    )

    val EXPENSE_CATEGORIES = listOf(
        "Groceries",
        "Canteen and food",
        "Snacks and drinks",
        "Vegetables and fresh produce",
        "Transport",
        "Rent and accommodation",
        "Electricity and water",
        "Airtime and data",
        "Toiletries and personal care",
        "Academic expenses",
        "Entertainment",
        "Clothing",
        "Emergency expenses",
        "Other expenses"
    )

    val CURRENCIES = listOf(
        CurrencyOption("USD", "$", "United States Dollar"),
        CurrencyOption("ZiG", "ZiG ", "Zimbabwe Gold"),
        CurrencyOption("ZAR", "R ", "South African Rand"),
        CurrencyOption("EUR", "€", "Euro"),
        CurrencyOption("GBP", "£", "British Pound"),
        CurrencyOption("BWP", "P ", "Botswana Pula")
    )

    // Quick-entry student presets (Takes only 5-10 seconds to log)
    val QUICK_PRESETS = listOf(
        QuickPreset("Bread", 1.50, "Groceries", "Purchased fresh bread"),
        QuickPreset("Canteen Lunch", 3.00, "Canteen and food", "Lunch at campus canteen"),
        QuickPreset("Airtime", 2.00, "Airtime and data", "Airtime recharge"),
        QuickPreset("Kombi / Transport", 1.00, "Transport", "Commute to/from campus"),
        QuickPreset("Tomatoes & Veg", 2.00, "Vegetables and fresh produce", "Fresh vegetables"),
        QuickPreset("Snack & Drink", 1.25, "Snacks and drinks", "Quick afternoon snack")
    )

    fun getCategoryIcon(category: String): ImageVector {
        return when (category) {
            "Groceries" -> Icons.Default.LocalGroceryStore
            "Canteen and food" -> Icons.Default.Restaurant
            "Snacks and drinks" -> Icons.Default.Fastfood
            "Vegetables and fresh produce" -> Icons.Default.Spa
            "Transport" -> Icons.Default.DirectionsBus
            "Rent and accommodation" -> Icons.Default.Home
            "Electricity and water" -> Icons.Default.ElectricalServices
            "Airtime and data" -> Icons.Default.PhoneAndroid
            "Toiletries and personal care" -> Icons.Default.Spa
            "Academic expenses" -> Icons.Default.School
            "Entertainment" -> Icons.Default.Movie
            "Clothing" -> Icons.Default.ShoppingBag
            "Emergency expenses" -> Icons.Default.MedicalServices
            "Parents or guardians" -> Icons.Default.CardGiftcard
            "Personal savings" -> Icons.Default.Savings
            "Part-time employment" -> Icons.Default.Work
            "Scholarships or allowances" -> Icons.Default.School
            "Business income" -> Icons.Default.MonetizationOn
            else -> Icons.Default.Payments
        }
    }

    fun getCategoryColor(category: String): Color {
        return when (category) {
            "Groceries" -> Color(0xFF16A34A)
            "Canteen and food" -> Color(0xFFEA580C)
            "Snacks and drinks" -> Color(0xFFF59E0B)
            "Vegetables and fresh produce" -> Color(0xFF10B981)
            "Transport" -> Color(0xFF2563EB)
            "Rent and accommodation" -> Color(0xFF7C3AED)
            "Electricity and water" -> Color(0xFF0284C7)
            "Airtime and data" -> Color(0xFF0D9488)
            "Toiletries and personal care" -> Color(0xFFEC4899)
            "Academic expenses" -> Color(0xFF4F46E5)
            "Entertainment" -> Color(0xFFDB2777)
            "Clothing" -> Color(0xFF8B5CF6)
            "Emergency expenses" -> Color(0xFFDC2626)
            else -> Color(0xFF64748B)
        }
    }
}

data class CurrencyOption(
    val code: String,
    val symbol: String,
    val name: String
)

data class QuickPreset(
    val title: String,
    val amount: Double,
    val category: String,
    val defaultDescription: String
)
