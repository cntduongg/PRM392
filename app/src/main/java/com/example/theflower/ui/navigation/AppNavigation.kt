package com.example.theflower.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.theflower.ui.viewmodels.AppViewModel
import com.example.theflower.ui.screens.*
import com.example.theflower.domain.models.Product

/**
 * Main Navigation Routing
 * Handles screen switching based on AppViewModel state
 */
@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        !uiState.isLoggedIn && uiState.currentScreen == "login" -> {
            LoginScreen(
                onLoginClick = { email, password ->
                    // TODO: Call viewModel.login(email, password)
                    viewModel.setLoggedIn(true, userId = 1, userName = "User")
                },
                onRegisterClick = { viewModel.navigateToRegister() },
                onForgotPasswordClick = {}
            )
        }
        
        !uiState.isLoggedIn && uiState.currentScreen == "register" -> {
            RegisterScreen(
                onRegisterClick = { name, email, password ->
                    // TODO: Call viewModel.register(name, email, password)
                    viewModel.setLoggedIn(true, userId = 1, userName = name)
                },
                onLoginClick = { viewModel.navigateToLogin() }
            )
        }
        
        uiState.isLoggedIn -> {
            when {
                uiState.selectedProduct != null && uiState.currentScreen == "detail" -> {
                    ProductDetailScreen(
                        product = uiState.selectedProduct!!,
                        onAddToCart = { product, stems, message ->
                            viewModel.navigateToCart()
                        },
                        onBackClick = { viewModel.navigateBack() }
                    )
                }
                
                uiState.currentScreen == "payment" -> {
                    PaymentScreen(
                        totalAmount = 599000,
                        onPaymentSuccess = { 
                            viewModel.navigateToScreen("success")
                        },
                        onPaymentFailed = { 
                            viewModel.navigateToScreen("cancel")
                        }
                    )
                }
                
                uiState.currentScreen == "success" -> {
                    PaymentSuccessScreen(
                        onBackHome = { 
                            viewModel.selectTab(com.example.theflower.ui.components.NavTab.HOME)
                        },
                        onViewOrder = {}
                    )
                }
                
                uiState.currentScreen == "cancel" -> {
                    PaymentCancelScreen(
                        onRetryPayment = { viewModel.navigateToPayment() },
                        onBackHome = { viewModel.selectTab(com.example.theflower.ui.components.NavTab.HOME) }
                    )
                }
                
                uiState.currentScreen == "cart" -> {
                    CartScreen(
                        onCheckout = { viewModel.navigateToPayment() },
                        onContinueShopping = { viewModel.selectTab(com.example.theflower.ui.components.NavTab.HOME) }
                    )
                }
                
                else -> {
                    // Main app layout with bottom navigation
                    MainAppLayout(
                        currentTab = uiState.currentTab,
                        onTabClick = { viewModel.selectTab(it) },
                        onProductClick = { viewModel.navigateToProductDetail(it) },
                        onChatClick = { viewModel.navigateToChat() },
                        onOccasionClick = { viewModel.navigateToScreen("occasion_$it") },
                        onCheckout = { viewModel.navigateToPayment() },
                        onContinueShopping = { viewModel.selectTab(com.example.theflower.ui.components.NavTab.HOME) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppLayout(
    currentTab: com.example.theflower.ui.components.NavTab,
    onTabClick: (com.example.theflower.ui.components.NavTab) -> Unit,
    onProductClick: (Product) -> Unit,
    onChatClick: () -> Unit,
    onOccasionClick: (String) -> Unit,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    // This would contain the actual layout with BottomNavBar
    // Import and use appropriate screens based on currentTab
    when (currentTab) {
        com.example.theflower.ui.components.NavTab.HOME -> {
            HomeScreen(
                onProductClick = onProductClick,
                onChatClick = onChatClick,
                onOccasionClick = onOccasionClick
            )
        }
        com.example.theflower.ui.components.NavTab.CATEGORY -> {
            AllProductsScreen(
                onProductClick = onProductClick
            )
        }
        com.example.theflower.ui.components.NavTab.CART -> {
            CartScreen(
                onCheckout = onCheckout,
                onContinueShopping = onContinueShopping
            )
        }
        com.example.theflower.ui.components.NavTab.PROFILE -> {
            // ProfileScreen would go here
            Box(modifier = Modifier)
        }
    }
}
