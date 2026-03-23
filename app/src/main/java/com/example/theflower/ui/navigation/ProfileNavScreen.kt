package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.ui.theme.*

// ─── ProfileApiScreen ─────────────────────────────────────────────────────────
// Called by: MainAppLayout() when NavTab.PROFILE

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
            .background(PaperWhite)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Avatar header ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MossGreen)
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PaperWhite.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (userName.isNotBlank()) userName.first().uppercaseChar().toString() else "👤",
                        fontSize = 28.sp,
                        color = PaperWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        if (userName.isNotBlank()) userName else "Người dùng",
                        style = MaterialTheme.typography.titleLarge,
                        color = PaperWhite,
                        fontWeight = FontWeight.Bold
                    )
                    if (userEmail.isNotBlank()) {
                        Text(userEmail, style = MaterialTheme.typography.bodySmall, color = PaperWhite.copy(alpha = 0.85f))
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(PaperWhite.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(userRole.uppercase(), style = MaterialTheme.typography.labelSmall, color = PaperWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Quick actions ──────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileActionChip(icon = "📦", label = "Đơn hàng", modifier = Modifier.weight(1f)) { onViewOrders() }
                if (!userRole.equals("ADMIN", ignoreCase = true)) {
                    ProfileActionChip(icon = "🔍", label = "Tìm hoa", modifier = Modifier.weight(1f)) { onOpenCustomerProductActivity() }
                }
                if (userRole.equals("ADMIN", ignoreCase = true)) {
                    ProfileActionChip(icon = "⚙️", label = "Admin", modifier = Modifier.weight(1f)) { onOpenAdminDashboard() }
                }
            }

            // ── Update profile card ────────────────────────────────────────────
            ProfileSection(title = "✏️  Cập nhật hồ sơ") {
                OutlinedTextField(
                    value = fullNameInput,
                    onValueChange = { fullNameInput = it },
                    label = { Text("Họ tên") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Số điện thoại") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    label = { Text("Địa chỉ") },
                    shape = RoundedCornerShape(12.dp),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onUpdateProfile(fullNameInput.trim(), phoneInput.trim(), addressInput.trim()) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                ) {
                    Text("💾  Lưu hồ sơ", fontWeight = FontWeight.SemiBold)
                }
                if (!errorMessage.isNullOrBlank()) {
                    ErrorNote(message = errorMessage)
                }
            }

            // ── Change password card ───────────────────────────────────────────
            ProfileSection(title = "🔒  Đổi mật khẩu") {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Mật khẩu hiện tại") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu mới") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = botanicalOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onChangePassword(currentPassword, newPassword, confirmPassword) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoilBrown)
                ) {
                    Text("🔑  Đổi mật khẩu", fontWeight = FontWeight.SemiBold)
                }
                if (!errorMessage.isNullOrBlank()) {
                    ErrorNote(message = errorMessage)
                }
            }

            // ── Logout ────────────────────────────────────────────────────────
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    "🚪  Đăng xuất",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── ProfileSection ───────────────────────────────────────────────────────────

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Sand),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = SoilBrown, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

// ─── ProfileActionChip ────────────────────────────────────────────────────────

@Composable
private fun ProfileActionChip(
    icon: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Sand),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(icon, fontSize = 24.sp)
            Text(label, style = MaterialTheme.typography.labelMedium, color = SoilBrown, fontWeight = FontWeight.SemiBold)
        }
    }
}

