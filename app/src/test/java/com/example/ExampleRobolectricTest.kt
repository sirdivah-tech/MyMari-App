package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class MyMariLogicTest {

    @Test
    fun `test budget remaining calculation`() {
        val monthlyBudget = 300.0
        val monthlySpent = 125.0
        val remaining = monthlyBudget - monthlySpent
        assertEquals(175.0, remaining, 0.001)
    }

    @Test
    fun `test available balance calculation`() {
        val startingBalance = 100.0
        val totalIncome = 250.0
        val totalExpenses = 85.0
        val totalSaved = 50.0
        val available = startingBalance + totalIncome - totalExpenses - totalSaved
        assertEquals(215.0, available, 0.001)
    }
}
