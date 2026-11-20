package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.VoiceParsedTransaction
import com.example.data.ai.VoiceTranscriptionRepository
import com.example.data.model.QuickPreset
import com.example.data.remote.BudgetDoc
import com.example.data.remote.DailyNoteDoc
import com.example.data.remote.FirestoreFinanceRepository
import com.example.data.remote.SavingsGoalDoc
import com.example.data.remote.TransactionDoc
import com.example.data.remote.UserProfileDoc
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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
    val dayLabel: String,
    val dateString: String,
    val amount: Double,
    val isToday: Boolean
)

data class BudgetAlert(
    val category: String,
    val levelPct: Int
)

data class FinanceUiState(
    val isProfileLoading: Boolean = true,
    val transactions: List<TransactionDoc> = emptyList(),
    val filteredTransactions: List<TransactionDoc> = emptyList(),
    val budgets: List<BudgetDoc> = emptyList(),
    val savingsGoals: List<SavingsGoalDoc> = emptyList(),
    val userProfile: UserProfileDoc = UserProfileDoc(),
    val selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val selectedDateTransactions: List<TransactionDoc> = emptyList(),
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
    val filterType: String = "ALL",
    val showAddIncomeSheet: Boolean = false,
    val showAddExpenseSheet: Boolean = false,
    val editingTransaction: TransactionDoc? = null,
    val showOnboarding: Boolean = false,
    val isTranscribing: Boolean = false,
    val voiceTranscript: String? = null,
    val voiceParsedTransaction: VoiceParsedTransaction? = null,
    val voiceError: String? = null,
    val statusError: String? = null,
    val budgetAlert: BudgetAlert? = null
)

