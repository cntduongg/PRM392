package com.example.theflower.data

import com.example.theflower.domain.models.CartItem
import com.example.theflower.domain.models.Notification
import com.example.theflower.domain.models.Occasion
import com.example.theflower.domain.models.Product
import com.example.theflower.domain.models.User

object MockData {
    val currentUser = User(
        id = "1",
        name = "Nguyá»…n HÃ²a",
        email = "hoa@example.com",
        phone = "0901234567",
        address = "123 ÄÆ°á»ng TÃ¢y Há»“, HÃ  Ná»™i"
    )

    val occasions = listOf(
        Occasion("1", "Sinh nháº­t", "ðŸŽ‚", "#F5E6D3"),
        Occasion("2", "TÃ¬nh yÃªu", "ðŸ’•", "#E8C4A0"),
        Occasion("3", "Tá»‘t nghiá»‡p", "ðŸŽ“", "#D4B5A0"),
        Occasion("4", "Cáº£m Æ¡n", "ðŸ™", "#E0D5C0"),
        Occasion("5", "Táº·ng máº¹", "ðŸ‘©", "#F0D4B8"),
        Occasion("6", "Chá»‰ vÃ¬ thÃ­ch", "âœ¨", "#E8D4B8")
    )

    val products = listOf(
        Product(
            id = "1",
            name = "Hoa Há»“ng Äá» Premium",
            price = 299000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Rose",
            origin = "ðŸŒ± ÄÃ  Láº¡t",
            description = "BÃ³ 20 cÃ¢y hoa há»“ng Ä‘á» tÆ°Æ¡i táº¯n, hÆ°Æ¡ng thÆ¡m nháº¹ nhÃ ng",
            stemCount = listOf(10, 20, 30),
            category = "Hoa Há»“ng"
        ),
        Product(
            id = "2",
            name = "Hoa Tulip Mix",
            price = 249000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Tulip",
            origin = "ðŸŒ± ÄÃ  Láº¡t",
            description = "BÃ³ hoa Tulip vá»›i cÃ¡c mÃ u sáº¯c tÆ°Æ¡i sÃ¡ng vÃ  báº¯t máº¯t",
            stemCount = listOf(10, 20, 30),
            category = "Hoa Tulip"
        ),
        Product(
            id = "3",
            name = "Hoa Máº·t Trá»i",
            price = 199000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Sunflower",
            origin = "ðŸŒ± ÄÃ  Láº¡t",
            description = "Hoa Máº·t Trá»i vÃ ng rá»±c rá»¡, tÆ°á»£ng trÆ°ng cho sá»± láº¡c quan",
            stemCount = listOf(5, 10, 15),
            category = "Hoa Máº·t Trá»i"
        ),
        Product(
            id = "4",
            name = "Hoa Cáº©m TÃº Cáº§u",
            price = 179000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Hydrangea",
            origin = "ðŸŒ± ÄÃ  Láº¡t",
            description = "BÃ³ hoa Cáº©m TÃº Cáº§u mÃ u tÃ­m lÃ£ng máº¡n",
            stemCount = listOf(3, 5, 7),
            category = "Hoa Cáº©m TÃº Cáº§u"
        ),
        Product(
            id = "5",
            name = "Hoa Lan Há»“ Äiá»‡p",
            price = 349000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Orchid",
            origin = "ðŸŒ± ÄÃ  Láº¡t",
            description = "Hoa Lan Há»“ Äiá»‡p sang trá»ng, bá»n lÃ¢u",
            stemCount = listOf(5, 10, 15),
            category = "Hoa Lan"
        ),
        Product(
            id = "6",
            name = "BÃ³ Hoa Láº«n Mixed",
            price = 399000,
            image = "https://via.placeholder.com/300x400/E8C4A0/3D2B1F?text=Mixed",
            origin = "ðŸŒ± ÄÃ  Láº¡t",
            description = "BÃ³ hoa láº«n nhiá»u loáº¡i, táº¡o sá»± phá»‘i há»£p Ä‘á»™c Ä‘Ã¡o",
            stemCount = listOf(20, 30, 40),
            category = "Mixed"
        )
    )

    val cartItems = listOf(
        CartItem(
            id = "1",
            product = products[0],
            quantity = 20,
            message = "Táº·ng em vá»›i yÃªu thÆ°Æ¡ng ðŸ’•"
        ),
        CartItem(
            id = "2",
            product = products[2],
            quantity = 10,
            message = ""
        )
    )

    val notifications = listOf(
        Notification(
            id = "1",
            title = "ÄÆ¡n hÃ ng #1012 Ä‘Ã£ Ä‘Æ°á»£c xÃ¡c nháº­n",
            message = "ÄÆ¡n hÃ ng cá»§a báº¡n Ä‘Ã£ Ä‘Æ°á»£c nhÃ¢n viÃªn xÃ¡c nháº­n vÃ  sáº½ sá»›m Ä‘Æ°á»£c giao",
            type = "success",
            createdAt = System.currentTimeMillis() - 3600000
        ),
        Notification(
            id = "2",
            title = "Khuyáº¿n mÃ£i má»›i tá»« The Flower",
            message = "Giáº£m 20% cho Ä‘Æ¡n hÃ ng trÃªn 500k. HÃ£y chá»n ngay hoa yÃªu thÃ­ch!",
            type = "info",
            createdAt = System.currentTimeMillis() - 7200000
        ),
        Notification(
            id = "3",
            title = "Giao hÃ ng thÃ nh cÃ´ng",
            message = "ÄÆ¡n hÃ ng #1000 Ä‘Ã£ Ä‘Æ°á»£c giao thÃ nh cÃ´ng. Cáº£m Æ¡n báº¡n!",
            type = "success",
            createdAt = System.currentTimeMillis() - 86400000,
            isRead = true
        )
    )

    val storeLocations = listOf(
        Pair("10.7769, 106.6966", "The Flower - Chi nhÃ¡nh TÃ¢y Há»“"),
        Pair("10.7882, 106.7507", "The Flower - Chi nhÃ¡nh Quáº­n 1"),
        Pair("10.8109, 106.6736", "The Flower - Chi nhÃ¡nh Thá»§ Äá»©c")
    )
}

