package com.example.theflower.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theflower.data.local.TokenManager
import com.example.theflower.data.remote.dtos.AddToCartRequest
import com.example.theflower.data.remote.dtos.CreateAdminUserRequest
import com.example.theflower.data.remote.dtos.CreateOrderRequest
import com.example.theflower.data.remote.dtos.CreatePaymentRequest
import com.example.theflower.data.remote.dtos.ChangeUserPasswordDto
import com.example.theflower.data.remote.dtos.CartDto
import com.example.theflower.data.remote.dtos.CategoryDto
import com.example.theflower.data.remote.dtos.LoginRequest
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.data.remote.dtos.ProductUpsertRequest
import com.example.theflower.data.remote.dtos.RegisterRequest
import com.example.theflower.data.remote.dtos.UpdateAdminUserRequest
import com.example.theflower.data.remote.dtos.UpdateProfileRequest
import com.example.theflower.data.remote.dtos.UpdateProductRequest
import com.example.theflower.di.DIContainer
import com.example.theflower.domain.repositories.IAdminRepository
import com.example.theflower.domain.models.Product
import com.example.theflower.domain.repositories.IAuthRepository
import com.example.theflower.domain.repositories.ICategoryRepository
import com.example.theflower.domain.repositories.ICartRepository
import com.example.theflower.domain.repositories.IOrderRepository
import com.example.theflower.domain.repositories.IPaymentRepository
import com.example.theflower.domain.repositories.IProductRepository
import com.example.theflower.domain.repositories.IUserRepository
import com.example.theflower.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AppViewModel(
    private val authRepository: IAuthRepository = DIContainer.getAuthRepository(),
    private val categoryRepository: ICategoryRepository = DIContainer.getCategoryRepository(),
    private val productRepository: IProductRepository = DIContainer.getProductRepository(),
    private val cartRepository: ICartRepository = DIContainer.getCartRepository(),
    private val orderRepository: IOrderRepository = DIContainer.getOrderRepository(),
    private val paymentRepository: IPaymentRepository = DIContainer.getPaymentRepository(),
    private val adminRepository: IAdminRepository = DIContainer.getAdminRepository(),
    private val userRepository: IUserRepository = DIContainer.getUserRepository(),
    private val tokenManager: TokenManager = DIContainer.getTokenManager()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        restoreSession()
        loadCategories()
        loadProducts()
    }

    fun selectTab(tab: NavTab) {
        if ((tab == NavTab.PROFILE || tab == NavTab.CART) && !_uiState.value.isLoggedIn) {
            navigateToLogin()
            return
        }

        _uiState.update {
            it.copy(
                currentTab = tab,
                selectedCategory = if (tab == NavTab.CATEGORY) it.selectedCategory else null
            )
        }
        when (tab) {
            NavTab.HOME -> {
                _uiState.update { it.copy(selectedCategory = null) }
                navigateToScreen("home")
            }
            NavTab.CATEGORY -> {
                _uiState.update { it.copy(selectedCategory = null) }
                navigateToScreen("category")
                if (_uiState.value.categories.isEmpty()) {
                    loadCategories()
                }
            }
            NavTab.CART -> {
                _uiState.update { it.copy(selectedCategory = null) }
                navigateToScreen("cart")
                loadCart()
            }
            NavTab.PROFILE -> {
                _uiState.update { it.copy(selectedCategory = null) }
                navigateToScreen("profile")
            }
        }
    }

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

    fun navigateToProductDetail(product: ProductDto) {
        _uiState.update {
            it.copy(
                currentScreen = "detail",
                selectedProductDto = product
            )
        }
        loadProductDetail(product.id)
    }

    fun navigateToCategoryDetail(category: CategoryDto) {
        _uiState.update {
            it.copy(
                currentTab = NavTab.CATEGORY,
                currentScreen = "category_detail",
                selectedCategory = category
            )
        }
    }

    fun navigateToCart() {
        if (!_uiState.value.isLoggedIn) {
            navigateToLogin()
            return
        }
        _uiState.update { it.copy(currentScreen = "cart") }
        selectTab(NavTab.CART)
        loadCart()
        _navigationEvent.value = NavigationEvent.NavigateToCart
    }

    fun navigateToLogin() {
        _uiState.update { it.copy(currentScreen = "login", isLoggedIn = false) }
        _navigationEvent.value = NavigationEvent.NavigateToLogin
    }

    fun navigateToRegister() {
        _uiState.update { it.copy(currentScreen = "register") }
        _navigationEvent.value = NavigationEvent.NavigateToRegister
    }

    fun navigateBack() {
        _navigationEvent.value = NavigationEvent.NavigateBack
        when (_uiState.value.currentScreen) {
            "detail" -> {
                if (_uiState.value.currentTab == NavTab.CATEGORY && _uiState.value.selectedCategory != null) {
                    _uiState.update {
                        it.copy(
                            currentScreen = "category_detail",
                            selectedProduct = null,
                            selectedProductDto = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            currentScreen = "home",
                            selectedProduct = null,
                            selectedProductDto = null,
                            selectedCategory = null
                        )
                    }
                    selectTab(NavTab.HOME)
                }
            }
            "orders" -> navigateToScreen("profile")
            "chat" -> navigateToScreen("home")
            "admin_dashboard" -> navigateToScreen("profile")
            "category_detail" -> {
                _uiState.update { it.copy(currentScreen = "category", selectedCategory = null) }
            }
            else -> navigateToScreen("home")
        }
    }

    fun navigateToOrders() {
        _uiState.update { it.copy(currentScreen = "orders") }
        loadOrders()
    }

    fun navigateToAdminDashboard() {
        _uiState.update { it.copy(currentScreen = "admin_dashboard") }
        loadAdminUsers()
        loadProducts()
        loadOrders()
    }

    fun navigateToChat() {
        if (!_uiState.value.isLoggedIn) {
            navigateToLogin()
            return
        }
        _uiState.update { it.copy(currentScreen = "chat") }
    }


    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setError(message: String?) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateCheckoutAddress(address: String) {
        _uiState.update { it.copy(checkoutAddress = address) }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            setError("Vui lÃ²ng nháº­p email vÃ  máº­t kháº©u.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            clearError()
            authRepository.login(LoginRequest(email = email, password = password))
                .onSuccess { response ->
                    val accessToken = response.accessToken.orEmpty()
                    val refreshToken = response.refreshToken.orEmpty()
                    val userEmail = response.email.orEmpty()
                    val userName = response.fullName.orEmpty().ifBlank { response.username.orEmpty() }
                    val userRole = response.role.orEmpty().ifBlank { "USER" }
                    val isAdmin = userRole.equals("ADMIN", ignoreCase = true)
                    val targetScreen = if (isAdmin) "admin_dashboard" else "home"
                    val targetTab = if (isAdmin) NavTab.PROFILE else NavTab.HOME

                    tokenManager.saveTokens(accessToken, refreshToken, response.expiresIn)
                    tokenManager.saveUserInfo(
                        userId = response.userId,
                        email = userEmail,
                        userName = userName,
                        role = userRole
                    )
                    _uiState.update {
                        it.copy(
                            isLoggedIn = true,
                            userId = response.userId,
                            userName = userName,
                            userEmail = userEmail,
                            userRole = userRole,
                            accessToken = accessToken,
                            currentScreen = targetScreen,
                            currentTab = targetTab
                        )
                    }
                    loadUserProfile(silent = true)
                    loadProducts()
                    if (isAdmin) {
                        loadAdminUsers()
                    } else {
                        loadCart()
                    }
                }
                .onFailure {
                    setError(it.message ?: "ÄÄng nháº­p tháº¥t báº¡i")
                }
            setLoading(false)
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            setError("ThÃ´ng tin ÄÄng kÃ½ chÆ°a há»£p lá» (máº­t kháº©u tá»i thiá»u 6 kÃ½ tá»±).")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            clearError()
            val request = RegisterRequest(
                username = name,
                email = email,
                password = password
            )
            authRepository.register(request)
                .onSuccess { response ->
                    val accessToken = response.accessToken.orEmpty()
                    val refreshToken = response.refreshToken.orEmpty()
                    val userEmail = response.email.orEmpty()
                    val userName = response.fullName.orEmpty().ifBlank { response.username.orEmpty() }
                    val userRole = response.role.orEmpty().ifBlank { "USER" }

                    tokenManager.saveTokens(accessToken, refreshToken, response.expiresIn)
                    tokenManager.saveUserInfo(
                        userId = response.userId,
                        email = userEmail,
                        userName = userName,
                        role = userRole
                    )
                    _uiState.update {
                        it.copy(
                            isLoggedIn = true,
                            userId = response.userId,
                            userName = userName,
                            userEmail = userEmail,
                            userRole = userRole,
                            accessToken = accessToken,
                            currentScreen = "home",
                            currentTab = NavTab.HOME
                        )
                    }
                    loadUserProfile(silent = true)
                    loadProducts()
                    loadCart()
                }
                .onFailure {
                    setError(it.message ?: "ÄÄng kÃ½ tháº¥t báº¡i")
                }
            setLoading(false)
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    userId = null,
                    userName = "",
                    userEmail = "",
                    userPhone = "",
                    userAddress = "",
                    userRole = "USER",
                    accessToken = "",
                    categories = emptyList(),
                    cart = null,
                    orders = emptyList(),
                    pendingOrder = null,
                    selectedCategory = null,
                    currentScreen = "login",
                    currentTab = NavTab.HOME
                )
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            setLoading(true)
            clearError()
            productRepository.getProducts(pageNumber = 1, pageSize = 50)
                .onSuccess { response ->
                    _uiState.update { it.copy(products = response.items.orEmpty()) }
                }
                .onFailure {
                    setError(it.message ?: "KhÃ´ng thá» táº£i sáº£n pháº©m")
                }
            setLoading(false)
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
                .onFailure {
                    setError(it.message ?: "KhÃ´ng thá» táº£i danh má»¥c")
                }
        }
    }

    fun loadUserProfile(silent: Boolean = false) {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            userRepository.getUserProfile()
                .onSuccess { profile ->
                    tokenManager.saveUserInfo(
                        userId = profile.id,
                        email = profile.email,
                        userName = profile.fullName.ifBlank { profile.username },
                        phoneNumber = profile.phoneNumber.orEmpty(),
                        address = profile.address.orEmpty(),
                        role = profile.role
                    )
                    _uiState.update {
                        it.copy(
                            userId = profile.id,
                            userName = profile.fullName.ifBlank { profile.username },
                            userEmail = profile.email,
                            userPhone = profile.phoneNumber.orEmpty(),
                            userAddress = profile.address.orEmpty(),
                            userRole = profile.role
                        )
                    }
                }
                .onFailure {
                    if (!silent) {
                        setError(it.message ?: "KhÃ´ng thá» táº£i há» sÆ¡ ngÆ°á»i dÃ¹ng")
                    }
                }
        }
    }

    fun updateProfile(fullName: String, phoneNumber: String, address: String) {
        if (!_uiState.value.isLoggedIn) {
            setError("Báº¡n cáº§n ÄÄng nháº­p Äá» cáº­p nháº­t há» sÆ¡.")
            return
        }

        if (fullName.isBlank()) {
            setError("Há» tÃªn khÃ´ng ÄÆ°á»£c Äá» trá»ng.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            clearError()
            val request = UpdateProfileRequest(
                fullName = fullName,
                phoneNumber = phoneNumber,
                address = address
            )
            userRepository.updateUserProfile(request)
                .onSuccess { profile ->
                    tokenManager.saveUserInfo(
                        userId = profile.id,
                        email = profile.email,
                        userName = profile.fullName.ifBlank { profile.username },
                        phoneNumber = profile.phoneNumber.orEmpty(),
                        address = profile.address.orEmpty(),
                        role = profile.role
                    )
                    _uiState.update {
                        it.copy(
                            userId = profile.id,
                            userName = profile.fullName.ifBlank { profile.username },
                            userEmail = profile.email,
                            userPhone = profile.phoneNumber.orEmpty(),
                            userAddress = profile.address.orEmpty(),
                            userRole = profile.role
                        )
                    }
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Cáº­p nháº­t há» sÆ¡ tháº¥t báº¡i")
                }
            setLoading(false)
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (!_uiState.value.isLoggedIn) {
            setError("Báº¡n cáº§n ÄÄng nháº­p Äá» Äá»i máº­t kháº©u.")
            return
        }

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            setError("Vui lÃ²ng nháº­p Äáº§y Äá»§ thÃ´ng tin Äá»i máº­t kháº©u.")
            return
        }

        if (newPassword.length < 6) {
            setError("Máº­t kháº©u má»i pháº£i cÃ³ Ã­t nháº¥t 6 kÃ½ tá»±.")
            return
        }

        if (newPassword != confirmPassword) {
            setError("XÃ¡c nháº­n máº­t kháº©u khÃ´ng khá»p.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            clearError()
            userRepository.changePassword(
                ChangeUserPasswordDto(
                    oldPassword = currentPassword,
                    newPassword = newPassword
                )
            )
                .onSuccess {
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Äá»i máº­t kháº©u tháº¥t báº¡i")
                }
            setLoading(false)
        }
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        if (!_uiState.value.isLoggedIn) {
            clearError()
            navigateToLogin()
            return
        }
        viewModelScope.launch {
            setLoading(true)
            cartRepository.addToCart(AddToCartRequest(productId = productId, quantity = quantity))
                .onSuccess { cart ->
                    _uiState.update { it.copy(cart = cart) }
                    loadCart()
                }
                .onFailure {
                    setError(it.message ?: "Khong the them san pham vao gio hang")
                }
            setLoading(false)
        }
    }

    fun loadCart() {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            cartRepository.getCart()
                .onSuccess { cart ->
                    _uiState.update { it.copy(cart = cart) }
                }
                .onFailure {
                    if (it.message?.contains("Not found", ignoreCase = true) == true ||
                        it.message?.contains("404", ignoreCase = true) == true
                    ) {
                        _uiState.update { state -> state.copy(cart = CartDto()) }
                    } else {
                        setError(it.message ?: "Khong the tai gio hang")
                    }
                }
        }
    }

    fun removeCartItem(itemId: String) {
        viewModelScope.launch {
            setLoading(true)
            cartRepository.removeFromCart(itemId)
                .onSuccess { cart -> _uiState.update { it.copy(cart = cart) } }
                .onFailure { setError(it.message ?: "Khong the xoa san pham") }
            setLoading(false)
        }
    }

    private fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            productRepository.getProductDetail(productId)
                .onSuccess { product ->
                    _uiState.update { it.copy(selectedProductDto = product) }
                }
                .onFailure {
                    setError(it.message ?: "Khong the tai chi tiet san pham")
                }
        }
    }

    fun updateCartItemQuantity(itemId: String, quantity: Int) {
        val safeQuantity = quantity.coerceAtLeast(1)
        viewModelScope.launch {
            setLoading(true)
            cartRepository.updateCartItem(itemId, safeQuantity)
                .onSuccess { cart -> _uiState.update { it.copy(cart = cart) } }
                .onFailure { setError(it.message ?: "Khong the cap nhat so luong san pham") }
            setLoading(false)
        }
    }

    fun clearCart() {
        if (!_uiState.value.isLoggedIn) {
            navigateToLogin()
            return
        }

        viewModelScope.launch {
            setLoading(true)
            cartRepository.clearCart()
                .onSuccess {
                    loadCart()
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Khong the xoa gio hang")
                }
            setLoading(false)
        }
    }

    fun createOrder() {
        val address = _uiState.value.checkoutAddress
        if (address.isBlank()) {
            setError("Vui long nhap dia chi giao hang.")
            return
        }

        if (!_uiState.value.isLoggedIn) {
            navigateToLogin()
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = CreateOrderRequest(
                paymentMethod = "COD",
                billingAddress = address
            )
            orderRepository.createOrder(request)
                .onSuccess { order ->
                    loadOrders()
                    loadCart()
                    _uiState.update {
                        it.copy(
                            cart = null,
                            checkoutAddress = "",
                            pendingOrder = order
                        )
                    }
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Khong the tao don hang")
                }
            setLoading(false)
        }
    }

    fun createPaymentForPendingOrder() {
        val order = _uiState.value.pendingOrder
        if (!_uiState.value.isLoggedIn || order == null) {
            setError("Chua co don hang de thanh toan.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            paymentRepository.createPayment(
                CreatePaymentRequest(
                    orderId = order.id,
                    amount = order.totalPrice.roundToInt(),
                    paymentMethod = order.paymentMethod.ifBlank { "COD" }
                )
            )
                .onSuccess {
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Khong the tao thanh toan cho don hang ${order.id}")
                }
            setLoading(false)
        }
    }

    fun loadOrders() {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            setLoading(true)
            orderRepository.getOrders()
                .onSuccess { response -> _uiState.update { it.copy(orders = response.items.orEmpty()) } }
                .onFailure { setError(it.message ?: "Khong the tai don hang") }
            setLoading(false)
        }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().orEmpty()
            if (token.isNotBlank()) {
                val storedUserId = tokenManager.getUserId().orEmpty()
                val storedUserEmail = tokenManager.getUserEmail().orEmpty()
                val storedUserName = tokenManager.getUserName().orEmpty()
                val storedUserPhone = tokenManager.getUserPhone().orEmpty()
                val storedUserAddress = tokenManager.getUserAddress().orEmpty()
                val storedUserRole = tokenManager.getUserRole().orEmpty().ifBlank { "USER" }

                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        userId = storedUserId.ifBlank { null },
                        userName = storedUserName,
                        userEmail = storedUserEmail,
                        userPhone = storedUserPhone,
                        userAddress = storedUserAddress,
                        userRole = storedUserRole,
                        accessToken = token,
                        currentScreen = "home",
                        currentTab = NavTab.HOME
                    )
                }
                loadUserProfile(silent = true)
                loadCart()
                loadOrders()
            }
        }
    }

    fun captureNavigationEvent() {
        _navigationEvent.value = null
    }

    fun loadAdminUsers() {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.getUsers()
                .onSuccess { users -> _uiState.update { it.copy(adminUsers = users) } }
                .onFailure { setError(it.message ?: "KhÃ´ng thá» táº£i danh sÃ¡ch ngÆ°á»i dÃ¹ng") }
            setLoading(false)
        }
    }

    fun createAdminUser(
        username: String,
        email: String,
        password: String,
        phoneNumber: String,
        address: String,
        role: String
    ) {
        if (!_uiState.value.isLoggedIn) return

        if (username.isBlank() || email.isBlank() || password.length < 6) {
            setError("ThÃ´ng tin user khÃ´ng há»£p lá».")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = CreateAdminUserRequest(
                username = username,
                email = email,
                password = password,
                phoneNumber = phoneNumber.ifBlank { null },
                address = address.ifBlank { null },
                role = role.ifBlank { "Customer" }
            )
            adminRepository.createUser(request)
                .onSuccess {
                    loadAdminUsers()
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Táº¡o user tháº¥t báº¡i")
                }
            setLoading(false)
        }
    }

    fun updateAdminUser(
        userId: String,
        username: String,
        email: String,
        phoneNumber: String,
        address: String,
        role: String,
        password: String
    ) {
        if (!_uiState.value.isLoggedIn) return

        if (username.isBlank() || email.isBlank()) {
            setError("Thiáº¿u thÃ´ng tin Äá» cáº­p nháº­t user.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = UpdateAdminUserRequest(
                username = username,
                email = email,
                phoneNumber = phoneNumber.ifBlank { null },
                address = address.ifBlank { null },
                role = role.ifBlank { "Customer" },
                password = password.ifBlank { null }
            )
            adminRepository.updateUser(userId, request)
                .onSuccess {
                    loadAdminUsers()
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Cáº­p nháº­t user tháº¥t báº¡i")
                }
            setLoading(false)
        }
    }

    fun deleteAdminUser(userId: String) {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.deleteUser(userId)
                .onSuccess {
                    loadAdminUsers()
                    clearError()
                }
                .onFailure { setError(it.message ?: "XÃ³a user tháº¥t báº¡i") }
            setLoading(false)
        }
    }

    fun createAdminProduct(
        productName: String,
        price: String,
        categoryId: String,
        stock: String,
        briefDescription: String,
        fullDescription: String,
        technicalSpecifications: String,
        imageUrl: String
    ) {
        if (!_uiState.value.isLoggedIn) return

        val parsedPrice = price.toDoubleOrNull()
        if (productName.isBlank() || parsedPrice == null) {
            setError("ThÃ´ng tin sáº£n pháº©m khÃ´ng há»£p lá»")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = ProductUpsertRequest(
                productName = productName,
                price = parsedPrice,
                categoryId = categoryId.ifBlank { null },
                stockQuantity = stock.toIntOrNull() ?: 0,
                briefDescription = briefDescription.ifBlank { null },
                fullDescription = fullDescription.ifBlank { null },
                technicalSpecifications = technicalSpecifications.ifBlank { null },
                imageUrl = imageUrl.ifBlank { null }
            )
            adminRepository.createProduct(request)
                .onSuccess {
                    loadProducts()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Táº¡o sáº£n pháº©m tháº¥t báº¡i") }
            setLoading(false)
        }
    }

    fun updateAdminProduct(
        productId: String,
        productName: String,
        price: String,
        categoryId: String,
        stock: String,
        briefDescription: String,
        fullDescription: String,
        technicalSpecifications: String,
        imageUrl: String
    ) {
        if (!_uiState.value.isLoggedIn) return

        val parsedPrice = price.toDoubleOrNull()
        if (productName.isBlank() || parsedPrice == null) {
            setError("ThÃ´ng tin sáº£n pháº©m khÃ´ng há»£p lá»")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = UpdateProductRequest(
                productId = productId,
                productName = productName,
                price = parsedPrice,
                categoryId = categoryId.ifBlank { null },
                stockQuantity = stock.toIntOrNull() ?: 0,
                briefDescription = briefDescription.ifBlank { null },
                fullDescription = fullDescription.ifBlank { null },
                technicalSpecifications = technicalSpecifications.ifBlank { null },
                imageUrl = imageUrl.ifBlank { null }
            )
            adminRepository.updateProduct(productId, request)
                .onSuccess {
                    loadProducts()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Cáº­p nháº­t sáº£n pháº©m tháº¥t báº¡i") }
            setLoading(false)
        }
    }

    fun deleteAdminProduct(productId: String) {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.deleteProduct(productId)
                .onSuccess {
                    loadProducts()
                    clearError()
                }
                .onFailure { setError(it.message ?: "XÃ³a sáº£n pháº©m tháº¥t báº¡i") }
            setLoading(false)
        }
    }
}
