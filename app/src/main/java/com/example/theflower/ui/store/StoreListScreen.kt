package com.example.theflower.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.StoreLocationDto
import com.example.theflower.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreListScreen(
    stores: List<StoreLocationDto>,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Hệ thống cửa hàng", style = MaterialTheme.typography.titleLarge, color = SoilBrown, fontWeight = FontWeight.Bold)
                        Text("Tìm chi nhánh gần bạn nhất", style = MaterialTheme.typography.labelMedium, color = SandDark)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp, color = SoilBrown)
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Text("🔄", fontSize = 18.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite)
            )
        }
    ) { padding ->
        if (stores.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏪", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đang tải danh sách cửa hàng...", color = SandDark)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(stores.filter { (it.status ?: "Active").equals("Active", ignoreCase = true) }) { store ->
                    StoreItemCard(store = store, context = context)
                }
            }
        }
    }
}

@Composable
fun StoreItemCard(store: StoreLocationDto, context: Context) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Sand, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏪", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "The Flower Studio",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoilBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Chi nhánh TP.HCM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MossGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row {
                Text("📍", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = store.address ?: "Đang cập nhật địa chỉ...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoilBrown,
                    lineHeight = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { openMap(context, store.address) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🔍 Xem bản đồ", color = SoilBrown, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = { openDirections(context, store.address) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🗺️ Chỉ đường", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun openMap(context: Context, address: String?) {
    if (address.isNullOrBlank()) return
    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    // mapIntent.setPackage("com.google.android.apps.maps") // Optional: force Google Maps
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}

private fun openDirections(context: Context, address: String?) {
    if (address.isNullOrBlank()) return
    val gmmIntentUri = Uri.parse("google.navigation:q=${Uri.encode(address)}")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        // Fallback to browser/other map apps
        val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(address)}")
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}
