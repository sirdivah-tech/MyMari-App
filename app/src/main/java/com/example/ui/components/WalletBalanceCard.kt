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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.NeonLime
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintContainer
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletBalanceCard(
    currencySymbol: String,
    balance: Double,
    budgetConsumedPct: Float,
    onSendClick: () -> Unit, // Spent
    onReceiveClick: () -> Unit, // Got Money
    onTopUpClick: () -> Unit, // Save
    onMoreClick: () -> Unit, // Diary
    modifier: Modifier = Modifier
) {
    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    val formattedBalance = String.format(Locale.US, "%,.2f", balance)
    val pctBadge = "${(budgetConsumedPct * 100).toInt()}% of limit used"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, SoftMintBorder, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        color = Color.Transparent,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SoftMintSurface,
                            SoftMintContainer
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Money Left to Spend",
                        color = ForestGreenDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, SoftMintBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pctBadge,
                            color = EmeraldBrand,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$currencySymbol$formattedBalance",
                    color = ForestGreenDark,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("wallet_balance_text")
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Updated today • $formattedDate",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WalletActionButton(
                        title = "I Spent",
                        icon = Icons.AutoMirrored.Filled.CallMade,
                        onClick = onSendClick,
                        tag = "btn_send_expense"
                    )
                    WalletActionButton(
                        title = "Got Paid",
                        icon = Icons.AutoMirrored.Filled.CallReceived,
                        onClick = onReceiveClick,
                        tag = "btn_receive_income"
                    )
                    WalletActionButton(
                        title = "Save",
                        icon = Icons.Default.Savings,
                        onClick = onTopUpClick,
                        tag = "btn_topup_savings"
                    )
                    WalletActionButton(
                        title = "Diary",
                        icon = Icons.Default.Book,
                        onClick = onMoreClick,
                        tag = "btn_more_diary"
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .border(1.dp, SoftMintBorder, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = EmeraldBrand,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = ForestGreenDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
