package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Notification
import com.example.myapplication.data.MockData
import com.example.myapplication.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen() {
    val notifications = MockData.notifications

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(bottom = 60.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thông báo",
                style = MaterialTheme.typography.headlineSmall,
                color = SoilBrown
            )
            if (notifications.any { !it.isRead }) {
                Text(
                    text = "${notifications.count { !it.isRead }} mới",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmPeach,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarmPeach.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Divider()

        // Notifications list
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không có thông báo nào",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SandDark
                )
            }
        } else {
            LazyColumn {
                items(notifications) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale("vi", "VN"))
    val formattedDate = dateFormat.format(Date(notification.timestamp))
    
    val backgroundColor = when {
        !notification.isRead -> Sand
        else -> PaperWhite
    }
    
    val icon = when (notification.type) {
        "success" -> "✓"
        "warning" -> "⚠️"
        else -> "ℹ️"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (notification.type) {
                            "success" -> MossGreen.copy(alpha = 0.2f)
                            "warning" -> WarmPeach.copy(alpha = 0.2f)
                            else -> Sand
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoilBrown,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MossGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = SandDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = SandDark
                )
            }
        }
    }
}
