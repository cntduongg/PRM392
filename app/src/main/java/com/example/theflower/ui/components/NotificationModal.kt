package com.example.theflower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.theflower.ui.theme.*

// ─── AppNotification model ────────────────────────────────────────────────────

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val icon: String = "🔔",
    val isRead: Boolean = false,
    val timestamp: String = ""
)

// ─── NotificationButton ───────────────────────────────────────────────────────
// Drop into AppTopBar actions row

@Composable
fun NotificationButton(
    notifications: List<AppNotification>,
    onClick: () -> Unit
) {
    val unreadCount = notifications.count { !it.isRead }
    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(Sand),
        contentAlignment = Alignment.Center
    ) {
        Text("🔔", fontSize = 18.sp)
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unreadCount.coerceAtMost(99).toString(),
                    fontSize = 9.sp,
                    color = PaperWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─── NotificationModal ────────────────────────────────────────────────────────

@Composable
fun NotificationModal(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MossGreen)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🔔", fontSize = 22.sp)
                        Text(
                            "Thông báo",
                            style = MaterialTheme.typography.titleLarge,
                            color = PaperWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (notifications.any { !it.isRead }) {
                            TextButton(
                                onClick = onMarkAllRead,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Đọc tất cả",
                                    color = PaperWhite.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Text("✕", color = PaperWhite, fontSize = 16.sp)
                        }
                    }
                }

                // Body
                if (notifications.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🌸", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Không có thông báo nào",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoilBrown
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Các thông báo về đơn hàng sẽ hiển thị tại đây",
                            style = MaterialTheme.typography.bodySmall,
                            color = SandDark
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(notifications) { index, notif ->
                            NotificationItem(notification = notif)
                            if (index < notifications.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = SandDark.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── NotificationItem ─────────────────────────────────────────────────────────

@Composable
private fun NotificationItem(notification: AppNotification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (!notification.isRead) MossGreen.copy(alpha = 0.06f)
                else PaperWhite
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (!notification.isRead) MossGreen.copy(alpha = 0.15f) else Sand),
            contentAlignment = Alignment.Center
        ) {
            Text(notification.icon, fontSize = 20.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = SoilBrown,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal
                )
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MossGreen)
                    )
                }
            }
            if (notification.body.isNotBlank()) {
                Spacer(Modifier.height(3.dp))
                Text(
                    notification.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = SandDark
                )
            }
            if (notification.timestamp.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    notification.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = SandDark.copy(alpha = 0.7f)
                )
            }
        }
    }
}
