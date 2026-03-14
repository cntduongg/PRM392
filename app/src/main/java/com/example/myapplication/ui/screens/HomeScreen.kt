package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.MockData
import com.example.myapplication.data.Product
import com.example.myapplication.ui.theme.*

@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit,
    onChatClick: () -> Unit,
    onOccasionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(bottom = 60.dp)
    ) {
        // Header with greeting and chat icon
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
                Button(
                    onClick = onChatClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MossGreen
                    ),
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("💬", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
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
                placeholder = { Text("Tìm hoa, lựa chọn dịp...", color = SandDark) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Sand,
                    unfocusedContainerColor = Sand,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
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
                        text = "Tặng hoa — tặng cả cảm xúc",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PaperWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "🍃 Tặng tình cảm qua từng cánh hoa xinh đẹp",
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

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OccasionPill(
    occasion: com.example.myapplication.data.Occasion,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                color = Color(android.graphics.Color.parseColor(occasion.color))
            )
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
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Sand),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌸",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        }
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.labelMedium,
                color = SoilBrown,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₫${product.price}",
                style = MaterialTheme.typography.labelLarge,
                color = MossGreen
            )
        }
    }
}