private sealed interface StreamState<out T> {
    data object Loading : StreamState<Nothing>
    data class Data<T>(val value: T) : StreamState<T>
    data class Error(val message: String) : StreamState<Nothing>
}

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModel(
    private val repository: FirestoreFinanceRepository,
    private val voiceRepository: VoiceTranscriptionRepository,
    private val currentUserId: String,
    private val initialDisplayName: String,
    private val initialEmail: String
) : ViewModel() {

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
    private val _editingTransaction = MutableStateFlow<TransactionDoc?>(null)

    private val _isTranscribing = MutableStateFlow(false)
    private val _voiceTranscript = MutableStateFlow<String?>(null)
    private val _voiceParsedTransaction = MutableStateFlow<VoiceParsedTransaction?>(null)
    private val _voiceError = MutableStateFlow<String?>(null)
    private val _statusError = MutableStateFlow<String?>(null)
    private val _budgetAlert = MutableStateFlow<BudgetAlert?>(null)

    private fun <T> Flow<T>.toSafeStreamState(fallbackMessage: String): Flow<StreamState<T>> {
        return this
            .map<T, StreamState<T>> { StreamState.Data(it) }
            .catch { error ->
                val fsErr = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (fsErr == null && error is CancellationException) throw error
                val root = fsErr ?: error
                emit(StreamState.Error(root.message ?: fallbackMessage))
            }
    }

    private val profileFlow = repository.observeUserProfile(currentUserId)
        .toSafeStreamState("Failed to load profile")

    private val transactionsFlow = repository.observeTransactions(currentUserId)
        .toSafeStreamState("Failed to load money records")

    private val budgetsFlow = repository.observeBudgets(currentUserId)
        .toSafeStreamState("Failed to load spending limits")

    private val savingsFlow = repository.observeSavingsGoals(currentUserId)
        .toSafeStreamState("Failed to load savings goals")

    private val dailyNoteFlow = _selectedDate.flatMapLatest { date ->
        repository.observeDailyNote(currentUserId, date)
            .toSafeStreamState("Failed to load diary note")
    }

    val uiState: StateFlow<FinanceUiState> = combine(
        profileFlow,
        transactionsFlow,
        budgetsFlow,
        savingsFlow,
        dailyNoteFlow,
        _selectedDate,
        _searchQuery,
        _filterCategory,
        _filterPaymentMethod,
        _filterType,
        _activeScreen,
        _showAddIncomeSheet,
        _showAddExpenseSheet,
        _editingTransaction,
        _isTranscribing,
        _voiceTranscript,
        _voiceParsedTransaction,
        _voiceError,
        _statusError,
        _budgetAlert
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val profileState = args[0] as StreamState<UserProfileDoc?>
        @Suppress("UNCHECKED_CAST")
        val txState = args[1] as StreamState<List<TransactionDoc>>
        @Suppress("UNCHECKED_CAST")
        val budgetsState = args[2] as StreamState<List<BudgetDoc>>
        @Suppress("UNCHECKED_CAST")
        val savingsState = args[3] as StreamState<List<SavingsGoalDoc>>
        @Suppress("UNCHECKED_CAST")
        val noteState = args[4] as StreamState<DailyNoteDoc?>

        val selectedDate = args[5] as String
        val search = args[6] as String
        val filterCat = args[7] as String?
        val filterPay = args[8] as String?
        val filterType = args[9] as String
        val activeScreen = args[10] as Screen
        val showAddInc = args[11] as Boolean
        val showAddExp = args[12] as Boolean
        val editingTx = args[13] as TransactionDoc?
        val isTranscribing = args[14] as Boolean
        val voiceTranscript = args[15] as String?
        val voiceParsed = args[16] as VoiceParsedTransaction?
        val voiceError = args[17] as String?
        val statusError = args[18] as String?
        val budgetAlert = args[19] as BudgetAlert?

        val isProfileLoading = profileState is StreamState.Loading
        val profile = (profileState as? StreamState.Data)?.value ?: UserProfileDoc(
            userId = currentUserId,
            fullName = initialDisplayName.ifBlank { "Student" },
            email = initialEmail
        )
        val showOnboarding = profileState is StreamState.Data && (profileState.value == null || !profileState.value.hasCompletedOnboarding)

        val transactions = (txState as? StreamState.Data)?.value ?: emptyList()
        val budgets = (budgetsState as? StreamState.Data)?.value ?: emptyList()
        val savings = (savingsState as? StreamState.Data)?.value ?: emptyList()
        val dailyNote = (noteState as? StreamState.Data)?.value?.note ?: ""

        val streamError = listOfNotNull(
            (profileState as? StreamState.Error)?.message,
            (txState as? StreamState.Error)?.message,
            (budgetsState as? StreamState.Error)?.message,
            (savingsState as? StreamState.Error)?.message
        ).firstOrNull()

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

            txCalendar.timeInMillis = t.clientTimestamp
            if (txCalendar.get(Calendar.MONTH) == currentMonth &&
                txCalendar.get(Calendar.YEAR) == currentYear
            ) {
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
        val monthlyLimit = if (profile.monthlyBudgetTarget > 0) profile.monthlyBudgetTarget else 300.0
        val remainingLimit = monthlyLimit - monthExpense
        val budgetPct = if (monthlyLimit > 0) ((monthExpense / monthlyLimit).toFloat()).coerceIn(0f, 2f) else 0f

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

        val dateTransactions = transactions.filter { it.dateString == selectedDate }

        FinanceUiState(
            isProfileLoading = isProfileLoading,
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
            monthlyBudgetRemaining = remainingLimit,
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
            showOnboarding = showOnboarding,
            isTranscribing = isTranscribing,
            voiceTranscript = voiceTranscript,
            voiceParsedTransaction = voiceParsed,
            voiceError = voiceError,
            statusError = statusError ?: streamError,
            budgetAlert = budgetAlert
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = FinanceUiState()
    )

    fun setScreen(screen: Screen) {
        _activeScreen.value = screen
    }

    fun setSelectedDate(dateString: String) {
        _selectedDate.value = dateString
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

    fun clearVoiceState() {
        _voiceTranscript.value = null
        _voiceParsedTransaction.value = null
        _voiceError.value = null
    }

    fun clearStatusError() {
        _statusError.value = null
    }

    fun clearBudgetAlert() {
        _budgetAlert.value = null
    }

    fun showAddIncome(show: Boolean) {
        if (show) clearVoiceState()
        _showAddIncomeSheet.value = show
    }

    fun showAddExpense(show: Boolean) {
        if (show) clearVoiceState()
        _showAddExpenseSheet.value = show
    }

    fun setEditingTransaction(tx: TransactionDoc?) {
        _editingTransaction.value = tx
    }

    fun transcribeVoiceForTransaction(audioBytes: ByteArray, defaultType: String) {
        viewModelScope.launch {
            _isTranscribing.value = true
            _voiceError.value = null
            val result = voiceRepository.transcribeAndParseTransaction(
                audioBytes = audioBytes,
                mimeType = "audio/mp4",
                defaultType = defaultType
            )
            _isTranscribing.value = false
            result.onSuccess { parsed ->
                _voiceTranscript.value = parsed.transcript
                _voiceParsedTransaction.value = parsed
            }.onFailure { err ->
                _voiceError.value = err.message ?: "Could not transcribe voice recording."
            }
        }
    }

    fun transcribeVoiceForDiary(audioBytes: ByteArray) {
        viewModelScope.launch {
            _isTranscribing.value = true
            _voiceError.value = null
            val result = voiceRepository.transcribeAudio(audioBytes, "audio/mp4")
            _isTranscribing.value = false
            result.onSuccess { text ->
                val existing = uiState.value.currentDailyNote
                val combined = if (existing.isBlank()) text else "$existing\n$text"
                saveDailyNote(combined)
                _voiceTranscript.value = text
            }.onFailure { err ->
                _voiceError.value = err.message ?: "Could not transcribe voice note."
            }
        }
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
            val tx = TransactionDoc(
                userId = currentUserId,
                type = "INCOME",
                amount = amount,
                category = source,
                paymentMethod = paymentMethod,
                source = source,
                description = description.ifBlank { "Money from $source" },
                notes = notes,
                dateString = date,
                timeString = time,
                currency = uiState.value.userProfile.preferredCurrency,
                clientTimestamp = System.currentTimeMillis()
            )
            val result = repository.addTransaction(currentUserId, tx)
            result.onSuccess {
                _showAddIncomeSheet.value = false
                clearVoiceState()
            }.onFailure {
                _statusError.value = it.localizedMessage ?: "Failed to save income."
            }
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
            val tx = TransactionDoc(
                userId = currentUserId,
                type = "EXPENSE",
                amount = amount,
                category = category,
                paymentMethod = paymentMethod,
                description = description.ifBlank { category },
                notes = notes,
                dateString = date,
                timeString = time,
                currency = uiState.value.userProfile.preferredCurrency,
                clientTimestamp = System.currentTimeMillis()
            )
            val result = repository.addTransaction(currentUserId, tx)
            result.onSuccess {
                _showAddExpenseSheet.value = false
                clearVoiceState()
                checkBudgetAlertBanner(category, amount)
            }.onFailure {
                _statusError.value = it.localizedMessage ?: "Failed to save spending record."
            }
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

    fun updateTransaction(transaction: TransactionDoc) {
        viewModelScope.launch {
            val result = repository.updateTransaction(currentUserId, transaction)
            result.onSuccess {
                _editingTransaction.value = null
            }.onFailure {
                _statusError.value = it.localizedMessage ?: "Failed to update record."
            }
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            val result = repository.deleteTransaction(currentUserId, id)
            result.onSuccess {
                if (_editingTransaction.value?.id == id) {
                    _editingTransaction.value = null
                }
            }.onFailure {
                _statusError.value = it.localizedMessage ?: "Failed to delete record."
            }
        }
    }

    fun addBudget(name: String, category: String?, amount: Double, period: String = "MONTHLY") {
        viewModelScope.launch {
            repository.addBudget(currentUserId, name, category, amount, period)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to save spending limit." }
        }
    }

    fun deleteBudget(id: String) {
        viewModelScope.launch {
            repository.deleteBudget(currentUserId, id)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to remove spending limit." }
        }
    }

    fun addSavingsGoal(
        name: String,
        targetAmount: Double,
        categoryIcon: String = "savings",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addSavingsGoal(currentUserId, name, targetAmount, categoryIcon, notes)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to create savings goal." }
        }
    }

    fun contributeToSavings(goalId: String, amount: Double, note: String = "") {
        viewModelScope.launch {
            repository.contributeToSavingsGoal(currentUserId, goalId, amount, note)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to add money to goal." }
        }
    }

    fun deleteSavingsGoal(id: String) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(currentUserId, id)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to delete savings goal." }
        }
    }

    fun saveDailyNote(note: String) {
        viewModelScope.launch {
            repository.saveDailyNote(currentUserId, _selectedDate.value, note)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to save note." }
        }
    }

    fun saveProfile(profile: UserProfileDoc) {
        viewModelScope.launch {
            repository.saveUserProfile(currentUserId, profile)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to save settings." }
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
                repository.loadRealisticDemoData(currentUserId)
                    .onFailure { _statusError.value = it.localizedMessage ?: "Failed to load demo data." }
            } else {
                val updated = uiState.value.userProfile.copy(
                    userId = currentUserId,
                    fullName = fullName.ifBlank { initialDisplayName.ifBlank { "Student" } },
                    email = initialEmail,
                    university = university.ifBlank { "University" },
                    preferredCurrency = preferredCurrency,
                    currencySymbol = currencySymbol,
                    startingBalance = startingBalance,
                    monthlyBudgetTarget = monthlyBudget,
                    hasCompletedOnboarding = true
                )
                repository.saveUserProfile(currentUserId, updated)
                    .onFailure { _statusError.value = it.localizedMessage ?: "Failed to save profile." }
            }
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            repository.loadRealisticDemoData(currentUserId)
                .onFailure { _statusError.value = it.localizedMessage ?: "Failed to load sample data." }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData(currentUserId)
            val resetProfile = uiState.value.userProfile.copy(
                userId = currentUserId,
                hasCompletedOnboarding = true,
                startingBalance = 0.0,
                monthlyBudgetTarget = 300.0
            )
            repository.saveUserProfile(currentUserId, resetProfile)
        }
    }

    fun exportCsv(): String {
        return repository.exportTransactionsToCsv(uiState.value.transactions)
    }

    private fun checkBudgetAlertBanner(category: String, addedAmount: Double) {
        val catBudget = uiState.value.budgets.find { it.category == category } ?: return
        val currentSpent = (uiState.value.categorySpendingMonth[category] ?: 0.0) + addedAmount
        val ratio = if (catBudget.amount > 0) currentSpent / catBudget.amount else 0.0

        when {
            ratio >= 1.0 -> _budgetAlert.value = BudgetAlert(category, 100)
            ratio >= 0.8 -> _budgetAlert.value = BudgetAlert(category, 80)
            ratio >= 0.5 -> _budgetAlert.value = BudgetAlert(category, 50)
        }
    }
}
