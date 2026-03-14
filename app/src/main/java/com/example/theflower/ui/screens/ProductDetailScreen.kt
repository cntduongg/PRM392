package com.example.theflower.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.theflower.data.Product
import com.example.theflower.ui.theme.*

@Composable
fun ProductDetailScreen(
    product: Product,
    onAddToCart: (Product, Int, String) -> Unit,
    onBackClick: () -> Unit
) {
    val selectedStems = remember { mutableStateOf(20) }
    val message = remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        // Header with back button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Sand),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("←")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Sand),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("❤️")
                }
            }
        }

        // Product image (42% of screen)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Sand),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌸",
                    fontSize = MaterialTheme.typography.displayLarge.fontSize
                )
            }
        }

        // Origin badge
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MossGreen)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = product.origin,
                    style = MaterialTheme.typography.labelSmall,
                    color = PaperWhite
                )
            }
        }

        // Product details
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                // Product name (2 lines, italic style)
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = SoilBrown,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text("⭐ ${product.rating} (${product.reviews} đánh giá)", 
                        style = MaterialTheme.typography.bodySmall,
                        color = SandDark
                    )
                }

                // Description
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoilBrown,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Price
                Text(
                    text = "₫${product.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MossGreen
                )
            }
        }

        // Stem quantity selector
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Chọn số cây",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.stemCount.forEach { stems ->
                        val isSelected = selectedStems.value == stems
                        Button(
                            onClick = { selectedStems.value = stems },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MossGreen else Sand
                            ),
                            modifier = Modifier.graphicsLayer(
                                scaleX = if (isSelected) 1.05f else 1f,
                                scaleY = if (isSelected) 1.05f else 1f
                            )
                        ) {
                            Text(
                                text = "$stems bông",
                                color = if (isSelected) PaperWhite else SoilBrown
                            )
                        }
                    }
                }
            }
        }

        // Message card
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Lời nhắn (tùy chọn)",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    placeholder = { Text("Ghi lời nhắn cho người nhận...", color = SandDark) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = MossGreen,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        // Add to cart button
        item {
            Button(
                onClick = {
                    onAddToCart(product, selectedStems.value, message.value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🛒 Thêm vào giỏ — ₫${product.price * selectedStems.value}",
                    style = MaterialTheme.typography.titleMedium,
                    color = PaperWhite
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
