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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenMedium
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GlassWhiteBorder
import com.example.ui.theme.NeonLime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletBalanceCard(
    currencySymbol: String,
    balance: Double,
    budgetConsumedPct: Float,
    onSendClick: () -> Unit, // Add Expense
    onReceiveClick: () -> Unit, // Add Income
    onTopUpClick: () -> Unit, // Budget / Savings
    onMoreClick: () -> Unit, // Diary
    modifier: Modifier = Modifier
) {
    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val formattedBalance = String.format(Locale.US, "%,.2f", balance)
    val pctBadge = "${(budgetConsumedPct * 100).toInt()}%"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = ForestGreenDark
            ),
        shape = RoundedCornerShape(26.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ForestGreenMedium,
                            ForestGreenDark
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                // Top Row: Wallet Balance + Pill badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wallet Balance",
                        color = Color(0xFFD1FAE5),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Pill badge (e.g. 4% in neon lime)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonLime)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pctBadge,
                            color = ForestGreenDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Big balance
                Text(
                    text = "$currencySymbol$formattedBalance",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("wallet_balance_text")
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Updated: $formattedDate",
                    color = Color(0xFFA7F3D0).copy(alpha = 0.8f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom 4 Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WalletActionButton(
                        title = "Send",
                        icon = Icons.Default.CallMade,
                        onClick = onSendClick,
                        tag = "btn_send_expense"
                    )
                    WalletActionButton(
                        title = "Receive",
                        icon = Icons.Default.CallReceived,
                        onClick = onReceiveClick,
                        tag = "btn_receive_income"
                    )
                    WalletActionButton(
                        title = "Top-Up",
                        icon = Icons.Default.Savings,
                        onClick = onTopUpClick,
                        tag = "btn_topup_savings"
                    )
                    WalletActionButton(
                        title = "More",
                        icon = Icons.Default.GridView,
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
                .background(GlassWhite)
                .border(1.dp, GlassWhiteBorder, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = Color(0xFFE5E7EB),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
