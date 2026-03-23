package com.example.theflower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    cartItemCount: Int = 0,
    onMenuClick: () -> Unit,
    onCartClick: (() -> Unit)? = null,
    showSearch: Boolean = false,
    searchQuery: String = "",
    onSearchChange: ((String) -> Unit)? = null,
    notifications: List<AppNotification> = emptyList(),
    onNotificationClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PaperWhite)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                // Hamburger button
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onMenuClick)
                        .background(Sand),
                    contentAlignment = Alignment.Center
                ) {
                    Text("☰", fontSize = 18.sp, color = SoilBrown)
                }
            },
            actions = {
                // Notification bell
                if (onNotificationClick != null) {
                    NotificationButton(
                        notifications = notifications,
                        onClick = onNotificationClick
                    )
                }

                // Cart icon with badge
                if (onCartClick != null) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onCartClick)
                            .background(Sand),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛒", fontSize = 18.sp)
                        if (cartItemCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(MossGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartItemCount.coerceAtMost(99).toString(),
                                    fontSize = 9.sp,
                                    color = PaperWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PaperWhite
            )
        )

        // Search bar (inline below TopAppBar when enabled)
        if (showSearch && onSearchChange != null) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("🔍 Tìm hoa...", color = PlaceholderGray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Sand,
                    unfocusedContainerColor = Sand,
                    focusedTextColor = SoilBrown,
                    unfocusedTextColor = SoilBrown,
                    focusedBorderColor = MossGreen,
                    unfocusedBorderColor = Sand,
                    cursorColor = MossGreen,
                    focusedPlaceholderColor = PlaceholderGray,
                    unfocusedPlaceholderColor = PlaceholderGray
                )
            )
        }

        HorizontalDivider(color = SandDark.copy(alpha = 0.2f))
    }
}
