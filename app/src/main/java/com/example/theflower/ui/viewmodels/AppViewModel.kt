package com.example.theflower.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theflower.data.local.TokenManager
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.di.DIContainer
import com.example.theflower.domain.repositories.*
import com.example.theflower.domain.models.Product
import com.example.theflower.ui.components.NavTab
import com.example.theflower.utils.NotificationHelper
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
    private val storeRepository: IStoreRepository = DIContainer.getStoreRepository(),
    private val userRepository: IUserRepository = DIContainer.getUserRepository(),
    private val tokenManager: TokenManager = DIContainer.getTokenManager()
) : ViewModel() {

    private var hasNotifiedPendingOrder = false

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        restoreSession()
        loadCategories()
        loadProducts()
        loadStores()
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
            NavTab.STORES -> {
                _uiState.update { it.copy(selectedCategory = null) }
                navigateToScreen("stores")
                loadStores()
            }
        }
    }

    fun navigateToScreen(screenName: String) {
        _uiState.update { it.copy(currentScreen = screenName) }
    }

    fun onPaymentResult(success: Boolean, orderId: String) {
        if (success) {
            NotificationHelper.showNotification(
                DIContainer.getContext(),
                "Thanh toán thành công! 🌸",
                "Đơn hàng #$orderId đã được xác nhận. Chúng tôi sẽ sớm giao đến bạn!"
            )
        }

        _uiState.update {
            it.copy(
                currentScreen = "payment_result",
                lastPaymentResult = success,
                lastPaymentOrderId = orderId,
                cart = null,          // Clear cart state so it reloads or shows empty
                pendingOrder = null    // Clear the "pending" banner info
            )
        }
        loadOrders()
        loadCart()
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
            "stores" -> navigateToScreen("home")
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
                .onFailure { exception ->
                    val userFriendlyMessage = when (exception) {
                        is com.example.theflower.data.exceptions.ApiException.Unauthorized -> "Email hoặc mật khẩu không đúng"
                        is com.example.theflower.data.exceptions.ApiException.ValidationError -> "Thông tin không hợp lệ"
                        is com.example.theflower.data.exceptions.ApiException.NetworkError -> "Không thể kết nối máy chủ"
                        else -> "Có lỗi xảy ra"
                    }
                    setError(userFriendlyMessage)
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
            val state = _uiState.value
            productRepository.getProducts(
                pageNumber = 1,
                pageSize = 50,
                categoryId = state.productFilterCategoryId,
                minPrice = state.productFilterMinPrice,
                maxPrice = state.productFilterMaxPrice,
                sortBy = state.productSortBy,
                sortOrder = state.productSortOrder,
                search = if (state.searchQuery.isNotBlank()) state.searchQuery else null
            )
                .onSuccess { response ->
                    _uiState.update { it.copy(products = response.items.orEmpty()) }
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Không thể tải sản phẩm")
                }
            setLoading(false)
        }
    }

    fun setProductFilterCategory(categoryId: String?) {
        _uiState.update { it.copy(productFilterCategoryId = categoryId) }
        loadProducts()
    }

    fun setProductPriceRange(min: Double?, max: Double?) {
        _uiState.update { it.copy(productFilterMinPrice = min, productFilterMaxPrice = max) }
        loadProducts()
    }

    fun setProductSort(sortBy: String, order: String) {
        _uiState.update { it.copy(productSortBy = sortBy, productSortOrder = order) }
        loadProducts()
    }

    fun clearProductFilters() {
        _uiState.update { 
            it.copy(
                productFilterCategoryId = null,
                productFilterMinPrice = null,
                productFilterMaxPrice = null,
                productSortBy = "Name",
                productSortOrder = "asc"
            )
        }
        loadProducts()
    }

    fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories) }
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Không thể tải danh mục")
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
                        setError(it.message ?: "Không thể tải hồ sơ người dùng")
                    }
                }
        }
    }

    fun updateProfile(fullName: String, phoneNumber: String, address: String) {
        if (!_uiState.value.isLoggedIn) {
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
                    setError(it.message ?: "Cập nhật hồ sơ thất bại")
                }
            setLoading(false)
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (!_uiState.value.isLoggedIn) {
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
                    setError(it.message ?: "Không thể thêm sản phẩm vào giỏ hàng")
                }
            setLoading(false)
        }
    }

    fun loadCart() {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            cartRepository.getCart()
                .onSuccess { cart ->
                    _uiState.update { it.copy(cart = cart, pendingOrder = cart.pendingOrder) }
                    
                    // Trigger notification for pending order
                    if (cart.pendingOrder != null && !hasNotifiedPendingOrder) {
                        NotificationHelper.showNotification(
                            DIContainer.getContext(),
                            "Bạn có đơn hàng chưa thanh toán 🌸",
                            "Sản phẩm trong giỏ hàng đang chờ bạn. Hãy hoàn tất thanh toán để nhận nhiều ưu đãi nhé!"
                        )
                        hasNotifiedPendingOrder = true
                    }
                    
                    clearError()
                }
                .onFailure {
                    if (it.message?.contains("Not found", ignoreCase = true) == true ||
                        it.message?.contains("404", ignoreCase = true) == true
                    ) {
                        _uiState.update { state -> state.copy(cart = com.example.theflower.data.remote.dtos.CartDto(), pendingOrder = null) }
                    } else {
                        setError(it.message ?: "Không thể tải giỏ hàng")
                    }
                }
        }
    }

    fun removeCartItem(itemId: String) {
        viewModelScope.launch {
            setLoading(true)
            cartRepository.removeFromCart(itemId)
                .onSuccess { cart -> _uiState.update { it.copy(cart = cart) } }
                .onFailure { setError(it.message ?: "Không thể xóa sản phẩm") }
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
                    setError(it.message ?: "Không thể tải chi tiết sản phẩm")
                }
        }
    }

    fun updateCartItemQuantity(itemId: String, quantity: Int) {
        val safeQuantity = quantity.coerceAtLeast(1)
        viewModelScope.launch {
            setLoading(true)
            cartRepository.updateCartItem(itemId, safeQuantity)
                .onSuccess { cart -> _uiState.update { it.copy(cart = cart) } }
                .onFailure { setError(it.message ?: "Không thể cập nhật số lượng sản phẩm") }
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
                    setError(it.message ?: "Không thể xóa giỏ hàng")
                }
            setLoading(false)
        }
    }

    fun createOrder() {
        if (!_uiState.value.isLoggedIn) {
            navigateToLogin()
            return
        }

        navigateToScreen("payment")
        _navigationEvent.value = NavigationEvent.NavigateToPayment
    }

    fun executeOrder(
        paymentMethod: String,
        recipientName: String,
        recipientPhone: String,
        deliveryDate: String,
        billingAddress: String
    ) {
        if (!_uiState.value.isLoggedIn) {
            navigateToLogin()
            return
        }

        viewModelScope.launch {
            setLoading(true)
            clearError()

            // Combine recipient info into billing address for backend storage
            val combinedAddress = buildString {
                append("Người nhận: $recipientName")
                if (recipientPhone.isNotBlank()) append(" ($recipientPhone)")
                if (deliveryDate.isNotBlank()) append(" - Ngày giao: $deliveryDate")
                append(" - Địa chỉ: $billingAddress")
            }

            val request = CreateOrderRequest(
                paymentMethod = paymentMethod,
                billingAddress = combinedAddress
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

                    if (paymentMethod.equals("VnPay", ignoreCase = true) && !order.paymentUrl.isNullOrBlank()) {
                        _navigationEvent.value = NavigationEvent.OpenUrl(order.paymentUrl)
                    } else if (paymentMethod.equals("COD", ignoreCase = true)) {
                        onPaymentResult(true, order.id)
                    } else {
                        // Default fallback
                        navigateToOrders()
                    }
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Không thể tạo đơn hàng")
                    navigateToScreen("cart") // Go back to cart on failure
                }
            setLoading(false)
        }
    }

    fun createPaymentForPendingOrder() {
        val order = _uiState.value.pendingOrder
        if (!_uiState.value.isLoggedIn || order == null) {
            setError("Chưa có đơn hàng để thanh toán.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            paymentRepository.createPayment(
                CreatePaymentRequest(
                    orderId = order.id,
                    amount = order.totalPrice.roundToInt(),
                    paymentMethod = order.paymentMethod.ifBlank { "VnPay" }
                )
            )
                .onSuccess {
                    clearError()
                }
                .onFailure {
                    setError(it.message ?: "Không thể tạo thanh toán cho đơn hàng ${order.id}")
                }
            setLoading(false)
        }
    }

    fun loadOrders() {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            setLoading(true)
            val result = if (_uiState.value.userRole == "Admin") {
                adminRepository.getOrders()
            } else {
                orderRepository.getOrders().map { it.items ?: emptyList() }
            }
            
            result.onSuccess { orders -> 
                _uiState.update { it.copy(orders = orders) }
                clearError()
            }
            .onFailure { setError(it.message ?: "Không thể tải đơn hàng") }
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
                .onSuccess { users -> 
                    _uiState.update { it.copy(adminUsers = users) }
                    clearError()
                }
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
        if (!_uiState.value.isLoggedIn) return

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
            adminRepository.createUser(request)
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
        if (!_uiState.value.isLoggedIn) return

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
            adminRepository.updateUser(userId, request)
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
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.deleteUser(userId)
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
        if (!_uiState.value.isLoggedIn) return

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
            adminRepository.createProduct(request)
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
        if (!_uiState.value.isLoggedIn) return

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
            adminRepository.updateProduct(productId, request)
                .onSuccess {
                    loadProducts()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Cập nhật sản phẩm thất bại") }
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
                .onFailure { setError(it.message ?: "Xóa sản phẩm thất bại") }
            setLoading(false)
        }
    }


    // ─── ADMIN SORTING ──────────────────────────────────────────────────

    fun setAdminUserSort(sortBy: String) {
        _uiState.update {
            val newOrder = if (it.adminUserSortBy == sortBy && it.adminUserSortOrder == "asc") "desc" else "asc"
            it.copy(adminUserSortBy = sortBy, adminUserSortOrder = newOrder)
        }
    }

    fun setAdminProductSort(sortBy: String) {
        _uiState.update {
            val newOrder = if (it.adminProductSortBy == sortBy && it.adminProductSortOrder == "asc") "desc" else "asc"
            it.copy(adminProductSortBy = sortBy, adminProductSortOrder = newOrder)
        }
    }

    fun setAdminCategorySort(sortBy: String) {
        _uiState.update {
            val newOrder = if (it.adminCategorySortBy == sortBy && it.adminCategorySortOrder == "asc") "desc" else "asc"
            it.copy(adminCategorySortBy = sortBy, adminCategorySortOrder = newOrder)
        }
    }

    // ─── STORES ──────────────────────────────────────────────────────────

    fun loadStores() {
        viewModelScope.launch {
            setLoading(true)
            val result = if (_uiState.value.userRole.equals("Admin", ignoreCase = true)) {
                adminRepository.getStoresAdmin()
            } else {
                storeRepository.getStores()
            }
            
            result.onSuccess { stores ->
                _uiState.update { it.copy(stores = stores) }
                clearError()
            }
            .onFailure { setError(it.message ?: "Không thể tải danh sách cửa hàng") }
            setLoading(false)
        }
    }

    fun openMap(address: String, lat: Double? = null, lon: Double? = null, directions: Boolean = false) {
        val uri = if (directions && lat != null && lon != null) {
            "google.navigation:q=$lat,$lon"
        } else if (lat != null && lon != null) {
            "geo:$lat,$lon?q=${android.net.Uri.encode(address)}"
        } else {
            "geo:0,0?q=${android.net.Uri.encode(address)}"
        }
        _navigationEvent.update { NavigationEvent.OpenUrl(uri) }
    }

    fun createAdminStore(address: String) {
        if (!_uiState.value.isLoggedIn) return
        if (address.isBlank()) {
            setError("Địa chỉ không được để trống")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            adminRepository.createStoreAdmin(CreateStoreLocationDto(address))
                .onSuccess {
                    loadStores()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Tạo cửa hàng thất bại") }
            setLoading(false)
        }
    }

    fun updateAdminStore(locationId: String, address: String, status: String) {
        if (!_uiState.value.isLoggedIn) return
        if (address.isBlank()) {
            setError("Địa chỉ không được để trống")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            adminRepository.updateStoreAdmin(locationId, UpdateStoreLocationDto(locationId, address, status))
                .onSuccess {
                    loadStores()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Cập nhật cửa hàng thất bại") }
            setLoading(false)
        }
    }

    fun deleteAdminStore(locationId: String) {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            setLoading(true)
            adminRepository.deleteStoreAdmin(locationId)
                .onSuccess {
                    loadStores()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Xóa cửa hàng thất bại") }
            setLoading(false)
        }
    }

    fun setAdminStoreSort(sortBy: String) {
        _uiState.update {
            val newOrder = if (it.adminStoreSortBy == sortBy && it.adminStoreSortOrder == "asc") "desc" else "asc"
            it.copy(adminStoreSortBy = sortBy, adminStoreSortOrder = newOrder)
        }
    }

    // ─── Admin Dashboard Stats ──────────────────────────────────────────────
    fun loadAdminDashboardStats() {
        if (!_uiState.value.isLoggedIn || _uiState.value.userRole != "Admin") return
        viewModelScope.launch {
            setLoading(true)
            adminRepository.getDashboardStats()
                .onSuccess { stats ->
                    _uiState.update { it.copy(adminDashboardStats = stats) }
                    clearError()
                }
                .onFailure { setError(it.message ?: "Không thể tải thống kê dashboard") }
            setLoading(false)
        }
    }

    // ─── Admin Orders ────────────────────────────────────────────────────────
    fun loadAdminOrders() {
        if (!_uiState.value.isLoggedIn || _uiState.value.userRole != "Admin") return
        viewModelScope.launch {
            setLoading(true)
            adminRepository.getOrders()
                .onSuccess { orders ->
                    _uiState.update { it.copy(orders = orders) }
                    clearError()
                }
                .onFailure { setError(it.message ?: "Không thể tải danh sách đơn hàng") }
            setLoading(false)
        }
    }

    fun updateAdminOrderStatus(orderId: String, status: String) {
        if (!_uiState.value.isLoggedIn || _uiState.value.userRole != "Admin") return
        viewModelScope.launch {
            setLoading(true)
            adminRepository.updateOrderStatus(orderId, status)
                .onSuccess {
                    loadAdminOrders()
                    loadAdminDashboardStats()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Cập nhật trạng thái đơn hàng thất bại") }
            setLoading(false)
        }
    }

    // ─── Admin Categories ──────────────────────────────────────────────────
    fun createAdminCategory(name: String) {
        if (!_uiState.value.isLoggedIn || _uiState.value.userRole != "Admin") return
        if (name.isBlank()) {
            setError("Tên danh mục không được để trống")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            adminRepository.createCategory(CategoryUpsertRequest(name))
                .onSuccess {
                    loadCategories()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Tạo danh mục thất bại") }
            setLoading(false)
        }
    }

    fun updateAdminCategory(categoryId: String, name: String) {
        if (!_uiState.value.isLoggedIn || _uiState.value.userRole != "Admin") return
        if (name.isBlank()) {
            setError("Tên danh mục không được để trống")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            adminRepository.updateCategory(categoryId, CategoryUpsertRequest(name))
                .onSuccess {
                    loadCategories()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Cập nhật danh mục thất bại") }
            setLoading(false)
        }
    }

    fun deleteAdminCategory(categoryId: String) {
        if (!_uiState.value.isLoggedIn || _uiState.value.userRole != "Admin") return
        viewModelScope.launch {
            setLoading(true)
            adminRepository.deleteCategory(categoryId)
                .onSuccess {
                    loadCategories()
                    clearError()
                }
                .onFailure { setError(it.message ?: "Xoá danh mục thất bại") }
            setLoading(false)
        }
    }
}
