package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuickPreset
import com.example.data.remote.TransactionDoc
import com.example.ui.FinanceUiState
import com.example.ui.Screen
import com.example.ui.components.HeaderBar
import com.example.ui.components.QuickPresetsRow
import com.example.ui.components.TransactionRow
import com.example.ui.components.WalletBalanceCard
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.MyMariTheme
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: FinanceUiState,
    onNavigate: (Screen) -> Unit,
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onTopUpClick: () -> Unit,
    onQuickPresetClick: (QuickPreset) -> Unit,
    onTransactionClick: (TransactionDoc) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onDismissStatusError: () -> Unit,
    onDismissBudgetAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    val currency = uiState.userProfile.currencySymbol
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val motivation = strings.motivationalMessages[(uiState.transactions.size + hour) % strings.motivationalMessages.size]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvasBg),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header
        item {
            HeaderBar(
                userName = uiState.userProfile.fullName,
                onSearchClick = { onNavigate(Screen.DIARY) },
                onNotificationClick = { onNavigate(Screen.PROFILE) },
                onProfileClick = { onNavigate(Screen.PROFILE) }
            )
        }

        // Optional Status Error or Budget Alert Banner
        if (!uiState.statusError.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.errorBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.statusError,
                            fontSize = 12.sp,
                            color = colors.errorText,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismissStatusError, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = strings.close, tint = colors.errorText)
                        }
                    }
                }
            }
        }

        uiState.budgetAlert?.let { alert ->
            val catDisplay = strings.categoryLabel(alert.category)
            val alertMsg = when {
                alert.levelPct >= 100 -> String.format(strings.budgetAlertReached, catDisplay)
                alert.levelPct >= 80 -> String.format(strings.budgetAlert80, catDisplay)
                else -> String.format(strings.budgetAlert50, catDisplay)
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.warningBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = colors.alertOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = alertMsg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.warningText
                            )
                        }
                        IconButton(onClick = onDismissBudgetAlert, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = strings.close, tint = colors.warningText)
                        }
                    }
                }
            }
        }

        // 2. Friendly encouragement chip
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.softSurface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = motivation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 3. Balance Hero Card
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

        // 4. Monthly Limit Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.thisMonthLimitTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = String.format(strings.pctUsedShort, (uiState.monthlyBudgetPercentage * 100).toInt()),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.monthlyBudgetPercentage >= 0.8f) colors.alertOrange else colors.accentPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progressColor = when {
                        uiState.monthlyBudgetPercentage >= 1.0f -> colors.expenseRed
                        uiState.monthlyBudgetPercentage >= 0.8f -> colors.alertOrange
                        else -> colors.incomeGreen
                    }

                    LinearProgressIndicator(
                        progress = { uiState.monthlyBudgetPercentage.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = progressColor,
                        trackColor = colors.softSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryStatItem(strings.statLimit, "$currency${String.format(Locale.US, "%,.2f", uiState.userProfile.monthlyBudgetTarget)}", colors.textPrimary)
                        SummaryStatItem(strings.statSpent, "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyExpense)}", colors.textPrimary)
                        SummaryStatItem(
                            strings.statLeft,
                            "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)}",
                            if (uiState.monthlyBudgetRemaining < 0) colors.expenseRed else colors.incomeGreen
                        )
                        SummaryStatItem(strings.statSaved, "$currency${String.format(Locale.US, "%,.2f", uiState.totalSaved)}", colors.accentText)
                    }
                }
            }
        }

        // 5. Quick Presets Row
        item {
            QuickPresetsRow(
                currencySymbol = currency,
                onPresetClick = onQuickPresetClick,
                onCustomAddClick = onSendClick
            )
        }

        // 6. Recent Money Activity Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.recentActivityTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Text(
                    text = strings.seeAll,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accentPrimary,
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
                        .padding(horizontal = 20.dp)
                        .border(1.dp, colors.softBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.nothingAddedYet,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.emptyHomeTip,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(recentList, key = { it.id }) { transaction ->
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

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
private fun SummaryStatItem(label: String, value: String, valueColor: Color) {
    val colors = MyMariTheme.colors
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = colors.textSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
