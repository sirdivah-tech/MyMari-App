package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.model.FinanceConstants
import com.example.ui.FinanceUiState
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.NeonLime
import java.util.Locale

@Composable
fun BudgetsScreen(
    uiState: FinanceUiState,
    onAddBudget: (name: String, category: String?, amount: Double) -> Unit,
    onDeleteBudget: (Long) -> Unit,
    onAddSavingsGoal: (name: String, targetAmount: Double, notes: String) -> Unit,
    onContributeSavings: (goalId: Long, amount: Double) -> Unit,
    onDeleteSavingsGoal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.userProfile.currencySymbol
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddSavingsDialog by remember { mutableStateOf(false) }
    var contributingGoal by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Budgets & Savings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Plan your student limits and track your goals",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 1: Monthly Overall Budget Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreenDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Monthly Budget",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD1FAE5)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonLime)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${(uiState.monthlyBudgetPercentage * 100).toInt()}% Used",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyExpense)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "of $currency${String.format(Locale.US, "%,.2f", uiState.userProfile.monthlyBudgetTarget)} monthly target",
                        fontSize = 12.sp,
                        color = Color(0xFFA7F3D0)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val pColor = when {
                        uiState.monthlyBudgetPercentage >= 1.0f -> Color(0xFFDC2626)
                        uiState.monthlyBudgetPercentage >= 0.8f -> AlertOrange
                        else -> NeonLime
                    }

                    LinearProgressIndicator(
                        progress = { uiState.monthlyBudgetPercentage.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = pColor,
                        trackColor = Color(0x33FFFFFF)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (uiState.monthlyBudgetRemaining >= 0) {
                            "Remaining: $currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)}"
                        } else {
                            "Over budget by $currency${String.format(Locale.US, "%,.2f", -uiState.monthlyBudgetRemaining)}"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.monthlyBudgetRemaining >= 0) NeonLime else Color(0xFFF87171)
                    )
                }
            }
        }

        // Section 2: Category Budgets Header + Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category Budgets",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "+ Add Budget",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { showAddBudgetDialog = true }
                        .testTag("btn_add_category_budget")
                )
            }
        }

        // Category Budgets List
        if (uiState.budgets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No category budgets set",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Set specific spending limits for Groceries, Canteen, Transport, or Airtime to get alerts at 50%, 80%, and 100%!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.budgets) { budget ->
                val spent = uiState.categorySpendingMonth[budget.category ?: ""] ?: 0.0
                val ratio = if (budget.amount > 0) (spent / budget.amount).toFloat() else 0f
                val remaining = budget.amount - spent

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            FinanceConstants.getCategoryColor(budget.category ?: "").copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = FinanceConstants.getCategoryIcon(budget.category ?: ""),
                                        contentDescription = budget.name,
                                        tint = FinanceConstants.getCategoryColor(budget.category ?: ""),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = budget.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${budget.period.lowercase().replaceFirstChar { it.uppercase() }} limit: $currency${String.format(Locale.US, "%,.2f", budget.amount)}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteBudget(budget.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Budget",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val bColor = when {
                            ratio >= 1.0f -> Color(0xFFDC2626)
                            ratio >= 0.8f -> AlertOrange
                            else -> IncomeGreen
                        }

                        LinearProgressIndicator(
                            progress = { ratio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = bColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: $currency${String.format(Locale.US, "%,.2f", spent)} (${(ratio * 100).toInt()}%)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = if (remaining >= 0) "$currency${String.format(Locale.US, "%,.2f", remaining)} left" else "Over by $currency${String.format(Locale.US, "%,.2f", -remaining)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remaining >= 0) ForestGreenDark else Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }
        }

        // Section 3: "My Savings" Goals Header + Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Savings Goals",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "+ New Goal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { showAddSavingsDialog = true }
                        .testTag("btn_new_savings_goal")
                )
            }
        }

        // Savings Goals List
        if (uiState.savingsGoals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No savings goals established yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create savings targets for an Emergency Fund, New Laptop, School Fees, or End-of-Semester trip!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.savingsGoals) { goal ->
                val ratio = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(NeonLime),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = goal.name,
                                        tint = ForestGreenDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = goal.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (goal.notes.isNotBlank()) {
                                        Text(
                                            text = goal.notes,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ForestGreenDark)
                                        .clickable { contributingGoal = goal }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "+ Contribute",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonLime
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteSavingsGoal(goal.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Goal",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { ratio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = IncomeGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Saved: $currency${String.format(Locale.US, "%,.2f", goal.currentAmount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreenDark
                            )

                            Text(
                                text = "Target: $currency${String.format(Locale.US, "%,.2f", goal.targetAmount)} (${(ratio * 100).toInt()}%)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Floating nav bar spacer
        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    // Dialog: Add Category Budget
    if (showAddBudgetDialog) {
        var bName by remember { mutableStateOf("") }
        var bCategory by remember { mutableStateOf(FinanceConstants.EXPENSE_CATEGORIES.first()) }
        var bAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddBudgetDialog = false },
            title = { Text("Add Category Budget", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bName,
                        onValueChange = { bName = it },
                        label = { Text("Budget Name") },
                        placeholder = { Text("e.g. Groceries & Snacks") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bAmount,
                        onValueChange = { bAmount = it },
                        label = { Text("Budget Limit ($currency)") },
                        placeholder = { Text("e.g. 80.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Category: $bCategory",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = bAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onAddBudget(bName.ifBlank { bCategory }, bCategory, amount)
                            showAddBudgetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreenDark,
                        contentColor = NeonLime
                    )
                ) {
                    Text("Save Budget")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Add Savings Goal
    if (showAddSavingsDialog) {
        var gName by remember { mutableStateOf("") }
        var gTarget by remember { mutableStateOf("") }
        var gNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSavingsDialog = false },
            title = { Text("New Savings Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = gName,
                        onValueChange = { gName = it },
                        label = { Text("Goal Name") },
                        placeholder = { Text("e.g. New Coding Laptop") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = gTarget,
                        onValueChange = { gTarget = it },
                        label = { Text("Target Amount ($currency)") },
                        placeholder = { Text("e.g. 450.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = gNotes,
                        onValueChange = { gNotes = it },
                        label = { Text("Purpose / Notes") },
                        placeholder = { Text("e.g. For semester computer science labs") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = gTarget.toDoubleOrNull() ?: 0.0
                        if (gName.isNotBlank() && target > 0) {
                            onAddSavingsGoal(gName, target, gNotes)
                            showAddSavingsDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreenDark,
                        contentColor = NeonLime
                    )
                ) {
                    Text("Create Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSavingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Contribute to Savings Goal
    contributingGoal?.let { goal ->
        var cAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { contributingGoal = null },
            title = { Text("Contribute to ${goal.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter amount to transfer from available wallet balance into this savings goal.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = cAmount,
                        onValueChange = { cAmount = it },
                        label = { Text("Contribution Amount ($currency)") },
                        placeholder = { Text("e.g. 20.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = cAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onContributeSavings(goal.id, amount)
                            contributingGoal = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreenDark,
                        contentColor = NeonLime
                    )
                ) {
                    Text("Deposit")
                }
            },
            dismissButton = {
                TextButton(onClick = { contributingGoal = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
