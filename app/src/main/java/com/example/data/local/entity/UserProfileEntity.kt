package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_notes")
data class DailyNoteEntity(
    @PrimaryKey
    val dateString: String, // "yyyy-MM-dd"
    val note: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String = "Student",
    val email: String = "student@university.ac.zw",
    val university: String = "University",
    val preferredCurrency: String = "USD",
    val currencySymbol: String = "$",
    val startingBalance: Double = 0.0,
    val monthlyBudgetTarget: Double = 300.0,
    val morningReminderEnabled: Boolean = true,
    val morningReminderTime: String = "07:30",
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "20:00",
    val hasCompletedOnboarding: Boolean = false
)
