package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.VoiceParsedTransaction
import com.example.data.model.FinanceConstants
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyMariTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionSheet(
    isIncome: Boolean,
    currencySymbol: String,
    isTranscribing: Boolean,
    voiceTranscript: String?,
    voiceParsed: VoiceParsedTransaction?,
    voiceError: String?,
    onVoiceAudioCaptured: (ByteArray) -> Unit,
    onDismiss: () -> Unit,
    onSubmitIncome: (amount: Double, source: String, method: String, desc: String, notes: String, date: String, time: String) -> Unit,
    onSubmitExpense: (amount: Double, category: String, method: String, desc: String, notes: String, date: String, time: String) -> Unit
) {
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())

    var amountText by remember { mutableStateOf("") }
    var selectedCategoryOrSource by remember {
        mutableStateOf(if (isIncome) FinanceConstants.INCOME_SOURCES.first() else FinanceConstants.EXPENSE_CATEGORIES.first())
    }
    var selectedPaymentMethod by remember { mutableStateOf(FinanceConstants.PAYMENT_METHODS.first()) }
    var descriptionText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(sdfDate.format(Date())) }
    var timeText by remember { mutableStateOf(sdfTime.format(Date())) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(voiceParsed) {
        if (voiceParsed != null) {
            voiceParsed.amount?.let { amt ->
                if (amt > 0.0) {
                    amountText = if (amt % 1.0 == 0.0) amt.toInt().toString() else String.format(Locale.US, "%.2f", amt)
                }
            }
            val validOptions = if (isIncome) FinanceConstants.INCOME_SOURCES else FinanceConstants.EXPENSE_CATEGORIES
            if (validOptions.contains(voiceParsed.categoryOrSource)) {
                selectedCategoryOrSource = voiceParsed.categoryOrSource
            }
            if (FinanceConstants.PAYMENT_METHODS.contains(voiceParsed.paymentMethod)) {
                selectedPaymentMethod = voiceParsed.paymentMethod
            }
            if (voiceParsed.description.isNotBlank()) {
                descriptionText = voiceParsed.description
            }
            errorMessage = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.cardBg,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isIncome) strings.moneyYouGot else strings.moneyYouSpent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            VoiceTranscribeBar(
                isTranscribing = isTranscribing,
                transcriptText = voiceTranscript,
                errorMessage = voiceError,
                promptHint = if (isIncome) strings.voiceTapMicIncome else strings.voiceTapMicExpense,
                onAudioCaptured = onVoiceAudioCaptured
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = strings.howMuch,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Text(
                        text = currencySymbol,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentPrimary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_amount"),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isIncome) strings.whereMoneyCameFrom else strings.whatDidYouSpendOn,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val options = if (isIncome) FinanceConstants.INCOME_SOURCES else FinanceConstants.EXPENSE_CATEGORIES
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { opt ->
                    val isSelected = selectedCategoryOrSource == opt
                    val displayOpt = if (isIncome) strings.sourceLabel(opt) else strings.categoryLabel(opt)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.softContainer else colors.softSurface)
                            .border(
                                1.dp,
                                if (isSelected) colors.accentPrimary else colors.softBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategoryOrSource = opt }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = displayOpt,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) colors.accentText else colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = strings.howWasItPaid,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FinanceConstants.PAYMENT_METHODS.forEach { method ->
                    val isSelected = selectedPaymentMethod == method
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) EmeraldBrand else colors.softSurface)
                            .border(
                                1.dp,
                                if (isSelected) EmeraldBrand else colors.softBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPaymentMethod = method }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = strings.paymentLabel(method),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = strings.shortDescription,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                placeholder = { Text(if (isIncome) strings.placeholderIncomeDesc else strings.placeholderExpenseDesc) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_desc"),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.dateLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.timeLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { timeText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = strings.extraNoteOptional,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                placeholder = { Text(strings.placeholderExtraNote) },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = colors.expenseRed,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorMessage = strings.errorAmountPositive
                        return@Button
                    }
                    if (isIncome) {
                        onSubmitIncome(
                            amount,
                            selectedCategoryOrSource,
                            selectedPaymentMethod,
                            descriptionText,
                            notesText,
                            dateText,
                            timeText
                        )
                    } else {
                        onSubmitExpense(
                            amount,
                            selectedCategoryOrSource,
                            selectedPaymentMethod,
                            descriptionText,
                            notesText,
                            dateText,
                            timeText
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_transaction_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldBrand,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isIncome) strings.saveMoneyIn else strings.saveWhatISpent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
