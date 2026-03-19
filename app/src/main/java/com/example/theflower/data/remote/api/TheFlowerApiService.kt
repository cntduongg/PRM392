package com.example.theflower.data.remote.api

import retrofit2.http.*
import com.example.theflower.data.remote.dtos.*

/**
 * The Flower API Service
 * Base URL: https://10.0.2.2:7225 (Development on Android emulator) or https://api.theflower.com (Production)
 * 
 * All endpoints with authentication require "Authorization: Bearer {token}" header
 */
interface TheFlowerApiService {
    // ────── BACKEND ALIGNED ENDPOINTS (CURRENT .NET API) ────────────────────

    @POST("api/auth/register")
    suspend fun registerBackend(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun loginBackend(@Body request: LoginRequest): AuthResponse

    @GET("api/products")
    suspend fun getProductsBackend(
        @Query("page") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null
    ): PaginatedResponse<ProductDto>

    @GET("api/products/{id}")
    suspend fun getProductDetailBackend(@Path("id") productId: Int): ProductDto

    @GET("api/products/categories")
    suspend fun getCategoriesBackend(): List<CategoryDto>

    @GET("api/carts")
    suspend fun getCartBackend(@Header("Authorization") token: String): CartDto

    @POST("api/carts/items")
    suspend fun addToCartBackend(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): CartDto

    @DELETE("api/carts/items/{itemId}")
    suspend fun removeFromCartBackend(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Int
    ): CartDto

    @PUT("api/carts/items/{itemId}")
    suspend fun updateCartItemBackend(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Int,
        @Body quantity: Map<String, Int>
    ): CartDto

    @DELETE("api/carts")
    suspend fun clearCartBackend(@Header("Authorization") token: String)

    @GET("api/orders")
    suspend fun getOrdersBackend(@Header("Authorization") token: String): List<OrderDto>

    @GET("api/orders/{id}")
    suspend fun getOrderDetailBackend(
        @Header("Authorization") token: String,
        @Path("id") orderId: Int
    ): OrderDto

    @POST("api/orders")
    suspend fun createOrderBackend(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): CreateOrderResponseDto

    @GET("api/users")
    suspend fun getUsersAdmin(@Header("Authorization") token: String): List<AdminUserDto>

    @POST("api/users")
    suspend fun createUserAdmin(
        @Header("Authorization") token: String,
        @Body request: CreateAdminUserRequest
    ): AdminUserDto

    @PUT("api/users/{id}")
    suspend fun updateUserAdmin(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UpdateAdminUserRequest
    ): AdminUserDto

    @DELETE("api/users/{id}")
    suspend fun deleteUserAdmin(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    )

    @POST("api/products")
    suspend fun createProductAdmin(
        @Header("Authorization") token: String,
        @Body request: ProductUpsertRequest
    ): ApiResponse<ProductDto>

    @PUT("api/products/{id}")
    suspend fun updateProductAdmin(
        @Header("Authorization") token: String,
        @Path("id") productId: Int,
        @Body request: UpdateProductRequest
    ): ApiResponse<Any>

    @DELETE("api/products/{id}")
    suspend fun deleteProductAdmin(
        @Header("Authorization") token: String,
        @Path("id") productId: Int
    ): ApiResponse<Any>
    
    // ────── AUTH ──────────────────────────────────────────────────────────────
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>
    
    @POST("api/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthResponse>
    
    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): ApiResponse<Any>
    
    // ────── PRODUCTS ──────────────────────────────────────────────────────────
    
    @GET("api/products")
    suspend fun getProducts(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("categoryId") categoryId: Int? = null,
        @Query("search") search: String? = null
    ): ApiResponse<PaginatedResponse<ProductDto>>
    
    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") productId: Int): ApiResponse<ProductDto>
    
    // ────── CATEGORIES ────────────────────────────────────────────────────────
    
    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryDto>>
    
    // ────── CART ──────────────────────────────────────────────────────────────
    
    @GET("api/cart")
    suspend fun getCart(@Header("Authorization") token: String): ApiResponse<CartDto>
    
    @POST("api/cart/items")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): ApiResponse<CartDto>
    
    @DELETE("api/cart/items/{itemId}")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Int
    ): ApiResponse<CartDto>
    
    @PUT("api/cart/items/{itemId}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Int,
        @Body quantity: Map<String, Int>
    ): ApiResponse<CartDto>
    
    @DELETE("api/cart")
    suspend fun clearCart(@Header("Authorization") token: String): ApiResponse<Any>
    
    // ────── ORDERS ────────────────────────────────────────────────────────────
    
    @GET("api/orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PaginatedResponse<OrderDto>>
    
    @GET("api/orders/{id}")
    suspend fun getOrderDetail(
        @Header("Authorization") token: String,
        @Path("id") orderId: Int
    ): ApiResponse<OrderDto>
    
    @POST("api/orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): ApiResponse<OrderDto>
    
    @PUT("api/orders/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Int
    ): ApiResponse<OrderDto>
    
    // ────── PAYMENTS ──────────────────────────────────────────────────────────
    
    @POST("api/payments")
    suspend fun createPayment(
        @Header("Authorization") token: String,
        @Body request: CreatePaymentRequest
    ): ApiResponse<PaymentDto>
    
    @GET("api/payments/{id}")
    suspend fun getPaymentStatus(
        @Header("Authorization") token: String,
        @Path("id") paymentId: Int
    ): ApiResponse<PaymentDto>
    
    // ────── NOTIFICATIONS ─────────────────────────────────────────────────────
    
    @GET("api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PaginatedResponse<NotificationDto>>
    
    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Header("Authorization") token: String,
        @Path("id") notificationId: Int
    ): ApiResponse<NotificationDto>
    
    @PUT("api/notifications/read-all")
    suspend fun markAllNotificationsAsRead(
        @Header("Authorization") token: String
    ): ApiResponse<Any>
    
    // ────── CHAT ──────────────────────────────────────────────────────────────
    
    @GET("api/chat/conversations")
    suspend fun getChatConversations(
        @Header("Authorization") token: String
    ): ApiResponse<List<ChatMessageDto>>
    
    @GET("api/chat/conversations/{conversationId}")
    suspend fun getConversationMessages(
        @Header("Authorization") token: String,
        @Path("conversationId") conversationId: Int
    ): ApiResponse<List<ChatMessageDto>>
    
    @POST("api/chat/send")
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Body request: SendChatMessageRequest
    ): ApiResponse<ChatMessageDto>
    
    // ────── USER PROFILE ──────────────────────────────────────────────────────
    
    @GET("api/users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): ApiResponse<UserProfileDto>
    
    @PUT("api/users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): ApiResponse<UserProfileDto>
    
    @POST("api/users/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>  // { "oldPassword": "...", "newPassword": "..." }
    ): ApiResponse<Any>
}
