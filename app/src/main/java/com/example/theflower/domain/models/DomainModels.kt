package com.example.theflower.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val id: String = "1",
    val name: String = "Nguyễn Hòa",
    val email: String = "hoa@example.com",
    val phone: String = "0901234567",
    val avatar: String = "https://via.placeholder.com/100",
    val address: String = "123 Đường Tây Hồ, Hà Nội"
)

@Immutable
data class Product(
    val id: String,
    val name: String,
    val price: Int, // in VND
    val image: String,
    val origin: String, // e.g., "🌱 Đà Lạt"
    val description: String,
    val stemCount: List<Int> = listOf(10, 20, 30),
    val rating: Float = 4.5f,
    val reviews: Int = 128,
    val category: String
)

@Immutable
data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int, // number of stems
    val message: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Immutable
data class Occasion(
    val id: String,
    val name: String,
    val emoji: String,
    val color: String // Hex color code
)

@Immutable
data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalPrice: Int,
    val recipientName: String = "",
    val recipientPhone: String = "",
    val deliveryAddress: String = "",
    val deliveryDate: String = "",
    val message: String = "",
    val status: String = "Pending" // Processing, Delivered, Cancelled
)

@Immutable
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // order, delivery, chat, promotion
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Immutable
data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

@Immutable
data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: Long,
    val isFromUser: Boolean
)
