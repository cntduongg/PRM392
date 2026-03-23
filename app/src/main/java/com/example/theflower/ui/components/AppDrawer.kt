package com.example.theflower.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.CategoryDto
import com.example.theflower.ui.theme.*

enum class NavTab {
    HOME, CATEGORY, CART, PROFILE
}

@Composable
fun AppDrawerContent(
    userName: String,
    userEmail: String,
    userRole: String,
    currentTab: NavTab,
    cartItemCount: Int,
    categories: List<CategoryDto>,
    onTabClick: (NavTab) -> Unit,
    onCategoryClick: (CategoryDto) -> Unit,
    onViewOrders: () -> Unit,
    onChatClick: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onLogout: () -> Unit,
    closeDrawer: () -> Unit
) {
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(PaperWhite)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MossGreen)
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(PaperWhite.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (userName.isNotBlank()) userName.first().uppercaseChar().toString() else "👤",
                        fontSize = 22.sp,
                        color = PaperWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = if (userName.isNotBlank()) userName else "Người dùng",
                        style = MaterialTheme.typography.titleMedium,
                        color = PaperWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (userEmail.isNotBlank()) {
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = PaperWhite.copy(alpha = 0.8f)
                        )
                    }
                    if (userRole.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(PaperWhite.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = userRole.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = PaperWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Nav Items ────────────────────────────────────────────────────────────

        DrawerNavItem(
            icon = "🏠",
            label = "Trang chủ",
            isSelected = currentTab == NavTab.HOME,
            onClick = {
                onTabClick(NavTab.HOME)
                closeDrawer()
            }
        )

        // Danh mục với dropdown
        DrawerNavItem(
            icon = "🌸",
            label = "Danh mục",
            isSelected = currentTab == NavTab.CATEGORY,
            trailingIcon = if (categories.isNotEmpty()) (if (categoryExpanded) "▲" else "▼") else null,
            onClick = {
                if (categories.isEmpty()) {
                    onTabClick(NavTab.CATEGORY)
                    closeDrawer()
                } else {
                    categoryExpanded = !categoryExpanded
                }
            }
        )

        // Dropdown categories
        AnimatedVisibility(
            visible = categoryExpanded && categories.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // "Tất cả" option
                DrawerSubItem(
                    label = "Tất cả danh mục",
                    onClick = {
                        onTabClick(NavTab.CATEGORY)
                        categoryExpanded = false
                        closeDrawer()
                    }
                )
                categories.forEach { category ->
                    DrawerSubItem(
                        label = category.name,
                        onClick = {
                            onCategoryClick(category)
                            categoryExpanded = false
                            closeDrawer()
                        }
                    )
                }
            }
        }

        DrawerNavItem(
            icon = "🛒",
            label = "Giỏ hàng",
            isSelected = currentTab == NavTab.CART,
            badgeCount = cartItemCount,
            onClick = {
                onTabClick(NavTab.CART)
                closeDrawer()
            }
        )

        DrawerNavItem(
            icon = "📦",
            label = "Đơn hàng của tôi",
            isSelected = false,
            onClick = {
                onViewOrders()
                closeDrawer()
            }
        )

        DrawerNavItem(
            icon = "💬",
            label = "Chat với Admin",
            isSelected = false,
            onClick = {
                onChatClick()
                closeDrawer()
            }
        )

        DrawerNavItem(
            icon = "👤",
            label = "Tài khoản",
            isSelected = currentTab == NavTab.PROFILE,
            onClick = {
                onTabClick(NavTab.PROFILE)
                closeDrawer()
            }
        )

        if (userRole.equals("ADMIN", ignoreCase = true)) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = SandDark.copy(alpha = 0.4f)
            )
            DrawerNavItem(
                icon = "⚙️",
                label = "Admin Dashboard",
                isSelected = false,
                onClick = {
                    onOpenAdminDashboard()
                    closeDrawer()
                }
            )
        }

        // ── Footer ───────────────────────────────────────────────────────────────
        Spacer(Modifier.weight(1f))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = SandDark.copy(alpha = 0.4f)
        )
        DrawerNavItem(
            icon = "🚪",
            label = "Đăng xuất",
            isSelected = false,
            labelColor = MaterialTheme.colorScheme.error,
            onClick = {
                onLogout()
                closeDrawer()
            }
        )
        Spacer(Modifier.height(16.dp))
    }
}

// ── DrawerNavItem ────────────────────────────────────────────────────────────

@Composable
private fun DrawerNavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    trailingIcon: String? = null,
    labelColor: androidx.compose.ui.graphics.Color = SoilBrown,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MossGreen.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent
    val textColor = if (isSelected) MossGreen else labelColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(start = 20.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(MossGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.coerceAtMost(99).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PaperWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (trailingIcon != null) {
            Text(text = trailingIcon, fontSize = 12.sp, color = SandDark)
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MossGreen)
            )
        }
    }
}

// ── DrawerSubItem (category dropdown) ───────────────────────────────────────

@Composable
private fun DrawerSubItem(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 58.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(SandDark)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SoilBrown
        )
    }
}
