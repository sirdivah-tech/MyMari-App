package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.ui.theme.CanvasBgLight
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.NeonLimeSoft
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintContainer
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    initialName: String = "Student",
    onComplete: (name: String, university: String, currency: String, symbol: String, startingBal: Double, budget: Double, loadDemo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }

    var studentName by remember(initialName) { mutableStateOf(initialName.ifBlank { "Student" }) }
    var university by remember { mutableStateOf("University") }
    var selectedCurrency by remember { mutableStateOf(FinanceConstants.CURRENCIES.first()) }
    var startingBalanceText by remember { mutableStateOf("250.00") }
    var monthlyBudgetText by remember { mutableStateOf("300.00") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CanvasBgLight, SoftMintSurface)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        if (step == 1) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MyMari",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark
                    )
                    Text(
                        text = "Student Helper",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreenDark,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftMintContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Light pastel tilted showcase cards
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = EmeraldBrand.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (-90).dp, y = (-70).dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = EmeraldBrand.copy(alpha = 0.45f),
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 110.dp, y = 50.dp)
                    )

                    Box(
                        modifier = Modifier
                            .offset(x = (-24).dp, y = (-22).dp)
                            .rotate(-14f)
                            .width(168.dp)
                            .height(206.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(NeonLimeSoft)
                            .border(1.dp, SoftMintBorder, RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "WALLET",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                            Column {
                                Text(
                                    text = "Money Left",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$175.00",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Saved $50.00",
                                    fontSize = 11.sp,
                                    color = EmeraldBrand,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = 24.dp, y = 20.dp)
                            .rotate(-6f)
                            .width(172.dp)
                            .height(208.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SoftMintContainer)
                            .border(1.5.dp, EmeraldBrand.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldBrand)
                                )
                                Text(
                                    text = "MyMari",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                            }
                            Column {
                                Text(
                                    text = "Money In",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$300.00",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Mukuru • EcoCash",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EmeraldBrand
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = "Simple money\ntracking made\nfor students",
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 37.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Keep your money in mind. Track Mukuru, EcoCash, food, transport & rent without any confusing words.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Try Sample Data",
                            color = EmeraldBrand,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    onComplete(studentName, "University", "USD", "$", 245.00, 350.00, true)
                                }
                                .padding(vertical = 12.dp)
                                .testTag("btn_onboarding_demo")
                        )

                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(EmeraldBrand)
                                .clickable { step = 2 }
                                .testTag("btn_onboarding_next"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Get Started",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Quick Setup",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Takes 30 seconds — you can change this anytime.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Your Name", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        placeholder = { Text("e.g. Tafadzwa or Divine") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("School / University", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = university,
                        onValueChange = { university = it },
                        placeholder = { Text("e.g. University of Zimbabwe") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Your Currency", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FinanceConstants.CURRENCIES.take(4).forEach { cur ->
                            val isSel = selectedCurrency.code == cur.code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) EmeraldBrand else Color.White)
                                    .border(1.dp, if (isSel) EmeraldBrand else SoftMintBorder, RoundedCornerShape(12.dp))
                                    .clickable { selectedCurrency = cur }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cur.code,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "How much money do you have right now? (${selectedCurrency.symbol.trim()})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = startingBalanceText,
                        onValueChange = { startingBalanceText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Monthly spending limit (${selectedCurrency.symbol.trim()})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = monthlyBudgetText,
                        onValueChange = { monthlyBudgetText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val startBal = startingBalanceText.toDoubleOrNull() ?: 0.0
                            val budget = monthlyBudgetText.toDoubleOrNull() ?: 300.0
                            onComplete(
                                studentName.ifBlank { "Student" },
                                university.ifBlank { "University" },
                                selectedCurrency.code,
                                selectedCurrency.symbol,
                                startBal,
                                budget,
                                false
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("btn_complete_setup"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldBrand,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Start Tracking My Money",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onComplete(studentName, "University", "USD", "$", 245.00, 350.00, true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreenDark)
                    ) {
                        Text(
                            text = "Load Sample Student Data First",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
