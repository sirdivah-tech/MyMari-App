package com.example.ui.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.model.FinanceConstants
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.ThemeMode
import com.example.ui.i18n.MyMariI18n
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.MyMariTheme

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    appLanguage: AppLanguage = AppLanguage.ENGLISH,
    onAppLanguageChange: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val colors = MyMariTheme.colors
    val strings = MyMariI18n.strings

    var isLoading by remember { mutableStateOf(false) }
    var isAutoSigningIn by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                isAutoSigningIn = false
                onAuthSuccess()
            },
            onUnauthenticated = {
                isAutoSigningIn = false
            },
            scope = scope
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvasBg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Brand + Quick Language & Theme Switcher
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mymari_logo),
                        contentDescription = "MyMari Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = FinanceConstants.APP_NAME,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentText
                    )
                }

                // Cycle Theme pill (System -> Light -> Dark)
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
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = themeIcon,
                        contentDescription = strings.appThemeTitle,
                        tint = colors.accentText,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = themeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentText
                    )
                }
            }

            // Quick 3-language selector row
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
                            .background(if (selected) EmeraldBrand else colors.softSurface)
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

        // Center Showcase
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = strings.authHeadline,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 35.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = strings.authSubhead,
                fontSize = 14.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AuthFeatureTile(
                    icon = Icons.Default.Mic,
                    title = strings.authFeatureVoiceTitle,
                    subtitle = strings.authFeatureVoiceSub
                )
                AuthFeatureTile(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = strings.authFeatureTrackTitle,
                    subtitle = strings.authFeatureTrackSub
                )
                AuthFeatureTile(
                    icon = Icons.Default.Savings,
                    title = strings.authFeatureCloudTitle,
                    subtitle = strings.authFeatureCloudSub
                )
            }
        }

        // Bottom Google Sign-In Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.errorBg)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = colors.errorText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = {
                    errorMessage = null
                    isLoading = true
                    onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                            isLoading = false
                            onAuthSuccess()
                        },
                        onAuthError = { msg ->
                            isLoading = false
                            errorMessage = msg
                        },
                        scope = scope,
                        onAuthCancelled = {
                            isLoading = false
                        }
                    )
                },
                enabled = !isLoading && !isAutoSigningIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_google_sign_in"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldBrand,
                    contentColor = Color.White
                )
            ) {
                if (isLoading || isAutoSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAutoSigningIn) strings.authCheckingAccount else strings.authSigningIn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = strings.authSignInGoogle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = String.format(strings.authFooterPrefix, FinanceConstants.DEVELOPER_NAME),
                fontSize = 11.sp,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun AuthFeatureTile(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    val colors = MyMariTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.softSurface)
            .border(1.dp, colors.softBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(colors.softContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colors.accentText,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
    }
}
