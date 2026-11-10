package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FinanceViewModel
import com.example.ui.Screen
import com.example.ui.components.AddTransactionSheet
import com.example.ui.components.EditTransactionDialog
import com.example.ui.components.FloatingNavBar
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyMariApp()
            }
        }
    }
}

@Composable
fun MyMariApp(viewModel: FinanceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Notification permission handled
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Back handling: if not on Home screen, pressing back returns to Home
    if (uiState.activeScreen != Screen.HOME && !uiState.showOnboarding) {
        BackHandler {
            viewModel.setScreen(Screen.HOME)
        }
    }

    if (uiState.showOnboarding) {
        OnboardingScreen(
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
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Screen Content
                when (uiState.activeScreen) {
                    Screen.HOME -> HomeScreen(
                        uiState = uiState,
                        onNavigate = { screen -> viewModel.setScreen(screen) },
                        onSendClick = { viewModel.showAddExpense(true) },
                        onReceiveClick = { viewModel.showAddIncome(true) },
                        onTopUpClick = { viewModel.setScreen(Screen.BUDGETS) },
                        onQuickPresetClick = { preset -> viewModel.quickAddExpense(preset) },
                        onTransactionClick = { tx -> viewModel.setEditingTransaction(tx) },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) }
                    )

                    Screen.DIARY -> DiaryScreen(
                        uiState = uiState,
                        onDateSelected = { date -> viewModel.setSelectedDate(date) },
                        onSaveNote = { note -> viewModel.saveDailyNote(note) },
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
                        onAddSavingsGoal = { name, target, notes -> viewModel.addSavingsGoal(name, target, "savings", notes) },
                        onContributeSavings = { goalId, amount -> viewModel.contributeToSavings(goalId, amount) },
                        onDeleteSavingsGoal = { id -> viewModel.deleteSavingsGoal(id) }
                    )

                    Screen.REPORTS -> ReportsScreen(
                        uiState = uiState,
                        onBackClick = { viewModel.setScreen(Screen.HOME) }
                    )

                    Screen.PROFILE -> ProfileScreen(
                        uiState = uiState,
                        onSaveProfile = { profile -> viewModel.saveProfile(profile) },
                        onTestReminder = { isMorning -> viewModel.testReminder(isMorning) },
                        onLoadDemoData = { viewModel.loadDemoData() },
                        onClearAllData = { viewModel.clearAllData() },
                        onExportCsv = { viewModel.exportCsv() }
                    )
                }

                // Floating Capsule Navigation Bar (Bottom Dock)
                FloatingNavBar(
                    currentScreen = uiState.activeScreen,
                    onScreenSelected = { screen -> viewModel.setScreen(screen) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Modal Sheet: Add Income
    if (uiState.showAddIncomeSheet) {
        AddTransactionSheet(
            isIncome = true,
            currencySymbol = uiState.userProfile.currencySymbol,
            onDismiss = { viewModel.showAddIncome(false) },
            onSubmitIncome = { amount, source, method, desc, notes, date, time ->
                viewModel.addIncome(amount, source, method, desc, notes, date, time)
            },
            onSubmitExpense = { _, _, _, _, _, _, _ -> }
        )
    }

    // Modal Sheet: Add Expense
    if (uiState.showAddExpenseSheet) {
        AddTransactionSheet(
            isIncome = false,
            currencySymbol = uiState.userProfile.currencySymbol,
            onDismiss = { viewModel.showAddExpense(false) },
            onSubmitIncome = { _, _, _, _, _, _, _ -> },
            onSubmitExpense = { amount, category, method, desc, notes, date, time ->
                viewModel.addExpense(amount, category, method, desc, notes, date, time)
            }
        )
    }

    // Dialog: Edit Transaction
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
