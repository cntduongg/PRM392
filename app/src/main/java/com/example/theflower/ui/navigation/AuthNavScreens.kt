package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown

// ─── AuthFlow ───────────────────────────────────────────────────────────────
// Gọi bởi: AppNavigation() khi !uiState.isLoggedIn

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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(PaperWhite)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            HeroHeader(
                title = "The Flower",
                subtitle = "Tặng hoa - tặng cả cảm xúc"
            )
            Spacer(modifier = Modifier.height(20.dp))

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
}

// ─── HeroHeader ─────────────────────────────────────────────────────────────

@Composable
internal fun HeroHeader(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🌸", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SandDark)
        }
    }
}

// ─── LoginForm ──────────────────────────────────────────────────────────────

@Composable
internal fun LoginForm(
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Đăng nhập", style = MaterialTheme.typography.titleLarge, color = SoilBrown)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                colors = botanicalOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = botanicalOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onLogin(email.trim(), password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Đăng nhập")
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorNote(message = errorMessage)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Chưa có tài khoản? Đăng ký",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoToRegister),
                style = MaterialTheme.typography.bodyMedium,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── RegisterForm ────────────────────────────────────────────────────────────

@Composable
internal fun RegisterForm(
    errorMessage: String?,
    onRegister: (String, String, String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tạo tài khoản", style = MaterialTheme.typography.titleLarge, color = SoilBrown)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên hiển thị") },
                singleLine = true,
                colors = botanicalOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                colors = botanicalOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = botanicalOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onRegister(name.trim(), email.trim(), password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Đăng ký")
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorNote(message = errorMessage)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Đã có tài khoản? Đăng nhập",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoToLogin),
                style = MaterialTheme.typography.bodyMedium,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}
