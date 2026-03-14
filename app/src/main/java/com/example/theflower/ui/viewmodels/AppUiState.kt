package com.example.theflower.ui.viewmodels

import com.example.theflower.ui.components.NavTab
import com.example.theflower.domain.models.Product

/**
 * Main Application UI State
 */
data class AppUiState(
    val currentTab: NavTab = NavTab.HOME,
    val currentScreen: String = "home",
    val selectedProduct: Product? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userId: Int? = null,
    val userName: String = ""
)

/**
 * Navigation event
 */
sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    object NavigateToLogin : NavigationEvent()
    object NavigateToRegister : NavigationEvent()
    data class NavigateToProductDetail(val product: Product) : NavigationEvent()
    object NavigateToCart : NavigationEvent()
    object NavigateToPayment : NavigationEvent()
    object NavigateToChat : NavigationEvent()
    object NavigateBack : NavigationEvent()
}
