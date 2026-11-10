package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MyMariDatabase
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.FinanceConstants
import com.example.data.model.QuickPreset
import com.example.data.repository.MyMariRepository
import com.example.reminder.NotificationHelper
import com.example.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class Screen {
    HOME,
    DIARY,
    BUDGETS,
    REPORTS,
    PROFILE
}

data class DaySpending(
    val dayLabel: String, // "Mon", "Tue", etc.
    val dateString: String,
    val amount: Double,
    val isToday: Boolean
)

data class FinanceUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val savingsGoals: List<SavingsGoalEntity> = emptyList(),
    val userProfile: UserProfileEntity = UserProfileEntity(),
    val selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val selectedDateTransactions: List<TransactionEntity> = emptyList(),
    val currentDailyNote: String = "",
    val totalAvailableBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyBudgetRemaining: Double = 0.0,
    val monthlyBudgetPercentage: Float = 0f,
    val totalSaved: Double = 0.0,
    val categorySpendingMonth: Map<String, Double> = emptyMap(),
    val weeklySpending: List<DaySpending> = emptyList(),
    val activeScreen: Screen = Screen.HOME,
    val searchQuery: String = "",
    val filterCategory: String? = null,
    val filterPaymentMethod: String? = null,
    val filterType: String = "ALL", // "ALL", "INCOME", "EXPENSE"
    val showAddIncomeSheet: Boolean = false,
    val showAddExpenseSheet: Boolean = false,
    val editingTransaction: TransactionEntity? = null,
    val showOnboarding: Boolean = false,
    val showDemoDialog: Boolean = false,
    val isLoading: Boolean = false
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MyMariRepository
    private val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val _selectedDate = MutableStateFlow(sdfDate.format(Date()))
    private val _searchQuery = MutableStateFlow("")
    private val _filterCategory = MutableStateFlow<String?>(null)
    private val _filterPaymentMethod = MutableStateFlow<String?>(null)
    private val _filterType = MutableStateFlow("ALL")
    private val _activeScreen = MutableStateFlow(Screen.HOME)
    private val _showAddIncomeSheet = MutableStateFlow(false)
    private val _showAddExpenseSheet = MutableStateFlow(false)
    private val _editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    private val _currentDailyNote = MutableStateFlow("")

    init {
        val db = MyMariDatabase.getDatabase(application)
        repository = MyMariRepository(db.appDao())

        // Initial check for profile and schedule reminders
        viewModelScope.launch {
            val existing = repository.getUserProfileOnce()
            if (existing != null) {
                if (existing.morningReminderEnabled) {
                    val parts = existing.morningReminderTime.split(":")
                    if (parts.size == 2) {
                        ReminderScheduler.scheduleDailyReminder(
                            application,
                            ReminderScheduler.TYPE_MORNING,
                            parts[0].toIntOrNull() ?: 7,
                            parts[1].toIntOrNull() ?: 30,
                            true
                        )
                    }
                }
                if (existing.eveningReminderEnabled) {
                    val parts = existing.eveningReminderTime.split(":")
                    if (parts.size == 2) {
                        ReminderScheduler.scheduleDailyReminder(
                            application,
                            ReminderScheduler.TYPE_EVENING,
                            parts[0].toIntOrNull() ?: 20,
                            parts[1].toIntOrNull() ?: 0,
                            true
                        )
                    }
                }
            } else {
                // Initialize default profile
                repository.saveUserProfile(UserProfileEntity())
            }
        }
    }

    val uiState: StateFlow<FinanceUiState> = combine(
        repository.allTransactions,
        repository.allBudgets,
        repository.allSavingsGoals,
        repository.userProfile,
        _selectedDate,
        _searchQuery,
        _filterCategory,
        _filterPaymentMethod,
        _filterType,
        _activeScreen,
        _showAddIncomeSheet,
        _showAddExpenseSheet,
        _editingTransaction,
        _currentDailyNote
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val transactions = args[0] as? List<TransactionEntity> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val budgets = args[1] as? List<BudgetEntity> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val savings = args[2] as? List<SavingsGoalEntity> ?: emptyList()
        val profile = args[3] as? UserProfileEntity ?: UserProfileEntity()
        val selectedDate = args[4] as? String ?: sdfDate.format(Date())
        val search = args[5] as? String ?: ""
        val filterCat = args[6] as? String
        val filterPay = args[7] as? String
        val filterType = args[8] as? String ?: "ALL"
        val activeScreen = args[9] as? Screen ?: Screen.HOME
        val showAddInc = args[10] as? Boolean ?: false
        val showAddExp = args[11] as? Boolean ?: false
        val editingTx = args[12] as? TransactionEntity
        val dailyNote = args[13] as? String ?: ""

        // Date calculations
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        var totalIncome = 0.0
        var totalExpense = 0.0
        var monthIncome = 0.0
        var monthExpense = 0.0
        val catMap = mutableMapOf<String, Double>()

        val txCalendar = Calendar.getInstance()

        transactions.forEach { t ->
            if (t.type == "INCOME") {
                totalIncome += t.amount
            } else {
                totalExpense += t.amount
            }

            txCalendar.timeInMillis = t.timestamp
            val txMonth = txCalendar.get(Calendar.MONTH)
            val txYear = txCalendar.get(Calendar.YEAR)

            if (txMonth == currentMonth && txYear == currentYear) {
                if (t.type == "INCOME") {
                    monthIncome += t.amount
                } else {
                    monthExpense += t.amount
                    catMap[t.category] = (catMap[t.category] ?: 0.0) + t.amount
                }
            }
        }

        val totalSaved = savings.sumOf { it.currentAmount }
        val availableBalance = profile.startingBalance + totalIncome - totalExpense - totalSaved
        val monthlyBudget = if (profile.monthlyBudgetTarget > 0) profile.monthlyBudgetTarget else 300.0
        val remainingBudget = monthlyBudget - monthExpense
        val budgetPct = if (monthlyBudget > 0) ((monthExpense / monthlyBudget).toFloat()).coerceIn(0f, 2f) else 0f

        // Weekly spending breakdown (past 7 days for the chart)
        val weeklySpending = mutableListOf<DaySpending>()
        val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
        val todayStr = sdfDate.format(Date())

        for (i in 6 downTo 0) {
            val dCal = Calendar.getInstance()
            dCal.add(Calendar.DAY_OF_YEAR, -i)
            val dStr = sdfDate.format(dCal.time)
            val daySpent = transactions.filter { it.dateString == dStr && it.type == "EXPENSE" }
                .sumOf { it.amount }
            weeklySpending.add(
                DaySpending(
                    dayLabel = dayFormatter.format(dCal.time),
                    dateString = dStr,
                    amount = daySpent,
                    isToday = dStr == todayStr
                )
            )
        }

        // Filtered transactions for list / search
        val filtered = transactions.filter { t ->
            val matchesSearch = if (search.isBlank()) true else {
                t.description.contains(search, ignoreCase = true) ||
                    t.category.contains(search, ignoreCase = true) ||
                    t.notes.contains(search, ignoreCase = true) ||
                    t.paymentMethod.contains(search, ignoreCase = true)
            }
            val matchesCat = filterCat == null || t.category == filterCat
            val matchesPay = filterPay == null || t.paymentMethod == filterPay
            val matchesType = when (filterType) {
                "INCOME" -> t.type == "INCOME"
                "EXPENSE" -> t.type == "EXPENSE"
                else -> true
            }
            matchesSearch && matchesCat && matchesPay && matchesType
        }

        // Transactions for selected date in Diary
        val dateTransactions = transactions.filter { it.dateString == selectedDate }

        FinanceUiState(
            transactions = transactions,
            filteredTransactions = filtered,
            budgets = budgets,
            savingsGoals = savings,
            userProfile = profile,
            selectedDate = selectedDate,
            selectedDateTransactions = dateTransactions,
            currentDailyNote = dailyNote,
            totalAvailableBalance = availableBalance,
            monthlyIncome = monthIncome,
            monthlyExpense = monthExpense,
            monthlyBudgetRemaining = remainingBudget,
            monthlyBudgetPercentage = budgetPct,
            totalSaved = totalSaved,
            categorySpendingMonth = catMap,
            weeklySpending = weeklySpending,
            activeScreen = activeScreen,
            searchQuery = search,
            filterCategory = filterCat,
            filterPaymentMethod = filterPay,
            filterType = filterType,
            showAddIncomeSheet = showAddInc,
            showAddExpenseSheet = showAddExp,
            editingTransaction = editingTx,
            showOnboarding = !profile.hasCompletedOnboarding
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinanceUiState()
    )

    fun setScreen(screen: Screen) {
        _activeScreen.value = screen
    }

    fun setSelectedDate(dateString: String) {
        _selectedDate.value = dateString
        viewModelScope.launch {
            repository.getDailyNote(dateString).collect { noteEntity ->
                _currentDailyNote.value = noteEntity?.note ?: ""
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterCategory(category: String?) {
        _filterCategory.value = category
    }

    fun setFilterPaymentMethod(method: String?) {
        _filterPaymentMethod.value = method
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun showAddIncome(show: Boolean) {
        _showAddIncomeSheet.value = show
    }

    fun showAddExpense(show: Boolean) {
        _showAddExpenseSheet.value = show
    }

    fun setEditingTransaction(tx: TransactionEntity?) {
        _editingTransaction.value = tx
    }

    fun addIncome(
        amount: Double,
        source: String,
        paymentMethod: String,
        description: String,
        notes: String = "",
        date: String = sdfDate.format(Date()),
        time: String = sdfTime.format(Date())
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                type = "INCOME",
                amount = amount,
                category = source,
                paymentMethod = paymentMethod,
                source = source,
                description = description.ifBlank { "Income from $source" },
                notes = notes,
                dateString = date,
                timeString = time,
                currency = uiState.value.userProfile.preferredCurrency
            )
            repository.addTransaction(tx)
            _showAddIncomeSheet.value = false
        }
    }

    fun addExpense(
        amount: Double,
        category: String,
        paymentMethod: String,
        description: String,
        notes: String = "",
        date: String = sdfDate.format(Date()),
        time: String = sdfTime.format(Date())
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                type = "EXPENSE",
                amount = amount,
                category = category,
                paymentMethod = paymentMethod,
                description = description.ifBlank { category },
                notes = notes,
                dateString = date,
                timeString = time,
                currency = uiState.value.userProfile.preferredCurrency
            )
            repository.addTransaction(tx)
            _showAddExpenseSheet.value = false

            // Check budget thresholds for alerts
            checkBudgetAlerts(category, amount)
        }
    }

    fun quickAddExpense(preset: QuickPreset, paymentMethod: String = "Cash") {
        addExpense(
            amount = preset.amount,
            category = preset.category,
            paymentMethod = paymentMethod,
            description = preset.title,
            notes = preset.defaultDescription
        )
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            _editingTransaction.value = null
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            if (_editingTransaction.value?.id == id) {
                _editingTransaction.value = null
            }
        }
    }

    fun addBudget(name: String, category: String?, amount: Double, period: String = "MONTHLY") {
        viewModelScope.launch {
            repository.addBudget(
                BudgetEntity(
                    name = name,
                    category = category,
                    amount = amount,
                    period = period
                )
            )
        }
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.updateBudget(budget)
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudget(id)
        }
    }

    fun addSavingsGoal(
        name: String,
        targetAmount: Double,
        categoryIcon: String = "savings",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addSavingsGoal(
                SavingsGoalEntity(
                    name = name,
                    targetAmount = targetAmount,
                    currentAmount = 0.0,
                    categoryIcon = categoryIcon,
                    notes = notes
                )
            )
        }
    }

    fun contributeToSavings(goalId: Long, amount: Double, note: String = "") {
        viewModelScope.launch {
            repository.contributeToSavingsGoal(goalId, amount, note)
        }
    }

    fun deleteSavingsGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(id)
        }
    }

    fun saveDailyNote(note: String) {
        viewModelScope.launch {
            val date = _selectedDate.value
            repository.saveDailyNote(date, note)
            _currentDailyNote.value = note
        }
    }

    fun saveProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
            val app = getApplication<Application>()
            // Reschedule reminders
            val mParts = profile.morningReminderTime.split(":")
            if (mParts.size == 2) {
                ReminderScheduler.scheduleDailyReminder(
                    app,
                    ReminderScheduler.TYPE_MORNING,
                    mParts[0].toIntOrNull() ?: 7,
                    mParts[1].toIntOrNull() ?: 30,
                    profile.morningReminderEnabled
                )
            }
            val eParts = profile.eveningReminderTime.split(":")
            if (eParts.size == 2) {
                ReminderScheduler.scheduleDailyReminder(
                    app,
                    ReminderScheduler.TYPE_EVENING,
                    eParts[0].toIntOrNull() ?: 20,
                    eParts[1].toIntOrNull() ?: 0,
                    profile.eveningReminderEnabled
                )
            }
        }
    }

    fun completeOnboarding(
        fullName: String,
        university: String,
        preferredCurrency: String,
        currencySymbol: String,
        startingBalance: Double,
        monthlyBudget: Double,
        loadDemo: Boolean
    ) {
        viewModelScope.launch {
            if (loadDemo) {
                repository.loadRealisticDemoData()
            } else {
                val updatedProfile = uiState.value.userProfile.copy(
                    fullName = fullName.ifBlank { "Student" },
                    university = university.ifBlank { "University" },
                    preferredCurrency = preferredCurrency,
                    currencySymbol = currencySymbol,
                    startingBalance = startingBalance,
                    monthlyBudgetTarget = monthlyBudget,
                    hasCompletedOnboarding = true
                )
                repository.saveUserProfile(updatedProfile)
            }
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            repository.loadRealisticDemoData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            val resetProfile = UserProfileEntity(
                id = 1,
                hasCompletedOnboarding = true,
                startingBalance = 0.0,
                monthlyBudgetTarget = 300.0
            )
            repository.saveUserProfile(resetProfile)
        }
    }

    fun testReminder(isMorning: Boolean) {
        val app = getApplication<Application>()
        if (isMorning) {
            NotificationHelper.showNotification(
                context = app,
                notificationId = NotificationHelper.NOTIFICATION_ID_TEST,
                title = "Good morning from MyMari!",
                message = "Check your remaining balance and today's budget before you start your day."
            )
        } else {
            NotificationHelper.showNotification(
                context = app,
                notificationId = NotificationHelper.NOTIFICATION_ID_TEST,
                title = "Time for your MyMari check-in!",
                message = "Have you recorded everything you spent or received today? Review your money before you sleep."
            )
        }
    }

    fun exportCsv(): String {
        val currency = uiState.value.userProfile.currencySymbol
        return repository.exportTransactionsToCsv(uiState.value.transactions, currency)
    }

    private fun checkBudgetAlerts(category: String, addedAmount: Double) {
        val budgets = uiState.value.budgets
        val catBudget = budgets.find { it.category == category } ?: return
        val currentSpent = (uiState.value.categorySpendingMonth[category] ?: 0.0) + addedAmount
        val ratio = currentSpent / catBudget.amount

        val app = getApplication<Application>()
        when {
            ratio >= 1.0 -> {
                NotificationHelper.showNotification(
                    context = app,
                    notificationId = NotificationHelper.NOTIFICATION_ID_BUDGET,
                    title = "Budget Alert: $category reached 100%!",
                    message = "You have spent all of your allocated ${catBudget.name} budget. Spend with care."
                )
            }
            ratio >= 0.8 -> {
                NotificationHelper.showNotification(
                    context = app,
                    notificationId = NotificationHelper.NOTIFICATION_ID_BUDGET,
                    title = "Budget Alert: $category at 80%",
                    message = "You have used 80% of your ${catBudget.name} budget. Keep an eye on remaining funds."
                )
            }
        }
    }
}
