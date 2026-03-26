package com.example.theflower.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.StoreLocationDto
import com.example.theflower.ui.theme.*

@Composable
fun StoreListScreen(
    modifier: Modifier = Modifier,
    stores: List<StoreLocationDto>,
    onViewOnMap: (StoreLocationDto) -> Unit,
    onGetDirections: (StoreLocationDto) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        // Header Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SoilBrown)
                .padding(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Text(
                    text = "Hệ thống cửa hàng",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Tìm chi nhánh gần bạn nhất 🌸",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        if (stores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🏪",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Chưa có thông tin cửa hàng",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoilBrown.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                    ) {
                        Text("Tải lại")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(stores) { store ->
                    UserStoreCard(
                        store = store,
                        onViewOnMap = { onViewOnMap(store) },
                        onGetDirections = { onGetDirections(store) }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Extra space for bottom nav/fab
                }
            }
        }
    }
}

@Composable
fun UserStoreCard(
    store: StoreLocationDto,
    onViewOnMap: () -> Unit,
    onGetDirections: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MossGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍", fontSize = 24.sp)
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cửa hàng The Flower",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SoilBrown
                    )
                    if (store.status != "Active") {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Red.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Tạm đóng cửa",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red
                            )
                        }
                    } else {
                        Text(
                            text = "Đang hoạt động",
                            style = MaterialTheme.typography.labelSmall,
                            color = MossGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = store.address ?: "Không có địa chỉ",
                style = MaterialTheme.typography.bodyMedium,
                color = SoilBrown.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            if (store.latitude != null && store.longitude != null) {
                Text(
                    text = "Tọa độ: ${"%.4f".format(store.latitude)}, ${"%.4f".format(store.longitude)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewOnMap,
                    modifier = Modifier.weight(1f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MossGreen)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🗺️ Xem bản đồ", color = MossGreen, style = MaterialTheme.typography.labelMedium)
                }
                
                Button(
                    onClick = onGetDirections,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🚀 Chỉ đường", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
