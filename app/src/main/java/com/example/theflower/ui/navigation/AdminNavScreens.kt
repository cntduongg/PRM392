package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.AdminUserDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.domain.repositories.ChatConnectionStatus
import com.example.theflower.ui.viewmodels.AdminChatViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminDashboardScreen(
    users: List<AdminUserDto>,
    products: List<ProductDto>,
    orders: List<OrderDto>,
    errorMessage: String?,
    adminChatVm: AdminChatViewModel,
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

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Admin Dashboard", style = MaterialTheme.typography.titleLarge, color = SoilBrown, fontWeight = FontWeight.Bold)
                        Text("Quản trị hệ thống", style = MaterialTheme.typography.labelMedium, color = SandDark)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp, color = SoilBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = PaperWhite,
                edgePadding = 16.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MossGreen
                        )
                    }
                }
            ) {
                val tabs = listOf("Tổng quan", "Người dùng", "Sản phẩm", "CSKH")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                color = if (selectedTab == index) MossGreen else SandDark,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (selectedTab) {
                    0 -> AdminOverviewSection(
                        users = users,
                        products = products,
                        orders = orders,
                        onRefreshUsers = onRefreshUsers,
                        onRefreshProducts = onRefreshProducts,
                        onRefreshOrders = onRefreshOrders
                    )
                    1 -> UserManagementSection(
                        users = users,
                        errorMessage = errorMessage,
                        onRefreshUsers = onRefreshUsers,
                        onCreateUser = onCreateUser,
                        onUpdateUser = onUpdateUser,
                        onDeleteUser = onDeleteUser
                    )
                    2 -> ProductManagementSection(
                        products = products,
                        errorMessage = errorMessage,
                        onRefreshProducts = onRefreshProducts,
                        onCreateProduct = onCreateProduct,
                        onUpdateProduct = onUpdateProduct,
                        onDeleteProduct = onDeleteProduct
                    )
                    3 -> AdminChatSection(adminChatVm = adminChatVm)
                }
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
    val adminCount = users.count { it.role.equals("Admin", ignoreCase = true) }
    val customerCount = users.count { it.role.equals("Customer", ignoreCase = true) }
    val lowStockCount = products.count { it.stock <= 5 }
    val successOrders = orders.filter { !it.status.contains("cancel", ignoreCase = true) }
    val totalRevenue = successOrders.sumOf { it.totalPrice }
    val monthlyRevenue = buildMonthlyRevenue(orders)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tổng quan", style = MaterialTheme.typography.titleMedium, color = SoilBrown, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onRefreshUsers, modifier = Modifier.size(36.dp).background(Sand, CircleShape)) { Text("🔄", fontSize = 14.sp) }
                    IconButton(onClick = onRefreshProducts, modifier = Modifier.size(36.dp).background(Sand, CircleShape)) { Text("📦", fontSize = 14.sp) }
                    IconButton(onClick = onRefreshOrders, modifier = Modifier.size(36.dp).background(Sand, CircleShape)) { Text("🧾", fontSize = 14.sp) }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Doanh thu",
                    value = formatCurrency(totalRevenue),
                    icon = "💰",
                    color = MossGreen
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Đơn hàng",
                    value = "${successOrders.size}",
                    icon = "🧾",
                    color = SoilBrown
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Người dùng",
                    value = "${users.size}",
                    subtitle = "$adminCount AD, $customerCount KH",
                    icon = "👥",
                    color = SandDark
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Cảnh báo kho",
                    value = "$lowStockCount",
                    subtitle = "Sắp hết hàng",
                    icon = "⚠️",
                    color = Color(0xFFE57373)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            MonthlyRevenueChart(monthlyRevenue = monthlyRevenue)
        }
    }
}

