package com.example.theflower.data.remote.api

import com.example.theflower.data.remote.dtos.*
import retrofit2.http.*

interface TheFlowerApiService {

    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("api/Auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthResponse>

    @POST("api/Auth/logout")
    suspend fun logout(): ApiResponse<Any>

    @GET("api/Products")
    suspend fun getProducts(
        @Query("Page") pageNumber: Int = 1,
        @Query("PageSize") pageSize: Int = 20,
        @Query("CategoryId") categoryId: String? = null,
        @Query("MinPrice") minPrice: Double? = null,
        @Query("MaxPrice") maxPrice: Double? = null,
        @Query("SortBy") sortBy: String? = null,
        @Query("SortOrder") sortOrder: String? = null
    ): ApiResponse<PaginatedResponse<ProductDto>>

    @GET("api/Products/{id}")
    suspend fun getProductDetail(@Path("id") productId: String): ApiResponse<ProductDto>

    @GET("api/Products/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryDto>>

    @GET("api/Carts")
    suspend fun getCart(): ApiResponse<CartDto>

    @POST("api/Carts/items")
    suspend fun addToCart(
        @Body request: AddToCartRequest
    ): ApiResponse<CartDto>

    @DELETE("api/Carts/items/{cartItemId}")
    suspend fun removeFromCart(
        @Path("cartItemId") cartItemId: String
    ): ApiResponse<CartDto>

    @PUT("api/Carts/items/{cartItemId}")
    suspend fun updateCartItem(
        @Path("cartItemId") cartItemId: String,
        @Body request: UpdateCartItemDto
    ): ApiResponse<CartDto>

    @DELETE("api/Carts")
    suspend fun clearCart(): ApiResponse<Any>

    @GET("api/Orders")
    suspend fun getOrders(): ApiResponse<List<OrderDto>>

    @GET("api/Orders/{id}")
    suspend fun getOrderDetail(
        @Path("id") orderId: String
    ): ApiResponse<OrderDto>

    @POST("api/Orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): ApiResponse<CreateOrderResponseDto>

    @GET("api/Users")
    suspend fun getUsersAdmin(): ApiResponse<List<AdminUserDto>>

    @POST("api/Users")
    suspend fun createUserAdmin(
        @Body request: CreateAdminUserRequest
    ): ApiResponse<AdminUserDto>

    @PUT("api/Users/{id}")
    suspend fun updateUserAdmin(
        @Path("id") userId: String,
        @Body request: UpdateAdminUserRequest
    ): ApiResponse<AdminUserDto>

    @DELETE("api/Users/{id}")
    suspend fun deleteUserAdmin(
        @Path("id") userId: String
    ): ApiResponse<Any>

    @POST("api/Products")
    suspend fun createProductAdmin(
        @Body request: ProductUpsertRequest
    ): ApiResponse<ProductDto>

    @PUT("api/Products/{id}")
    suspend fun updateProductAdmin(
        @Path("id") productId: String,
        @Body request: UpdateProductRequest
    ): ApiResponse<Any>

    @DELETE("api/Products/{id}")
    suspend fun deleteProductAdmin(
        @Path("id") productId: String
    ): ApiResponse<Any>

    @GET("api/Users/profile")
    suspend fun getUserProfile(): ApiResponse<UserProfileDto>

    @PUT("api/Users/profile")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest
    ): ApiResponse<UserProfileDto>

    @POST("api/Users/change-password")
    suspend fun changePassword(
        @Body request: ChangeUserPasswordDto
    ): ApiResponse<Any>

    @GET("api/Notifications")
    suspend fun getNotifications(): ApiResponse<List<NotificationDto>>

    @GET("api/Notifications/badge")
    suspend fun getNotificationBadge(): ApiResponse<NotificationBadgeDto>

    @PUT("api/Notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") notificationId: String
    ): ApiResponse<NotificationDto>

    @PUT("api/Notifications/read-all")
    suspend fun markAllNotificationsAsRead(): ApiResponse<Any>

    // ─── Chat Endpoints ──────────────────────────────────────────────────────

    @GET("api/Chats/messages")
    suspend fun getChatMessages(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 30
    ): ApiResponse<List<ChatMessageDto>>

    @POST("api/Chats/messages")
    suspend fun sendChatMessage(
        @Body request: SendMessageRequest
    ): ApiResponse<ChatMessageDto>

    @GET("api/Chats/conversations")
    suspend fun getConversations(): ApiResponse<List<ConversationSummaryDto>>

    @GET("api/Chats/messages/{userId}")
    suspend fun getMessagesForUser(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 30
    ): ApiResponse<List<ChatMessageDto>>
}

