package com.example.theflower.data.remote.dtos

import com.google.gson.annotations.SerializedName

/**
 * ==================== COMMON ==================
 * Standard wrapper for all API responses
 */

data class ApiResponse<T>(
    @SerializedName(value = "isSuccess", alternate = ["success", "IsSuccess"])
    val success: Boolean = false,
    @SerializedName(value = "data", alternate = ["Data"])
    val data: T? = null,
    @SerializedName(value = "message", alternate = ["Message"])
    val message: String? = null
)

data class PaginatedResponse<T>(
    @SerializedName(value = "items", alternate = ["data"])
    val items: List<T>? = emptyList(),
    @SerializedName(value = "pageNumber", alternate = ["page", "currentPage"])
    val pageNumber: Int = 1,
    @SerializedName(value = "pageSize", alternate = ["size"])
    val pageSize: Int = 0,
    @SerializedName(value = "totalItems", alternate = ["totalCount"])
    val totalItems: Int = 0,
    @SerializedName(value = "totalPages", alternate = ["pageCount"])
    val totalPages: Int = 0
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
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("address")
    val address: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName(value = "userId", alternate = ["id"])
    val userId: String = "",
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("username")
    val username: String? = "",
    @SerializedName("role")
    val role: String? = "",
    @SerializedName(value = "fullName", alternate = ["name"])
    val fullName: String? = "",
    @SerializedName(value = "token", alternate = ["accessToken", "jwtToken"])
    val accessToken: String? = "",
    @SerializedName("refreshToken")
    val refreshToken: String? = "",
    @SerializedName("expiresIn")
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
    @SerializedName(value = "productId", alternate = ["id"])
    val id: String = "",
    @SerializedName(value = "productName", alternate = ["name"])
    val name: String = "",
    @SerializedName("price")
    val price: Double = 0.0,
    @SerializedName("briefDescription")
    val briefDescription: String? = null,
    @SerializedName("fullDescription")
    val fullDescription: String? = null,
    @SerializedName("technicalSpecifications")
    val technicalSpecifications: String? = null,
    @SerializedName(value = "imageUrl", alternate = ["image", "productImage"])
    val image: String? = null,
    @SerializedName("categoryId")
    val categoryId: String = "",
    @SerializedName(value = "categoryName", alternate = ["category"])
    val categoryName: String = "",
    @SerializedName(value = "stockQuantity", alternate = ["stock"])
    val stock: Int = 0,
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName(value = "reviewCount", alternate = ["reviews"])
    val reviewCount: Int = 0
) {
    val description: String?
        get() = fullDescription ?: briefDescription
}

data class CategoryDto(
    @SerializedName(value = "categoryId", alternate = ["id"])
    val id: String = "",
    @SerializedName(value = "categoryName", alternate = ["name"])
    val name: String = "",
    val description: String = "",
    val image: String = ""
)

/**
 * ==================== CART ==================
 * Shopping cart related DTOs
 */

data class CartDto(
    @SerializedName(value = "cartId", alternate = ["id"])
    val id: String = "",
    val userId: String = "",
    val items: List<CartItemDto> = emptyList(),
    @SerializedName(value = "totalPrice", alternate = ["totalAmount"])
    val totalPrice: Double = 0.0,
    val totalItems: Int = 0,
    val createdAt: String = ""
)

data class CartItemDto(
    @SerializedName(value = "cartItemId", alternate = ["id"])
    val id: String = "",
    val cartId: String = "",
    val productId: String = "",
    val productName: String = "",
    @SerializedName(value = "imageUrl", alternate = ["productImage"])
    val productImage: String? = null,
    @SerializedName(value = "unitPrice", alternate = ["productPrice"])
    val productPrice: Double = 0.0,
    val quantity: Int = 0,
    @SerializedName(value = "subTotal", alternate = ["totalPrice"])
    val totalPrice: Double = 0.0
)

data class AddToCartRequest(
    val productId: String,
    val quantity: Int
)

data class UpdateCartItemDto(
    val quantity: Int
)

/**
 * ==================== ORDER ==================
 * Order management DTOs
 */

