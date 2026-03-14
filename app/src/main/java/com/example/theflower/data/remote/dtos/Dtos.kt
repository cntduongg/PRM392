package com.example.theflower.data.remote.dtos

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
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val userId: Int,
    val email: String,
    val fullName: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)

data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * ==================== PRODUCT ==================
 * Product and category related DTOs
 */

data class ProductDto(
    val id: Int,
    val name: String,
    val price: Int,
    val description: String,
    val image: String,
    val categoryId: Int,
    val categoryName: String,
    val stock: Int,
    val rating: Float,
    val reviewCount: Int
)

data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String,
    val image: String
)

/**
 * ==================== CART ==================
 * Shopping cart related DTOs
 */

data class CartDto(
    val id: Int,
    val userId: Int,
    val items: List<CartItemDto>,
    val totalPrice: Int,
    val totalItems: Int,
    val createdAt: String
)

data class CartItemDto(
    val id: Int,
    val cartId: Int,
    val productId: Int,
    val productName: String,
    val productImage: String,
    val productPrice: Int,
    val quantity: Int,
    val totalPrice: Int
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
    val id: Int,
    val userId: Int,
    val items: List<CartItemDto>,
    val totalPrice: Int,
    val recipientName: String,
    val recipientPhone: String,
    val recipientAddress: String,
    val occasion: String? = null,
    val message: String? = null,
    val status: String,  // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    val paymentMethod: String,
    val createdAt: String,
    val updatedAt: String
)

data class CreateOrderRequest(
    val items: List<CartItemDto>,
    val recipientName: String,
    val recipientPhone: String,
    val recipientAddress: String,
    val occasion: String? = null,
    val message: String? = null,
    val paymentMethod: String
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
