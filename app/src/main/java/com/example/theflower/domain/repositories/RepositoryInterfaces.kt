package com.example.theflower.domain.repositories

import com.example.theflower.data.remote.dtos.*

/**
 * Authentication Repository Interface
 */
interface IAuthRepository {
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun refreshToken(request: RefreshTokenRequest): Result<AuthResponse>
    suspend fun logout(): Result<Unit>
}

/**
 * Products Repository Interface
 */
interface IProductRepository {
    suspend fun getProducts(
        pageNumber: Int = 1,
        pageSize: Int = 20,
        categoryId: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        search: String? = null
    ): Result<PaginatedResponse<ProductDto>>

    suspend fun getProductDetail(productId: String): Result<ProductDto>
}

/**
 * Categories Repository Interface
 */
interface ICategoryRepository {
    suspend fun getCategories(): Result<List<CategoryDto>>
}

/**
 * Cart Repository Interface
 */
interface ICartRepository {
    suspend fun getCart(): Result<CartDto>
    suspend fun addToCart(request: AddToCartRequest): Result<CartDto>
    suspend fun removeFromCart(cartItemId: String): Result<CartDto>
    suspend fun updateCartItem(cartItemId: String, quantity: Int): Result<CartDto>
    suspend fun clearCart(): Result<Unit>
}

/**
 * Orders Repository Interface
 */
interface IOrderRepository {
    suspend fun getOrders(
        pageNumber: Int = 1,
        pageSize: Int = 20
    ): Result<PaginatedResponse<OrderDto>>

    suspend fun getOrderDetail(orderId: String): Result<OrderDto>
    suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto>
    suspend fun cancelOrder(orderId: String): Result<OrderDto>
}

/**
 * Payments Repository Interface
 */
interface IPaymentRepository {
    suspend fun createPayment(request: CreatePaymentRequest): Result<PaymentDto>
    suspend fun getPaymentStatus(paymentId: String): Result<PaymentDto>
}

/**
 * Notifications Repository Interface
 */
interface INotificationRepository {
    suspend fun getNotifications(
        pageNumber: Int = 1,
        pageSize: Int = 20
    ): Result<PaginatedResponse<NotificationDto>>

    suspend fun getBadge(): Result<NotificationBadgeDto>
    suspend fun markAsRead(notificationId: String): Result<NotificationDto>
    suspend fun markAllAsRead(): Result<Unit>
}

/**
 * Chat Repository Interface
 */
interface IChatRepository {
    suspend fun getChatConversations(): Result<List<ChatMessageDto>>
    suspend fun getConversationMessages(conversationId: String): Result<List<ChatMessageDto>>
    suspend fun sendChatMessage(request: SendChatMessageRequest): Result<ChatMessageDto>
}

/**
 * User Profile Repository Interface
 */
interface IUserRepository {
    suspend fun getUserProfile(): Result<UserProfileDto>
    suspend fun updateUserProfile(request: UpdateProfileRequest): Result<UserProfileDto>
    suspend fun changePassword(request: ChangeUserPasswordDto): Result<Unit>
}

/**
 * Admin Repository Interface
 */
interface IAdminRepository {
    suspend fun getUsers(): Result<List<AdminUserDto>>
    suspend fun createUser(request: CreateAdminUserRequest): Result<AdminUserDto>
    suspend fun updateUser(userId: String, request: UpdateAdminUserRequest): Result<AdminUserDto>
    suspend fun deleteUser(userId: String): Result<Unit>

    suspend fun createProduct(request: ProductUpsertRequest): Result<Unit>
    suspend fun updateProduct(productId: String, request: UpdateProductRequest): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
