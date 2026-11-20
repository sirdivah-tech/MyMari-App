package com.example

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.ai.VoiceTranscriptionRepository
import com.example.data.remote.FirestoreFinanceRepository
import com.example.data.remote.UserProfileDoc
import com.example.firebase.configureAppCheck
import com.example.reminder.NotificationHelper
import com.example.reminder.ReminderScheduler
import com.example.ui.FinanceViewModel
import com.example.ui.Screen
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.authStateFlow
import com.example.ui.auth.signOut
import com.example.ui.components.AddTransactionSheet
import com.example.ui.components.EditTransactionDialog
import com.example.ui.components.FloatingNavBar
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.CanvasBgLight
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase App Check BEFORE any UI, ViewModel, or Firebase calls
        configureAppCheck(this, intent)
        NotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false) {
                MyMariRoot()
            }
        }
    }
}

@Composable
fun MyMariRoot() {
    val currentUser by Firebase.auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = { /* authStateFlow updates automatically */ })
    } else {
        key(user.uid) {
            val context = LocalContext.current
            val appContext = context.applicationContext
            val viewModel: FinanceViewModel = viewModel(
                key = user.uid,
                factory = viewModelFactory {
                    initializer {
                        val dbId = appContext.getString(R.string.firestore_database_id)
                        val firestore = FirebaseFirestore.getInstance(dbId)
                        val repository = FirestoreFinanceRepository(firestore)
                        val voiceRepository = VoiceTranscriptionRepository()
                        FinanceViewModel(
                            repository = repository,
                            voiceRepository = voiceRepository,
                            currentUserId = user.uid,
                            initialDisplayName = user.displayName ?: "Student",
                            initialEmail = user.email ?: ""
                        )
                    }
                }
            )
            MyMariApp(
                viewModel = viewModel,
                initialName = user.displayName ?: "Student"
            )
        }
    }
}

