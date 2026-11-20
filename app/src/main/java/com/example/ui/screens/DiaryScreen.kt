package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.TransactionDoc
import com.example.ui.FinanceUiState
import com.example.ui.components.TransactionRow
import com.example.ui.components.VoiceTranscribeBar
import com.example.ui.theme.CanvasBgLight
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintContainer
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DiaryScreen(
    uiState: FinanceUiState,
    onDateSelected: (String) -> Unit,
    onSaveNote: (String) -> Unit,
    onVoiceDiaryAudio: (ByteArray) -> Unit,
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onPaymentMethodFilterChange: (String?) -> Unit,
    onTypeFilterChange: (String) -> Unit,
    onTransactionClick: (TransactionDoc) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.userProfile.currencySymbol
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sdfDayName = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val sdfShort = SimpleDateFormat("EEE\ndd", Locale.getDefault())

    var noteText by remember(uiState.selectedDate, uiState.currentDailyNote) {
        mutableStateOf(uiState.currentDailyNote)
    }
    var isEditingNote by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance()
    try {
        val parsed = sdfDate.parse(uiState.selectedDate)
        if (parsed != null) cal.time = parsed
    } catch (_: Exception) {}

    val fullDateHeader = sdfDayName.format(cal.time)

    val dayTransactions = uiState.selectedDateTransactions
    val dayIncome = dayTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val dayExpense = dayTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val dayDifference = dayIncome - dayExpense

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBgLight),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "My Money Diary",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "See what you got and spent each day",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Horizontal Date Selector (Last 14 days)
        item {
            val dateList = remember {
                val list = mutableListOf<Date>()
                for (i in 0 downTo -13) {
                    val c = Calendar.getInstance()
                    c.add(Calendar.DAY_OF_YEAR, i)
                    list.add(c.time)
                }
                list.reversed()
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dateList) { date ->
                    val dStr = sdfDate.format(date)
                    val isSelected = dStr == uiState.selectedDate

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) EmeraldBrand else SoftMintSurface)
                            .border(
                                1.dp,
                                if (isSelected) EmeraldBrand else SoftMintBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onDateSelected(dStr) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sdfShort.format(date),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary,
                            textAlign = TextAlign.Center
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
                    .padding(horizontal = 20.dp)
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = fullDateHeader,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Money In",
                                fontSize = 11.sp,
                                color = TextSecondary
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
                                text = "Money Out",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "-$currency${String.format(Locale.US, "%,.2f", dayExpense)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dayExpense > 0) Color(0xFFDC2626) else TextPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "Today's Difference",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${if (dayDifference >= 0) "+" else ""}$currency${String.format(Locale.US, "%,.2f", dayDifference)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dayDifference >= 0) IncomeGreen else Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }
        }

        // Personal Diary Note + Speak with Voice AI
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, SoftMintBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftMintSurface)
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
                                text = "Today's Story Note",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = if (isEditingNote) "Done" else "Type Note",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldBrand,
                            modifier = Modifier.clickable {
                                if (isEditingNote) {
                                    onSaveNote(noteText)
                                }
                                isEditingNote = !isEditingNote
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    VoiceTranscribeBar(
                        isTranscribing = uiState.isTranscribing,
                        transcriptText = uiState.voiceTranscript,
                        errorMessage = uiState.voiceError,
                        promptHint = "Tap mic to speak your diary note for today",
                        onAudioCaptured = onVoiceDiaryAudio
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isEditingNote) {
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("e.g. Shared groceries with my roommate today.") },
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
                                containerColor = EmeraldBrand,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = if (noteText.isNotBlank()) noteText else "No note written for this day yet. Speak above or tap 'Type Note' to remember why you spent today.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontStyle = if (noteText.isBlank()) FontStyle.Italic else FontStyle.Normal,
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
                    placeholder = { Text("Search food, Mukuru, EcoCash, rent...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("ALL" to "All", "INCOME" to "Money In", "EXPENSE" to "Money Out").forEach { (type, label) ->
                        val isSelected = uiState.filterType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) EmeraldBrand else SoftMintSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldBrand else SoftMintBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onTypeFilterChange(type) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        val displayList = if (uiState.searchQuery.isNotBlank() || uiState.filterType != "ALL") {
            uiState.filteredTransactions
        } else {
            dayTransactions
        }

        item {
            Text(
                text = if (uiState.searchQuery.isNotBlank()) {
                    "Found (${displayList.size})"
                } else {
                    "Records for $fullDateHeader (${displayList.size})"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 22.dp)
            )
        }

        if (displayList.isEmpty()) {
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No money records for this day",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Go back to Home and tap 'I Spent' or 'Got Paid' to add one.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayList, key = { it.id }) { tx ->
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

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}
