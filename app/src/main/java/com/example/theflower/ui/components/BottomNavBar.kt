package com.example.theflower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.SandDark

enum class NavTab {
    HOME, CATEGORY, CART, PROFILE
}

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    cartItemCount: Int = 0,
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
            icon = "\uD83C\uDFE0",
            label = "Trang chủ",
            isSelected = currentTab == NavTab.HOME,
            onClick = { onTabClick(NavTab.HOME) }
        )
        NavTabButton(
            icon = "\uD83C\uDF38",
            label = "Danh mục",
            isSelected = currentTab == NavTab.CATEGORY,
            onClick = { onTabClick(NavTab.CATEGORY) }
        )
        NavTabButton(
            icon = "\uD83D\uDED2",
            label = "Giỏ hàng",
            isSelected = currentTab == NavTab.CART,
            badgeCount = cartItemCount,
            onClick = { onTabClick(NavTab.CART) }
        )
        NavTabButton(
            icon = "\uD83D\uDC64",
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
    badgeCount: Int = 0,
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
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge {
                        Text(text = badgeCount.coerceAtMost(99).toString())
                    }
                }
            }
        ) {
            Text(icon, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
        }
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
