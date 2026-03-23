package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.CartDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.ui.theme.*

// ─── PendingOrderBar ──────────────────────────────────────────────────────────

@Composable
internal fun PendingOrderBar(order: OrderDto, onPay: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MossGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("⏳", fontSize = 18.sp)
                Column {
                    Text(
                        "Đơn hàng chờ thanh toán",
                        style = MaterialTheme.typography.titleSmall,
                        color = PaperWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "#${order.id.take(8)}  •  ${formatCurrency(order.totalPrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PaperWhite.copy(alpha = 0.85f)
                    )
                }
            }
            Button(
                onClick = onPay,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PaperWhite),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Thanh toán", color = MossGreen, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── QuantityButton ───────────────────────────────────────────────────────────

@Composable
internal fun QuantityButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(SoilBrown),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(28.dp)
        ) {
            Text(label, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// ─── CartApiScreen ────────────────────────────────────────────────────────────
// Called by: MainAppLayout() when NavTab.CART

@Composable
internal fun CartApiScreen(
    modifier: Modifier = Modifier,
    cart: CartDto?,
    checkoutAddress: String,
    onCheckoutAddressChange: (String) -> Unit,
    onCreateOrder: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onUpdateItemQuantity: (String, Int) -> Unit,
    onClearCart: () -> Unit,
    onRefresh: () -> Unit
) {
    val items = cart?.items.orEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        if (items.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🛒", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Giỏ hàng đang trống",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Hãy thêm sản phẩm yêu thích vào giỏ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SandDark
                )
                Spacer(Modifier.height(20.dp))
                OutlinedButton(
                    onClick = onRefresh,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MossGreen),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Làm mới")
                }
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${items.size} sản phẩm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SandDark
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = onRefresh,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("↻ Làm mới", color = SandDark, style = MaterialTheme.typography.labelMedium)
                        }
                        TextButton(
                            onClick = onClearCart,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("🗑 Xóa giỏ", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Cart items
            items(items) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProductThumbnail(imageUrl = item.productImage, label = item.productName)

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                item.productName,
                                style = MaterialTheme.typography.titleSmall,
                                color = SoilBrown,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                formatCurrency(item.productPrice),
                                style = MaterialTheme.typography.bodySmall,
                                color = SandDark
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                QuantityButton(label = "−") {
                                    onUpdateItemQuantity(item.id, item.quantity - 1)
                                }
                                Text(
                                    item.quantity.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = SoilBrown,
                                    fontWeight = FontWeight.Bold
                                )
                                QuantityButton(label = "+") {
                                    onUpdateItemQuantity(item.id, item.quantity + 1)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                formatCurrency(item.totalPrice),
                                style = MaterialTheme.typography.titleSmall,
                                color = MossGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Delete button
                        IconButton(
                            onClick = { onRemoveItem(item.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("🗑️", fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        // Sticky checkout bottom
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Sand),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Thông tin giao hàng",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = checkoutAddress,
                    onValueChange = onCheckoutAddressChange,
                    placeholder = { Text("📍 Nhập địa chỉ giao hàng", color = SandDark) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tổng tiền", style = MaterialTheme.typography.bodySmall, color = SandDark)
                        Text(
                            formatCurrency(cart?.totalPrice ?: 0.0),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MossGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = onCreateOrder,
                        modifier = Modifier
                            .height(50.dp)
                            .widthIn(min = 160.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                    ) {
                        Text("Đặt hàng 🌿", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