@Composable
fun MyMariApp(
    viewModel: FinanceViewModel,
    initialName: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Keep reminder schedules synced with user preferences
    LaunchedEffect(
        uiState.userProfile.morningReminderEnabled,
        uiState.userProfile.morningReminderTime,
        uiState.userProfile.eveningReminderEnabled,
        uiState.userProfile.eveningReminderTime
    ) {
        syncReminderAlarms(context, uiState.userProfile)
    }

    if (uiState.activeScreen != Screen.HOME && !uiState.showOnboarding) {
        BackHandler {
            viewModel.setScreen(Screen.HOME)
        }
    }

    if (uiState.isProfileLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasBgLight),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = EmeraldBrand,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Loading your MyMari wallet...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }
        return
    }

    if (uiState.showOnboarding) {
        OnboardingScreen(
            initialName = initialName,
            onComplete = { name, university, currency, symbol, startBal, budget, loadDemo ->
                viewModel.completeOnboarding(
                    fullName = name,
                    university = university,
                    preferredCurrency = currency,
                    currencySymbol = symbol,
                    startingBalance = startBal,
                    monthlyBudget = budget,
                    loadDemo = loadDemo
                )
            }
        )
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets.systemBars,
            containerColor = CanvasBgLight,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CanvasBgLight)
                    .padding(innerPadding)
            ) {
                when (uiState.activeScreen) {
                    Screen.HOME -> HomeScreen(
                        uiState = uiState,
                        onNavigate = { screen -> viewModel.setScreen(screen) },
                        onSendClick = { viewModel.showAddExpense(true) },
                        onReceiveClick = { viewModel.showAddIncome(true) },
                        onTopUpClick = { viewModel.setScreen(Screen.BUDGETS) },
                        onQuickPresetClick = { preset -> viewModel.quickAddExpense(preset) },
                        onTransactionClick = { tx -> viewModel.setEditingTransaction(tx) },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) },
                        onDismissStatusError = { viewModel.clearStatusError() },
                        onDismissBudgetAlert = { viewModel.clearBudgetAlert() }
                    )

                    Screen.DIARY -> DiaryScreen(
                        uiState = uiState,
                        onDateSelected = { date -> viewModel.setSelectedDate(date) },
                        onSaveNote = { note -> viewModel.saveDailyNote(note) },
                        onVoiceDiaryAudio = { bytes -> viewModel.transcribeVoiceForDiary(bytes) },
                        onSearchChange = { q -> viewModel.setSearchQuery(q) },
                        onCategoryFilterChange = { cat -> viewModel.setFilterCategory(cat) },
                        onPaymentMethodFilterChange = { method -> viewModel.setFilterPaymentMethod(method) },
                        onTypeFilterChange = { type -> viewModel.setFilterType(type) },
                        onTransactionClick = { tx -> viewModel.setEditingTransaction(tx) },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) }
                    )

                    Screen.BUDGETS -> BudgetsScreen(
                        uiState = uiState,
                        onAddBudget = { name, cat, amount -> viewModel.addBudget(name, cat, amount) },
                        onDeleteBudget = { id -> viewModel.deleteBudget(id) },
                        onAddSavingsGoal = { name, target, notes ->
                            viewModel.addSavingsGoal(name, target, "savings", notes)
                        },
                        onContributeSavings = { goalId, amount ->
                            viewModel.contributeToSavings(goalId, amount)
                        },
                        onDeleteSavingsGoal = { id -> viewModel.deleteSavingsGoal(id) }
                    )

                    Screen.REPORTS -> ReportsScreen(
                        uiState = uiState,
                        onBackClick = { viewModel.setScreen(Screen.HOME) }
                    )

                    Screen.PROFILE -> ProfileScreen(
                        uiState = uiState,
                        onSaveProfile = { profile ->
                            viewModel.saveProfile(profile)
                            syncReminderAlarms(context, profile)
                        },
                        onTestReminder = { isMorning ->
                            val title = if (isMorning) {
                                "Good morning from MyMari!"
                            } else {
                                "Evening MyMari Check-in"
                            }
                            val msg = if (isMorning) {
                                "Check your remaining money for today before heading to class."
                            } else {
                                "Take 30 seconds to write down or speak today's spending!"
                            }
                            NotificationHelper.showNotification(
                                context = context,
                                notificationId = NotificationHelper.NOTIFICATION_ID_TEST,
                                title = title,
                                message = msg
                            )
                            Toast.makeText(context, "Sent test notification!", Toast.LENGTH_SHORT).show()
                        },
                        onLoadDemoData = { viewModel.loadDemoData() },
                        onClearAllData = { viewModel.clearAllData() },
                        onExportCsv = { viewModel.exportCsv() },
                        onSignOut = {
                            signOut(credentialManager = credentialManager, scope = scope)
                        }
                    )
                }

                FloatingNavBar(
                    currentScreen = uiState.activeScreen,
                    onScreenSelected = { screen -> viewModel.setScreen(screen) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    if (uiState.showAddIncomeSheet) {
        AddTransactionSheet(
            isIncome = true,
            currencySymbol = uiState.userProfile.currencySymbol,
            isTranscribing = uiState.isTranscribing,
            voiceTranscript = uiState.voiceTranscript,
            voiceParsed = uiState.voiceParsedTransaction,
            voiceError = uiState.voiceError,
            onVoiceAudioCaptured = { bytes -> viewModel.transcribeVoiceForTransaction(bytes, "INCOME") },
            onDismiss = { viewModel.showAddIncome(false) },
            onSubmitIncome = { amount, source, method, desc, notes, date, time ->
                viewModel.addIncome(amount, source, method, desc, notes, date, time)
            },
            onSubmitExpense = { _, _, _, _, _, _, _ -> }
        )
    }

    if (uiState.showAddExpenseSheet) {
        AddTransactionSheet(
            isIncome = false,
            currencySymbol = uiState.userProfile.currencySymbol,
            isTranscribing = uiState.isTranscribing,
            voiceTranscript = uiState.voiceTranscript,
            voiceParsed = uiState.voiceParsedTransaction,
            voiceError = uiState.voiceError,
            onVoiceAudioCaptured = { bytes -> viewModel.transcribeVoiceForTransaction(bytes, "EXPENSE") },
            onDismiss = { viewModel.showAddExpense(false) },
            onSubmitIncome = { _, _, _, _, _, _, _ -> },
            onSubmitExpense = { amount, category, method, desc, notes, date, time ->
                viewModel.addExpense(amount, category, method, desc, notes, date, time)
            }
        )
    }

    uiState.editingTransaction?.let { tx ->
        EditTransactionDialog(
            transaction = tx,
            currencySymbol = uiState.userProfile.currencySymbol,
            onDismiss = { viewModel.setEditingTransaction(null) },
            onSave = { updated -> viewModel.updateTransaction(updated) },
            onDelete = { id -> viewModel.deleteTransaction(id) }
        )
    }
}

private fun syncReminderAlarms(context: Context, profile: UserProfileDoc) {
    val (mHour, mMin) = parseHourMinute(profile.morningReminderTime, 7, 30)
    val (eHour, eMin) = parseHourMinute(profile.eveningReminderTime, 20, 0)
    ReminderScheduler.scheduleDailyReminder(
        context = context,
        type = ReminderScheduler.TYPE_MORNING,
        hour = mHour,
        minute = mMin,
        enabled = profile.morningReminderEnabled
    )
    ReminderScheduler.scheduleDailyReminder(
        context = context,
        type = ReminderScheduler.TYPE_EVENING,
        hour = eHour,
        minute = eMin,
        enabled = profile.eveningReminderEnabled
    )
}

private fun parseHourMinute(timeStr: String, defaultHour: Int, defaultMinute: Int): Pair<Int, Int> {
    val parts = timeStr.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: defaultHour
    val m = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: defaultMinute
    return h to m
}
