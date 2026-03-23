package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.theflower.data.remote.dtos.AdminUserDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown
import java.time.OffsetDateTime
import java.time.YearMonth
import kotlin.math.roundToInt

// ─── AdminDashboardScreen ─────────────────────────────────────────────────────
// Gọi bởi: AppNavigation() khi route = "admin_dashboard"

@Composable
internal fun AdminDashboardScreen(
    users: List<AdminUserDto>,
    products: List<ProductDto>,
    orders: List<OrderDto>,
    errorMessage: String?,
    onBack: () -> Unit,
    onRefreshUsers: () -> Unit,
    onRefreshProducts: () -> Unit,
    onRefreshOrders: () -> Unit,
    onCreateUser: (String, String, String, String, String, String) -> Unit,
    onUpdateUser: (String, String, String, String, String, String, String) -> Unit,
    onDeleteUser: (String) -> Unit,
    onCreateProduct: (String, String, String, String, String, String, String, String) -> Unit,
    onUpdateProduct: (String, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(containerColor = PaperWhite) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand)
            ) {
                Text("Quay lại", color = SoilBrown)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Admin Dashboard", style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Text("Quản trị hệ thống", style = MaterialTheme.typography.bodyMedium, color = SandDark)

            Spacer(modifier = Modifier.height(12.dp))
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("User Management") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Product Management") })
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (selectedTab == 0) {
                AdminOverviewSection(
                    users = users,
                    products = products,
                    orders = orders,
                    onRefreshUsers = onRefreshUsers,
                    onRefreshProducts = onRefreshProducts,
                    onRefreshOrders = onRefreshOrders
                )
            } else if (selectedTab == 1) {
                UserManagementSection(
                    users = users,
                    errorMessage = errorMessage,
                    onRefreshUsers = onRefreshUsers,
                    onCreateUser = onCreateUser,
                    onUpdateUser = onUpdateUser,
                    onDeleteUser = onDeleteUser
                )
            } else {
                ProductManagementSection(
                    products = products,
                    errorMessage = errorMessage,
                    onRefreshProducts = onRefreshProducts,
                    onCreateProduct = onCreateProduct,
                    onUpdateProduct = onUpdateProduct,
                    onDeleteProduct = onDeleteProduct
                )
            }
        }
    }
}

// ─── AdminOverviewSection ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminOverviewSection(
    users: List<AdminUserDto>,
    products: List<ProductDto>,
    orders: List<OrderDto>,
    onRefreshUsers: () -> Unit,
    onRefreshProducts: () -> Unit,
    onRefreshOrders: () -> Unit
) {
    val options = listOf("Tất cả", "User", "Sản phẩm", "Order", "Doanh thu theo tháng")
    var selectedOption by remember { mutableStateOf(options.first()) }
    var expanded by remember { mutableStateOf(false) }

    val adminCount = users.count { it.role.equals("Admin", ignoreCase = true) }
    val customerCount = users.count { it.role.equals("Customer", ignoreCase = true) }
    val lowStockCount = products.count { it.stock <= 5 }
    val totalStock = products.sumOf { it.stock }
    val cancelledOrders = orders.count { it.status.contains("cancel", ignoreCase = true) }
    val successOrders = orders.count { !it.status.contains("cancel", ignoreCase = true) }
    val totalRevenue = orders
        .filter { !it.status.contains("cancel", ignoreCase = true) }
        .sumOf { it.totalPrice }
    val monthlyRevenue = buildMonthlyRevenue(orders)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onRefreshUsers, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Sand)) {
                Text("Refresh User", color = SoilBrown)
            }
            Button(onClick = onRefreshProducts, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Sand)) {
                Text("Refresh Product", color = SoilBrown)
            }
            Button(onClick = onRefreshOrders, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Sand)) {
                Text("Refresh Order", color = SoilBrown)
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                label = { Text("Chọn thống kê") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = botanicalOutlinedTextFieldColors()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { selectedOption = option; expanded = false }
                    )
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (selectedOption == "Tất cả" || selectedOption == "User") {
                item {
                    OverviewStatCard(
                        title = "Thống kê User",
                        lines = listOf("Tổng user: ${users.size}", "Admin: $adminCount", "Customer: $customerCount")
                    )
                }
            }
            if (selectedOption == "Tất cả" || selectedOption == "Sản phẩm") {
                item {
                    OverviewStatCard(
                        title = "Thống kê sản phẩm",
                        lines = listOf("Tổng sản phẩm: ${products.size}", "Tổng tồn kho: $totalStock", "Sắp hết hàng (<=5): $lowStockCount")
                    )
                }
            }
            if (selectedOption == "Tất cả" || selectedOption == "Order") {
                item {
                    OverviewStatCard(
                        title = "Thống kê Order",
                        lines = listOf(
                            "Tổng order: ${orders.size}",
                            "Order thành công: $successOrders",
                            "Order hủy: $cancelledOrders",
                            "Doanh thu (không tính hủy): ${formatCurrency(totalRevenue)}"
                        )
                    )
                }
            }
            if (selectedOption == "Tất cả" || selectedOption == "Doanh thu theo tháng") {
                item { MonthlyRevenueChart(monthlyRevenue = monthlyRevenue) }
            }
        }
    }
}

