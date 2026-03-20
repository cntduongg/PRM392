package com.example.theflower.ui.viewmodels

import com.example.theflower.ui.components.NavTab
import com.example.theflower.domain.models.Product
import com.example.theflower.data.remote.dtos.CartDto
import com.example.theflower.data.remote.dtos.CategoryDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.data.remote.dtos.AdminUserDto

/**
 * Main Application UI State
 */
data class AppUiState(
    val currentTab: NavTab = NavTab.HOME,
    val currentScreen: String = "login",
    val selectedProduct: Product? = null,
    val selectedProductDto: ProductDto? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userId: String? = null,
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userAddress: String = "",
    val userRole: String = "USER",
    val accessToken: String = "",
    val categories: List<CategoryDto> = emptyList(),
    val products: List<ProductDto> = emptyList(),
    val searchQuery: String = "",
    val cart: CartDto? = null,
    val orders: List<OrderDto> = emptyList(),
    val adminUsers: List<AdminUserDto> = emptyList(),
    val checkoutAddress: String = ""
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
