package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown

// ─── StatusPill ───────────────────────────────────────────────────────────────

@Composable
internal fun StatusPill(status: String) {
    Box(
        modifier = Modifier
            .background(MossGreen.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = MossGreen,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── OrdersApiScreen ──────────────────────────────────────────────────────────
// Gọi bởi: AppNavigation() khi route = "orders"

@Composable
internal fun OrdersApiScreen(
    orders: List<OrderDto>,
    pendingOrder: OrderDto?,
    onBack: () -> Unit,
    onPayPendingOrder: () -> Unit
) {
    Scaffold(
        containerColor = PaperWhite,
        bottomBar = {
            if (pendingOrder != null) {
                PendingOrderBar(order = pendingOrder, onPay = onPayPendingOrder)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand)
            ) {
                Text("Quay lại", color = SoilBrown)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Đơn hàng của tôi", style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Spacer(modifier = Modifier.height(10.dp))

            if (orders.isEmpty()) {
                EmptyState(message = "Chưa có đơn hàng")
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(orders) { order ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Sand)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Đơn #${order.id}", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                                StatusPill(order.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Phương thức: ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall)
                            Text("Thanh toán: ${order.paymentStatus}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(formatCurrency(order.totalPrice), style = MaterialTheme.typography.titleSmall, color = MossGreen)
                            Text(order.recipientAddress, style = MaterialTheme.typography.bodySmall, color = SandDark)
                        }
                    }
                }
            }
        }
    }
}
