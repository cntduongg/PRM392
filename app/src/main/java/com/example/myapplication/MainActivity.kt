package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Product
import com.example.myapplication.ui.components.BottomNavBar
import com.example.myapplication.ui.components.NavTab
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentTab = remember { mutableStateOf(NavTab.HOME) }
                val currentScreen = remember { mutableStateOf<String>("home") }
                val selectedProduct = remember { mutableStateOf<Product?>(null) }
                val isLoggedIn = remember { mutableStateOf(true) } // Default to logged in for demo

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        !isLoggedIn.value && currentScreen.value == "login" -> {
                            LoginScreen(
                                onLoginClick = { _, _ ->
                                    isLoggedIn.value = true
                                    currentTab.value = NavTab.HOME
                                    currentScreen.value = "home"
                                },
                                onRegisterClick = { currentScreen.value = "register" },
                                onForgotPasswordClick = {}
                            )
                        }
                        !isLoggedIn.value && currentScreen.value == "register" -> {
                            RegisterScreen(
                                onRegisterClick = { _, _, _ ->
                                    isLoggedIn.value = true
                                    currentTab.value = NavTab.HOME
                                    currentScreen.value = "home"
                                },
                                onLoginClick = { currentScreen.value = "login" }
                            )
                        }
                        isLoggedIn.value -> {
                            // Main app screens
                            when {
                                selectedProduct.value != null && currentScreen.value == "detail" -> {
                                    ProductDetailScreen(
                                        product = selectedProduct.value!!,
                                        onAddToCart = { product, stems, message ->
                                            currentScreen.value = "cart"
                                            selectedProduct.value = null
                                        },
                                        onBackClick = {
                                            selectedProduct.value = null
                                            currentScreen.value = "home"
                                        }
                                    )
                                }
                                currentScreen.value == "payment" -> {
                                    PaymentScreen(
                                        totalAmount = 599000,
                                        onPaymentSuccess = { currentScreen.value = "success" },
                                        onPaymentFailed = { currentScreen.value = "cancel" }
                                    )
                                }
                                currentScreen.value == "success" -> {
                                    PaymentSuccessScreen(
                                        onBackHome = {
                                            currentScreen.value = "home"
                                            currentTab.value = NavTab.HOME
                                        },
                                        onViewOrder = {}
                                    )
                                }
                                currentScreen.value == "cancel" -> {
                                    PaymentCancelScreen(
                                        onRetryPayment = { currentScreen.value = "payment" },
                                        onBackHome = {
                                            currentScreen.value = "home"
                                            currentTab.value = NavTab.HOME
                                        }
                                    )
                                }
                                currentScreen.value == "chat" -> {
                                    Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp)) {
                                        // Placeholder for chat screen
                                    }
                                }
                                currentScreen.value == "map" -> {
                                    MapScreen()
                                }
                                currentTab.value == NavTab.HOME && currentScreen.value != "detail" -> {
                                    HomeScreen(
                                        onProductClick = { product ->
                                            selectedProduct.value = product
                                            currentScreen.value = "detail"
                                        },
                                        onChatClick = { currentScreen.value = "chat" },
                                        onOccasionClick = {}
                                    )
                                }
                                currentTab.value == NavTab.CATEGORY -> {
                                    AllProductsScreen(
                                        onProductClick = { product ->
                                            selectedProduct.value = product
                                            currentScreen.value = "detail"
                                        }
                                    )
                                }
                                currentTab.value == NavTab.CART -> {
                                    CartScreen(
                                        onCheckout = { currentScreen.value = "payment" },
                                        onContinueShopping = { currentTab.value = NavTab.HOME }
                                    )
                                }
                                currentTab.value == NavTab.PROFILE -> {
                                    when {
                                        currentScreen.value == "dashboard" -> {
                                            DashboardScreen(
                                                onChatClick = { currentScreen.value = "chat" },
                                                onUserManagementClick = {}
                                            )
                                        }
                                        currentScreen.value == "notifications" -> {
                                            NotificationScreen()
                                        }
                                        else -> {
                                            DashboardScreen(
                                                onChatClick = { currentScreen.value = "chat" },
                                                onUserManagementClick = {}
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom navigation bar
                            if (currentTab.value != NavTab.PROFILE || currentScreen.value == "dashboard" || currentScreen.value == "notifications") {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    BottomNavBar(
                                        currentTab = currentTab.value,
                                        onTabClick = { newTab ->
                                            currentTab.value = newTab
                                            currentScreen.value = when (newTab) {
                                                NavTab.HOME -> "home"
                                                NavTab.CATEGORY -> "category"
                                                NavTab.CART -> "cart"
                                                NavTab.PROFILE -> "dashboard"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}