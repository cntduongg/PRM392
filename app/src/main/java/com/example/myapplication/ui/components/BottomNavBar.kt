package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.*

enum class NavTab {
    HOME, CATEGORY, CART, PROFILE
}

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    onTabClick: (NavTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(PaperWhite)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavTabButton(
            icon = "🏠",
            label = "Trang chủ",
            isSelected = currentTab == NavTab.HOME,
            onClick = { onTabClick(NavTab.HOME) }
        )
        NavTabButton(
            icon = "🌸",
            label = "Danh mục",
            isSelected = currentTab == NavTab.CATEGORY,
            onClick = { onTabClick(NavTab.CATEGORY) }
        )
        NavTabButton(
            icon = "🛒",
            label = "Giỏ hàng",
            isSelected = currentTab == NavTab.CART,
            onClick = { onTabClick(NavTab.CART) }
        )
        NavTabButton(
            icon = "👤",
            label = "Tôi",
            isSelected = currentTab == NavTab.PROFILE,
            onClick = { onTabClick(NavTab.PROFILE) }
        )
    }
}

@Composable
fun NavTabButton(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) MossGreen.copy(alpha = 0.1f) else PaperWhite
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(icon, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MossGreen)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MossGreen else SandDark,
            maxLines = 1
        )
    }
}
