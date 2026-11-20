package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.data.remote.TransactionDoc
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.MyMariTheme
import java.util.Locale

@Composable
fun TransactionRow(
    transaction: TransactionDoc,
    currencySymbol: String,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    val isIncome = transaction.type == "INCOME"
    val formattedAmount = String.format(Locale.US, "%,.2f", transaction.amount)
    val catColor = FinanceConstants.getCategoryColor(transaction.category)
    val localizedPayment = strings.paymentLabel(transaction.paymentMethod)
    val localizedCat = strings.categoryLabel(transaction.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.softBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("tx_row_${transaction.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(catColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = FinanceConstants.getCategoryIcon(transaction.category),
                        contentDescription = localizedCat,
                        tint = catColor,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.description.ifBlank { localizedCat },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "$localizedPayment • ${transaction.dateString} ${transaction.timeString}".trim(),
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isIncome) "+$currencySymbol$formattedAmount" else "-$currencySymbol$formattedAmount",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) colors.incomeGreen else colors.textPrimary
                )

                if (onDeleteClick != null) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = strings.delete,
                            tint = colors.expenseRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
