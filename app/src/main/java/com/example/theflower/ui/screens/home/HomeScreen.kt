package com.example.theflower.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.theflower.data.MockData
import com.example.theflower.domain.models.Notification
import com.example.theflower.domain.models.Occasion
import com.example.theflower.domain.models.Product
import com.example.theflower.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit,
    onChatClick: () -> Unit,
    onOccasionClick: (String) -> Unit
) {
    val notifications = remember { mutableStateListOf(*MockData.notifications.toTypedArray()) }
    val showNotificationPanel = remember { mutableStateOf(false) }
    val selectedNotification = remember { mutableStateOf<Notification?>(null) }
    val unreadCount = notifications.count { !it.isRead }

    // Simulate real-time polling: every 10s check for new notifications
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            val newNotif = Notification(
                id = System.currentTimeMillis().toString(),
                title = "Thông báo mới từ The Flower",
                message = "Bạn có ưu đãi đặc biệt hôm nay! Đặt hoa ngay để nhận giảm 15%.",
                type = "info",
                createdAt = System.currentTimeMillis(),
                isRead = false
            )
            notifications.add(0, newNotif)
        }
    }

    // Notification detail modal
    selectedNotification.value?.let { notif ->
        Dialog(onDismissRequest = { selectedNotification.value = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val typeIcon = when (notif.type) {
                        "success" -> "✅"
                        "warning" -> "⚠️"
                        else -> "💬"
                    }
                    val typeBg = when (notif.type) {
                        "success" -> MossGreen.copy(alpha = 0.15f)
                        "warning" -> WarmPeach.copy(alpha = 0.15f)
                        else -> Sand
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(typeBg)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(typeIcon, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = notif.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = SoilBrown,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Sand)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = notif.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoilBrown,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN")).format(Date(notif.createdAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = SandDark
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedNotification.value = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Đóng", color = PaperWhite)
                    }
                }
            }
        }
    }

    // Notification dropdown panel
    if (showNotificationPanel.value) {
        Dialog(onDismissRequest = { showNotificationPanel.value = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔔 Thông báo",
                            style = MaterialTheme.typography.titleLarge,
                            color = SoilBrown,
                            fontWeight = FontWeight.Bold
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WarmPeach.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$unreadCount mới",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = WarmPeach,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Sand)

                    if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
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
                            items(notifications) { notif ->
                                val isUnread = !notif.isRead
                                val notifIcon = when (notif.type) {
                                    "success" -> "✅"
                                    "warning" -> "⚠️"
                                    else -> "💬"
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isUnread) Sand.copy(alpha = 0.5f) else PaperWhite)
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(notifIcon, fontSize = 22.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = notif.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = SoilBrown,
                                                fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (isUnread) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(MossGreen)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = notif.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SandDark,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        TextButton(
                                            onClick = {
                                                val idx = notifications.indexOf(notif)
                                                if (idx >= 0) {
                                                    notifications[idx] = notif.copy(isRead = true)
                                                }
                                                selectedNotification.value = notif.copy(isRead = true)
                                                showNotificationPanel.value = false
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = "Xem chi tiết →",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MossGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = Sand.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(bottom = 60.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Xin chào, ${MockData.currentUser.name}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SoilBrown
                    )
                    Text(
                        text = "🌿 Tìm hoa đẹp hôm nay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MossGreen
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification bell button
                    Box {
                        Button(
                            onClick = { showNotificationPanel.value = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (unreadCount > 0) WarmPeach else Sand
                            ),
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("🔔", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                        }
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD32F2F)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Button(
                        onClick = onChatClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("💬", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                    }
                }
            }
        }

        // Search bar
        item {
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Tìm hoa, lựa chọn dịp...", color = PlaceholderGray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Sand,
                    unfocusedContainerColor = Sand,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = SoilBrown,
                    unfocusedTextColor = SoilBrown,
                    cursorColor = MossGreen,
                    focusedPlaceholderColor = PlaceholderGray,
                    unfocusedPlaceholderColor = PlaceholderGray
                ),
                leadingIcon = { Text("🔍", modifier = Modifier.padding(12.dp)) },
                singleLine = true
            )
        }

        // Hero banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(SoilBrown, MossGreen)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tặng hoa – tặng cả cảm xúc",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PaperWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "🌺 Tặng tình cảm qua từng cánh hoa xinh đẹp",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Sand,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Occasion picker
        item {
            Text(
                text = "Chọn dịp",
                style = MaterialTheme.typography.titleLarge,
                color = SoilBrown,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    items(MockData.occasions) { occasion ->
                        OccasionPill(
                            occasion = occasion,
                            onClick = { onOccasionClick(occasion.name) }
                        )
                    }
                }
            )
        }

        // Featured products section
        item {
            Text(
                text = "Hoa nổi bật",
                style = MaterialTheme.typography.titleLarge,
                color = SoilBrown,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    items(MockData.products.take(6)) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun OccasionPill(
    occasion: Occasion,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(color = Color(android.graphics.Color.parseColor(occasion.color)))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = occasion.emoji,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = occasion.name,
            style = MaterialTheme.typography.labelSmall,
            color = SoilBrown,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFE8DFD8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌸",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = SoilBrown,
                    maxLines = 2,
                    minLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "₫${product.price}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MossGreen
                )
            }
        }
    }
}
