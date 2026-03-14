package com.example.myapplication.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.*

@Composable
fun PaymentSuccessScreen(
    orderId: String = "#1012",
    onBackHome: () -> Unit,
    onViewOrder: () -> Unit
) {
    val scale = remember { mutableStateOf(0f) }
    val animatedScale = animateFloatAsState(
        targetValue = scale.value,
        animationSpec = tween(800),
        label = "scaleAnimation"
    )

    LaunchedEffect(Unit) {
        scale.value = 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PaperWhite, Sand)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success icon with animation
            Text(
                text = "✓",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                modifier = Modifier
                    .graphicsLayer(scaleX = animatedScale.value, scaleY = animatedScale.value),
                color = MossGreen
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Thanh toán thành công!",
                style = MaterialTheme.typography.headlineMedium,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = "Đơn hàng $orderId của bạn đã được xác nhận. Chúng tôi sẽ sớm chuẩn bị và giao tận tay bạn.",
                style = MaterialTheme.typography.bodyLarge,
                color = SandDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Order details card
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Sand),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mã đơn hàng:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                        Text(orderId, style = MaterialTheme.typography.bodyMedium, color = MossGreen)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Trạng thái:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                        Text(
                            "✓ Đã xác nhận",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MossGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Button(
                onClick = onViewOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Xem chi tiết đơn hàng",
                    style = MaterialTheme.typography.titleMedium,
                    color = PaperWhite
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Quay lại trang chủ",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown
                )
            }
        }
    }
}

@Composable
fun PaymentCancelScreen(
    orderId: String = "#1012",
    onRetryPayment: () -> Unit,
    onBackHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Error icon
            Text(
                text = "✕",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                color = WarmPeach
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Thanh toán không thành công",
                style = MaterialTheme.typography.headlineMedium,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = "Có lỗi xảy ra trong quá trình thanh toán. Vui lòng kiểm tra lại thông tin và thử lại.",
                style = MaterialTheme.typography.bodyLarge,
                color = SandDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error details card
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Sand),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mã đơn hàng:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                        Text(orderId, style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Trạng thái:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                        Text(
                            "✕ Lỗi thanh toán",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmPeach
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Button(
                onClick = onRetryPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Thử lại thanh toán",
                    style = MaterialTheme.typography.titleMedium,
                    color = PaperWhite
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Quay lại mua sắm",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown
                )
            }
        }
    }
}
