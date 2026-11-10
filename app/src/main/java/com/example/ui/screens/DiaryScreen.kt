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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.FinanceConstants
import com.example.ui.FinanceUiState
import com.example.ui.components.TransactionRow
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.NeonLime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DiaryScreen(
    uiState: FinanceUiState,
    onDateSelected: (String) -> Unit,
    onSaveNote: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onPaymentMethodFilterChange: (String?) -> Unit,
    onTypeFilterChange: (String) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.userProfile.currencySymbol
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sdfDayName = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val sdfShort = SimpleDateFormat("EEE\ndd", Locale.getDefault())

    var noteText by remember(uiState.currentDailyNote) { mutableStateOf(uiState.currentDailyNote) }
    var isEditingNote by remember { mutableStateOf(false) }

    // Selected date parse
    val cal = Calendar.getInstance()
    try {
        val parsed = sdfDate.parse(uiState.selectedDate)
        if (parsed != null) cal.time = parsed
    } catch (_: Exception) {}

    val fullDateHeader = sdfDayName.format(cal.time)

    // Daily totals for the selected date
    val dayTransactions = uiState.selectedDateTransactions
    val dayIncome = dayTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val dayExpense = dayTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val netChange = dayIncome - dayExpense

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "My Money Diary",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Your daily personal financial journal",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Horizontal Date Selector (Last 14 days)
        item {
            val dateList = remember {
                val list = mutableListOf<Date>()
                val dCal = Calendar.getInstance()
                for (i in 0 downTo -13) {
                    val c = Calendar.getInstance()
                    c.add(Calendar.DAY_OF_YEAR, i)
                    list.add(c.time)
                }
                list.reversed()
            }

            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dateList) { date ->
                    val dStr = sdfDate.format(date)
                    val isSelected = dStr == uiState.selectedDate

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) ForestGreenDark else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onDateSelected(dStr) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sdfShort.format(date),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) NeonLime else MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Selected Day Summary Card
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
                        text = fullDateHeader,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Daily Income",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "+$currency${String.format(Locale.US, "%,.2f", dayIncome)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }

                        Column {
                            Text(
                                text = "Daily Total Spent",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "-$currency${String.format(Locale.US, "%,.2f", dayExpense)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dayExpense > 0) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column {
                            Text(
                                text = "Net Daily Change",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${if (netChange >= 0) "+" else ""}$currency${String.format(Locale.US, "%,.2f", netChange)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (netChange >= 0) IncomeGreen else Color.Red
                            )
                        }
                    }
                }
            }
        }

        // Personal Diary Note for this day
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Diary Note",
                                tint = ForestGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Today's Money Diary Note",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = if (isEditingNote) "Done" else "Edit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                if (isEditingNote) {
                                    onSaveNote(noteText)
                                }
                                isEditingNote = !isEditingNote
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditingNote) {
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("e.g. Bought extra groceries because I ran out of cooking oil.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("diary_note_input"),
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onSaveNote(noteText)
                                isEditingNote = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreenDark,
                                contentColor = NeonLime
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Diary Note", fontSize = 12.sp)
                        }
                    } else {
                        Text(
                            text = if (noteText.isNotBlank()) noteText else "No notes added for this date. Tap 'Edit' to record your thoughts or context behind today's spending.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = if (noteText.isBlank()) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Search & Filter Box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search by description, category, payment...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input"),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter chips: All, Income, Expense
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("ALL" to "All", "INCOME" to "Money In", "EXPENSE" to "Money Out").forEach { (type, label) ->
                        val isSelected = uiState.filterType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) ForestGreenDark else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { onTypeFilterChange(type) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonLime else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Transactions List for this Date / Filtered
        val displayList = if (uiState.searchQuery.isNotBlank() || uiState.filterType != "ALL") {
            uiState.filteredTransactions
        } else {
            dayTransactions
        }

        item {
            Text(
                text = if (uiState.searchQuery.isNotBlank()) "Search Results (${displayList.size})" else "Transactions for $fullDateHeader (${displayList.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 22.dp)
            )
        }

        if (displayList.isEmpty()) {
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No transactions found for this day",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Use the buttons on Home to record money received or spent on this date.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayList) { tx ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TransactionRow(
                        transaction = tx,
                        currencySymbol = currency,
                        onClick = { onTransactionClick(tx) },
                        onDeleteClick = { onDeleteTransaction(tx.id) }
                    )
                }
            }
        }

        // Floating nav bar spacer
        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
