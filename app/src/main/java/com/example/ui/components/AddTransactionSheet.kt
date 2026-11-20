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
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintContainer
import com.example.ui.theme.SoftMintSurface
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

    // Auto-fill form when Gemini finishes transcribing & parsing voice audio
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
        containerColor = Color.White,
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
                    text = if (isIncome) "Money You Got" else "Money You Spent",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Voice AI Mic Bar (Gemini 3.5 Transcribe)
            VoiceTranscribeBar(
                isTranscribing = isTranscribing,
                transcriptText = voiceTranscript,
                errorMessage = voiceError,
                promptHint = if (isIncome) {
                    "Tap mic & say e.g. \"Got $50 from Mom on Mukuru\""
                } else {
                    "Tap mic & say e.g. \"Spent $3 on lunch via EcoCash\""
                },
                onAudioCaptured = onVoiceAudioCaptured
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Field
            Text(
                text = "How much?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        color = EmeraldBrand,
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
                text = if (isIncome) "Where did the money come from?" else "What did you spend it on?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SoftMintContainer else SoftMintSurface)
                            .border(
                                1.dp,
                                if (isSelected) EmeraldBrand else SoftMintBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategoryOrSource = opt }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = opt,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ForestGreenDark else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "How was it paid?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            .background(if (isSelected) EmeraldBrand else SoftMintSurface)
                            .border(
                                1.dp,
                                if (isSelected) EmeraldBrand else SoftMintBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPaymentMethod = method }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = method,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Short description",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                placeholder = { Text(if (isIncome) "e.g. Allowance from Mom" else "e.g. Lunch at canteen") },
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
                        text = "Date",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        text = "Time",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                text = "Extra note (Optional)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                placeholder = { Text("e.g. Shared groceries with roommate") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorMessage = "Please enter an amount bigger than 0"
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
                    text = if (isIncome) "Save Money In" else "Save What I Spent",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
