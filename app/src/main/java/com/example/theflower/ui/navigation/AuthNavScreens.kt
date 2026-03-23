package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.ui.theme.*

// ─── AuthFlow ────────────────────────────────────────────────────────────────
// Called by: AppNavigation() when !uiState.isLoggedIn

@Composable
internal fun AuthFlow(
    currentScreen: String,
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onGoToRegister: () -> Unit,
    onGoToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        containerColor = PaperWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to PaperWhite,
                            0.55f to Sand.copy(alpha = 0.6f),
                            1f to WarmPeach.copy(alpha = 0.4f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Logo / hero ──────────────────────────────────────────────
                Spacer(Modifier.height(16.dp))
                Text("🌸", fontSize = 64.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "The Flower",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tặng hoa – tặng cả cảm xúc 🌿",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SandDark,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))

                // ── Form card ────────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        if (currentScreen == "register") {
                            RegisterForm(
                                errorMessage = errorMessage,
                                onRegister = onRegister,
                                onGoToLogin = onGoToLogin
                            )
                        } else {
                            LoginForm(
                                errorMessage = errorMessage,
                                onLogin = onLogin,
                                onGoToRegister = onGoToRegister
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                // Leaf decoration
                Text("🌱 🌺 🌱", fontSize = 20.sp)
            }
        }
    }
}

// ─── LoginForm ───────────────────────────────────────────────────────────────

@Composable
internal fun LoginForm(
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Đăng nhập",
            style = MaterialTheme.typography.headlineSmall,
            color = SoilBrown,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Chào mừng bạn quay trở lại 🌷",
            style = MaterialTheme.typography.bodyMedium,
            color = SandDark
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("✉️  Email") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = botanicalOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("🔒  Mật khẩu") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = botanicalOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        if (!errorMessage.isNullOrBlank()) {
            ErrorNote(message = errorMessage)
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = { onLogin(email.trim(), password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
        ) {
            Text("Đăng nhập", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Chưa có tài khoản? ", style = MaterialTheme.typography.bodyMedium, color = SandDark)
            Text(
                "Đăng ký",
                style = MaterialTheme.typography.bodyMedium,
                color = MossGreen,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onGoToRegister)
            )
        }
    }
}

// ─── RegisterForm ─────────────────────────────────────────────────────────────

@Composable
internal fun RegisterForm(
    errorMessage: String?,
    onRegister: (String, String, String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Tạo tài khoản",
            style = MaterialTheme.typography.headlineSmall,
            color = SoilBrown,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Tham gia cùng hàng triệu shoppers 🌸",
            style = MaterialTheme.typography.bodyMedium,
            color = SandDark
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("👤  Tên hiển thị") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = botanicalOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("✉️  Email") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = botanicalOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("🔒  Mật khẩu") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = botanicalOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        if (!errorMessage.isNullOrBlank()) {
            ErrorNote(message = errorMessage)
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = { onRegister(name.trim(), email.trim(), password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
        ) {
            Text("Đăng ký", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Đã có tài khoản? ", style = MaterialTheme.typography.bodyMedium, color = SandDark)
            Text(
                "Đăng nhập",
                style = MaterialTheme.typography.bodyMedium,
                color = MossGreen,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onGoToLogin)
            )
        }
    }
}

// ─── HeroHeader (kept for backward compat) ───────────────────────────────────

@Composable
internal fun HeroHeader(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🌸", fontSize = 48.sp)
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, color = SoilBrown, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SandDark)
    }
}
