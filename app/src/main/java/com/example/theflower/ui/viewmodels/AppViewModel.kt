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

    private suspend fun resolveAccessToken(): String {
        val stateToken = _uiState.value.accessToken
        if (stateToken.isNotBlank()) return stateToken

        val storedToken = tokenManager.getAccessToken().orEmpty()
        if (storedToken.isNotBlank()) {
            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    accessToken = storedToken
                )
            }
        }
        return storedToken
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
            userRepository.changePassword(
                token,
                ChangeUserPasswordDto(
                    oldPassword = currentPassword,
                    newPassword = newPassword
                )
            )
                .onSuccess {
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Đổi mật khẩu thất bại")
                }
            setLoading(false)
        }
    }
    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            val token = resolveAccessToken()
            if (token.isBlank() || !_uiState.value.isLoggedIn) {
                clearError()
                navigateToLogin()
                return@launch
            }
            setLoading(true)
            cartRepository.addToCart(token, AddToCartRequest(productId = productId, quantity = quantity))
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
        viewModelScope.launch {
            val token = resolveAccessToken()
            if (token.isBlank()) return@launch

            cartRepository.getCart(token)
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
            val token = resolveAccessToken()
            if (token.isBlank()) return@launch

            setLoading(true)
            cartRepository.removeFromCart(token, itemId)
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
            val token = resolveAccessToken()
            if (token.isBlank()) return@launch

            setLoading(true)
            cartRepository.updateCartItem(token, itemId, safeQuantity)
                .onSuccess { cart -> _uiState.update { it.copy(cart = cart) } }
                .onFailure { setError(it.message ?: "Khong the cap nhat so luong san pham") }
            setLoading(false)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            val token = resolveAccessToken()
            if (token.isBlank()) {
                navigateToLogin()
                return@launch
            }

            setLoading(true)
            cartRepository.clearCart(token)
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

        viewModelScope.launch {
            val token = resolveAccessToken()
            if (token.isBlank()) {
                navigateToLogin()
                return@launch
            }
            setLoading(true)
            val request = CreateOrderRequest(
                paymentMethod = "COD",
                billingAddress = address
            )
            orderRepository.createOrder(token, request)
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
        val token = _uiState.value.accessToken
        val order = _uiState.value.pendingOrder
        if (token.isBlank() || order == null) {
            setError("Chua co don hang de thanh toan.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            paymentRepository.createPayment(
                token,
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
        viewModelScope.launch {
            val token = resolveAccessToken()
            if (token.isBlank()) return@launch
            setLoading(true)
            orderRepository.getOrders(token)
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
        userId: String,
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

    fun deleteAdminUser(userId: String) {
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
                categoryId = categoryId.ifBlank { null },
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
                categoryId = categoryId.ifBlank { null },
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

    fun deleteAdminProduct(productId: String) {
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
