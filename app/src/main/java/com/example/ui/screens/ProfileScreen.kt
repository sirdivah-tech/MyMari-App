package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.ThemeMode
import com.example.data.remote.UserProfileDoc
import com.example.ui.FinanceUiState
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyMariTheme
import java.util.Locale

@Composable
fun ProfileScreen(
    uiState: FinanceUiState,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onAppLanguageChange: (AppLanguage) -> Unit,
    onSaveProfile: (UserProfileDoc) -> Unit,
    onTestReminder: (Boolean) -> Unit,
    onLoadDemoData: () -> Unit,
    onClearAllData: () -> Unit,
    onExportCsv: () -> String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val profile = uiState.userProfile

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvasBg),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = strings.profileTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = strings.profileSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }
        }

        // Student Profile Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(colors.softContainer)
                            .border(1.dp, colors.softBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.fullName.take(1).uppercase(),
                            color = colors.accentText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.fullName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = profile.university,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                        if (profile.email.isNotBlank()) {
                            Text(
                                text = profile.email,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldBrand)
                            .clickable { showEditProfileDialog = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = strings.edit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Theme & Language Card (System / Light / Dark + English / ChiShona / IsiNdebele)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = strings.appearanceAndLanguage,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.appearanceAndLanguage,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = strings.appThemeTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = strings.appThemeSubtitle,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = strings.themeSystem,
                            icon = Icons.Default.BrightnessAuto,
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                            tag = "theme_option_system",
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = strings.themeLight,
                            icon = Icons.Default.LightMode,
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                            tag = "theme_option_light",
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = strings.themeDark,
                            icon = Icons.Default.DarkMode,
                            selected = themeMode == ThemeMode.DARK,
                            onClick = { onThemeModeChange(ThemeMode.DARK) },
                            tag = "theme_option_dark",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = strings.appLanguageTitle,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.appLanguageTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = strings.appLanguageSubtitle,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val languages = listOf(
                            Triple(AppLanguage.ENGLISH, strings.langEnglish, "lang_option_en"),
                            Triple(AppLanguage.SHONA, strings.langShona, "lang_option_sn"),
                            Triple(AppLanguage.NDEBELE, strings.langNdebele, "lang_option_nd")
                        )
                        languages.forEach { (lang, label, tag) ->
                            val selected = appLanguage == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selected) EmeraldBrand else colors.softSurface)
                                    .border(
                                        1.dp,
                                        if (selected) EmeraldBrand else colors.softBorder,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onAppLanguageChange(lang) }
                                    .padding(vertical = 10.dp)
                                    .testTag(tag),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color.White else colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Currency & Monthly Limit Preferences
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.moneySettingsTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCurrencyDialog = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.currencyLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "${profile.preferredCurrency} (${profile.currencySymbol.trim()})",
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.softSurface)
                                .border(1.dp, colors.softBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = strings.change,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEditProfileDialog = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.monthlyLimitLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "${profile.currencySymbol}${String.format(Locale.US, "%,.2f", profile.monthlyBudgetTarget)}",
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // Daily Reminders
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = strings.dailyRemindersTitle,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.dailyRemindersTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = String.format(strings.eveningCheckIn, profile.eveningReminderTime),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = strings.eveningCheckInDesc,
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                lineHeight = 15.sp
                            )
                        }

                        Switch(
                            checked = profile.eveningReminderEnabled,
                            onCheckedChange = { checked ->
                                onSaveProfile(profile.copy(eveningReminderEnabled = checked))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldBrand
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = String.format(strings.morningCheckIn, profile.morningReminderTime),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = strings.morningCheckInDesc,
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                lineHeight = 15.sp
                            )
                        }

                        Switch(
                            checked = profile.morningReminderEnabled,
                            onCheckedChange = { checked ->
                                onSaveProfile(profile.copy(morningReminderEnabled = checked))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldBrand
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onTestReminder(false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.softSurface,
                                contentColor = colors.accentText
                            )
                        ) {
                            Text(strings.testEvening, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onTestReminder(true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.softSurface,
                                contentColor = colors.accentText
                            )
                        ) {
                            Text(strings.testMorning, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Data & Cloud Backup
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.yourDataTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val csv = onExportCsv()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/csv"
                                    putExtra(Intent.EXTRA_SUBJECT, "MyMari Money Records")
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, strings.shareCsvChooser))
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = strings.shareCsvTitle,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.shareCsvTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = strings.shareCsvSub,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLoadDemoData()
                                Toast.makeText(context, strings.loadDemoToast, Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = strings.loadDemoTitle,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.loadDemoTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = strings.loadDemoSub,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearConfirmDialog = true }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = strings.clearAllTitle,
                            tint = colors.expenseRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.clearAllTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.expenseRed
                            )
                            Text(
                                text = strings.clearAllSub,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // Sign Out Button
        item {
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(50.dp)
                    .testTag("btn_sign_out"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.expenseRed)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = strings.signOutGoogle,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.signOutGoogle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // About MyMari
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, colors.softBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.softSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.aboutMyMari,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = String.format(strings.createdByPrefix, FinanceConstants.DEVELOPER_NAME),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentText
                    )

                    Text(
                        text = strings.tagline,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"${strings.brandMessage}\"",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = colors.textSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(profile.fullName) }
        var uniInput by remember { mutableStateOf(profile.university) }
        var budgetInput by remember { mutableStateOf(profile.monthlyBudgetTarget.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = colors.cardBg,
            title = { Text(strings.editProfileTitle, fontWeight = FontWeight.Bold, color = colors.textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(strings.yourNameLabel) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = uniInput,
                        onValueChange = { uniInput = it },
                        label = { Text(strings.schoolUniLabel) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text(strings.monthlyLimitLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bAmount = budgetInput.toDoubleOrNull()?.takeIf { it > 0.0 } ?: profile.monthlyBudgetTarget
                        onSaveProfile(
                            profile.copy(
                                fullName = nameInput.ifBlank { profile.fullName },
                                university = uniInput.ifBlank { profile.university },
                                monthlyBudgetTarget = bAmount
                            )
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldBrand,
                        contentColor = Color.White
                    )
                ) {
                    Text(strings.saveChangesBtn)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            containerColor = colors.cardBg,
            title = { Text(strings.chooseCurrencyTitle, fontWeight = FontWeight.Bold, color = colors.textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FinanceConstants.CURRENCIES.forEach { opt ->
                        val isSelected = profile.preferredCurrency == opt.code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) EmeraldBrand else colors.softSurface)
                                .border(1.dp, if (isSelected) EmeraldBrand else colors.softBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    onSaveProfile(
                                        profile.copy(
                                            preferredCurrency = opt.code,
                                            currencySymbol = opt.symbol
                                        )
                                    )
                                    showCurrencyDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${opt.name} (${opt.code})",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else colors.textPrimary
                            )
                            Text(
                                text = opt.symbol.trim(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else colors.accentText
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            containerColor = colors.cardBg,
            title = { Text(strings.clearEverythingTitle, fontWeight = FontWeight.Bold, color = colors.textPrimary) },
            text = {
                Text(strings.clearEverythingBody, color = colors.textSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirmDialog = false
                        Toast.makeText(context, strings.clearEverythingToast, Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.expenseRed)
                ) {
                    Text(strings.clearEverythingConfirm, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun ThemeOptionChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    val colors = MyMariTheme.colors
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) EmeraldBrand else colors.softSurface)
            .border(
                1.dp,
                if (selected) EmeraldBrand else colors.softBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .testTag(tag),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color.White else colors.textPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else colors.textPrimary
        )
    }
}