data class OrderDto(
    @SerializedName(value = "orderId", alternate = ["id"])
    val id: String = "",
    val userId: String = "",
    @SerializedName("items")
    val items: List<OrderItemDto> = emptyList(),
    @SerializedName(value = "totalAmount", alternate = ["totalPrice"])
    val totalPrice: Double = 0.0,
    val recipientName: String = "",
    val recipientPhone: String = "",
    @SerializedName(value = "billingAddress", alternate = ["recipientAddress", "shippingAddress"])
    val recipientAddress: String = "",
    val occasion: String? = null,
    val message: String? = null,
    @SerializedName(value = "orderStatus", alternate = ["status"])
    val status: String = "",
    val paymentMethod: String = "",
    @SerializedName(value = "orderDate", alternate = ["createdAt"])
    val createdAt: String = "",
    @SerializedName("paymentStatus")
    val paymentStatus: String = "",
    @SerializedName("paymentUrl")
    val paymentUrl: String? = null,
    val updatedAt: String = ""
)

data class OrderItemDto(
    val productId: String = "",
    val productName: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("unitPrice")
    val unitPrice: Double = 0.0,
    val quantity: Int = 0,
    @SerializedName("subTotal")
    val subTotal: Double = 0.0
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
    val orderId: String = "",
    @SerializedName("orderStatus")
    val orderStatus: String = "",
    val paymentMethod: String = "",
    @SerializedName("totalAmount")
    val totalAmount: Double = 0.0,
    val paymentUrl: String? = null
)

/**
 * ==================== PAYMENT ==================
 * Payment processing DTOs
 */

data class PaymentDto(
    val id: String = "",
    val orderId: String = "",
    val amount: Int = 0,
    val paymentMethod: String = "",
    val status: String = "",
    val transactionId: String? = null,
    val createdAt: String = "",
    val completedAt: String? = null
)

data class CreatePaymentRequest(
    val orderId: String,
    val amount: Int,
    val paymentMethod: String
)

/**
 * ==================== NOTIFICATION ==================
 * User notification DTOs
 */

data class NotificationDto(
    @SerializedName(value = "notificationId", alternate = ["id"])
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val orderId: String? = null,
    val isRead: Boolean = false,
    val createdAt: String = ""
)

data class NotificationBadgeDto(
    @SerializedName(value = "unreadCount", alternate = ["count", "badgeCount"])
    val count: Int = 0
)

/**
 * ==================== CHAT ==================
 * Real-time chat DTOs
 */

data class ChatMessageDto(
    @SerializedName("chatMessageId") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("message") val message: String,
    @SerializedName("sentAt") val sentAt: String,
    @SerializedName("isFromAdmin") val isFromAdmin: Boolean
)

data class SendMessageRequest(
    @SerializedName("message") val message: String
)

data class ConversationSummaryDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("lastMessage") val lastMessage: String,
    @SerializedName("lastMessageAt") val lastMessageAt: String,
    @SerializedName("unreadCount") val unreadCount: Int
)

/**
 * ==================== MAPS ==================
 * Store location DTOs
 */

data class StoreLocationDto(
    @SerializedName(value = "locationId", alternate = ["id"])
    val locationId: String = "",
    val address: String? = null
)

data class CreateStoreLocationDto(
    val address: String? = null
)

data class UpdateStoreLocationDto(
    val locationId: String,
    val address: String? = null
)

/**
 * ==================== USER ==================
 * User profile DTOs
 */

data class UserProfileDto(
    @SerializedName(value = "userId", alternate = ["id"])
    val id: String = "",
    val email: String = "",
    @SerializedName("username")
    val username: String = "",
    val fullName: String = "",
    val phoneNumber: String? = null,
    val address: String? = null,
    val avatar: String? = null,
    val role: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class UpdateProfileRequest(
    val fullName: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val avatar: String? = null
)

data class ChangeUserPasswordDto(
    val oldPassword: String,
    val newPassword: String
)

/**
 * ==================== ADMIN ==================
 * Admin dashboard DTOs
 */

data class AdminUserDto(
    @SerializedName(value = "userId", alternate = ["id"])
    val userId: String = "",
    @SerializedName("username")
    val username: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("role")
    val role: String = ""
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
    val categoryId: String? = null,
    @SerializedName("stockQuantity")
    val stockQuantity: Int? = 0
)

data class UpdateProductRequest(
    @SerializedName("productId")
    val productId: String,
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
    val categoryId: String? = null,
    @SerializedName("stockQuantity")
    val stockQuantity: Int? = 0
)

data class CategoryUpsertRequest(
    @SerializedName("categoryName")
    val name: String
)

data class AdminReplyRequest(
    @SerializedName("targetUserId")
    val targetUserId: String,
    @SerializedName("message")
    val message: String
)
