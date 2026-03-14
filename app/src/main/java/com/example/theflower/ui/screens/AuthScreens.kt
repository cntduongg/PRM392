package com.example.theflower.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.theflower.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo/Icon
        Text(
            text = "🌸",
            fontSize = MaterialTheme.typography.displayLarge.fontSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "The Flower",
            style = MaterialTheme.typography.headlineMedium,
            color = SoilBrown
        )

        Text(
            text = "Tặng hoa — tặng cả cảm xúc",
            style = MaterialTheme.typography.bodyMedium,
            color = MossGreen
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Login form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.titleLarge,
                color = SoilBrown
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email field
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = { Text("Email") },
                leadingIcon = { Text("✉️") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Sand,
                    unfocusedContainerColor = Sand,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password field
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = { Text("Mật khẩu") },
                leadingIcon = { Text("🔒") },
                trailingIcon = {
                    Text(
                        text = if (showPassword.value) "👁️" else "👁️‍🗨️",
                        modifier = Modifier.clickable { showPassword.value = !showPassword.value }
                    )
                },
                visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Sand,
                    unfocusedContainerColor = Sand,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot password link
            Text(
                text = "Quên mật khẩu?",
                style = MaterialTheme.typography.labelSmall,
                color = MossGreen,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(onClick = onForgotPasswordClick)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = { onLoginClick(email.value, password.value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Đăng nhập",
                    style = MaterialTheme.typography.titleMedium,
                    color = PaperWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Divider(modifier = Modifier.weight(1f), color = SandDark)
                Text("hoặc", style = MaterialTheme.typography.bodySmall, color = SandDark)
                Divider(modifier = Modifier.weight(1f), color = SandDark)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social login buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("f", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("G", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Register link
        Text(
            text = "Chưa có tài khoản? Đăng ký ngay",
            style = MaterialTheme.typography.bodyMedium,
            color = SoilBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onRegisterClick)
                .padding(16.dp)
        )
    }
}

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    val agreeToTerms = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }

        item {
            Text(
                text = "🌸",
                fontSize = MaterialTheme.typography.displayLarge.fontSize
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tạo tài khoản",
                style = MaterialTheme.typography.headlineMedium,
                color = SoilBrown
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Name field
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Họ tên") },
                    leadingIcon = { Text("👤") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email field
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Email") },
                    leadingIcon = { Text("✉️") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Mật khẩu") },
                    leadingIcon = { Text("🔒") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm password field
                TextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Xác nhận mật khẩu") },
                    leadingIcon = { Text("🔒") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Terms checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = agreeToTerms.value,
                        onCheckedChange = { agreeToTerms.value = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MossGreen,
                            uncheckedColor = SandDark
                        )
                    )
                    Text(
                        text = "Tôi đồng ý với điều khoản sử dụng",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoilBrown
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Register button
                Button(
                    onClick = { onRegisterClick(name.value, email.value, password.value) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tạo tài khoản",
                        style = MaterialTheme.typography.titleMedium,
                        color = PaperWhite
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login link
                Text(
                    text = "Đã có tài khoản? Đăng nhập tại đây",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoilBrown,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onLoginClick)
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
