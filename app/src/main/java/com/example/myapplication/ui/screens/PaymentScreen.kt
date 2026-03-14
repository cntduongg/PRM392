package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.MockData
import com.example.myapplication.ui.theme.*

@Composable
fun PaymentScreen(
    totalAmount: Int = 599000,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit
) {
    val selectedPaymentMethod = remember { mutableStateOf("cod") }
    val recipientName = remember { mutableStateOf("") }
    val recipientPhone = remember { mutableStateOf("") }
    val deliveryDate = remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(bottom = 60.dp)
    ) {
        // Header
        item {
            Text(
                text = "Thanh toán",
                style = MaterialTheme.typography.headlineSmall,
                color = SoilBrown,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Order summary
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Sand),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tổng tiền:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                        Text("₫${totalAmount}", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Phí vận chuyển:", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                        Text("Miễn phí", style = MaterialTheme.typography.bodyMedium, color = MossGreen)
                    }
                }
            }
        }

        // Recipient info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Thông tin người nhận",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                TextField(
                    value = recipientName.value,
                    onValueChange = { recipientName.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Tên người nhận") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = recipientPhone.value,
                    onValueChange = { recipientPhone.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Số điện thoại") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = deliveryDate.value,
                    onValueChange = { deliveryDate.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Ngày giao hàng") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        // Payment methods
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Phương thức thanh toán",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // COD
                PaymentMethodCard(
                    icon = "💵",
                    name = "Thanh toán khi nhận hàng (COD)",
                    isSelected = selectedPaymentMethod.value == "cod",
                    onClick = { selectedPaymentMethod.value = "cod" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bank transfer
                PaymentMethodCard(
                    icon = "🏦",
                    name = "Chuyển khoản ngân hàng",
                    isSelected = selectedPaymentMethod.value == "bank",
                    onClick = { selectedPaymentMethod.value = "bank" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // E-wallet
                PaymentMethodCard(
                    icon = "📱",
                    name = "Ví điện tử",
                    isSelected = selectedPaymentMethod.value == "ewallet",
                    onClick = { selectedPaymentMethod.value = "ewallet" }
                )
            }
        }

        // Payment buttons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Button(
                    onClick = onPaymentSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "✓ Xác nhận thanh toán — ₫${totalAmount}",
                        style = MaterialTheme.typography.titleMedium,
                        color = PaperWhite
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onPaymentFailed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Hủy",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoilBrown
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    icon: String,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        2.dp,
                        MossGreen,
                        RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MossGreen.copy(alpha = 0.2f) else Sand
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,
                color = SoilBrown,
                modifier = Modifier.weight(1f)
            )
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MossGreen,
                    unselectedColor = SoilBrown
                )
            )
        }
    }
}
