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
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyMariTheme
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
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
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
            .background(colors.canvasBg),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = strings.diaryTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = strings.diarySubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }
        }

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
                            .background(if (isSelected) EmeraldBrand else colors.softSurface)
                            .border(
                                1.dp,
                                if (isSelected) EmeraldBrand else colors.softBorder,
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
                            color = if (isSelected) Color.White else colors.textPrimary,
                            textAlign = TextAlign.Center
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
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = fullDateHeader,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = strings.moneyIn,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "+$currency${String.format(Locale.US, "%,.2f", dayIncome)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.incomeGreen
                            )
                        }

                        Column {
                            Text(
                                text = strings.moneyOut,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "-$currency${String.format(Locale.US, "%,.2f", dayExpense)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dayExpense > 0) colors.expenseRed else colors.textPrimary
                            )
                        }

                        Column {
                            Text(
                                text = strings.todaysDifference,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "${if (dayDifference >= 0) "+" else ""}$currency${String.format(Locale.US, "%,.2f", dayDifference)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dayDifference >= 0) colors.incomeGreen else colors.expenseRed
                            )
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
                    .border(1.dp, colors.softBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.softSurface)
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
                                contentDescription = strings.todaysStoryNote,
                                tint = colors.accentText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.todaysStoryNote,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        Text(
                            text = if (isEditingNote) strings.done else strings.typeNote,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentPrimary,
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
                        promptHint = strings.voiceTapMicDiary,
                        onAudioCaptured = onVoiceDiaryAudio
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isEditingNote) {
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text(strings.diaryNotePlaceholder) },
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
                            Text(strings.saveNote, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = if (noteText.isNotBlank()) noteText else strings.diaryNoteEmpty,
                            fontSize = 13.sp,
                            color = colors.textSecondary,
                            fontStyle = if (noteText.isBlank()) FontStyle.Italic else FontStyle.Normal,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text(strings.searchPlaceholder) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colors.textSecondary
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = strings.close,
                                    tint = colors.textSecondary
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
                    listOf("ALL" to strings.filterAll, "INCOME" to strings.moneyIn, "EXPENSE" to strings.moneyOut).forEach { (type, label) ->
                        val isSelected = uiState.filterType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) EmeraldBrand else colors.softSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldBrand else colors.softBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onTypeFilterChange(type) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else colors.textPrimary
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
                    String.format(strings.foundCount, displayList.size)
                } else {
                    String.format(strings.recordsForDateCount, fullDateHeader, displayList.size)
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = 22.dp)
            )
        }

        if (displayList.isEmpty()) {
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.noRecordsForDay,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.noRecordsForDayTip,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
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
