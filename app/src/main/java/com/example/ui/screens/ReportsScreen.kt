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
import com.example.ui.theme.CanvasBgLight
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintContainer
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun ReportsScreen(
    uiState: FinanceUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("EXPENSE") }
    val currency = uiState.userProfile.currencySymbol

    val totalBalanceStr = String.format(Locale.US, "%,.2f", uiState.totalAvailableBalance)
    val totalExpenseStr = String.format(Locale.US, "%,.2f", uiState.monthlyExpense)
    val totalIncomeStr = String.format(Locale.US, "%,.2f", uiState.monthlyIncome)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBgLight),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
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
                        .background(SoftMintSurface)
                        .border(1.dp, SoftMintBorder, CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Where Money Went",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // Light Segmented Toggle Pill: [ Money In | Money Out ]
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
                        .background(SoftMintSurface)
                        .border(1.dp, SoftMintBorder, RoundedCornerShape(26.dp))
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
                            text = "Money In",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (incomeSelected) Color.White else TextSecondary
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
                            text = "Money Out",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (expenseSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // Big Total Summary
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (selectedTab == "INCOME") "Money Received This Month" else "Money Left Right Now",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (selectedTab == "INCOME") "$currency$totalIncomeStr" else "$currency$totalBalanceStr",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Light Pastel Mint Chart Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
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
                            Column {
                                Text(
                                    text = if (selectedTab == "INCOME") "Total Money In" else "Spent This Month",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$currency${if (selectedTab == "INCOME") totalIncomeStr else totalExpenseStr}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, SoftMintBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = "Weekly summary",
                                    tint = EmeraldBrand,
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

        // Bills Coming Up
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
                        text = "Bills Coming Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Monthly",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldBrand
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val upcomingBills = listOf(
                    UpcomingPayment("Room Rent", "$80.00/m", Color(0xFFE11D48)),
                    UpcomingPayment("WiFi & Data", "$12.00/m", Color(0xFF0284C7)),
                    UpcomingPayment("Electricity", "$15.00/m", Color(0xFFF59E0B)),
                    UpcomingPayment("Health / Meds", "$10.00/m", Color(0xFF10B981))
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
                                .border(1.dp, SoftMintBorder, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = bill.amount,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Donut Spending Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "What You Spent On",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (uiState.categorySpendingMonth.isEmpty()) {
                        Text(
                            text = "No spending added this month yet.",
                            fontSize = 13.sp,
                            color = TextSecondary
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

        // Plain-language Money Tips
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = "Tips",
                            tint = EmeraldBrand,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simple Money Tips",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val totalSpent = uiState.monthlyExpense
                    val topCategory = uiState.categorySpendingMonth.maxByOrNull { it.value }
                    val lowestCategory = uiState.categorySpendingMonth.minByOrNull { it.value }

                    if (topCategory != null && totalSpent > 0) {
                        val pct = ((topCategory.value / totalSpent) * 100).toInt()
                        InsightItem(
                            text = "You spent $pct% of your money on ${topCategory.key} this month ($currency${String.format(Locale.US, "%,.2f", topCategory.value)})."
                        )
                    }

                    if (lowestCategory != null && lowestCategory.key != topCategory?.key) {
                        InsightItem(
                            text = "You spent the least on ${lowestCategory.key} ($currency${String.format(Locale.US, "%,.2f", lowestCategory.value)})."
                        )
                    }

                    InsightItem(
                        text = "You still have $currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)} left under this month's spending limit."
                    )

                    if (uiState.totalSaved > 0) {
                        InsightItem(
                            text = "Nice job! You put aside $currency${String.format(Locale.US, "%,.2f", uiState.totalSaved)} in your savings goals."
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
                .background(EmeraldBrand)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextSecondary,
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
                        .background(Color.White)
                        .border(1.dp, SoftMintBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Highest: $currencySymbol${String.format(Locale.US, "%,.0f", highestDay.amount)}",
                        color = ForestGreenDark,
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
                                if (isPeak) EmeraldBrand else EmeraldBright.copy(alpha = 0.28f)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = day.dayLabel,
                        fontSize = 11.sp,
                        fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal,
                        color = if (isPeak) ForestGreenDark else TextSecondary
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
                color = TextPrimary
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = entry.key,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "$pct%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