@Composable
internal fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    icon: String,
    color: Color
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = SoilBrown, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = SandDark)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
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
    var showAddForm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Người dùng (${users.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onRefreshUsers,
                    modifier = Modifier.size(36.dp).background(Sand, CircleShape)
                ) {
                    Text("🔄", fontSize = 14.sp)
                }
            }

            if (showAddForm) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Thêm người dùng mới", style = MaterialTheme.typography.titleSmall, color = SoilBrown, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Tên hiển thị") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mật khẩu") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Địa chỉ xuất") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (Admin/Customer)") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showAddForm = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                            ) { Text("Hủy", color = PaperWhite) }
                            Button(
                                onClick = {
                                    onCreateUser(username, email, password, phone, address, role)
                                    username = ""; email = ""; password = ""; phone = ""; address = ""; role = "Customer"
                                    showAddForm = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                            ) { Text("Tạo") }
                        }
                        if (!errorMessage.isNullOrBlank()) { ErrorNote(message = errorMessage) }
                    }
                }
            }

            if (users.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(message = "Không có người dùng nào")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                ) {
                    items(users.reversed()) { user -> // Show newest users at the top usually, assuming backend doesn't sort.
                        val initial = user.username.firstOrNull()?.uppercase() ?: "?"
                        val isAdmin = user.role.equals("Admin", ignoreCase = true)
                        
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PaperWhite),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar
                                    Box(
                                        modifier = Modifier.size(48.dp).background(if (isAdmin) SandDark else Sand, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(initial, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isAdmin) PaperWhite else SoilBrown)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    // Info
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(user.username, style = MaterialTheme.typography.titleMedium, color = SoilBrown, fontWeight = FontWeight.Bold)
                                            // Role Badge
                                            Box(
                                                modifier = Modifier.background(if (isAdmin) Color(0xFFE57373).copy(alpha = 0.2f) else MossGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = user.role,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isAdmin) Color(0xFFD32F2F) else MossGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(user.email, style = MaterialTheme.typography.bodySmall, color = SandDark)
                                        if (!user.phoneNumber.isNullOrBlank()) {
                                            Text("☎ ${user.phoneNumber}", style = MaterialTheme.typography.labelSmall, color = SandDark)
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateUser(user.userId, user.username, user.email, user.phoneNumber.orEmpty(), user.address.orEmpty(), user.role, "") },
                                        modifier = Modifier.size(32.dp).background(Sand, RoundedCornerShape(8.dp))
                                    ) { Text("✎", fontSize = 14.sp) }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { onDeleteUser(user.userId) },
                                        modifier = Modifier.size(32.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                    ) { Text("🗑", fontSize = 14.sp, color = Color.Red) }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add FAB
        androidx.compose.material3.FloatingActionButton(
            onClick = { showAddForm = !showAddForm },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 16.dp, end = 8.dp),
            containerColor = MossGreen,
            contentColor = PaperWhite,
            shape = CircleShape
        ) {
            Text(if (showAddForm) "✕" else "＋", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
    
    var showAddForm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sản phẩm (${products.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onRefreshProducts,
                    modifier = Modifier.size(36.dp).background(Sand, CircleShape)
                ) {
                    Text("🔄", fontSize = 14.sp)
                }
            }

            if (showAddForm) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Thêm sản phẩm mới", style = MaterialTheme.typography.titleSmall, color = SoilBrown, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Tên sản phẩm") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Giá") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = categoryId, onValueChange = { categoryId = it }, label = { Text("CategoryId") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = briefDescription, onValueChange = { briefDescription = it }, label = { Text("Mô tả ngắn") }, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = fullDescription, onValueChange = { fullDescription = it }, label = { Text("Mô tả chi tiết") }, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = technicalSpecifications, onValueChange = { technicalSpecifications = it }, label = { Text("Thông số kỹ thuật") }, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showAddForm = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                            ) { Text("Hủy", color = PaperWhite) }
                            Button(
                                onClick = {
                                    onCreateProduct(productName, price, categoryId, stock, briefDescription, fullDescription, technicalSpecifications, imageUrl)
                                    productName = ""; price = ""; categoryId = ""; stock = "0"; briefDescription = ""; fullDescription = ""; technicalSpecifications = ""; imageUrl = ""
                                    showAddForm = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                            ) { Text("Tạo") }
                        }
                        if (!errorMessage.isNullOrBlank()) { ErrorNote(message = errorMessage) }
                    }
                }
            }

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(message = "Không có sản phẩm nào")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                ) {
                    items(products.reversed()) { product ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PaperWhite),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail
                                AsyncImage(
                                    model = product.image,
                                    contentDescription = product.name,
                                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).background(SandDark),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                // Info
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = product.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = SoilBrown,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Danh mục: ${product.categoryName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SandDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = formatCurrency(product.price),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MossGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            if (product.stock <= 5) {
                                                val badgeColor = if (product.stock == 0) Color(0xFFD32F2F) else Color(0xFFF57C00)
                                                val badgeBg = if (product.stock == 0) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
                                                Box(modifier = Modifier.background(badgeBg, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                                    Text(if (product.stock == 0) "Hết hàng" else "Sắp hết", fontSize = 10.sp, color = badgeColor, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Text(
                                                text = "Kho: ${product.stock}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (product.stock <= 5) Color(0xFFD32F2F) else SandDark
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                // Actions vertically stacked
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { onUpdateProduct(product.id, product.name, product.price.toString(), product.categoryId.toString(), product.stock.toString(), product.briefDescription.orEmpty(), product.fullDescription.orEmpty(), product.technicalSpecifications.orEmpty(), product.image.orEmpty()) },
                                        modifier = Modifier.size(32.dp).background(Sand, RoundedCornerShape(8.dp))
                                    ) { Text("✎", fontSize = 14.sp) }
                                    IconButton(
                                        onClick = { onDeleteProduct(product.id) },
                                        modifier = Modifier.size(32.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                    ) { Text("🗑", fontSize = 14.sp, color = Color.Red) }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add FAB
        androidx.compose.material3.FloatingActionButton(
            onClick = { showAddForm = !showAddForm },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 16.dp, end = 8.dp),
            containerColor = MossGreen,
            contentColor = PaperWhite,
            shape = CircleShape
        ) {
            Text(if (showAddForm) "✕" else "＋", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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

// ─── AdminChatSection ─────────────────────────────────────────────────────────

@Composable
internal fun AdminChatSection(adminChatVm: AdminChatViewModel) {
    val conversations by adminChatVm.conversations.collectAsState()
    val selectedUserId by adminChatVm.selectedUserId.collectAsState()
    val messages by adminChatVm.messages.collectAsState()
    val status by adminChatVm.connectionStatus.collectAsState()

    var inputText by remember { mutableStateOf("") }

    if (selectedUserId == null) {
        // Show Conversations List
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hỗ trợ khách hàng", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                Button(
                    onClick = adminChatVm::loadConversations,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand)
                ) { Text("Làm mới", color = SoilBrown) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            if (conversations.isEmpty()) {
                EmptyState(message = "Không có cuộc trò chuyện nào")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(conversations) { conv ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Sand),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { adminChatVm.selectUser(conv.userId) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MossGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = conv.userName.take(1).uppercase(),
                                        color = PaperWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(conv.userName, style = MaterialTheme.typography.titleSmall, color = SoilBrown)
                                    Text(
                                        if (conv.lastMessage.length > 30) conv.lastMessage.take(30) + "..." else conv.lastMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SandDark
                                    )
                                }
                                if (conv.unreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${conv.unreadCount}", color = PaperWhite, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Show Chat Detail
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = adminChatVm::clearSelectedUser,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand)
                ) { Text("← Trở về", color = SoilBrown) }
                Spacer(modifier = Modifier.width(16.dp))
                val targetConv = conversations.find { it.userId == selectedUserId }
                Text(
                    text = "Chat với ${targetConv?.userName ?: "Khách hàng"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown
                )
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(PaperWhite, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                if (messages.isEmpty() && status == ChatConnectionStatus.CONNECTED) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có tin nhắn nào được ghi nhận", color = SandDark)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = true
                    ) {
                        // Reverse messages to show latest at bottom
                        items(messages.reversed()) { message ->
                            val isAdmin = message.isFromAdmin
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = if (isAdmin) Arrangement.End else Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isAdmin) MossGreen else Sand,
                                            shape = RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isAdmin) 16.dp else 4.dp,
                                                bottomEnd = if (isAdmin) 4.dp else 16.dp
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = message.message,
                                        color = if (isAdmin) PaperWhite else SoilBrown,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Trả lời...", color = SandDark) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = botanicalOutlinedTextFieldColors()
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        adminChatVm.sendReply(inputText)
                        inputText = ""
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    modifier = Modifier.size(50.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("➤", fontSize = 18.sp, color = PaperWhite)
                }
            }
        }
    }
}
