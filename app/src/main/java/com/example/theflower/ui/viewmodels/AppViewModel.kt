package com.example.theflower.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theflower.data.local.TokenManager
import com.example.theflower.data.remote.dtos.AddToCartRequest
import com.example.theflower.data.remote.dtos.CreateAdminUserRequest
import com.example.theflower.data.remote.dtos.CreateOrderRequest
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
import com.example.theflower.domain.repositories.IProductRepository
import com.example.theflower.domain.repositories.IUserRepository
import com.example.theflower.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val authRepository: IAuthRepository = DIContainer.getAuthRepository(),
    private val categoryRepository: ICategoryRepository = DIContainer.getCategoryRepository(),
    private val productRepository: IProductRepository = DIContainer.getProductRepository(),
    private val cartRepository: ICartRepository = DIContainer.getCartRepository(),
    private val orderRepository: IOrderRepository = DIContainer.getOrderRepository(),
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
        _uiState.update { it.copy(currentTab = tab) }
        when (tab) {
            NavTab.HOME -> navigateToScreen("home")
            NavTab.CATEGORY -> {
                navigateToScreen("category")
                if (_uiState.value.categories.isEmpty()) {
                    loadCategories()
                }
            }
            NavTab.CART -> navigateToScreen("cart")
            NavTab.PROFILE -> navigateToScreen("profile")
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
    }

    fun navigateToCart() {
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
                _uiState.update { it.copy(currentScreen = "home", selectedProduct = null, selectedProductDto = null) }
                selectTab(NavTab.HOME)
            }
            "orders" -> navigateToScreen("profile")
            "admin_dashboard" -> navigateToScreen("profile")
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
            setError("Vui lòng nhập email và mật khẩu.")
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
                    tokenManager.saveUserInfo(response.userId.toString(), userEmail)
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
                    setError(it.message ?: "Đăng nhập thất bại")
                }
            setLoading(false)
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            setError("Thông tin đăng ký chưa hợp lệ (mật khẩu tối thiểu 6 ký tự).")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            clearError()
            val request = RegisterRequest(
                username = name,
                email = email,
                password = password,
                fullName = name
            )
            authRepository.register(request)
                .onSuccess { response ->
                    val accessToken = response.accessToken.orEmpty()
                    val refreshToken = response.refreshToken.orEmpty()
                    val userEmail = response.email.orEmpty()
                    val userName = response.fullName.orEmpty().ifBlank { response.username.orEmpty() }
                    val userRole = response.role.orEmpty().ifBlank { "USER" }

                    tokenManager.saveTokens(accessToken, refreshToken, response.expiresIn)
                    tokenManager.saveUserInfo(response.userId.toString(), userEmail)
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
                    setError(it.message ?: "Đăng ký thất bại")
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
                    setError(it.message ?: "Không thể tải sản phẩm")
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
                    setError(it.message ?: "Không thể tải danh mục")
                }
        }
    }

    fun loadUserProfile(silent: Boolean = false) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        viewModelScope.launch {
            userRepository.getUserProfile(token)
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
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
                        setError(it.message ?: "Không thể tải hồ sơ người dùng")
                    }
                }
        }
    }

    fun updateProfile(fullName: String, phoneNumber: String, address: String) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) {
            setError("Bạn cần đăng nhập để cập nhật hồ sơ.")
            return
        }

        if (fullName.isBlank()) {
            setError("Họ tên không được để trống.")
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
            userRepository.updateUserProfile(token, request)
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
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
                    setError(it.message ?: "Cập nhật hồ sơ thất bại")
                }
            setLoading(false)
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) {
            setError("Bạn cần đăng nhập để đổi mật khẩu.")
            return
        }

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            setError("Vui lòng nhập đầy đủ thông tin đổi mật khẩu.")
            return
        }

        if (newPassword.length < 6) {
            setError("Mật khẩu mới phải có ít nhất 6 ký tự.")
            return
        }

        if (newPassword != confirmPassword) {
            setError("Xác nhận mật khẩu không khớp.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            clearError()
            userRepository.changePassword(token, currentPassword, newPassword)
                .onSuccess {
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Đổi mật khẩu thất bại")
                }
            setLoading(false)
        }
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) {
            setError("Bạn cần đăng nhập để thêm vào giỏ hàng.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            cartRepository.addToCart(token, AddToCartRequest(productId = productId, quantity = quantity))
                .onSuccess { cart ->
                    _uiState.update { it.copy(cart = cart) }
                }
                .onFailure {
                    setError(it.message ?: "Không thể thêm sản phẩm vào giỏ hàng")
                }
            setLoading(false)
        }
    }

    fun loadCart() {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        viewModelScope.launch {
            cartRepository.getCart(token)
                .onSuccess { cart ->
                    _uiState.update { it.copy(cart = cart) }
                }
                .onFailure {
                    setError(it.message ?: "Không thể tải giỏ hàng")
                }
        }
    }

    fun removeCartItem(itemId: Int) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        viewModelScope.launch {
            setLoading(true)
            cartRepository.removeFromCart(token, itemId)
                .onSuccess { cart -> _uiState.update { it.copy(cart = cart) } }
                .onFailure { setError(it.message ?: "Không thể xóa sản phẩm") }
            setLoading(false)
        }
    }

    fun createOrder() {
        val token = _uiState.value.accessToken
        val address = _uiState.value.checkoutAddress
        if (token.isBlank()) {
            setError("Bạn cần đăng nhập để đặt hàng.")
            return
        }
        if (address.isBlank()) {
            setError("Vui lòng nhập địa chỉ giao hàng.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = CreateOrderRequest(
                paymentMethod = "COD",
                billingAddress = address
            )
            orderRepository.createOrder(token, request)
                .onSuccess {
                    loadOrders()
                    loadCart()
                    _uiState.update { it.copy(checkoutAddress = "") }
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Không thể tạo đơn hàng")
                }
            setLoading(false)
        }
    }

    fun loadOrders() {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        viewModelScope.launch {
            setLoading(true)
            orderRepository.getOrders(token)
                .onSuccess { response -> _uiState.update { it.copy(orders = response.items.orEmpty()) } }
                .onFailure { setError(it.message ?: "Không thể tải đơn hàng") }
            setLoading(false)
        }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().orEmpty()
            if (token.isNotBlank()) {
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
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
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.getUsers(token)
                .onSuccess { users -> _uiState.update { it.copy(adminUsers = users) } }
                .onFailure { setError(it.message ?: "Không thể tải danh sách người dùng") }
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
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        if (username.isBlank() || email.isBlank() || password.length < 6) {
            setError("Thông tin user không hợp lệ.")
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
            adminRepository.createUser(token, request)
                .onSuccess {
                    loadAdminUsers()
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Tạo user thất bại")
                }
            setLoading(false)
        }
    }

    fun updateAdminUser(
        userId: Int,
        username: String,
        email: String,
        phoneNumber: String,
        address: String,
        role: String,
        password: String
    ) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        if (username.isBlank() || email.isBlank()) {
            setError("Thiếu thông tin để cập nhật user.")
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
            adminRepository.updateUser(token, userId, request)
                .onSuccess {
                    loadAdminUsers()
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Cập nhật user thất bại")
                }
            setLoading(false)
        }
    }

    fun deleteAdminUser(userId: Int) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.deleteUser(token, userId)
                .onSuccess {
                    loadAdminUsers()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Xóa user thất bại") }
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
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        val parsedPrice = price.toDoubleOrNull()
        if (productName.isBlank() || parsedPrice == null) {
            setError("Thông tin sản phẩm không hợp lệ")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = ProductUpsertRequest(
                productName = productName,
                price = parsedPrice,
                categoryId = categoryId.toIntOrNull(),
                stockQuantity = stock.toIntOrNull() ?: 0,
                briefDescription = briefDescription.ifBlank { null },
                fullDescription = fullDescription.ifBlank { null },
                technicalSpecifications = technicalSpecifications.ifBlank { null },
                imageUrl = imageUrl.ifBlank { null }
            )
            adminRepository.createProduct(token, request)
                .onSuccess {
                    loadProducts()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Tạo sản phẩm thất bại") }
            setLoading(false)
        }
    }

    fun updateAdminProduct(
        productId: Int,
        productName: String,
        price: String,
        categoryId: String,
        stock: String,
        briefDescription: String,
        fullDescription: String,
        technicalSpecifications: String,
        imageUrl: String
    ) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        val parsedPrice = price.toDoubleOrNull()
        if (productName.isBlank() || parsedPrice == null) {
            setError("Thông tin sản phẩm không hợp lệ")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val request = UpdateProductRequest(
                productId = productId,
                productName = productName,
                price = parsedPrice,
                categoryId = categoryId.toIntOrNull(),
                stockQuantity = stock.toIntOrNull() ?: 0,
                briefDescription = briefDescription.ifBlank { null },
                fullDescription = fullDescription.ifBlank { null },
                technicalSpecifications = technicalSpecifications.ifBlank { null },
                imageUrl = imageUrl.ifBlank { null }
            )
            adminRepository.updateProduct(token, productId, request)
                .onSuccess {
                    loadProducts()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Cập nhật sản phẩm thất bại") }
            setLoading(false)
        }
    }

    fun deleteAdminProduct(productId: Int) {
        val token = _uiState.value.accessToken
        if (token.isBlank()) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.deleteProduct(token, productId)
                .onSuccess {
                    loadProducts()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Xóa sản phẩm thất bại") }
            setLoading(false)
        }
    }
}
