package com.example.theflower.data

import com.example.theflower.domain.models.CartItem
import com.example.theflower.domain.models.Notification
import com.example.theflower.domain.models.Occasion
import com.example.theflower.domain.models.Product
import com.example.theflower.domain.models.User

object MockData {
    val currentUser = User(
        id = 1,
        name = "Nguyễn Hòa",
        email = "hoa@example.com",
        phone = "0901234567",
        address = "123 Đường Tây Hồ, Hà Nội"
    )

    val occasions = listOf(
        Occasion(1, "Sinh nhật", "🎂", "#F5E6D3"),
        Occasion(2, "Tình yêu", "💕", "#E8C4A0"),
        Occasion(3, "Tốt nghiệp", "🎓", "#D4B5A0"),
        Occasion(4, "Cảm ơn", "🙏", "#E0D5C0"),
        Occasion(5, "Tặng mẹ", "👩", "#F0D4B8"),
        Occasion(6, "Chỉ vì thích", "✨", "#E8D4B8")
    )

    val products = listOf(
        Product(
            id = 1,
            name = "Hoa Hồng Đỏ Premium",
            price = 299000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Rose",
            origin = "🌱 Đà Lạt",
            description = "Bó 20 cây hoa hồng đỏ tươi tắn, hương thơm nhẹ nhàng",
            stemCount = listOf(10, 20, 30),
            category = "Hoa Hồng"
        ),
        Product(
            id = 2,
            name = "Hoa Tulip Mix",
            price = 249000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Tulip",
            origin = "🌱 Đà Lạt",
            description = "Bó hoa Tulip với các màu sắc tươi sáng và bắt mắt",
            stemCount = listOf(10, 20, 30),
            category = "Hoa Tulip"
        ),
        Product(
            id = 3,
            name = "Hoa Mặt Trời",
            price = 199000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Sunflower",
            origin = "🌱 Đà Lạt",
            description = "Hoa Mặt Trời vàng rực rỡ, tượng trưng cho sự lạc quan",
            stemCount = listOf(5, 10, 15),
            category = "Hoa Mặt Trời"
        ),
        Product(
            id = 4,
            name = "Hoa Cẩm Tú Cầu",
            price = 179000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Hydrangea",
            origin = "🌱 Đà Lạt",
            description = "Bó hoa Cẩm Tú Cầu màu tím lãng mạn",
            stemCount = listOf(3, 5, 7),
            category = "Hoa Cẩm Tú Cầu"
        ),
        Product(
            id = 5,
            name = "Hoa Lan Hồ Điệp",
            price = 349000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Orchid",
            origin = "🌱 Đà Lạt",
            description = "Hoa Lan Hồ Điệp sang trọng, bền lâu",
            stemCount = listOf(5, 10, 15),
            category = "Hoa Lan"
        ),
        Product(
            id = 6,
            name = "Bó Hoa Lẫn Mixed",
            price = 399000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Mixed",
            origin = "🌱 Đà Lạt",
            description = "Bó hoa lẫn nhiều loại, tạo sự phối hợp độc đáo",
            stemCount = listOf(20, 30, 40),
            category = "Mixed"
        )
    )

    val cartItems = listOf(
        CartItem(
            id = 1,
            product = products[0],
            quantity = 20,
            message = "Tặng em với yêu thương 💕"
        ),
        CartItem(
            id = 2,
            product = products[2],
            quantity = 10,
            message = ""
        )
    )

    val notifications = listOf(
        Notification(
            id = 1,
            title = "Đơn hàng #1012 đã được xác nhận",
            message = "Đơn hàng của bạn đã được nhân viên xác nhận và sẽ sớm được giao",
            type = "success",
            createdAt = System.currentTimeMillis() - 3600000
        ),
        Notification(
            id = 2,
            title = "Khuyến mãi mới từ The Flower",
            message = "Giảm 20% cho đơn hàng trên 500k. Hãy chọn ngay hoa yêu thích!",
            type = "info",
            createdAt = System.currentTimeMillis() - 7200000
        ),
        Notification(
            id = 3,
            title = "Giao hàng thành công",
            message = "Đơn hàng #1000 đã được giao thành công. Cảm ơn bạn!",
            type = "success",
            createdAt = System.currentTimeMillis() - 86400000,
            isRead = true
        )
    )

    val storeLocations = listOf(
        Pair("10.7769, 106.6966", "The Flower - Chi nhánh Tây Hồ"),
        Pair("10.7882, 106.7507", "The Flower - Chi nhánh Quận 1"),
        Pair("10.8109, 106.6736", "The Flower - Chi nhánh Thủ Đức")
    )
}
