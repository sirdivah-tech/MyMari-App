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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.ui.FinanceUiState
import com.example.ui.Screen
import com.example.ui.components.HeaderBar
import com.example.ui.components.QuickPresetsRow
import com.example.ui.components.TransactionRow
import com.example.ui.components.WalletBalanceCard
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.NeonLime
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: FinanceUiState,
    onNavigate: (Screen) -> Unit,
    onSendClick: () -> Unit, // Add Expense
    onReceiveClick: () -> Unit, // Add Income
    onTopUpClick: () -> Unit, // Budget / Savings
    onQuickPresetClick: (com.example.data.model.QuickPreset) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.userProfile.currencySymbol
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    val motivationalMessages = listOf(
        "Financial freedom comes with financial responsibility.",
        "Every dollar counts. Small savings build big futures.",
        "Know where your money goes. Spend today, plan for tomorrow.",
        "You're doing well. Keep your money in mind!"
    )
    val motivation = motivationalMessages[(uiState.transactions.size + hour) % motivationalMessages.size]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header (Avatar, Greeting, Search, Bell)
        item {
            HeaderBar(
                userName = uiState.userProfile.fullName,
                onSearchClick = { onNavigate(Screen.DIARY) },
                onNotificationClick = { onNavigate(Screen.PROFILE) },
                onProfileClick = { onNavigate(Screen.PROFILE) }
            )
        }

        // 2. Motivational encouragement chip
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tip",
                        tint = ForestGreenDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = motivation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3. Hero Wallet Balance Card (Matching reference design)
        item {
            WalletBalanceCard(
                currencySymbol = currency,
                balance = uiState.totalAvailableBalance,
                budgetConsumedPct = uiState.monthlyBudgetPercentage,
                onSendClick = onSendClick,
                onReceiveClick = onReceiveClick,
                onTopUpClick = onTopUpClick,
                onMoreClick = { onNavigate(Screen.DIARY) }
            )
        }

        // 4. Monthly Budget Progress Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Budget Summary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(uiState.monthlyBudgetPercentage * 100).toInt()}% used",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.monthlyBudgetPercentage >= 0.8f) AlertOrange else ForestGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progressColor = when {
                        uiState.monthlyBudgetPercentage >= 1.0f -> Color(0xFFDC2626)
                        uiState.monthlyBudgetPercentage >= 0.8f -> AlertOrange
                        else -> IncomeGreen
                    }

                    LinearProgressIndicator(
                        progress = { uiState.monthlyBudgetPercentage.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Monthly Budget",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currency${String.format(Locale.US, "%,.2f", uiState.userProfile.monthlyBudgetTarget)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column {
                            Text(
                                text = "Total Spent",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyExpense)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column {
                            Text(
                                text = "Remaining",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.monthlyBudgetRemaining < 0) Color.Red else IncomeGreen
                            )
                        }

                        Column {
                            Text(
                                text = "Savings",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currency${String.format(Locale.US, "%,.2f", uiState.totalSaved)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                        }
                    }
                }
            }
        }

        // 5. Quick Presets Row (10-second fast logging)
        item {
            QuickPresetsRow(
                currencySymbol = currency,
                onPresetClick = onQuickPresetClick,
                onCustomAddClick = onSendClick
            )
        }

        // 6. "Manage Expenses" / Recent Transactions Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manage Expenses",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "See All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onNavigate(Screen.DIARY) }
                        .testTag("see_all_transactions")
                )
            }
        }

        // 7. Recent Transactions List
        val recentList = uiState.transactions.take(6)
        if (recentList.isEmpty()) {
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
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No transactions recorded yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Use '+ Add' or quick presets above to record your first student expense or allowance!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(recentList) { transaction ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TransactionRow(
                        transaction = transaction,
                        currencySymbol = currency,
                        onClick = { onTransactionClick(transaction) },
                        onDeleteClick = { onDeleteTransaction(transaction.id) }
                    )
                }
            }
        }

        // Space for floating nav bar
        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
