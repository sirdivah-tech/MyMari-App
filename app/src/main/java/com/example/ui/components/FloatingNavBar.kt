package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.CapsuleDarkGreen
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.NeonLime

data class NavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun FloatingNavBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(Screen.HOME, "Home", Icons.Default.Home, "nav_home"),
        NavItem(Screen.DIARY, "Diary", Icons.Default.Book, "nav_diary"),
        NavItem(Screen.BUDGETS, "Budgets", Icons.Default.AccountBalanceWallet, "nav_budgets"),
        NavItem(Screen.REPORTS, "Analytics", Icons.Default.BarChart, "nav_reports"),
        NavItem(Screen.PROFILE, "Profile", Icons.Default.Person, "nav_profile")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = ForestGreenDark)
                .clip(RoundedCornerShape(32.dp)),
            color = CapsuleDarkGreen,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) NeonLime else Color.Transparent,
                        animationSpec = tween(250),
                        label = "nav_bg"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) ForestGreenDark else Color(0xFF9CA3AF),
                        animationSpec = tween(250),
                        label = "nav_icon"
                    )

                    Box(
                        modifier = Modifier
                            .testTag(item.tag)
                            .clip(RoundedCornerShape(24.dp))
                            .background(bgColor)
                            .clickable { onScreenSelected(item.screen) }
                            .padding(
                                horizontal = if (isSelected) 14.dp else 10.dp,
                                vertical = 8.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.title,
                                    color = ForestGreenDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
