package com.example.theflower.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.theflower.data.MockData
import com.example.theflower.ui.theme.*

@Composable
fun DashboardScreen(
    onChatClick: () -> Unit,
    onUserManagementClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(bottom = 60.dp)
    ) {
        // Header
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
                        text = "Tài khoản của tôi",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SoilBrown
                    )
                    Text(
                        text = MockData.currentUser.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MossGreen
                    )
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

        // User info card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Sand),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PeachLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                        }

                        Column {
                            Text(
                                text = MockData.currentUser.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = SoilBrown
                            )
                            Text(
                                text = MockData.currentUser.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = SandDark
                            )
                            Text(
                                text = MockData.currentUser.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = SandDark
                            )
                        }
                    }
                }
            }
        }

        // Quick stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), icon = "📦", label = "Đơn hàng", value = "5")
                StatCard(modifier = Modifier.weight(1f), icon = "❤️", label = "Yêu thích", value = "12")
                StatCard(modifier = Modifier.weight(1f), icon = "⭐", label = "Đánh giá", value = "4.8")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Menu section
        item {
            Text(
                text = "Quản lý",
                style = MaterialTheme.typography.titleMedium,
                color = SoilBrown,
                modifier = Modifier.padding(16.dp)
            )
        }

        // User management
        item {
            MenuItem(
                icon = "👥",
                title = "Quản lý người dùng",
                subtitle = "Xem và chỉnh sửa thông tin",
                onClick = onUserManagementClick
            )
        }

        // Orders
        item {
            MenuItem(
                icon = "📋",
                title = "Đơn hàng của tôi",
                subtitle = "Xem lịch sử đơn hàng",
                onClick = {}
            )
        }

        // Favorites
        item {
            MenuItem(
                icon = "❤️",
                title = "Sản phẩm yêu thích",
                subtitle = "Danh sách hoa yêu thích",
                onClick = {}
            )
        }

        // Settings
        item {
            MenuItem(
                icon = "⚙️",
                title = "Cài đặt",
                subtitle = "Tùy chỉnh ứng dụng",
                onClick = {}
            )
        }

        // Support
        item {
            MenuItem(
                icon = "💬",
                title = "Hỗ trợ khách hàng",
                subtitle = "Liên hệ với chúng tôi",
                onClick = {}
            )
        }

        // Logout
        item {
            MenuItem(
                icon = "🚪",
                title = "Đăng xuất",
                subtitle = "Thoát khỏi tài khoản",
                onClick = {}
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String
) {
    Card(
        modifier = modifier
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Sand),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = MossGreen)
            Text(label, style = MaterialTheme.typography.labelSmall, color = SandDark)
        }
    }
}

@Composable
fun MenuItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SandDark)
            }
            Text("→", style = MaterialTheme.typography.bodyLarge, color = SandDark)
        }
        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}
