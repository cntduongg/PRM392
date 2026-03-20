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
    suspend fun logout(@Header("Authorization") token: String): ApiResponse<Any>

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
    suspend fun getCart(@Header("Authorization") token: String): ApiResponse<CartDto>

    @POST("api/Carts/items")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): ApiResponse<CartDto>

    @DELETE("api/Carts/items/{cartItemId}")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("cartItemId") cartItemId: String
    ): ApiResponse<CartDto>

    @PUT("api/Carts/items/{cartItemId}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("cartItemId") cartItemId: String,
        @Body request: UpdateCartItemDto
    ): ApiResponse<CartDto>

    @DELETE("api/Carts")
    suspend fun clearCart(@Header("Authorization") token: String): ApiResponse<Any>

    @GET("api/Orders")
    suspend fun getOrders(@Header("Authorization") token: String): ApiResponse<List<OrderDto>>

    @GET("api/Orders/{id}")
    suspend fun getOrderDetail(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): ApiResponse<OrderDto>

    @POST("api/Orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): ApiResponse<CreateOrderResponseDto>

    @GET("api/Users")
    suspend fun getUsersAdmin(@Header("Authorization") token: String): ApiResponse<List<AdminUserDto>>

    @POST("api/Users")
    suspend fun createUserAdmin(
        @Header("Authorization") token: String,
        @Body request: CreateAdminUserRequest
    ): ApiResponse<AdminUserDto>

    @PUT("api/Users/{id}")
    suspend fun updateUserAdmin(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body request: UpdateAdminUserRequest
    ): ApiResponse<AdminUserDto>

    @DELETE("api/Users/{id}")
    suspend fun deleteUserAdmin(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): ApiResponse<Any>

    @POST("api/Products")
    suspend fun createProductAdmin(
        @Header("Authorization") token: String,
        @Body request: ProductUpsertRequest
    ): ApiResponse<ProductDto>

    @PUT("api/Products/{id}")
    suspend fun updateProductAdmin(
        @Header("Authorization") token: String,
        @Path("id") productId: String,
        @Body request: UpdateProductRequest
    ): ApiResponse<Any>

    @DELETE("api/Products/{id}")
    suspend fun deleteProductAdmin(
        @Header("Authorization") token: String,
        @Path("id") productId: String
    ): ApiResponse<Any>

    @GET("api/Users/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): ApiResponse<UserProfileDto>

    @PUT("api/Users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): ApiResponse<UserProfileDto>

    @POST("api/Users/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangeUserPasswordDto
    ): ApiResponse<Any>

    @GET("api/Notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): ApiResponse<List<NotificationDto>>

    @GET("api/Notifications/badge")
    suspend fun getNotificationBadge(@Header("Authorization") token: String): ApiResponse<NotificationBadgeDto>

    @PUT("api/Notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Header("Authorization") token: String,
        @Path("id") notificationId: String
    ): ApiResponse<NotificationDto>

    @PUT("api/Notifications/read-all")
    suspend fun markAllNotificationsAsRead(@Header("Authorization") token: String): ApiResponse<Any>

    @GET("api/Chats/messages")
    suspend fun getChatMessages(
        @Header("Authorization") token: String,
        @Query("Page") page: Int = 1,
        @Query("PageSize") pageSize: Int = 20
    ): ApiResponse<List<ChatMessageDto>>

    @POST("api/Chats/messages")
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Body request: SendMessageDto
    ): ApiResponse<ChatMessageDto>
}
