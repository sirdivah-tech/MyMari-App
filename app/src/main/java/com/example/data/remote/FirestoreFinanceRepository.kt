package com.example.data.remote

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FirestoreFinanceRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(expectedUid: String? = null): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in with Google before saving or reading your money records.")
        if (expectedUid != null && uid != expectedUid) {
            throw IllegalStateException("Signed-in account changed; refusing cross-account access.")
        }
        return uid
    }

    fun observeUserProfile(userId: String): Flow<UserProfileDoc?> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid"
        emitAll(
            db.collection("users").document(uid)
                .snapshots()
                .map { snap ->
                    snap.toObject(
                        UserProfileDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    fun observeTransactions(userId: String): Flow<List<TransactionDoc>> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/transactions"
        emitAll(
            db.collection("users").document(uid).collection("transactions")
                .orderBy("clientTimestamp", Query.Direction.DESCENDING)
                .snapshots()
                .map { snap ->
                    snap.toObjects(
                        TransactionDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeBudgets(userId: String): Flow<List<BudgetDoc>> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/budgets"
        emitAll(
            db.collection("users").document(uid).collection("budgets")
                .snapshots()
                .map { snap ->
                    snap.toObjects(
                        BudgetDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeSavingsGoals(userId: String): Flow<List<SavingsGoalDoc>> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/savingsGoals"
        emitAll(
            db.collection("users").document(uid).collection("savingsGoals")
                .snapshots()
                .map { snap ->
                    snap.toObjects(
                        SavingsGoalDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeDailyNote(userId: String, dateString: String): Flow<DailyNoteDoc?> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/dailyNotes/$dateString"
        emitAll(
            db.collection("users").document(uid).collection("dailyNotes").document(dateString)
                .snapshots()
                .map { snap ->
                    snap.toObject(
                        DailyNoteDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveUserProfile(userId: String, profile: UserProfileDoc): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("users").document(uid)
            val existingSnap = docRef.get().await()

            val safeName = profile.fullName.trim().ifBlank { "Student" }.take(80)
            val safeEmail = profile.email.trim().take(120)
            val safeUni = profile.university.trim().take(120)
            val safeCurrency = profile.preferredCurrency.trim().ifBlank { "USD" }.take(10)
            val safeSymbol = profile.currencySymbol.ifBlank { "$" }.take(10)
            val safeStarting = profile.startingBalance.coerceAtLeast(0.0)
            val safeMonthlyTarget = if (profile.monthlyBudgetTarget > 0) profile.monthlyBudgetTarget else 300.0

            if (!existingSnap.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "fullName" to safeName,
                    "email" to safeEmail,
                    "university" to safeUni,
                    "preferredCurrency" to safeCurrency,
                    "currencySymbol" to safeSymbol,
                    "startingBalance" to safeStarting,
                    "monthlyBudgetTarget" to safeMonthlyTarget,
                    "morningReminderEnabled" to profile.morningReminderEnabled,
                    "morningReminderTime" to profile.morningReminderTime.ifBlank { "07:30" }.take(10),
                    "eveningReminderEnabled" to profile.eveningReminderEnabled,
                    "eveningReminderTime" to profile.eveningReminderTime.ifBlank { "20:00" }.take(10),
                    "hasCompletedOnboarding" to profile.hasCompletedOnboarding,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                val updates = mapOf(
                    "fullName" to safeName,
                    "email" to safeEmail,
                    "university" to safeUni,
                    "preferredCurrency" to safeCurrency,
                    "currencySymbol" to safeSymbol,
                    "startingBalance" to safeStarting,
                    "monthlyBudgetTarget" to safeMonthlyTarget,
                    "morningReminderEnabled" to profile.morningReminderEnabled,
                    "morningReminderTime" to profile.morningReminderTime.ifBlank { "07:30" }.take(10),
                    "eveningReminderEnabled" to profile.eveningReminderEnabled,
                    "eveningReminderTime" to profile.eveningReminderTime.ifBlank { "20:00" }.take(10),
                    "hasCompletedOnboarding" to profile.hasCompletedOnboarding,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updates).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addTransaction(userId: String, tx: TransactionDoc): Result<String> {
        var docPath = "users/$userId/transactions"
        return try {
            val uid = requireUserId(userId)
            require(tx.amount > 0.0) { "Amount must be greater than 0." }
            require(tx.type == "INCOME" || tx.type == "EXPENSE") { "Invalid type." }

            val docRef = db.collection("users").document(uid).collection("transactions").document()
            docPath = docRef.path

            val safeDate = tx.dateString.trim().take(20).let {
                if (it.length >= 8) it else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            }
            val safeTime = tx.timeString.trim().take(10).let {
                if (it.length >= 4) it else SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            }

            val payload = mapOf(
                "userId" to uid,
                "type" to tx.type,
                "amount" to tx.amount,
                "category" to tx.category.trim().ifBlank { "Other expenses" }.take(80),
                "paymentMethod" to tx.paymentMethod.trim().ifBlank { "Cash" }.take(50),
                "source" to tx.source.trim().take(80),
                "description" to tx.description.trim().take(200),
                "notes" to tx.notes.trim().take(500),
                "dateString" to safeDate,
                "timeString" to safeTime,
                "currency" to tx.currency.trim().ifBlank { "USD" }.take(10),
                "clientTimestamp" to tx.clientTimestamp.coerceAtLeast(0L),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateTransaction(userId: String, tx: TransactionDoc): Result<Unit> {
        val docPath = "users/$userId/transactions/${tx.id}"
        return try {
            val uid = requireUserId(userId)
            require(tx.id.isNotBlank()) { "Transaction ID is missing." }
            require(tx.amount > 0.0) { "Amount must be greater than 0." }

            val safeDate = tx.dateString.trim().take(20).let {
                if (it.length >= 8) it else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            }
            val safeTime = tx.timeString.trim().take(10).let {
                if (it.length >= 4) it else SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            }

            val docRef = db.collection("users").document(uid).collection("transactions").document(tx.id)
            val updates = mapOf(
                "type" to tx.type,
                "amount" to tx.amount,
                "category" to tx.category.trim().ifBlank { "Other expenses" }.take(80),
                "paymentMethod" to tx.paymentMethod.trim().ifBlank { "Cash" }.take(50),
                "source" to tx.source.trim().take(80),
                "description" to tx.description.trim().take(200),
                "notes" to tx.notes.trim().take(500),
                "dateString" to safeDate,
                "timeString" to safeTime,
                "currency" to tx.currency.trim().ifBlank { "USD" }.take(10),
                "clientTimestamp" to tx.clientTimestamp.coerceAtLeast(0L),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(userId: String, transactionId: String): Result<Unit> {
        val docPath = "users/$userId/transactions/$transactionId"
        return try {
            val uid = requireUserId(userId)
            require(transactionId.isNotBlank()) { "Transaction ID is missing." }
            db.collection("users").document(uid).collection("transactions")
                .document(transactionId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addBudget(
        userId: String,
        name: String,
        category: String?,
        amount: Double,
        period: String = "MONTHLY"
    ): Result<String> {
        var docPath = "users/$userId/budgets"
        return try {
            val uid = requireUserId(userId)
            require(amount > 0.0) { "Spending limit must be greater than 0." }
            val validPeriod = if (period == "WEEKLY") "WEEKLY" else "MONTHLY"

            val docRef = db.collection("users").document(uid).collection("budgets").document()
            docPath = docRef.path

            val payload = mutableMapOf<String, Any>(
                "userId" to uid,
                "name" to name.trim().ifBlank { category ?: "Spending Limit" }.take(100),
                "amount" to amount,
                "period" to validPeriod,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!category.isNullOrBlank()) {
                payload["category"] = category.trim().take(80)
            }
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteBudget(userId: String, budgetId: String): Result<Unit> {
        val docPath = "users/$userId/budgets/$budgetId"
        return try {
            val uid = requireUserId(userId)
            require(budgetId.isNotBlank()) { "Budget ID is missing." }
            db.collection("users").document(uid).collection("budgets")
                .document(budgetId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addSavingsGoal(
        userId: String,
        name: String,
        targetAmount: Double,
        categoryIcon: String = "savings",
        notes: String = ""
    ): Result<String> {
        var docPath = "users/$userId/savingsGoals"
        return try {
            val uid = requireUserId(userId)
            require(name.isNotBlank()) { "Goal name cannot be empty." }
            require(targetAmount > 0.0) { "Target amount must be greater than 0." }

            val docRef = db.collection("users").document(uid).collection("savingsGoals").document()
            docPath = docRef.path

            val payload = mapOf(
                "userId" to uid,
                "name" to name.trim().take(100),
                "targetAmount" to targetAmount,
                "currentAmount" to 0.0,
                "categoryIcon" to categoryIcon.ifBlank { "savings" }.take(40),
                "notes" to notes.trim().take(300),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun contributeToSavingsGoal(
        userId: String,
        goalId: String,
        amount: Double,
        note: String = ""
    ): Result<Unit> {
        val goalPath = "users/$userId/savingsGoals/$goalId"
        return try {
            val uid = requireUserId(userId)
            require(goalId.isNotBlank()) { "Goal ID is missing." }
            require(amount > 0.0) { "Amount must be greater than 0." }

            val goalRef = db.collection("users").document(uid).collection("savingsGoals").document(goalId)
            val contribRef = goalRef.collection("contributions").document()

            val snap = goalRef.get().await()
            val currentAmt = snap.getDouble("currentAmount") ?: 0.0
            val newAmt = currentAmt + amount

            val batch = db.batch()
            batch.update(
                goalRef,
                mapOf(
                    "currentAmount" to newAmt,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(
                contribRef,
                mapOf(
                    "userId" to uid,
                    "goalId" to goalId,
                    "amount" to amount,
                    "notes" to note.trim().take(300),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, goalPath)
            Result.failure(e)
        }
    }

    suspend fun deleteSavingsGoal(userId: String, goalId: String): Result<Unit> {
        val docPath = "users/$userId/savingsGoals/$goalId"
        return try {
            val uid = requireUserId(userId)
            require(goalId.isNotBlank()) { "Goal ID is missing." }
            db.collection("users").document(uid).collection("savingsGoals")
                .document(goalId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun saveDailyNote(userId: String, dateString: String, note: String): Result<Unit> {
        val docPath = "users/$userId/dailyNotes/$dateString"
        return try {
            val uid = requireUserId(userId)
            require(dateString.isNotBlank()) { "Date is missing." }
            val docRef = db.collection("users").document(uid).collection("dailyNotes").document(dateString)
            val snap = docRef.get().await()

            if (!snap.exists()) {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "dateString" to dateString,
                        "note" to note.trim().take(1000),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.update(
                    mapOf(
                        "note" to note.trim().take(1000),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    fun exportTransactionsToCsv(transactions: List<TransactionDoc>): String {
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

    suspend fun loadRealisticDemoData(userId: String): Result<Unit> {
        return try {
            val uid = requireUserId(userId)
            clearAllData(uid)

            val currentEmail = auth.currentUser?.email ?: ""
            val currentName = auth.currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Student"

            saveUserProfile(
                uid,
                UserProfileDoc(
                    userId = uid,
                    fullName = currentName,
                    email = currentEmail,
                    university = "University of Zimbabwe",
                    preferredCurrency = "USD",
                    currencySymbol = "$",
                    startingBalance = 250.00,
                    monthlyBudgetTarget = 350.00,
                    morningReminderEnabled = true,
                    morningReminderTime = "07:30",
                    eveningReminderEnabled = true,
                    eveningReminderTime = "20:00",
                    hasCompletedOnboarding = true
                )
            )

            val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()

            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -5)
            addTransaction(
                uid,
                TransactionDoc(
                    type = "INCOME",
                    amount = 250.00,
                    category = "Parents or guardians",
                    paymentMethod = "Mukuru",
                    source = "Parents or guardians",
                    description = "Monthly allowance from Mom & Dad",
                    notes = "Collected at Mukuru campus booth",
                    dateString = sdfDate.format(cal.time),
                    timeString = "10:15",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis
                )
            )

            cal.add(Calendar.DAY_OF_YEAR, 2)
            addTransaction(
                uid,
                TransactionDoc(
                    type = "INCOME",
                    amount = 45.00,
                    category = "Part-time employment",
                    paymentMethod = "EcoCash",
                    source = "Part-time employment",
                    description = "Peer tutoring session payment",
                    notes = "Received on EcoCash",
                    dateString = sdfDate.format(cal.time),
                    timeString = "16:40",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis
                )
            )

            cal.add(Calendar.DAY_OF_YEAR, -1)
            addTransaction(
                uid,
                TransactionDoc(
                    type = "EXPENSE",
                    amount = 80.00,
                    category = "Rent and accommodation",
                    paymentMethod = "Bank transfer",
                    description = "Off-campus cottage room rent",
                    notes = "Monthly room share",
                    dateString = sdfDate.format(cal.time),
                    timeString = "09:00",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis
                )
            )

            cal.add(Calendar.DAY_OF_YEAR, 1)
            addTransaction(
                uid,
                TransactionDoc(
                    type = "EXPENSE",
                    amount = 28.50,
                    category = "Groceries",
                    paymentMethod = "InnBucks",
                    description = "Cooking oil, rice, sugar, eggs & bread",
                    notes = "Bought groceries for the week",
                    dateString = sdfDate.format(cal.time),
                    timeString = "14:20",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis
                )
            )

            addTransaction(
                uid,
                TransactionDoc(
                    type = "EXPENSE",
                    amount = 4.00,
                    category = "Vegetables and fresh produce",
                    paymentMethod = "Cash",
                    description = "Tomatoes, onions & greens",
                    notes = "Market stall near campus",
                    dateString = sdfDate.format(cal.time),
                    timeString = "15:05",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis + 1000
                )
            )

            cal.add(Calendar.DAY_OF_YEAR, 1)
            addTransaction(
                uid,
                TransactionDoc(
                    type = "EXPENSE",
                    amount = 3.50,
                    category = "Canteen and food",
                    paymentMethod = "EcoCash",
                    description = "Lunch at campus canteen",
                    notes = "Quick lunch between classes",
                    dateString = sdfDate.format(cal.time),
                    timeString = "12:45",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis
                )
            )

            addTransaction(
                uid,
                TransactionDoc(
                    type = "EXPENSE",
                    amount = 5.00,
                    category = "Airtime and data",
                    paymentMethod = "EcoCash",
                    description = "Weekly student data bundle",
                    notes = "For class assignments & calls",
                    dateString = sdfDate.format(cal.time),
                    timeString = "19:10",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis + 1000
                )
            )

            cal.time = Date()
            addTransaction(
                uid,
                TransactionDoc(
                    type = "EXPENSE",
                    amount = 1.50,
                    category = "Transport",
                    paymentMethod = "Cash",
                    description = "Kombi fare to campus and back",
                    notes = "Daily commute",
                    dateString = sdfDate.format(cal.time),
                    timeString = "08:15",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis
                )
            )

            addTransaction(
                uid,
                TransactionDoc(
                    type = "EXPENSE",
                    amount = 2.00,
                    category = "Academic expenses",
                    paymentMethod = "Cash",
                    description = "Printing lecture handouts",
                    notes = "Library print shop",
                    dateString = sdfDate.format(cal.time),
                    timeString = "11:30",
                    currency = "USD",
                    clientTimestamp = cal.timeInMillis + 1000
                )
            )

            addBudget(uid, "Food & Groceries Limit", "Groceries", 90.00)
            addBudget(uid, "Campus Canteen Limit", "Canteen and food", 50.00)
            addBudget(uid, "Transport & Kombi Limit", "Transport", 30.00)
            addBudget(uid, "Airtime & Data Limit", "Airtime and data", 20.00)

            addSavingsGoal(uid, "Emergency Money", 150.00, "emergency", "For urgent transport or medicine")
            addSavingsGoal(uid, "New Laptop", 450.00, "laptop", "For coding & class projects")
            addSavingsGoal(uid, "End-of-Semester Trip", 80.00, "travel", "Trip with classmates")

            saveDailyNote(
                uid,
                sdfDate.format(Date()),
                "Stuck to my spending plan today! Brought tea from my room and walked part of the way."
            )

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "users/$userId")
            Result.failure(e)
        }
    }

    suspend fun clearAllData(userId: String): Result<Unit> {
        return try {
            val uid = requireUserId(userId)
            val userDoc = db.collection("users").document(uid)

            val txDocs = userDoc.collection("transactions").get().await()
            val budgetDocs = userDoc.collection("budgets").get().await()
            val goalDocs = userDoc.collection("savingsGoals").get().await()

            val batch = db.batch()
            txDocs.documents.forEach { batch.delete(it.reference) }
            budgetDocs.documents.forEach { batch.delete(it.reference) }
            goalDocs.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "users/$userId")
            Result.failure(e)
        }
    }
}