// ─── OverviewStatCard ─────────────────────────────────────────────────────────

@Composable
internal fun OverviewStatCard(title: String, lines: List<String>) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = SoilBrown)
            lines.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall, color = SandDark)
            }
        }
    }
}

// ─── MonthlyRevenueChart ──────────────────────────────────────────────────────

@Composable
internal fun MonthlyRevenueChart(monthlyRevenue: List<Pair<String, Double>>) {
    val maxRevenue = (monthlyRevenue.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Doanh thu theo tháng", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                monthlyRevenue.forEach { (monthLabel, revenue) ->
                    val ratio = (revenue / maxRevenue).toFloat().coerceIn(0f, 1f)
                    val barHeight = (ratio * 120f).dp
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = if (revenue <= 0.0) "0" else "${(revenue / 1000000.0).roundToInt()}M",
                            style = MaterialTheme.typography.labelSmall,
                            color = SandDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(barHeight)
                                .background(MossGreen, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(monthLabel, style = MaterialTheme.typography.labelSmall, color = SoilBrown)
                    }
                }
            }
        }
    }
}

// ─── UserManagementSection ────────────────────────────────────────────────────

@Composable
internal fun UserManagementSection(
    users: List<AdminUserDto>,
    errorMessage: String?,
    onRefreshUsers: () -> Unit,
    onCreateUser: (String, String, String, String, String, String) -> Unit,
    onUpdateUser: (String, String, String, String, String, String, String) -> Unit,
    onDeleteUser: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Customer") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Quản lý người dùng", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
        Button(
            onClick = onRefreshUsers,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Sand)
        ) { Text("Làm mới", color = SoilBrown) }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (Admin/Customer)") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    onCreateUser(username, email, password, phone, address, role)
                    username = ""; email = ""; password = ""; phone = ""; address = ""; role = "Customer"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) { Text("Tạo user") }
            if (!errorMessage.isNullOrBlank()) { ErrorNote(message = errorMessage) }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
    if (users.isEmpty()) { EmptyState(message = "Không có người dùng"); return }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(users) { user ->
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(user.username, style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                        StatusPill(user.role)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = SandDark)
                    Text("Role: ${user.role}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    if (!user.phoneNumber.isNullOrBlank()) Text("Phone: ${user.phoneNumber}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    if (!user.address.isNullOrBlank()) Text("Address: ${user.address}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onUpdateUser(user.userId, user.username, user.email, user.phoneNumber.orEmpty(), user.address.orEmpty(), user.role, "") },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoilBrown)
                        ) { Text("Cập nhật") }
                        Button(
                            onClick = { onDeleteUser(user.userId) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                        ) { Text("Xóa") }
                    }
                }
            }
        }
    }
}

