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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown

// ─── ProfileApiScreen ─────────────────────────────────────────────────────────
// Gọi bởi: MainAppLayout() khi NavTab.PROFILE

@Composable
internal fun ProfileApiScreen(
    modifier: Modifier = Modifier,
    errorMessage: String?,
    userName: String,
    userEmail: String,
    userPhone: String,
    userAddress: String,
    userRole: String,
    onOpenCustomerProductActivity: () -> Unit,
    onViewOrders: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onUpdateProfile: (String, String, String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onLogout: () -> Unit
) {
    var fullNameInput by remember { mutableStateOf(userName) }
    var phoneInput by remember { mutableStateOf(userPhone) }
    var addressInput by remember { mutableStateOf(userAddress) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(userName, userPhone, userAddress) {
        fullNameInput = userName
        phoneInput = userPhone
        addressInput = userAddress
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Tài khoản", style = MaterialTheme.typography.headlineSmall, color = SoilBrown)

        // User info card + logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Sand)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(if (userName.isBlank()) "Nguoi dung" else userName, style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (userEmail.isBlank()) "(chua co)" else userEmail, style = MaterialTheme.typography.bodyMedium, color = SandDark)
                    if (userPhone.isNotBlank()) {
                        Text(userPhone, style = MaterialTheme.typography.bodySmall, color = SandDark)
                    }
                    if (userAddress.isNotBlank()) {
                        Text(userAddress, style = MaterialTheme.typography.bodySmall, color = SandDark)
                    }
                }
            }

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .width(104.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SandDark),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text("Logout", color = PaperWhite, textAlign = TextAlign.Center)
            }
        }

        // Update profile card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Sand)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Cập nhật hồ sơ", style = MaterialTheme.typography.titleMedium, color = SoilBrown)

                OutlinedTextField(
                    value = fullNameInput,
                    onValueChange = { fullNameInput = it },
                    label = { Text("Họ tên") },
                    singleLine = true,
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Số điện thoại") },
                    singleLine = true,
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    label = { Text("Địa chỉ") },
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onUpdateProfile(fullNameInput.trim(), phoneInput.trim(), addressInput.trim()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Lưu hồ sơ")
                }

                if (!errorMessage.isNullOrBlank()) {
                    ErrorNote(message = errorMessage)
                }
            }
        }

        // Change password card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Sand)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Đổi mật khẩu", style = MaterialTheme.typography.titleMedium, color = SoilBrown)

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Mật khẩu hiện tại") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu mới") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onChangePassword(currentPassword, newPassword, confirmPassword) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SoilBrown),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Đổi mật khẩu")
                }

                if (!errorMessage.isNullOrBlank()) {
                    ErrorNote(message = errorMessage)
                }
            }
        }

        // Customer product list button
        if (!userRole.equals("ADMIN", ignoreCase = true)) {
            Button(
                onClick = onOpenCustomerProductActivity,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Danh sách sản phẩm (search/filter)", color = SoilBrown)
            }
        }

        Button(
            onClick = onViewOrders,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Xem đơn hàng")
        }

        if (userRole.equals("ADMIN", ignoreCase = true)) {
            Button(
                onClick = onOpenAdminDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoilBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Admin Dashboard")
            }
        }
    }
}
