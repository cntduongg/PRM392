package com.example.theflower.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.theflower.data.remote.dtos.CartDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SoilBrown

// ─── PendingOrderBar ──────────────────────────────────────────────────────────
// Dùng trong CartNavScreen, ProductDetailApiScreen, CategoryProductsScreen, OrdersNavScreen

@Composable
internal fun PendingOrderBar(
    order: OrderDto,
    onPay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoilBrown)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Đơn hàng chờ thanh toán",
                    style = MaterialTheme.typography.titleSmall,
                    color = PaperWhite
                )
                Text(
                    text = "#${order.id.take(8)}  •  ${formatCurrency(order.totalPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PaperWhite.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = onPay,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Thanh toán")
            }
        }
    }
}

// ─── QuantityButton ───────────────────────────────────────────────────────────

@Composable
internal fun QuantityButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SoilBrown)
    ) {
        Text(label)
    }
}

// ─── CartApiScreen ────────────────────────────────────────────────────────────
// Gọi bởi: MainAppLayout() khi NavTab.CART

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Giỏ hàng", style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onClearCart,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoilBrown)
                ) {
                    Text("Xóa giỏ")
                }
                Button(
                    onClick = onRefresh,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand)
                ) {
                    Text("Làm mới", color = SoilBrown)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (items.isEmpty()) {
            EmptyState(message = "Giỏ hàng đang trống")
            return
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProductThumbnail(
                                imageUrl = item.productImage,
                                label = item.productName
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.productName, style = MaterialTheme.typography.titleSmall, color = SoilBrown)
                                Text(
                                    formatCurrency(item.productPrice),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = com.example.theflower.ui.theme.SandDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    QuantityButton(
                                        label = "-",
                                        onClick = { onUpdateItemQuantity(item.id, item.quantity - 1) }
                                    )
                                    Text(
                                        text = item.quantity.toString(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = SoilBrown
                                    )
                                    QuantityButton(
                                        label = "+",
                                        onClick = { onUpdateItemQuantity(item.id, item.quantity + 1) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(formatCurrency(item.totalPrice), style = MaterialTheme.typography.titleSmall, color = MossGreen)
                            }
                        }
                        Button(
                            onClick = { onRemoveItem(item.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                        ) {
                            Text("Xóa")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Sand), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Thông tin thanh toán", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = checkoutAddress,
                    onValueChange = onCheckoutAddressChange,
                    label = { Text("Địa chỉ giao hàng") },
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Tổng tiền cần trả",
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.example.theflower.ui.theme.SandDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        formatCurrency(cart?.totalPrice ?: 0.0),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MossGreen,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onCreateOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                ) {
                    Text("Tạo đơn hàng")
                }
            }
        }
    }
}
