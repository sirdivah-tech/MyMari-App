package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.ui.DaySpending
import com.example.ui.FinanceUiState
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.MyMariTheme
import java.util.Locale

@Composable
fun ReportsScreen(
    uiState: FinanceUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    var selectedTab by remember { mutableStateOf("EXPENSE") }
    val currency = uiState.userProfile.currencySymbol

    val totalBalanceStr = String.format(Locale.US, "%,.2f", uiState.totalAvailableBalance)
    val totalExpenseStr = String.format(Locale.US, "%,.2f", uiState.monthlyExpense)
    val totalIncomeStr = String.format(Locale.US, "%,.2f", uiState.monthlyIncome)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvasBg),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.softSurface)
                        .border(1.dp, colors.softBorder, CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.back,
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = strings.reportsTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .background(colors.softSurface)
                        .border(1.dp, colors.softBorder, RoundedCornerShape(26.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val incomeSelected = selectedTab == "INCOME"
                    val expenseSelected = selectedTab == "EXPENSE"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (incomeSelected) EmeraldBrand else Color.Transparent)
                            .clickable { selectedTab = "INCOME" }
                            .padding(horizontal = 26.dp, vertical = 9.dp)
                            .testTag("tab_income"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.moneyIn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (incomeSelected) Color.White else colors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (expenseSelected) EmeraldBrand else Color.Transparent)
                            .clickable { selectedTab = "EXPENSE" }
                            .padding(horizontal = 26.dp, vertical = 9.dp)
                            .testTag("tab_expense"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.moneyOut,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (expenseSelected) Color.White else colors.textSecondary
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (selectedTab == "INCOME") strings.moneyReceivedThisMonth else strings.moneyLeftRightNow,
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (selectedTab == "INCOME") "$currency$totalIncomeStr" else "$currency$totalBalanceStr",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
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
                            Column {
                                Text(
                                    text = if (selectedTab == "INCOME") strings.totalMoneyIn else strings.spentThisMonth,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "$currency${if (selectedTab == "INCOME") totalIncomeStr else totalExpenseStr}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentText
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colors.pillBg)
                                    .border(1.dp, colors.softBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = strings.reportsTitle,
                                    tint = colors.accentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        WeeklySpendingBarChart(
                            days = uiState.weeklySpending,
                            currencySymbol = currency
                        )
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.billsComingUp,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = strings.monthlyLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val upcomingBills = listOf(
                    UpcomingPayment(strings.billRent, "${currency}80.00/m", Color(0xFFE11D48)),
                    UpcomingPayment(strings.billWifi, "${currency}12.00/m", Color(0xFF0284C7)),
                    UpcomingPayment(strings.billElectricity, "${currency}15.00/m", Color(0xFFF59E0B)),
                    UpcomingPayment(strings.billHealth, "${currency}10.00/m", Color(0xFF10B981))
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(upcomingBills) { bill ->
                        Card(
                            modifier = Modifier
                                .width(132.dp)
                                .height(118.dp)
                                .border(1.dp, colors.softBorder, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBg)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(bill.color.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = bill.name,
                                        tint = bill.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = bill.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.textSecondary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = bill.amount,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.whatYouSpentOn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (uiState.categorySpendingMonth.isEmpty()) {
                        Text(
                            text = strings.noSpendingThisMonth,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    } else {
                        DonutSpendingChart(
                            categoryMap = uiState.categorySpendingMonth,
                            currencySymbol = currency
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = strings.simpleMoneyTips,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.simpleMoneyTips,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val totalSpent = uiState.monthlyExpense
                    val topCategory = uiState.categorySpendingMonth.maxByOrNull { it.value }
                    val lowestCategory = uiState.categorySpendingMonth.minByOrNull { it.value }

                    if (topCategory != null && totalSpent > 0) {
                        val pct = ((topCategory.value / totalSpent) * 100).toInt()
                        InsightItem(
                            text = String.format(
                                strings.tipMostSpent,
                                pct,
                                strings.categoryLabel(topCategory.key),
                                "$currency${String.format(Locale.US, "%,.2f", topCategory.value)}"
                            )
                        )
                    }

                    if (lowestCategory != null && lowestCategory.key != topCategory?.key) {
                        InsightItem(
                            text = String.format(
                                strings.tipLeastSpent,
                                strings.categoryLabel(lowestCategory.key),
                                "$currency${String.format(Locale.US, "%,.2f", lowestCategory.value)}"
                            )
                        )
                    }

                    InsightItem(
                        text = String.format(
                            strings.tipStillLeft,
                            "$currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)}"
                        )
                    )

                    if (uiState.totalSaved > 0) {
                        InsightItem(
                            text = String.format(
                                strings.tipTotalSaved,
                                "$currency${String.format(Locale.US, "%,.2f", uiState.totalSaved)}"
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
private fun InsightItem(text: String) {
    val colors = MyMariTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(colors.accentPrimary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = colors.textSecondary,
            lineHeight = 18.sp
        )
    }
}

data class UpcomingPayment(
    val name: String,
    val amount: String,
    val color: Color
)

@Composable
fun WeeklySpendingBarChart(
    days: List<DaySpending>,
    currencySymbol: String
) {
    if (days.isEmpty()) return

    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    val maxAmount = (days.maxOfOrNull { it.amount } ?: 1.0).coerceAtLeast(10.0)
    val highestDay = days.maxByOrNull { it.amount }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (highestDay != null && highestDay.amount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.pillBg)
                        .border(1.dp, colors.softBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format(
                            strings.highestPrefix,
                            "$currencySymbol${String.format(Locale.US, "%,.0f", highestDay.amount)}"
                        ),
                        color = colors.accentText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { day ->
                val isPeak = day == highestDay && day.amount > 0
                val ratio = (day.amount / maxAmount).toFloat().coerceIn(0.14f, 1f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height((100 * ratio).dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isPeak) colors.accentPrimary else EmeraldBright.copy(alpha = 0.28f)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = day.dayLabel,
                        fontSize = 11.sp,
                        fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal,
                        color = if (isPeak) colors.accentText else colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DonutSpendingChart(
    categoryMap: Map<String, Double>,
    currencySymbol: String
) {
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    val total = categoryMap.values.sum().coerceAtLeast(1.0)
    val entries = categoryMap.entries.toList().sortedByDescending { it.value }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                entries.forEach { entry ->
                    val sweepAngle = ((entry.value / total) * 360f).toFloat()
                    val color = FinanceConstants.getCategoryColor(entry.key)

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 24f, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }

            Text(
                text = "$currencySymbol${String.format(Locale.US, "%,.0f", total)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            entries.take(4).forEach { entry ->
                val pct = ((entry.value / total) * 100).toInt()
                val color = FinanceConstants.getCategoryColor(entry.key)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.categoryLabel(entry.key),
                            fontSize = 12.sp,
                            color = colors.textPrimary,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "$pct%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}
