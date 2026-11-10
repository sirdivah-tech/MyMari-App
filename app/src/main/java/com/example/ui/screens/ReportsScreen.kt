package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.ui.FinanceUiState
import com.example.ui.Screen
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenMedium
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.NeonLime
import java.util.Locale

@Composable
fun ReportsScreen(
    uiState: FinanceUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("EXPENSE") } // "INCOME" or "EXPENSE"
    val currency = uiState.userProfile.currencySymbol

    val totalBalanceStr = String.format(Locale.US, "%,.2f", uiState.totalAvailableBalance)
    val totalExpenseStr = String.format(Locale.US, "%,.2f", uiState.monthlyExpense)
    val totalIncomeStr = String.format(Locale.US, "%,.2f", uiState.monthlyIncome)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: Back button, "Analytics", More options (matching reference)
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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Analytics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Segmented Toggle Pill: [ Income | Expense ] (matching reference)
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
                        .background(ForestGreenDark)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val incomeSelected = selectedTab == "INCOME"
                    val expenseSelected = selectedTab == "EXPENSE"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (incomeSelected) NeonLime else Color.Transparent)
                            .clickable { selectedTab = "INCOME" }
                            .padding(horizontal = 28.dp, vertical = 8.dp)
                            .testTag("tab_income"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (incomeSelected) ForestGreenDark else Color(0xFFD1D5DB)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (expenseSelected) NeonLime else Color.Transparent)
                            .clickable { selectedTab = "EXPENSE" }
                            .padding(horizontal = 28.dp, vertical = 8.dp)
                            .testTag("tab_expense"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (expenseSelected) ForestGreenDark else Color(0xFFD1D5DB)
                        )
                    }
                }
            }
        }

        // Total Balance Label & Big Amount (matching reference)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (selectedTab == "INCOME") "Total Monthly Income" else "Total Balance",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (selectedTab == "INCOME") "$currency$totalIncomeStr" else "$currency$totalBalanceStr",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Emerald Chart Card with Days of Week Bars & High-day Tooltip (matching reference)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(ForestGreenMedium, ForestGreenDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        // Header inside emerald card: "Total expenses $6282.20" with arrow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (selectedTab == "INCOME") "Total income" else "Total expenses",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA7F3D0)
                                )
                                Text(
                                    text = "$currency${if (selectedTab == "INCOME") totalIncomeStr else totalExpenseStr}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2AFFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallMade,
                                    contentDescription = "Arrow",
                                    tint = NeonLime,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Weekly Bar Chart
                        WeeklySpendingBarChart(
                            days = uiState.weeklySpending,
                            currencySymbol = currency
                        )
                    }
                }
            }
        }

        // "Upcoming Payments" / Planned Expenses (matching reference)
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
                        text = "Upcoming Payments",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "See All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val upcomingBills = listOf(
                    UpcomingPayment("Room Cottage Rent", "$80.00/m", Color(0xFFE11D48)),
                    UpcomingPayment("Campus WiFi & Data", "$12.00/m", Color(0xFF0284C7)),
                    UpcomingPayment("Electricity (ZESA)", "$15.00/m", Color(0xFFF59E0B)),
                    UpcomingPayment("Semester Medical", "$10.00/m", Color(0xFF10B981))
                )

                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(upcomingBills) { bill ->
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .height(120.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        .background(bill.color.copy(alpha = 0.15f)),
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
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = bill.amount,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Breakdown (Donut Chart + Legend)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Category Breakdown",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (uiState.categorySpendingMonth.isEmpty()) {
                        Text(
                            text = "No category expense recorded this month yet.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

        // Real Calculated Financial Insights (as required: actual recorded numbers)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = "Insights",
                            tint = ForestGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Financial Insights",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
                            text = "${lowestCategory.key} is currently your lowest spending category ($currency${String.format(Locale.US, "%,.2f", lowestCategory.value)})."
                        )
                    }

                    InsightItem(
                        text = "You have $currency${String.format(Locale.US, "%,.2f", uiState.monthlyBudgetRemaining)} remaining in your monthly budget."
                    )

                    if (uiState.totalSaved > 0) {
                        InsightItem(
                            text = "You have saved $currency${String.format(Locale.US, "%,.2f", uiState.totalSaved)} towards your future goals!"
                        )
                    }
                }
            }
        }

        // Floating nav bar spacer
        item {
            Spacer(modifier = Modifier.height(90.dp))
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
                .background(ForestGreenDark)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    days: List<com.example.ui.DaySpending>,
    currencySymbol: String
) {
    if (days.isEmpty()) return

    val maxAmount = (days.maxOfOrNull { it.amount } ?: 1.0).coerceAtLeast(10.0)
    val highestDay = days.maxByOrNull { it.amount }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Tooltip above highest day
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
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%,.0f", highestDay.amount)}",
                        color = ForestGreenDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Custom Canvas Bar Chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { day ->
                val isPeak = day == highestDay && day.amount > 0
                val ratio = (day.amount / maxAmount).toFloat().coerceIn(0.12f, 1f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Bar
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height((100 * ratio).dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isPeak) NeonLime else Color(0x38FFFFFF)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day label
                    Text(
                        text = day.dayLabel,
                        fontSize = 11.sp,
                        fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal,
                        color = if (isPeak) NeonLime else Color(0xFFD1FAE5)
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
        // Canvas Donut
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
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Legend
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
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "$pct%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
