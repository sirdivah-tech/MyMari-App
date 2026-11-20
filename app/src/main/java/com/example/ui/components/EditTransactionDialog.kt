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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.data.remote.TransactionDoc
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyMariTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTransactionDialog(
    transaction: TransactionDoc,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (TransactionDoc) -> Unit,
    onDelete: (String) -> Unit
) {
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    var amountText by remember { mutableStateOf(transaction.amount.toString()) }
    var descriptionText by remember { mutableStateOf(transaction.description) }
    var notesText by remember { mutableStateOf(transaction.notes) }
    var selectedCategory by remember { mutableStateOf(transaction.category) }
    var selectedPaymentMethod by remember { mutableStateOf(transaction.paymentMethod) }

    val options = if (transaction.type == "INCOME") {
        FinanceConstants.INCOME_SOURCES
    } else {
        FinanceConstants.EXPENSE_CATEGORIES
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.cardBg,
        title = {
            Text(
                text = if (transaction.type == "INCOME") strings.editMoneyIn else strings.editWhatISpent,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("${strings.howMuch} ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text(strings.whatWasItFor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Text(
                    text = if (transaction.type == "INCOME") strings.whereMoneyCameFrom else strings.categoryLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    options.take(6).forEach { cat ->
                        val isSel = selectedCategory == cat
                        val label = if (transaction.type == "INCOME") strings.sourceLabel(cat) else strings.categoryLabel(cat)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) colors.softContainer else colors.softSurface)
                                .border(1.dp, if (isSel) colors.accentPrimary else colors.softBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) colors.accentText else colors.textPrimary
                            )
                        }
                    }
                }

                Text(
                    text = strings.howWasItPaid,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FinanceConstants.PAYMENT_METHODS.forEach { method ->
                        val isSel = selectedPaymentMethod == method
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) EmeraldBrand else colors.softSurface)
                                .border(1.dp, if (isSel) EmeraldBrand else colors.softBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedPaymentMethod = method }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = strings.paymentLabel(method),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else colors.textPrimary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text(strings.extraNoteLabel) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()?.takeIf { it > 0.0 } ?: transaction.amount
                    onSave(
                        transaction.copy(
                            amount = amount,
                            description = descriptionText,
                            notes = notesText,
                            category = selectedCategory,
                            source = if (transaction.type == "INCOME") selectedCategory else transaction.source,
                            paymentMethod = selectedPaymentMethod
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldBrand,
                    contentColor = Color.White
                )
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = { onDelete(transaction.id) },
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.expenseRed)
                ) {
                    Text(strings.delete)
                }
                TextButton(onClick = onDismiss) {
                    Text(strings.cancel)
                }
            }
        }
    )
}
