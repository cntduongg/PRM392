package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.ui.theme.*

// ─── StatusPill ───────────────────────────────────────────────────────────────

@Composable
internal fun StatusPill(status: String) {
    val (bg, fg) = when {
        status.contains("PAID", ignoreCase = true) ||
        status.contains("COMPLETED", ignoreCase = true) ||
        status.contains("SUCCESS", ignoreCase = true) ->
            MossGreen.copy(alpha = 0.15f) to MossGreen

        status.contains("CANCEL", ignoreCase = true) ||
        status.contains("FAILED", ignoreCase = true) ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error

        status.contains("PENDING", ignoreCase = true) ->
            WarmPeach to SoilBrown

        else -> Sand to SandDark
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── OrdersApiScreen ──────────────────────────────────────────────────────────
// Called by: AppNavigation() when route = "orders"

@Composable
internal fun OrdersApiScreen(
    orders: List<OrderDto>,
    pendingOrder: OrderDto?,
    onBack: () -> Unit,
    onPayPendingOrder: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (pendingOrder != null) 80.dp else 0.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Sand),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "←",
                            fontSize = 16.sp,
                            color = SoilBrown,
                            modifier = Modifier.let {
                                // use foundation clickable
                                it
                            }
                        )
                    }
                    TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                        Text("← Quay lại", color = SoilBrown)
                    }
                }
            }

            if (orders.isEmpty()) {
                item { EmptyState(message = "Chưa có đơn hàng nào 📦") }
                return@LazyColumn
            }

            itemsIndexed(orders) { index, order ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Timeline indicator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MossGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PaperWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (index < orders.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(MossGreen.copy(alpha = 0.3f))
                            )
                        }
                    }

                    // Order card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Sand),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Đơn #${order.id.take(8)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = SoilBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                                StatusPill(order.status)
                            }
                            HorizontalDivider(color = SandDark.copy(alpha = 0.3f))
                            OrderInfoRow(label = "Thanh toán", value = order.paymentMethod)
                            OrderInfoRow(label = "Trạng thái TT", value = order.paymentStatus)
                            if (order.recipientAddress.isNotBlank()) {
                                OrderInfoRow(label = "Địa chỉ", value = order.recipientAddress)
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    formatCurrency(order.totalPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MossGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pending order bar pinned bottom
        if (pendingOrder != null) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                PendingOrderBar(order = pendingOrder, onPay = onPayPendingOrder)
            }
        }
    }
}

// ─── OrderInfoRow ─────────────────────────────────────────────────────────────

@Composable
private fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = SandDark)
        Text(value, style = MaterialTheme.typography.bodySmall, color = SoilBrown, fontWeight = FontWeight.Medium)
    }
}
