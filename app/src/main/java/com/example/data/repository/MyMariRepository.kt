package com.example.data.repository

import com.example.data.local.dao.AppDao
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.DailyNoteEntity
import com.example.data.local.entity.SavingsContributionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyMariRepository(private val appDao: AppDao) {

    val allTransactions: Flow<List<TransactionEntity>> = appDao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = appDao.getAllBudgets()
    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = appDao.getAllSavingsGoals()
    val userProfile: Flow<UserProfileEntity?> = appDao.getUserProfile()

    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> =
        appDao.getRecentTransactions(limit)

    fun getTransactionsForDate(dateString: String): Flow<List<TransactionEntity>> =
        appDao.getTransactionsForDate(dateString)

    fun getDailyNote(dateString: String): Flow<DailyNoteEntity?> =
        appDao.getDailyNote(dateString)

    suspend fun getUserProfileOnce(): UserProfileEntity? =
        appDao.getUserProfileOnce()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        appDao.insertOrUpdateProfile(profile)
    }

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return appDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        appDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        appDao.deleteTransactionById(id)
    }

    suspend fun addBudget(budget: BudgetEntity): Long {
        return appDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        appDao.updateBudget(budget)
    }

    suspend fun deleteBudget(id: Long) {
        appDao.deleteBudgetById(id)
    }

    suspend fun addSavingsGoal(goal: SavingsGoalEntity): Long {
        return appDao.insertSavingsGoal(goal)
    }

    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) {
        appDao.updateSavingsGoal(goal)
    }

    suspend fun deleteSavingsGoal(id: Long) {
        appDao.deleteSavingsGoalById(id)
    }

    suspend fun contributeToSavingsGoal(goalId: Long, amount: Double, note: String = "") {
        val goal = appDao.getSavingsGoalById(goalId) ?: return
        val updated = goal.copy(currentAmount = goal.currentAmount + amount)
        appDao.updateSavingsGoal(updated)
        appDao.insertSavingsContribution(
            SavingsContributionEntity(
                goalId = goalId,
                amount = amount,
                timestamp = System.currentTimeMillis(),
                notes = note
            )
        )
    }

    suspend fun saveDailyNote(dateString: String, note: String) {
        appDao.insertDailyNote(
            DailyNoteEntity(
                dateString = dateString,
                note = note,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // Export to CSV
    fun exportTransactionsToCsv(transactions: List<TransactionEntity>, currencySymbol: String): String {
        val sb = StringBuilder()
        sb.append("ID,Date,Time,Type,Category,Description,Amount,Payment Method,Source,Notes\n")
        transactions.forEach { t ->
            sb.append("${t.id},")
            sb.append("\"${t.dateString}\",")
            sb.append("\"${t.timeString}\",")
            sb.append("\"${t.type}\",")
            sb.append("\"${t.category.replace("\"", "\"\"")}\",")
            sb.append("\"${t.description.replace("\"", "\"\"")}\",")
            sb.append("${t.amount},")
            sb.append("\"${t.paymentMethod}\",")
            sb.append("\"${t.source}\",")
            sb.append("\"${t.notes.replace("\"", "\"\"")}\"\n")
        }
        return sb.toString()
    }

    // Populate realistic student demo data
    suspend fun loadRealisticDemoData() {
        appDao.clearAllTransactions()
        appDao.clearAllBudgets()
        appDao.clearAllSavingsGoals()

        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val cal = Calendar.getInstance()

        // 1. Initial Profile
        appDao.insertOrUpdateProfile(
            UserProfileEntity(
                id = 1,
                fullName = "Ridoy",
                email = "student@university.ac.zw",
                university = "University of Zimbabwe",
                preferredCurrency = "USD",
                currencySymbol = "$",
                startingBalance = 2455.00,
                monthlyBudgetTarget = 350.00,
                morningReminderEnabled = true,
                morningReminderTime = "07:30",
                eveningReminderEnabled = true,
                eveningReminderTime = "20:00",
                hasCompletedOnboarding = true
            )
        )

        // Demo transactions over the last 7 days
        val sampleTransactions = mutableListOf<TransactionEntity>()

        // Income 1: Allowance from parents via Mukuru
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -5)
        sampleTransactions.add(
            TransactionEntity(
                type = "INCOME",
                amount = 250.00,
                category = "Parents or guardians",
                paymentMethod = "Mukuru",
                source = "Parents or guardians",
                description = "Monthly living allowance from Mom & Dad",
                notes = "Collected at Mukuru campus booth",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "10:15",
                currency = "USD"
            )
        )

        // Income 2: Part-time tutoring income via EcoCash
        cal.add(Calendar.DAY_OF_YEAR, 2)
        sampleTransactions.add(
            TransactionEntity(
                type = "INCOME",
                amount = 45.00,
                category = "Part-time employment",
                paymentMethod = "EcoCash",
                source = "Part-time employment",
                description = "Math tutoring peer session payment",
                notes = "Received via EcoCash transfer",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "16:40",
                currency = "USD"
            )
        )

        // Expenses across the week
        // Rent & Electricity
        cal.add(Calendar.DAY_OF_YEAR, -1)
        sampleTransactions.add(
            TransactionEntity(
                type = "EXPENSE",
                amount = 80.00,
                category = "Rent and accommodation",
                paymentMethod = "Bank transfer",
                description = "Off-campus shared cottage rent share",
                notes = "Paid to landlord Mr. Moyo",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "09:00",
                currency = "USD"
            )
        )

        // Groceries
        cal.add(Calendar.DAY_OF_YEAR, 1)
        sampleTransactions.add(
            TransactionEntity(
                type = "EXPENSE",
                amount = 28.50,
                category = "Groceries",
                paymentMethod = "InnBucks",
                description = "Cooking oil, rice, sugar, eggs & bread",
                notes = "Purchased at OK Supermarket",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "14:20",
                currency = "USD"
            )
        )

        // Vegetables
        sampleTransactions.add(
            TransactionEntity(
                type = "EXPENSE",
                amount = 4.00,
                category = "Vegetables and fresh produce",
                paymentMethod = "Cash",
                description = "Bought tomatoes, onions & covo vegetables",
                notes = "Local market vendors",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "15:05",
                currency = "USD"
            )
        )

        // Canteen lunch
        cal.add(Calendar.DAY_OF_YEAR, 1)
        sampleTransactions.add(
            TransactionEntity(
                type = "EXPENSE",
                amount = 3.50,
                category = "Canteen and food",
                paymentMethod = "EcoCash",
                description = "Sadza and chicken lunch at campus canteen",
                notes = "Quick lunch between lectures",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "12:45",
                currency = "USD"
            )
        )

        // Airtime & Data bundle
        sampleTransactions.add(
            TransactionEntity(
                type = "EXPENSE",
                amount = 5.00,
                category = "Airtime and data",
                paymentMethod = "EcoCash",
                description = "Weekly WhatsApp & Research Data Bundle",
                notes = "Econet private student bundle",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "19:10",
                currency = "USD"
            )
        )

        // Transport (Kombi)
        cal.time = Date()
        sampleTransactions.add(
            TransactionEntity(
                type = "EXPENSE",
                amount = 1.50,
                category = "Transport",
                paymentMethod = "Cash",
                description = "Kombi fare to campus and back",
                notes = "Daily commute",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "08:15",
                currency = "USD"
            )
        )

        // Academic photocopy / printing
        sampleTransactions.add(
            TransactionEntity(
                type = "EXPENSE",
                amount = 2.00,
                category = "Academic expenses",
                paymentMethod = "Cash",
                description = "Lecture handouts printing & spiral binding",
                notes = "Library stationery shop",
                timestamp = cal.timeInMillis,
                dateString = sdfDate.format(cal.time),
                timeString = "11:30",
                currency = "USD"
            )
        )

        sampleTransactions.forEach { appDao.insertTransaction(it) }

        // Category Budgets
        appDao.insertBudget(
            BudgetEntity(
                name = "Monthly Food & Groceries",
                category = "Groceries",
                amount = 90.00,
                period = "MONTHLY"
            )
        )
        appDao.insertBudget(
            BudgetEntity(
                name = "Campus Canteen",
                category = "Canteen and food",
                amount = 50.00,
                period = "MONTHLY"
            )
        )
        appDao.insertBudget(
            BudgetEntity(
                name = "Transport & Kombi",
                category = "Transport",
                amount = 30.00,
                period = "MONTHLY"
            )
        )
        appDao.insertBudget(
            BudgetEntity(
                name = "Airtime & Connectivity",
                category = "Airtime and data",
                amount = 20.00,
                period = "MONTHLY"
            )
        )

        // Savings Goals
        val goal1Id = appDao.insertSavingsGoal(
            SavingsGoalEntity(
                name = "Emergency Savings",
                targetAmount = 150.00,
                currentAmount = 75.00,
                categoryIcon = "emergency",
                notes = "For unexpected medication or urgent travel"
            )
        )
        appDao.insertSavingsGoal(
            SavingsGoalEntity(
                name = "New Coding Laptop",
                targetAmount = 450.00,
                currentAmount = 180.00,
                categoryIcon = "laptop",
                notes = "Saving up for a Core i5 laptop for computer science labs"
            )
        )
        appDao.insertSavingsGoal(
            SavingsGoalEntity(
                name = "End-of-Semester Trip",
                targetAmount = 80.00,
                currentAmount = 30.00,
                categoryIcon = "travel",
                notes = "Trip to Victoria Falls with class friends"
            )
        )

        // Daily note for today
        appDao.insertDailyNote(
            DailyNoteEntity(
                dateString = sdfDate.format(Date()),
                note = "Managed my spending well today. Packed homemade tea and walked half the distance to save kombi fare!"
            )
        )
    }

    suspend fun clearAllData() {
        appDao.clearAllTransactions()
        appDao.clearAllBudgets()
        appDao.clearAllSavingsGoals()
    }
}
