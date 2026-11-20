package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.data.remote.SavingsGoalDoc
import com.example.ui.FinanceUiState
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.CanvasBgLight
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintContainer
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BudgetsScreen(
    uiState: FinanceUiState,
    onAddBudget: (name: String, category: String?, amount: Double) -> Unit,
    onDeleteBudget: (String) -> Unit,
    onAddSavingsGoal: (name: String, targetAmount: Double, notes: String) -> Unit,
    onContributeSavings: (goalId: String, amount: Double) -> Unit,
    onDeleteSavingsGoal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.userProfile.currencySymbol
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddSavingsDialog by remember { mutableStateOf(false) }
    var contributingGoal by remember { mutableStateOf<SavingsGoalDoc?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBgLight),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Spending Limits & Goals",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Set easy spending limits and save for what matters",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Section 1: Light Pastel Mint Overall Monthly Limit Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(SoftMintSurface, SoftMintContainer)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Monthly Spending Limit",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreenDark
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, SoftMintBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${(uiState.monthlyBudgetPercentage * 100).toInt()}% used",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldBrand
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyExpense)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )

                        Text(
                            text = "spent out of $currency${String.format(Locale.US, "%,.2f", uiState.userProfile.monthlyBudgetTarget)} limit",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val pColor = when {
                            uiState.monthlyBudgetPercentage >= 1.0f -> Color(0xFFDC2626)
                            uiState.monthlyBudgetPercentage >= 0.8f -> AlertOrange
                            else -> EmeraldBrand
                        }

                        LinearProgressIndicator(
                            progress = { uiState.monthlyBudgetPercentage.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = pColor,
                            trackColor = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (uiState.monthlyBudgetRemaining >= 0) {
                                "Still left to spend: $currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)}"
                            } else {
                                "Over your limit by $currency${String.format(Locale.US, "%,.2f", -uiState.monthlyBudgetRemaining)}"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.monthlyBudgetRemaining >= 0) ForestGreenDark else Color(0xFFDC2626)
                        )
                    }
                }
            }
        }

        // Section 2: Limits by Category Header + Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Limits by Category",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "+ Add Limit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldBrand,
                    modifier = Modifier
                        .clickable { showAddBudgetDialog = true }
                        .testTag("btn_add_category_budget")
                )
            }
        }

        // Category Limits List
        if (uiState.budgets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(1.dp, SoftMintBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No category limits yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Set limits for Groceries, Canteen, Transport, or Airtime so MyMari warns you when you get close.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.budgets, key = { it.id }) { budget ->
                val spent = uiState.categorySpendingMonth[budget.category ?: ""] ?: 0.0
                val ratio = if (budget.amount > 0) (spent / budget.amount).toFloat() else 0f
                val remaining = budget.amount - spent

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(1.dp, SoftMintBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            FinanceConstants.getCategoryColor(budget.category ?: "").copy(alpha = 0.14f)
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
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Limit: $currency${String.format(Locale.US, "%,.2f", budget.amount)}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteBudget(budget.id) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Limit",
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
                            trackColor = SoftMintSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: $currency${String.format(Locale.US, "%,.2f", spent)} (${(ratio * 100).toInt()}%)",
                                fontSize = 11.sp,
                                color = TextSecondary
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

        // Section 3: Things I'm Saving For Header + Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Things I'm Saving For",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "+ New Goal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldBrand,
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
                        .padding(horizontal = 20.dp)
                        .border(1.dp, SoftMintBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No savings goals yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Save little by little for an Emergency Fund, Laptop, Textbooks, or Semester Break!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.savingsGoals, key = { it.id }) { goal ->
                val ratio = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(1.dp, SoftMintBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(SoftMintContainer),
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
                                        color = TextPrimary
                                    )
                                    if (goal.notes.isNotBlank()) {
                                        Text(
                                            text = goal.notes,
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(EmeraldBrand)
                                        .clickable { contributingGoal = goal }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "+ Add Money",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteSavingsGoal(goal.id) },
                                    modifier = Modifier.size(48.dp)
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
                            trackColor = SoftMintSurface
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
                                text = "Goal: $currency${String.format(Locale.US, "%,.2f", goal.targetAmount)} (${(ratio * 100).toInt()}%)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    // Dialog: Add Category Limit
    if (showAddBudgetDialog) {
        var bName by remember { mutableStateOf("") }
        var bCategory by remember { mutableStateOf(FinanceConstants.EXPENSE_CATEGORIES.first()) }
        var bAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddBudgetDialog = false },
            containerColor = Color.White,
            title = { Text("Add Category Limit", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bName,
                        onValueChange = { bName = it },
                        label = { Text("Label (Optional)") },
                        placeholder = { Text("e.g. Food & Snacks") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = bAmount,
                        onValueChange = { bAmount = it },
                        label = { Text("Monthly Limit ($currency)") },
                        placeholder = { Text("e.g. 80.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Text(
                        text = "Pick a category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FinanceConstants.EXPENSE_CATEGORIES.take(6).forEach { cat ->
                            val isSel = bCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) SoftMintContainer else SoftMintSurface)
                                    .border(1.dp, if (isSel) EmeraldBrand else SoftMintBorder, RoundedCornerShape(10.dp))
                                    .clickable { bCategory = cat }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) ForestGreenDark else TextPrimary
                                )
                            }
                        }
                    }
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
                        containerColor = EmeraldBrand,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save Limit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: New Savings Goal
    if (showAddSavingsDialog) {
        var gName by remember { mutableStateOf("") }
        var gTarget by remember { mutableStateOf("") }
        var gNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSavingsDialog = false },
            containerColor = Color.White,
            title = { Text("New Savings Goal", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = gName,
                        onValueChange = { gName = it },
                        label = { Text("What are you saving for?") },
                        placeholder = { Text("e.g. New Laptop or Textbooks") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = gTarget,
                        onValueChange = { gTarget = it },
                        label = { Text("How much do you need? ($currency)") },
                        placeholder = { Text("e.g. 450.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = gNotes,
                        onValueChange = { gNotes = it },
                        label = { Text("Short note (Optional)") },
                        placeholder = { Text("e.g. For next semester") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
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
                        containerColor = EmeraldBrand,
                        contentColor = Color.White
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

    // Dialog: Add Money to Savings Goal
    contributingGoal?.let { goal ->
        var cAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { contributingGoal = null },
            containerColor = Color.White,
            title = { Text("Save for ${goal.name}", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Move money from your spending balance into this savings goal.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = cAmount,
                        onValueChange = { cAmount = it },
                        label = { Text("Amount to save ($currency)") },
                        placeholder = { Text("e.g. 20.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
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
                        containerColor = EmeraldBrand,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save Now")
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
