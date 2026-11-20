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
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyMariTheme
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
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    val currency = uiState.userProfile.currencySymbol
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddSavingsDialog by remember { mutableStateOf(false) }
    var contributingGoal by remember { mutableStateOf<SavingsGoalDoc?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvasBg),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = strings.budgetsTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = strings.budgetsSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(colors.heroGradientTop, colors.heroGradientBottom)
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
                                text = strings.monthlySpendingLimit,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentText
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.pillBg)
                                    .border(1.dp, colors.softBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = String.format(strings.pctUsedShort, (uiState.monthlyBudgetPercentage * 100).toInt()),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyExpense)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentText
                        )

                        Text(
                            text = String.format(
                                strings.spentOutOfLimit,
                                "$currency${String.format(Locale.US, "%,.2f", uiState.userProfile.monthlyBudgetTarget)}"
                            ),
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val pColor = when {
                            uiState.monthlyBudgetPercentage >= 1.0f -> colors.expenseRed
                            uiState.monthlyBudgetPercentage >= 0.8f -> colors.alertOrange
                            else -> colors.accentPrimary
                        }

                        LinearProgressIndicator(
                            progress = { uiState.monthlyBudgetPercentage.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = pColor,
                            trackColor = colors.pillBg
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (uiState.monthlyBudgetRemaining >= 0) {
                                String.format(
                                    strings.stillLeftToSpend,
                                    "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)}"
                                )
                            } else {
                                String.format(
                                    strings.overLimitBy,
                                    "$currency${String.format(Locale.US, "%,.2f", -uiState.monthlyBudgetRemaining)}"
                                )
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.monthlyBudgetRemaining >= 0) colors.accentText else colors.expenseRed
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.limitsByCategory,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Text(
                    text = strings.addLimitBtn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentPrimary,
                    modifier = Modifier
                        .clickable { showAddBudgetDialog = true }
                        .testTag("btn_add_category_budget")
                )
            }
        }

        if (uiState.budgets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(1.dp, colors.softBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.noCategoryLimitsYet,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.noCategoryLimitsTip,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.budgets, key = { it.id }) { budget ->
                val catKey = budget.category ?: ""
                val spent = uiState.categorySpendingMonth[catKey] ?: 0.0
                val ratio = if (budget.amount > 0) (spent / budget.amount).toFloat() else 0f
                val remaining = budget.amount - spent
                val displayName = if (budget.name == catKey) strings.categoryLabel(catKey) else budget.name

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(1.dp, colors.softBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg)
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
                                            FinanceConstants.getCategoryColor(catKey).copy(alpha = 0.14f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = FinanceConstants.getCategoryIcon(catKey),
                                        contentDescription = displayName,
                                        tint = FinanceConstants.getCategoryColor(catKey),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = displayName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = String.format(
                                            strings.limitPrefix,
                                            "$currency${String.format(Locale.US, "%,.2f", budget.amount)}"
                                        ),
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteBudget(budget.id) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = strings.delete,
                                    tint = colors.expenseRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val bColor = when {
                            ratio >= 1.0f -> colors.expenseRed
                            ratio >= 0.8f -> colors.alertOrange
                            else -> colors.incomeGreen
                        }

                        LinearProgressIndicator(
                            progress = { ratio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = bColor,
                            trackColor = colors.softSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = String.format(
                                    strings.spentPrefix,
                                    "$currency${String.format(Locale.US, "%,.2f", spent)}",
                                    (ratio * 100).toInt()
                                ),
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )

                            Text(
                                text = if (remaining >= 0) {
                                    String.format(strings.leftSuffix, "$currency${String.format(Locale.US, "%,.2f", remaining)}")
                                } else {
                                    String.format(strings.overByPrefix, "$currency${String.format(Locale.US, "%,.2f", -remaining)}")
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remaining >= 0) colors.accentText else colors.expenseRed
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.thingsSavingFor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Text(
                    text = strings.newGoalBtn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentPrimary,
                    modifier = Modifier
                        .clickable { showAddSavingsDialog = true }
                        .testTag("btn_new_savings_goal")
                )
            }
        }

        if (uiState.savingsGoals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(1.dp, colors.softBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.noSavingsGoalsYet,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.noSavingsGoalsTip,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
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
                        .border(1.dp, colors.softBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg)
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
                                        .background(colors.softContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = goal.name,
                                        tint = colors.accentText,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = goal.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    if (goal.notes.isNotBlank()) {
                                        Text(
                                            text = goal.notes,
                                            fontSize = 11.sp,
                                            color = colors.textSecondary,
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
                                        text = strings.addMoneyBtn,
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
                                        contentDescription = strings.delete,
                                        tint = colors.expenseRed,
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
                            color = colors.incomeGreen,
                            trackColor = colors.softSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = String.format(
                                    strings.savedPrefix,
                                    "$currency${String.format(Locale.US, "%,.2f", goal.currentAmount)}"
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentText
                            )

                            Text(
                                text = String.format(
                                    strings.goalPrefix,
                                    "$currency${String.format(Locale.US, "%,.2f", goal.targetAmount)}",
                                    (ratio * 100).toInt()
                                ),
                                fontSize = 12.sp,
                                color = colors.textSecondary
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

    if (showAddBudgetDialog) {
        var bName by remember { mutableStateOf("") }
        var bCategory by remember { mutableStateOf(FinanceConstants.EXPENSE_CATEGORIES.first()) }
        var bAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddBudgetDialog = false },
            containerColor = colors.cardBg,
            title = { Text(strings.addCategoryLimitDialogTitle, fontWeight = FontWeight.Bold, color = colors.textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bName,
                        onValueChange = { bName = it },
                        label = { Text(strings.labelOptional) },
                        placeholder = { Text(strings.placeholderBudgetLabel) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = bAmount,
                        onValueChange = { bAmount = it },
                        label = { Text(String.format(strings.monthlyLimitCurrency, currency)) },
                        placeholder = { Text("e.g. 80.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Text(
                        text = strings.pickCategory,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
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
                                    .background(if (isSel) colors.softContainer else colors.softSurface)
                                    .border(1.dp, if (isSel) colors.accentPrimary else colors.softBorder, RoundedCornerShape(10.dp))
                                    .clickable { bCategory = cat }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = strings.categoryLabel(cat),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) colors.accentText else colors.textPrimary
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
                    Text(strings.saveLimitBtn)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBudgetDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    if (showAddSavingsDialog) {
        var gName by remember { mutableStateOf("") }
        var gTarget by remember { mutableStateOf("") }
        var gNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSavingsDialog = false },
            containerColor = colors.cardBg,
            title = { Text(strings.newSavingsGoalDialogTitle, fontWeight = FontWeight.Bold, color = colors.textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = gName,
                        onValueChange = { gName = it },
                        label = { Text(strings.whatSavingFor) },
                        placeholder = { Text(strings.placeholderGoalName) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = gTarget,
                        onValueChange = { gTarget = it },
                        label = { Text(String.format(strings.howMuchNeed, currency)) },
                        placeholder = { Text("e.g. 450.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = gNotes,
                        onValueChange = { gNotes = it },
                        label = { Text(strings.shortNoteOptional) },
                        placeholder = { Text(strings.placeholderGoalNote) },
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
                    Text(strings.createGoalBtn)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSavingsDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    contributingGoal?.let { goal ->
        var cAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { contributingGoal = null },
            containerColor = colors.cardBg,
            title = { Text(String.format(strings.saveForGoalTitle, goal.name), fontWeight = FontWeight.Bold, color = colors.textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.saveForGoalDesc,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = cAmount,
                        onValueChange = { cAmount = it },
                        label = { Text(String.format(strings.amountToSave, currency)) },
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
                    Text(strings.saveNowBtn)
                }
            },
            dismissButton = {
                TextButton(onClick = { contributingGoal = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
