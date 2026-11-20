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
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
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
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.ThemeMode
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyMariTheme

@Composable
fun OnboardingScreen(
    initialName: String = "Student",
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    appLanguage: AppLanguage = AppLanguage.ENGLISH,
    onAppLanguageChange: (AppLanguage) -> Unit = {},
    onComplete: (name: String, university: String, currency: String, symbol: String, startingBal: Double, budget: Double, loadDemo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    var step by remember { mutableStateOf(1) }

    var studentName by remember(initialName) { mutableStateOf(initialName.ifBlank { strings.studentDefaultName }) }
    var university by remember { mutableStateOf("University") }
    var selectedCurrency by remember { mutableStateOf(FinanceConstants.CURRENCIES.first()) }
    var startingBalanceText by remember { mutableStateOf("250.00") }
    var monthlyBudgetText by remember { mutableStateOf("300.00") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.canvasBg, colors.softSurface)
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = FinanceConstants.APP_NAME,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentText
                        )

                        val themeIcon = when (themeMode) {
                            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                        }
                        val themeLabel = when (themeMode) {
                            ThemeMode.SYSTEM -> strings.themeSystem
                            ThemeMode.LIGHT -> strings.themeLight
                            ThemeMode.DARK -> strings.themeDark
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.softContainer)
                                .border(1.dp, colors.softBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    val next = when (themeMode) {
                                        ThemeMode.SYSTEM -> ThemeMode.LIGHT
                                        ThemeMode.LIGHT -> ThemeMode.DARK
                                        ThemeMode.DARK -> ThemeMode.SYSTEM
                                    }
                                    onThemeModeChange(next)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = themeIcon,
                                contentDescription = strings.appThemeTitle,
                                tint = colors.accentText,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = themeLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val selected = appLanguage == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) EmeraldBrand else colors.cardBg)
                                    .border(
                                        1.dp,
                                        if (selected) EmeraldBrand else colors.softBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onAppLanguageChange(lang) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang.nativeLabel,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color.White else colors.textPrimary
                                )
                            }
                        }
                    }
                }

                // Tilted showcase cards
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = colors.accentPrimary.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (-90).dp, y = (-70).dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = colors.accentPrimary.copy(alpha = 0.45f),
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 110.dp, y = 50.dp)
                    )

                    Box(
                        modifier = Modifier
                            .offset(x = (-24).dp, y = (-22).dp)
                            .rotate(-14f)
                            .width(168.dp)
                            .height(198.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(colors.limeCardBg)
                            .border(1.dp, colors.softBorder, RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = strings.onboardingWalletTag,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentText
                            )
                            Column {
                                Text(
                                    text = strings.onboardingMoneyLeftLabel,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "$175.00",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = strings.onboardingSavedExample,
                                    fontSize = 11.sp,
                                    color = colors.accentPrimary,
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
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(colors.softContainer)
                            .border(1.5.dp, colors.accentPrimary.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
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
                                    text = FinanceConstants.APP_NAME,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentText
                                )
                            }
                            Column {
                                Text(
                                    text = strings.onboardingMoneyInLabel,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "$300.00",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Mukuru • EcoCash",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.accentPrimary
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = strings.onboardingHeroTitle,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = strings.onboardingHeroSubtitle,
                        fontSize = 14.sp,
                        color = colors.textSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.onboardingTryDemo,
                            color = colors.accentPrimary,
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
                                contentDescription = strings.onboardingStartBtn,
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
                        text = strings.onboardingQuickSetup,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Text(
                        text = strings.onboardingSetupSub,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(strings.onboardingYourName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        placeholder = { Text(strings.onboardingNamePlaceholder) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(strings.onboardingSchool, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = university,
                        onValueChange = { university = it },
                        placeholder = { Text(strings.onboardingSchoolPlaceholder) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(strings.onboardingCurrency, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
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
                                    .background(if (isSel) EmeraldBrand else colors.cardBg)
                                    .border(1.dp, if (isSel) EmeraldBrand else colors.softBorder, RoundedCornerShape(12.dp))
                                    .clickable { selectedCurrency = cur }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cur.code,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else colors.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = String.format(strings.onboardingMoneyNow, selectedCurrency.symbol.trim()),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
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
                        text = String.format(strings.onboardingMonthlyLimit, selectedCurrency.symbol.trim()),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
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
                                studentName.ifBlank { strings.studentDefaultName },
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
                            text = strings.onboardingStartBtn,
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accentText)
                    ) {
                        Text(
                            text = strings.onboardingLoadDemoBtn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
