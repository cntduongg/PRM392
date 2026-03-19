package com.example.theflower.domain.repositories

import com.example.theflower.data.remote.dtos.*

/**
 * Authentication Repository Interface
 */
interface IAuthRepository {
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun refreshToken(request: RefreshTokenRequest): Result<AuthResponse>
    suspend fun logout(token: String): Result<Unit>
}

/**
 * Products Repository Interface
 */
interface IProductRepository {
    suspend fun getProducts(
        pageNumber: Int = 1,
        pageSize: Int = 20,
        categoryId: Int? = null,
        search: String? = null
    ): Result<PaginatedResponse<ProductDto>>

    suspend fun getProductDetail(productId: Int): Result<ProductDto>
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
    suspend fun getCart(token: String): Result<CartDto>
    suspend fun addToCart(token: String, request: AddToCartRequest): Result<CartDto>
    suspend fun removeFromCart(token: String, itemId: Int): Result<CartDto>
    suspend fun updateCartItem(token: String, itemId: Int, quantity: Int): Result<CartDto>
    suspend fun clearCart(token: String): Result<Unit>
}

/**
 * Orders Repository Interface
 */
interface IOrderRepository {
    suspend fun getOrders(
        token: String,
        pageNumber: Int = 1,
        pageSize: Int = 20
    ): Result<PaginatedResponse<OrderDto>>

    suspend fun getOrderDetail(token: String, orderId: Int): Result<OrderDto>
    suspend fun createOrder(token: String, request: CreateOrderRequest): Result<OrderDto>
    suspend fun cancelOrder(token: String, orderId: Int): Result<OrderDto>
}

/**
 * Payments Repository Interface
 */
interface IPaymentRepository {
    suspend fun createPayment(token: String, request: CreatePaymentRequest): Result<PaymentDto>
    suspend fun getPaymentStatus(token: String, paymentId: Int): Result<PaymentDto>
}

/**
 * Notifications Repository Interface
 */
interface INotificationRepository {
    suspend fun getNotifications(
        token: String,
        pageNumber: Int = 1,
        pageSize: Int = 20
    ): Result<PaginatedResponse<NotificationDto>>

    suspend fun markAsRead(token: String, notificationId: Int): Result<NotificationDto>
    suspend fun markAllAsRead(token: String): Result<Unit>
}

/**
 * Chat Repository Interface
 */
interface IChatRepository {
    suspend fun getChatConversations(token: String): Result<List<ChatMessageDto>>
    suspend fun getConversationMessages(token: String, conversationId: Int): Result<List<ChatMessageDto>>
    suspend fun sendChatMessage(token: String, request: SendChatMessageRequest): Result<ChatMessageDto>
}

/**
 * User Profile Repository Interface
 */
interface IUserRepository {
    suspend fun getUserProfile(token: String): Result<UserProfileDto>
    suspend fun updateUserProfile(token: String, request: UpdateProfileRequest): Result<UserProfileDto>
    suspend fun changePassword(token: String, oldPassword: String, newPassword: String): Result<Unit>
}

/**
 * Admin Repository Interface
 */
interface IAdminRepository {
    suspend fun getUsers(token: String): Result<List<AdminUserDto>>
    suspend fun createUser(token: String, request: CreateAdminUserRequest): Result<AdminUserDto>
    suspend fun updateUser(token: String, userId: Int, request: UpdateAdminUserRequest): Result<AdminUserDto>
    suspend fun deleteUser(token: String, userId: Int): Result<Unit>

    suspend fun createProduct(token: String, request: ProductUpsertRequest): Result<Unit>
    suspend fun updateProduct(token: String, productId: Int, request: UpdateProductRequest): Result<Unit>
    suspend fun deleteProduct(token: String, productId: Int): Result<Unit>
}
