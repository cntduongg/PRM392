package com.example.theflower.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theflower.ui.components.NavTab
import com.example.theflower.domain.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Application-level ViewModel for managing UI state and navigation
 */
class AppViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()
    
    // ─────── Tab Navigation ───────────────────────────────────────────────────
    
    fun selectTab(tab: NavTab) {
        _uiState.update { it.copy(currentTab = tab) }
        when (tab) {
            NavTab.HOME -> navigateToScreen("home")
            NavTab.CATEGORY -> navigateToScreen("category")
            NavTab.CART -> navigateToScreen("cart")
            NavTab.PROFILE -> navigateToScreen("profile")
        }
    }
    
    // ─────── Screen Navigation ──────────────────────────────────────────────────
    
    fun navigateToScreen(screenName: String) {
        _uiState.update { it.copy(currentScreen = screenName) }
    }
    
    fun navigateToProductDetail(product: Product) {
        _uiState.update { 
            it.copy(
                currentScreen = "detail",
                selectedProduct = product
            )
        }
        _navigationEvent.value = NavigationEvent.NavigateToProductDetail(product)
    }
    
    fun navigateToCart() {
        _uiState.update { it.copy(currentScreen = "cart") }
        selectTab(NavTab.CART)
        _navigationEvent.value = NavigationEvent.NavigateToCart
    }
    
    fun navigateToPayment() {
        _uiState.update { it.copy(currentScreen = "payment") }
        _navigationEvent.value = NavigationEvent.NavigateToPayment
    }
    
    fun navigateToLogin() {
        _uiState.update { it.copy(currentScreen = "login", isLoggedIn = false) }
        _navigationEvent.value = NavigationEvent.NavigateToLogin
    }
    
    fun navigateToRegister() {
        _uiState.update { it.copy(currentScreen = "register") }
        _navigationEvent.value = NavigationEvent.NavigateToRegister
    }
    
    fun navigateToChat() {
        _uiState.update { it.copy(currentScreen = "chat") }
        _navigationEvent.value = NavigationEvent.NavigateToChat
    }
    
    fun navigateBack() {
        _navigationEvent.value = NavigationEvent.NavigateBack
        when (_uiState.value.currentScreen) {
            "detail" -> {
                _uiState.update { it.copy(currentScreen = "home", selectedProduct = null) }
                selectTab(NavTab.HOME)
            }
            else -> navigateToScreen("home")
        }
    }
    
    // ─────── Authentication ─────────────────────────────────────────────────────
    
    fun setLoggedIn(isLoggedIn: Boolean, userId: Int? = null, userName: String = "") {
        _uiState.update { 
            it.copy(
                isLoggedIn = isLoggedIn,
                userId = userId,
                userName = userName
            )
        }
        if (isLoggedIn) {
            selectTab(NavTab.HOME)
        } else {
            navigateToLogin()
        }
    }
    
    fun logout() {
        _uiState.update { 
            it.copy(
                isLoggedIn = false,
                userId = null,
                userName = ""
            )
        }
        navigateToLogin()
    }
    
    // ─────── Loading & Error ────────────────────────────────────────────────────
    
    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
    
    fun setError(message: String?) {
        _uiState.update { it.copy(errorMessage = message) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    // ─────── Action Helpers ─────────────────────────────────────────────────────
    
    fun handleAddToCart(product: Product) {
        viewModelScope.launch {
            // TODO: Call repository to add to cart
            navigateToCart()
        }
    }
    
    fun captureNavigationEvent() {
        _navigationEvent.value = null
    }
}