// ─── ProductManagementSection ─────────────────────────────────────────────────

@Composable
internal fun ProductManagementSection(
    products: List<ProductDto>,
    errorMessage: String?,
    onRefreshProducts: () -> Unit,
    onCreateProduct: (String, String, String, String, String, String, String, String) -> Unit,
    onUpdateProduct: (String, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("0") }
    var briefDescription by remember { mutableStateOf("") }
    var fullDescription by remember { mutableStateOf("") }
    var technicalSpecifications by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Quản lý sản phẩm", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
        Button(onClick = onRefreshProducts, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), colors = ButtonDefaults.buttonColors(containerColor = Sand)) {
            Text("Làm mới", color = SoilBrown)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Tên sản phẩm") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Giá") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = categoryId, onValueChange = { categoryId = it }, label = { Text("CategoryId") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = briefDescription, onValueChange = { briefDescription = it }, label = { Text("Mô tả ngắn") }, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fullDescription, onValueChange = { fullDescription = it }, label = { Text("Mô tả chi tiết") }, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = technicalSpecifications, onValueChange = { technicalSpecifications = it }, label = { Text("Thông số kỹ thuật") }, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    onCreateProduct(productName, price, categoryId, stock, briefDescription, fullDescription, technicalSpecifications, imageUrl)
                    productName = ""; price = ""; categoryId = ""; stock = "0"; briefDescription = ""; fullDescription = ""; technicalSpecifications = ""; imageUrl = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) { Text("Tạo sản phẩm") }
            if (!errorMessage.isNullOrBlank()) { ErrorNote(message = errorMessage) }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
    if (products.isEmpty()) { EmptyState(message = "Không có sản phẩm"); return }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(products) { product ->
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(product.name, style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Danh mục: ${product.categoryName}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    Text("Giá: ${formatCurrency(product.price)}", style = MaterialTheme.typography.bodySmall, color = MossGreen)
                    Text("Kho: ${product.stock}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onUpdateProduct(product.id, product.name, product.price.toString(), product.categoryId.toString(), product.stock.toString(), product.briefDescription.orEmpty(), product.fullDescription.orEmpty(), product.technicalSpecifications.orEmpty(), product.image.orEmpty()) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoilBrown)
                        ) { Text("Sửa") }
                        Button(
                            onClick = { onDeleteProduct(product.id) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                        ) { Text("Xóa") }
                    }
                }
            }
        }
    }
}

// ─── Helper functions ─────────────────────────────────────────────────────────

internal fun buildMonthlyRevenue(orders: List<OrderDto>): List<Pair<String, Double>> {
    val months = (5 downTo 0).map { YearMonth.now().minusMonths(it.toLong()) }
    val revenueMap = mutableMapOf<YearMonth, Double>()
    months.forEach { revenueMap[it] = 0.0 }
    orders
        .filter { !it.status.contains("cancel", ignoreCase = true) }
        .forEach { order ->
            val month = parseOrderYearMonth(order.createdAt) ?: return@forEach
            if (month in revenueMap.keys) {
                revenueMap[month] = (revenueMap[month] ?: 0.0) + order.totalPrice
            }
        }
    return months.map { month ->
        "${month.monthValue}/${month.year.toString().takeLast(2)}" to (revenueMap[month] ?: 0.0)
    }
}

internal fun parseOrderYearMonth(raw: String?): YearMonth? {
    if (raw.isNullOrBlank()) return null
    if (raw.length >= 7 && raw[4] == '-') {
        val ymText = raw.substring(0, 7)
        runCatching { return YearMonth.parse(ymText) }
    }
    return runCatching { YearMonth.from(OffsetDateTime.parse(raw)) }.getOrNull()
}
