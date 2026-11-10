package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenMedium
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeSoft

@Composable
fun OnboardingScreen(
    onComplete: (name: String, university: String, currency: String, symbol: String, startingBal: Double, budget: Double, loadDemo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) } // 1: Hero visual, 2: Setup form

    var studentName by remember { mutableStateOf("Ridoy") }
    var university by remember { mutableStateOf("University") }
    var selectedCurrency by remember { mutableStateOf(FinanceConstants.CURRENCIES.first()) }
    var startingBalanceText by remember { mutableStateOf("250.00") }
    var monthlyBudgetText by remember { mutableStateOf("300.00") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ForestGreenDark,
                        Color(0xFF071B10)
                    )
                )
            )
    ) {
        if (step == 1) {
            // STEP 1: Hero Showcase (Matching reference left screen)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Brand Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MyMari",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonLime
                    )
                    Text(
                        text = "Student Edition",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA7F3D0),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Middle: 3D-styled tilted cards & sparkles (matching reference)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Sparkles in background
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = NeonLime.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (-90).dp, y = (-70).dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = NeonLime.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 110.dp, y = 50.dp)
                    )

                    // Back Tilted Card (Cream/lime)
                    Box(
                        modifier = Modifier
                            .offset(x = (-25).dp, y = (-25).dp)
                            .rotate(-15f)
                            .width(170.dp)
                            .height(210.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFD9F99D))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "VISA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                            Column {
                                Text(
                                    text = "Balance",
                                    fontSize = 10.sp,
                                    color = Color(0xFF374151)
                                )
                                Text(
                                    text = "$1,238.00",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "•••• 8545",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4B5563)
                                )
                            }
                        }
                    }

                    // Front Tilted Card (Neon Lime)
                    Box(
                        modifier = Modifier
                            .offset(x = 25.dp, y = 20.dp)
                            .rotate(-8f)
                            .width(170.dp)
                            .height(210.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(NeonLime)
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
                                        .background(Color(0xFF84CC16))
                                )
                                Text(
                                    text = "03/28",
                                    fontSize = 11.sp,
                                    color = ForestGreenDark
                                )
                            }
                            Column {
                                Text(
                                    text = "Balance",
                                    fontSize = 10.sp,
                                    color = ForestGreenDark
                                )
                                Text(
                                    text = "$2,455.00",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Student Pass",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ForestGreenDark
                                )
                            }
                        }
                    }
                }

                // Bottom Content: Headline + Subtitle + Circular Arrow Button
                Column {
                    Text(
                        text = "Built for\nforward-looking\nstudents",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 38.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Keep your money in mind. Track Mukuru, EcoCash, groceries, canteen & accommodation with ease.",
                        fontSize = 14.sp,
                        color = Color(0xFFD1FAE5).copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick demo button
                        Text(
                            text = "Instant Demo",
                            color = NeonLime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    onComplete("Ridoy", "University of Zimbabwe", "USD", "$", 2455.00, 350.00, true)
                                }
                                .padding(vertical = 12.dp)
                                .testTag("btn_onboarding_demo")
                        )

                        // Big circular arrow button (matching reference)
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NeonLime)
                                .clickable { step = 2 }
                                .testTag("btn_onboarding_next"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Get Started",
                                tint = ForestGreenDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // STEP 2: Quick Student Preferences Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Set Up Your Wallet",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Personalise your MyMari financial companion in 30 seconds.",
                        fontSize = 13.sp,
                        color = Color(0xFFA7F3D0)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Student Name
                    Text("Your Name", fontSize = 13.sp, color = Color(0xFFD1FAE5))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        placeholder = { Text("e.g. Ridoy or Divine") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // University
                    Text("University / College", fontSize = 13.sp, color = Color(0xFFD1FAE5))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = university,
                        onValueChange = { university = it },
                        placeholder = { Text("e.g. University of Zimbabwe / CUT") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Currency Selector
                    Text("Preferred Currency", fontSize = 13.sp, color = Color(0xFFD1FAE5))
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
                                    .background(if (isSel) NeonLime else Color(0x22FFFFFF))
                                    .clickable { selectedCurrency = cur }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cur.code,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) ForestGreenDark else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Starting Balance
                    Text("Current Wallet Balance (${selectedCurrency.symbol.trim()})", fontSize = 13.sp, color = Color(0xFFD1FAE5))
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

                    // Monthly Budget Target
                    Text("Monthly Spending Budget (${selectedCurrency.symbol.trim()})", fontSize = 13.sp, color = Color(0xFFD1FAE5))
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
                            containerColor = NeonLime,
                            contentColor = ForestGreenDark
                        )
                    ) {
                        Text(
                            text = "Start Tracking My Money",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            onComplete("Ridoy", "University of Zimbabwe", "USD", "$", 2455.00, 350.00, true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x22FFFFFF),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Load Sample Student Data First",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
