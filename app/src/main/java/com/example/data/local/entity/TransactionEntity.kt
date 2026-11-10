package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val category: String,
    val paymentMethod: String,
    val source: String = "", // e.g. "Parents or guardians", etc.
    val description: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // "yyyy-MM-dd"
    val timeString: String, // "HH:mm"
    val currency: String = "USD"
)
