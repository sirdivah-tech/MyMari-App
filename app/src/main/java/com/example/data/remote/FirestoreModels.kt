package com.example.data.remote

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class UserProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "Student",
    val email: String = "",
    val university: String = "University",
    val preferredCurrency: String = "USD",
    val currencySymbol: String = "$",
    val startingBalance: Double = 0.0,
    val monthlyBudgetTarget: Double = 300.0,
    val morningReminderEnabled: Boolean = true,
    val morningReminderTime: String = "07:30",
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "20:00",
    val hasCompletedOnboarding: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class TransactionDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "EXPENSE", // "INCOME" or "EXPENSE"
    val amount: Double = 0.0,
    val category: String = "Other expenses",
    val paymentMethod: String = "Cash",
    val source: String = "",
    val description: String = "",
    val notes: String = "",
    val dateString: String = "",
    val timeString: String = "",
    val currency: String = "USD",
    val clientTimestamp: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BudgetDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String? = null,
    val amount: Double = 0.0,
    val period: String = "MONTHLY",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SavingsGoalDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val categoryIcon: String = "savings",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SavingsContributionDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val goalId: String = "",
    val amount: Double = 0.0,
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class DailyNoteDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dateString: String = "",
    val note: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
