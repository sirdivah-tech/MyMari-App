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
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.CanvasBgLight
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
    val currency = uiState.userProfile.currencySymbol
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val motivationalMessages = listOf(
        "Every dollar has a job. Keep your money in mind!",
        "Small savings today help you handle tomorrow.",
        "Track each snack and ride so nothing disappears.",
        "You're doing great taking charge of your student money!"
    )
    val motivation = motivationalMessages[(uiState.transactions.size + hour) % motivationalMessages.size]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBgLight),
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
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
                            color = Color(0xFFB91C1C),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismissStatusError, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss error", tint = Color(0xFFB91C1C))
                        }
                    }
                }
            }
        }

        if (!uiState.budgetAlertMessage.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
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
                                contentDescription = "Limit alert",
                                tint = AlertOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.budgetAlertMessage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF9A3412)
                            )
                        }
                        IconButton(onClick = onDismissBudgetAlert, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss alert", tint = Color(0xFF9A3412))
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
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftMintSurface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tip",
                        tint = EmeraldBrand,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = motivation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
        }

        // 3. Light Pastel Mint Balance Card
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

        // 4. Plain-English Monthly Limit Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "This Month's Spending Limit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${(uiState.monthlyBudgetPercentage * 100).toInt()}% used",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.monthlyBudgetPercentage >= 0.8f) AlertOrange else EmeraldBrand
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
                        trackColor = SoftMintSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryStatItem("Limit", "$currency${String.format(Locale.US, "%,.2f", uiState.userProfile.monthlyBudgetTarget)}", TextPrimary)
                        SummaryStatItem("Spent", "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyExpense)}", TextPrimary)
                        SummaryStatItem(
                            "Left",
                            "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)}",
                            if (uiState.monthlyBudgetRemaining < 0) Color(0xFFDC2626) else IncomeGreen
                        )
                        SummaryStatItem("Saved", "$currency${String.format(Locale.US, "%,.2f", uiState.totalSaved)}", ForestGreenDark)
                    }
                }
            }
        }

        // 5. Quick Presets Row (1-Tap Common Buys + Voice Speak/Add)
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
                    text = "Recent Money Activity",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "See All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldBrand,
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
                        .border(1.dp, SoftMintBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nothing added yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'I Spent', 'Got Paid', or 'Speak/Add' above to write down your first money note.",
                            fontSize = 12.sp,
                            color = TextSecondary,
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
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
