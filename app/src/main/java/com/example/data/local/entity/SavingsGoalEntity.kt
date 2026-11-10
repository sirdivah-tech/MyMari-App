package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000),
    val categoryIcon: String = "savings",
    val notes: String = ""
)

@Entity(tableName = "savings_contributions")
data class SavingsContributionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
