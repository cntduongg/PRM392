package com.example.theflower.ui.screens.payment

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
import java.text.SimpleDateFormat
import java.util.*
import com.example.theflower.data.MockData
import com.example.theflower.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    totalAmount: Int,
    initialAddress: String = "",
    onConfirmPayment: (method: String, name: String, phone: String, date: String, address: String) -> Unit,
    onCancel: () -> Unit
) {
    val selectedPaymentMethod = remember { mutableStateOf("COD") }
    val recipientName = remember { mutableStateOf("") }
    val recipientPhone = remember { mutableStateOf("") }
    val deliveryDate = remember { mutableStateOf("") }
    val deliveryAddress = remember { mutableStateOf(initialAddress) }

    // Date/Time picker state
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = true)

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker.value = false
                    showTimePicker.value = true
                }) { Text("Xác nhận") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker.value) {
        AlertDialog(
            onDismissRequest = { showTimePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        sdf.format(Date(it))
                    } ?: ""
                    val time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    deliveryDate.value = "$date $time"
                    showTimePicker.value = false
                }) { Text("Xác nhận") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker.value = false }) { Text("Hủy") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Chọn giờ giao hàng (24h format)", modifier = Modifier.padding(bottom = 16.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(bottom = 60.dp)
    ) {
        item {
            Text(
                text = "Thanh toán",
                style = MaterialTheme.typography.headlineSmall,
                color = SoilBrown,
                modifier = Modifier.padding(16.dp)
            )
        }

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

                OutlinedTextField(
                    value = recipientName.value,
                    onValueChange = { recipientName.value = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Tên người nhận", color = PlaceholderGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = SoilBrown,
                        unfocusedTextColor = SoilBrown,
                        cursorColor = MossGreen,
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recipientPhone.value,
                    onValueChange = { recipientPhone.value = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Số điện thoại", color = PlaceholderGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = SoilBrown,
                        unfocusedTextColor = SoilBrown,
                        cursorColor = MossGreen,
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker.value = true }
                ) {
                    OutlinedTextField(
                        value = deliveryDate.value,
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        readOnly = true,
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text("Ngày giao hàng (Chọn ngày & giờ)", color = PlaceholderGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Sand,
                            disabledBorderColor = Color.Transparent,
                            disabledTextColor = SoilBrown,
                            disabledPlaceholderColor = PlaceholderGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deliveryAddress.value,
                    onValueChange = { deliveryAddress.value = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Địa chỉ giao hàng", color = PlaceholderGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = SoilBrown,
                        unfocusedTextColor = SoilBrown,
                        cursorColor = MossGreen,
                    )
                )
            }
        }

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

                PaymentMethodCard(
                    icon = "💵",
                    name = "Thanh toán khi nhận hàng (COD)",
                    isSelected = selectedPaymentMethod.value == "COD",
                    onClick = { selectedPaymentMethod.value = "COD" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PaymentMethodCard(
                    icon = "🏦",
                    name = "Thanh toán qua VnPay",
                    isSelected = selectedPaymentMethod.value == "VnPay",
                    onClick = { selectedPaymentMethod.value = "VnPay" }
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        onConfirmPayment(
                            selectedPaymentMethod.value,
                            recipientName.value,
                            recipientPhone.value,
                            deliveryDate.value,
                            deliveryAddress.value
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "✅ Xác nhận thanh toán – ₫${totalAmount}",
                        style = MaterialTheme.typography.titleMedium,
                        color = PaperWhite
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onCancel,
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
                    Modifier.border(2.dp, MossGreen, RoundedCornerShape(12.dp))
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
