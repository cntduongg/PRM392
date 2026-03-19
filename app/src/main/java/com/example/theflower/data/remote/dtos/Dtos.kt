package com.example.theflower.data.remote.dtos

import com.google.gson.annotations.SerializedName

/**
 * ==================== COMMON ==================
 * Standard wrapper for all API responses
 */

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String = ""
)

data class PaginatedResponse<T>(
    val items: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

/**
 * ==================== AUTH ==================
 * Authentication related DTOs
 */

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("username")
    val username: String = "",
    @SerializedName("role")
    val role: String = "",
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("token")
    val accessToken: String,
    val refreshToken: String = "",
    val expiresIn: Int = 0
)

data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * ==================== PRODUCT ==================
 * Product and category related DTOs
 */

data class ProductDto(
    @SerializedName("productId")
    val id: Int,
    @SerializedName("productName")
    val name: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("fullDescription")
    val description: String? = null,
    @SerializedName("imageUrl")
    val image: String? = null,
    val categoryId: Int,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("stockQuantity")
    val stock: Int,
    val rating: Float = 0f,
    @SerializedName("reviewCount")
    val reviewCount: Int = 0
)

data class CategoryDto(
    @SerializedName("categoryId")
    val id: Int,
    @SerializedName("categoryName")
    val name: String,
    val description: String = "",
    val image: String = ""
)

/**
 * ==================== CART ==================
 * Shopping cart related DTOs
 */

data class CartDto(
    @SerializedName("cartId")
    val id: Int,
    val userId: Int = 0,
    val items: List<CartItemDto>,
    val totalPrice: Double,
    val totalItems: Int,
    val createdAt: String = ""
)

data class CartItemDto(
    @SerializedName("cartItemId")
    val id: Int,
    val cartId: Int = 0,
    val productId: Int,
    val productName: String,
    @SerializedName("imageUrl")
    val productImage: String? = null,
    @SerializedName("unitPrice")
    val productPrice: Double,
    val quantity: Int,
    @SerializedName("subTotal")
    val totalPrice: Double
)

data class AddToCartRequest(
    val productId: Int,
    val quantity: Int
)

/**
 * ==================== ORDER ==================
 * Order management DTOs
 */

data class OrderDto(
    @SerializedName("orderId")
    val id: Int,
    val userId: Int = 0,
    @SerializedName("items")
    val items: List<OrderItemDto>,
    @SerializedName("totalAmount")
    val totalPrice: Double,
    val recipientName: String = "",
    val recipientPhone: String = "",
    @SerializedName("billingAddress")
    val recipientAddress: String,
    val occasion: String? = null,
    val message: String? = null,
    @SerializedName("orderStatus")
    val status: String,
    val paymentMethod: String,
    @SerializedName("orderDate")
    val createdAt: String,
    @SerializedName("paymentStatus")
    val paymentStatus: String = "",
    val updatedAt: String = ""
)

data class OrderItemDto(
    val productId: Int,
    val productName: String,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("unitPrice")
    val unitPrice: Double,
    val quantity: Int,
    @SerializedName("subTotal")
    val subTotal: Double
)

data class CreateOrderRequest(
    val paymentMethod: String,
    @SerializedName("billingAddress")
    val billingAddress: String,
    @SerializedName("returnUrl")
    val returnUrl: String? = null,
    @SerializedName("cancelUrl")
    val cancelUrl: String? = null
)

data class CreateOrderResponseDto(
    @SerializedName("orderId")
    val orderId: Int,
    @SerializedName("orderStatus")
    val orderStatus: String,
    val paymentMethod: String,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    val paymentUrl: String? = null
)

/**
 * ==================== PAYMENT ==================
 * Payment processing DTOs
 */

data class PaymentDto(
    val id: Int,
    val orderId: Int,
    val amount: Int,
    val paymentMethod: String,  // CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CASH_ON_DELIVERY
    val status: String,  // PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED
    val transactionId: String? = null,
    val createdAt: String,
    val completedAt: String? = null
)

data class CreatePaymentRequest(
    val orderId: Int,
    val amount: Int,
    val paymentMethod: String
)

/**
 * ==================== NOTIFICATION ==================
 * User notification DTOs
 */

data class NotificationDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val message: String,
    val type: String,  // ORDER_CONFIRMED, ORDER_SHIPPED, ORDER_DELIVERED, PAYMENT_RECEIVED, etc.
    val orderId: Int? = null,
    val isRead: Boolean,
    val createdAt: String
)

/**
 * ==================== CHAT ==================
 * Real-time chat DTOs
 */

data class ChatMessageDto(
    val id: Int,
    val conversationId: Int,
    val senderId: Int,
    val senderName: String,
    val senderAvatar: String? = null,
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)

data class SendChatMessageRequest(
    val conversationId: Int,
    val message: String
)

/**
 * ==================== USER ==================
 * User profile DTOs
 */

data class UserProfileDto(
    val id: Int,
    val email: String,
    @SerializedName("username")
    val username: String = "",
    val fullName: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val avatar: String? = null,
    val role: String,  // USER, ADMIN, STAFF
    val createdAt: String,
    val updatedAt: String
)

data class UpdateProfileRequest(
    val fullName: String,
    val phoneNumber: String,
    val address: String,
    val avatar: String? = null
)

/**
 * ==================== ADMIN ==================
 * Admin dashboard DTOs
 */

data class AdminUserDto(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("role")
    val role: String
)

data class CreateAdminUserRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("role")
    val role: String = "Customer"
)

data class UpdateAdminUserRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("role")
    val role: String = "Customer",
    @SerializedName("password")
    val password: String? = null
)

data class ProductUpsertRequest(
    @SerializedName("productName")
    val productName: String,
    @SerializedName("briefDescription")
    val briefDescription: String? = null,
    @SerializedName("fullDescription")
    val fullDescription: String? = null,
    @SerializedName("technicalSpecifications")
    val technicalSpecifications: String? = null,
    @SerializedName("price")
    val price: Double,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("categoryId")
    val categoryId: Int? = null,
    @SerializedName("stockQuantity")
    val stockQuantity: Int? = 0
)
