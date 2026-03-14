package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.CartItem
import com.example.myapplication.data.MockData
import com.example.myapplication.ui.theme.*

@Composable
fun CartScreen(
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    val cartItems = MockData.cartItems
    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }

    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PaperWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🛒", fontSize = MaterialTheme.typography.displayLarge.fontSize)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Giỏ hàng trống",
                    style = MaterialTheme.typography.titleLarge,
                    color = SoilBrown
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onContinueShopping,
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tiếp tục mua sắm", color = PaperWhite)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PaperWhite)
        ) {
            // Header
            Text(
                text = "Giỏ hàng (${cartItems.size})",
                style = MaterialTheme.typography.headlineSmall,
                color = SoilBrown,
                modifier = Modifier.padding(16.dp)
            )

            // Cart items
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(cartItems) { item ->
                    CartItemRow(item = item)
                }
            }

            // Order summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Sand)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng tiền hàng:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                    Text("₫${totalPrice}", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Phí vận chuyển:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                    Text("Miễn phí", style = MaterialTheme.typography.bodyMedium, color = MossGreen)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thành tiền:", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                    Text("₫${totalPrice}", style = MaterialTheme.typography.titleMedium, color = MossGreen)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkout button
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tiến hành thanh toán",
                        style = MaterialTheme.typography.titleMedium,
                        color = PaperWhite
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Product image
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Sand),
            contentAlignment = Alignment.Center
        ) {
            Text("🌸", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        }

        // Product info
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.labelMedium,
                color = SoilBrown
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.quantity} bông",
                style = MaterialTheme.typography.bodySmall,
                color = SandDark
            )
            if (item.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💬 ${item.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MossGreen,
                    maxLines = 1
                )
            }
        }

        // Price and remove
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "₫${item.product.price * item.quantity}",
                style = MaterialTheme.typography.titleSmall,
                color = MossGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.size(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("🗑️")
            }
        }
    }
    Divider()
}
