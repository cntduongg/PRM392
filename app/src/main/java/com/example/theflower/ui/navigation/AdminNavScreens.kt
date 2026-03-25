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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.domain.repositories.ChatConnectionStatus
import com.example.theflower.ui.viewmodels.AdminChatViewModel
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

// ─── CategoryManagementSection ────────────────────────────────────────────────

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryManagementSection(
    categories: List<CategoryDto>,
    errorMessage: String?,
    onRefreshCategories: () -> Unit,
    onCreateCategory: (String) -> Unit,
    onUpdateCategory: (String, String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onCategorySort: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    
    var showAddForm by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryDto?>(null) }

    fun clearFields() {
        categoryName = ""
        editingCategory = null
    }

    LaunchedEffect(editingCategory) {
        editingCategory?.let {
            categoryName = it.name
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh mục (${categories.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { 
                            clearFields()
                            showAddForm = !showAddForm 
                        },
                        modifier = Modifier.size(36.dp).background(MossGreen, CircleShape)
                    ) {
                        Text(if (showAddForm) "➖" else "➕", color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onRefreshCategories,
                        modifier = Modifier.size(36.dp).background(Sand, CircleShape)
                    ) {
                        Text("🔄", fontSize = 14.sp)
                    }
                }
            }

            // Categories Sorting UI
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sắp xếp:", style = MaterialTheme.typography.labelSmall, color = SoilBrown)
                for (sortField in listOf("Name")) {
                    AssistChip(
                        onClick = { onCategorySort(sortField) },
                        label = { Text(if (sortField == "Name") "Tên" else sortField, fontSize = 10.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Sand,
                            labelColor = SoilBrown
                        )
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                ErrorNote(message = errorMessage, modifier = Modifier.padding(bottom = 12.dp))
            }

            if (showAddForm || editingCategory != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (editingCategory != null) "Cập nhật danh mục 🔄" else "Thêm danh mục mới 📂", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = SoilBrown, 
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedTextField(
                            value = categoryName, 
                            onValueChange = { categoryName = it }, 
                            label = { Text("Tên danh mục") },
                            leadingIcon = { Text("🏷️") },
                            singleLine = true, 
                            colors = botanicalOutlinedTextFieldColors(), 
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { 
                                    showAddForm = false
                                    editingCategory = null
                                    clearFields()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                            ) { Text("Hủy", color = PaperWhite) }
                            Button(
                                onClick = {
                                    if (editingCategory != null) {
                                        onUpdateCategory(editingCategory!!.id, categoryName)
                                    } else {
                                        onCreateCategory(categoryName)
                                    }
                                    clearFields()
                                    showAddForm = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                            ) { Text(if (editingCategory != null) "Cập nhật" else "Tạo") }
                        }
                    }
                }
            }

            if (categories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(message = "Không có danh mục nào")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(categories) { category ->
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
                                AsyncImage(
                                    model = category.image,
                                    contentDescription = category.name,
                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(SandDark),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = category.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = SoilBrown,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row {
                                            IconButton(onClick = { editingCategory = category }) {
                                                Text("✏️", fontSize = 16.sp)
                                            }
                                            IconButton(onClick = { onDeleteCategory(category.id) }) {
                                                Text("🗑️", fontSize = 16.sp)
                                            }
                                        }
                                    }
                                    if (category.description.isNotBlank()) {
                                        Text(
                                            text = category.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SandDark,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── AdminDashboardScreen ─────────────────────────────────────────────────────
// Gọi bởi: AppNavigation() khi route = "admin_dashboard"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminDashboardScreen(
    users: List<AdminUserDto>,
    products: List<ProductDto>,
    orders: List<OrderDto>,
    categories: List<CategoryDto>,
    errorMessage: String?,
    adminChatVm: AdminChatViewModel,
    onBack: () -> Unit,
    onRefreshUsers: () -> Unit,
    onRefreshProducts: () -> Unit,
    onRefreshOrders: () -> Unit,
    onRefreshCategories: () -> Unit,
    onCreateUser: (String, String, String, String, String, String) -> Unit,
    onUpdateUser: (String, String, String, String, String, String, String) -> Unit,
    onDeleteUser: (String) -> Unit,
    onCreateProduct: (String, String, String, String, String, String, String, String) -> Unit,
    onUpdateProduct: (String, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onProductSort: (String) -> Unit,
    onCreateCategory: (String) -> Unit,
    onUpdateCategory: (String, String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onCategorySort: (String) -> Unit,
    onUserSort: (String) -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    dashboardStats: com.example.theflower.data.remote.dtos.DashboardStatsDto? = null
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
                .imePadding()
                .navigationBarsPadding()
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
                val tabs = listOf("Tổng quan", "Đơn hàng", "Thống kê", "Danh mục", "Sản phẩm", "Người dùng", "CSKH")
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
                        onRefreshOrders = onRefreshOrders,
                        onNavigateToTab = { selectedTab = it },
                        dashboardStats = dashboardStats
                    )
                    1 -> OrderManagementSection(
                        orders = orders,
                        onRefreshOrders = onRefreshOrders,
                        onUpdateOrderStatus = onUpdateOrderStatus
                    )
                    2 -> AdminStatisticsSection(
                        orders = orders,
                        products = products,
                        categories = categories,
                        dashboardStats = dashboardStats
                    )
                    3 -> CategoryManagementSection(
                        categories = categories,
                        errorMessage = errorMessage,
                        onRefreshCategories = onRefreshCategories,
                        onCreateCategory = onCreateCategory,
                        onUpdateCategory = onUpdateCategory,
                        onDeleteCategory = onDeleteCategory,
                        onCategorySort = onCategorySort
                    )
                    4 -> ProductManagementSection(
                        products = products,
                        categories = categories,
                        errorMessage = errorMessage,
                        onRefreshProducts = onRefreshProducts,
                        onCreateProduct = onCreateProduct,
                        onUpdateProduct = onUpdateProduct,
                        onDeleteProduct = onDeleteProduct,
                        onProductSort = onProductSort
                    )
                    5 -> UserManagementSection(
                        users = users,
                        errorMessage = errorMessage,
                        onRefreshUsers = onRefreshUsers,
                        onCreateUser = onCreateUser,
                        onUpdateUser = onUpdateUser,
                        onDeleteUser = onDeleteUser,
                        onUserSort = onUserSort
                    )
                    6 -> AdminChatSection(adminChatVm = adminChatVm)
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
    onRefreshOrders: () -> Unit,
    onNavigateToTab: (Int) -> Unit,
    dashboardStats: com.example.theflower.data.remote.dtos.DashboardStatsDto? = null
) {
    val adminCount = users.count { it.role.equals("Admin", ignoreCase = true) }
    val customerCount = users.count { it.role.equals("Customer", ignoreCase = true) }
    val lowStockCount = products.count { it.stock <= 5 }
    val totalRevenue = dashboardStats?.totalSales ?: orders.filter { !it.status.contains("cancel", ignoreCase = true) }.sumOf { it.totalPrice }
    val totalOrders = dashboardStats?.totalOrders ?: orders.size
    val totalProducts = dashboardStats?.totalProducts ?: products.size
    val totalUsers = dashboardStats?.totalUsers ?: users.size
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
                    color = MossGreen,
                    onClick = { onNavigateToTab(2) } // To Statistics
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Đơn hàng",
                    value = "$totalOrders",
                    icon = "🧾",
                    color = SoilBrown,
                    onClick = { onNavigateToTab(1) } // To Orders
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Người dùng",
                    value = "$totalUsers",
                    subtitle = "$adminCount AD, $customerCount KH",
                    icon = "👥",
                    color = SandDark,
                    onClick = { onNavigateToTab(5) } // To Users
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Cảnh báo kho",
                    value = "$lowStockCount",
                    subtitle = "Sắp hết hàng",
                    icon = "⚠️",
                    color = Color(0xFFE57373),
                    onClick = { onNavigateToTab(4) } // To Products
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            MonthlyRevenueChart(monthlyRevenue = monthlyRevenue)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Đơn hàng mới", style = MaterialTheme.typography.titleMedium, color = SoilBrown, fontWeight = FontWeight.Bold)
                Text(
                    "Xem tất cả", 
                    style = MaterialTheme.typography.labelMedium, 
                    color = MossGreen,
                    modifier = Modifier.clickable { onNavigateToTab(1) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val recentOrders = orders.reversed().take(5)
        if (recentOrders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Chưa có đơn hàng nào", color = SandDark)
                }
            }
        } else {
            items(recentOrders) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNavigateToTab(1) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Đơn #${order.id.takeLast(6)}", style = MaterialTheme.typography.titleSmall, color = SoilBrown)
                            Text(order.recipientName, style = MaterialTheme.typography.bodySmall, color = SandDark)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatCurrency(order.totalPrice), style = MaterialTheme.typography.titleSmall, color = MossGreen, fontWeight = FontWeight.Bold)
                            val statusColor = when {
                                order.status.contains("pending", true) -> Color(0xFFF57C00)
                                order.status.contains("success", true) || order.status.contains("completed", true) -> MossGreen
                                else -> Color.Red
                            }
                            Text(order.status, style = MaterialTheme.typography.labelSmall, color = statusColor)
                        }
                    }
                }
            }
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
    color: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
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
    onDeleteUser: (String) -> Unit,
    onUserSort: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Customer") }
    
    var showAddForm by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<AdminUserDto?>(null) }

    fun clearFields() {
        username = ""
        email = ""
        password = ""
        phone = ""
        address = ""
        role = "Customer"
        editingUser = null
    }

    LaunchedEffect(editingUser) {
        editingUser?.let {
            username = it.username
            email = it.email
            phone = it.phoneNumber ?: ""
            address = it.address ?: ""
            role = it.role
            password = "" // Don't pre-fill password for security
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Người dùng (${users.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { 
                            clearFields()
                            showAddForm = !showAddForm 
                        },
                        modifier = Modifier.size(36.dp).background(MossGreen, CircleShape)
                    ) {
                        Text(if (showAddForm) "➖" else "➕", color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onRefreshUsers,
                        modifier = Modifier.size(36.dp).background(Sand, CircleShape)
                    ) {
                        Text("🔄", fontSize = 14.sp)
                    }
                }
            }

            // User Sorting UI
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sắp xếp:", style = MaterialTheme.typography.labelSmall, color = SoilBrown)
                for (sortField in listOf("Name", "Email", "Role")) {
                    AssistChip(
                        onClick = { onUserSort(sortField) },
                        label = { Text(if (sortField == "Name") "Tên" else sortField, fontSize = 10.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Sand,
                            labelColor = SoilBrown
                        )
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                ErrorNote(message = errorMessage, modifier = Modifier.padding(bottom = 12.dp))
            }

            if (showAddForm || editingUser != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (editingUser != null) "Cập nhật người dùng 🔄" else "Thêm người dùng mới ✨", 
                            style = MaterialTheme.typography.titleSmall, 
                            color = SoilBrown, 
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Tên hiển thị") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        
                        if (editingUser == null) {
                            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mật khẩu") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        }
                        
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Địa chỉ") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (Admin/Customer)") }, singleLine = true, colors = botanicalOutlinedTextFieldColors(), modifier = Modifier.fillMaxWidth())
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { 
                                    showAddForm = false
                                    editingUser = null
                                    clearFields()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                            ) { Text("Hủy", color = PaperWhite) }
                            Button(
                                onClick = {
                                    if (editingUser != null) {
                                        onUpdateUser(editingUser!!.userId, username, email, phone, address, role, "")
                                    } else {
                                        onCreateUser(username, email, password, phone, address, role)
                                    }
                                    clearFields()
                                    showAddForm = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                            ) { Text(if (editingUser != null) "Cập nhật" else "Tạo") }
                        }
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
                    items(users) { user -> 
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
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically, 
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(user.username, style = MaterialTheme.typography.titleMedium, color = SoilBrown, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                            Row {
                                                IconButton(onClick = { editingUser = user }) {
                                                    Text("✏️", fontSize = 16.sp)
                                                }
                                                IconButton(onClick = { onDeleteUser(user.userId) }) {
                                                    Text("🗑️", fontSize = 16.sp)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = SandDark)
                                        }
                                        
                                        if (!user.phoneNumber.isNullOrBlank()) {
                                            Text("☎ ${user.phoneNumber}", style = MaterialTheme.typography.labelSmall, color = SandDark)
                                        }
                                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductManagementSection(
    products: List<ProductDto>,
    categories: List<CategoryDto>,
    errorMessage: String?,
    onRefreshProducts: () -> Unit,
    onCreateProduct: (String, String, String, String, String, String, String, String) -> Unit,
    onUpdateProduct: (String, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onProductSort: (String) -> Unit
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
    var editingProduct by remember { mutableStateOf<ProductDto?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    fun clearFields() {
        productName = ""
        price = ""
        categoryId = ""
        stock = "0"
        briefDescription = ""
        fullDescription = ""
        technicalSpecifications = ""
        imageUrl = ""
        editingProduct = null
    }

    LaunchedEffect(editingProduct) {
        editingProduct?.let {
            productName = it.name
            price = it.price.toString()
            categoryId = it.categoryId
            stock = it.stock.toString()
            briefDescription = it.briefDescription ?: ""
            fullDescription = it.fullDescription ?: ""
            technicalSpecifications = it.technicalSpecifications ?: ""
            imageUrl = it.image ?: ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sản phẩm (${products.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { 
                            clearFields()
                            showAddForm = !showAddForm 
                        },
                        modifier = Modifier.size(36.dp).background(MossGreen, CircleShape)
                    ) {
                        Text(if (showAddForm) "➖" else "➕", color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onRefreshProducts,
                        modifier = Modifier.size(36.dp).background(Sand, CircleShape)
                    ) {
                        Text("🔄", fontSize = 14.sp)
                    }
                }
            }

            // Products Sorting UI
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sắp xếp:", style = MaterialTheme.typography.labelSmall, color = SoilBrown)
                for (sortField in listOf("Name", "Price", "Stock")) {
                    AssistChip(
                        onClick = { onProductSort(sortField) },
                        label = { Text(if (sortField == "Name") "Tên" else if (sortField == "Price") "Giá" else "Kho", fontSize = 10.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Sand,
                            labelColor = SoilBrown
                        ),
                        border = null // Removed problematic AssistChipDefaults.assistChipBorder
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                ErrorNote(message = errorMessage, modifier = Modifier.padding(bottom = 12.dp))
            }

            if (showAddForm || editingProduct != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (editingProduct != null) "Cập nhật sản phẩm 🔄" else "Thêm sản phẩm mới 🏷️", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = SoilBrown, 
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedTextField(
                            value = productName, 
                            onValueChange = { productName = it }, 
                            label = { Text("Tên sản phẩm") },
                            leadingIcon = { Text("🏷️", modifier = Modifier.padding(start = 8.dp)) },
                            singleLine = true, 
                            colors = botanicalOutlinedTextFieldColors(), 
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Category Dropdown
                            ExposedDropdownMenuBox(
                                expanded = categoryExpanded,
                                onExpandedChange = { categoryExpanded = !categoryExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                val selectedCategory = categories.find { it.id == categoryId }
                                OutlinedTextField(
                                    value = selectedCategory?.name ?: "Danh mục",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Danh mục") },
                                    leadingIcon = { Text("📂") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                    colors = botanicalOutlinedTextFieldColors(),
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false },
                                    modifier = Modifier.background(Sand)
                                ) {
                                    categories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category.name, color = SoilBrown) },
                                            onClick = {
                                                categoryId = category.id
                                                categoryExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = price, 
                                onValueChange = { price = it }, 
                                label = { Text("Giá") },
                                leadingIcon = { Text("💰") },
                                singleLine = true, 
                                colors = botanicalOutlinedTextFieldColors(), 
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = stock, 
                                onValueChange = { stock = it }, 
                                label = { Text("Kho") },
                                leadingIcon = { Text("📦") },
                                singleLine = true, 
                                colors = botanicalOutlinedTextFieldColors(), 
                                modifier = Modifier.weight(0.7f)
                            )
                            OutlinedTextField(
                                value = imageUrl, 
                                onValueChange = { imageUrl = it }, 
                                label = { Text("Image URL") },
                                leadingIcon = { Text("🖼️") },
                                singleLine = true, 
                                colors = botanicalOutlinedTextFieldColors(), 
                                modifier = Modifier.weight(1.3f)
                            )
                        }

                        OutlinedTextField(
                            value = briefDescription, 
                            onValueChange = { briefDescription = it }, 
                            label = { Text("Mô tả ngắn") },
                            leadingIcon = { Text("📝") },
                            minLines = 2,
                            colors = botanicalOutlinedTextFieldColors(), 
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = fullDescription, 
                            onValueChange = { fullDescription = it }, 
                            label = { Text("Mô tả chi tiết") },
                            leadingIcon = { Text("📜") },
                            minLines = 3,
                            colors = botanicalOutlinedTextFieldColors(), 
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = technicalSpecifications, 
                            onValueChange = { technicalSpecifications = it }, 
                            label = { Text("Thông số kỹ thuật") },
                            leadingIcon = { Text("⚙️") },
                            minLines = 2,
                            colors = botanicalOutlinedTextFieldColors(), 
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { 
                                    showAddForm = false
                                    editingProduct = null
                                    clearFields()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                            ) { Text("Hủy", color = PaperWhite) }
                            Button(
                                onClick = {
                                    if (editingProduct != null) {
                                        onUpdateProduct(editingProduct!!.id, productName, price, categoryId, stock, briefDescription, fullDescription, technicalSpecifications, imageUrl)
                                    } else {
                                        onCreateProduct(productName, price, categoryId, stock, briefDescription, fullDescription, technicalSpecifications, imageUrl)
                                    }
                                    clearFields()
                                    showAddForm = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                            ) { Text(if (editingProduct != null) "Cập nhật" else "Tạo") }
                        }
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
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = SoilBrown,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row {
                                            IconButton(onClick = { editingProduct = product }) {
                                                Text("✏️", fontSize = 16.sp)
                                            }
                                            IconButton(onClick = { onDeleteProduct(product.id) }) {
                                                Text("🗑️", fontSize = 16.sp)
                                            }
                                        }
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
    val vnTimeZone = TimeZone.getTimeZone("GMT+7")
    val sdfMonth = SimpleDateFormat("MM/yy", Locale.getDefault()).apply { timeZone = vnTimeZone }
    val sdfKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).apply { timeZone = vnTimeZone }
    
    // Get last 6 months including current
    val months = (5 downTo 0).map { i ->
        val cal = Calendar.getInstance(vnTimeZone)
        cal.add(Calendar.MONTH, -i)
        cal
    }
    
    val revenueMap = mutableMapOf<String, Double>()
    months.forEach { cal -> 
        revenueMap[sdfKey.format(cal.time)] = 0.0 
    }
    
    orders
        .filter { !it.status.contains("cancel", ignoreCase = true) }
        .forEach { order ->
            val monthKey = parseOrderMonthKey(order.createdAt) ?: return@forEach
            if (revenueMap.containsKey(monthKey)) {
                revenueMap[monthKey] = (revenueMap[monthKey] ?: 0.0) + order.totalPrice
            }
        }
        
    return months.map { cal ->
        sdfMonth.format(cal.time) to (revenueMap[sdfKey.format(cal.time)] ?: 0.0)
    }
}

internal fun parseOrderMonthKey(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    // Assuming ISO date format: 2024-03-25... 
    // We just need the "yyyy-MM" part for grouping
    return if (raw.length >= 7) raw.substring(0, 7) else null
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
            var showClearConfirm by remember { mutableStateOf(false) }

            if (showClearConfirm) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showClearConfirm = false },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc muốn xóa toàn bộ lịch sử chat với người dùng này không?") },
                    confirmButton = {
                        androidx.compose.material3.TextButton(onClick = {
                            selectedUserId?.let { adminChatVm.clearUserHistory(it) }
                            showClearConfirm = false
                        }) { Text("Xóa", color = Color.Red) }
                    },
                    dismissButton = {
                        androidx.compose.material3.TextButton(onClick = { showClearConfirm = false }) { Text("Hủy") }
                    },
                    containerColor = PaperWhite
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = adminChatVm::clearSelectedUser,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand)
                ) { Text("← Trở về", color = SoilBrown) }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = { showClearConfirm = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
                ) { Text("🗑 Xóa lịch sử", color = Color.Red, fontSize = 12.sp) }

                Spacer(modifier = Modifier.width(16.dp))
                val targetConv = conversations.find { it.userId == selectedUserId }
                Text(
                    text = "Chat với ${targetConv?.userName ?: "Khách hàng"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    maxLines = 1
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
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = if (isAdmin) Alignment.End else Alignment.Start
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
                                    
                                    Text(
                                        text = formatTime(message.sentAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SandDark.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 2.dp, start = if (isAdmin) 0.dp else 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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

internal fun formatTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return ""
    return try {
        // Strip milliseconds if present
        val cleanIso = if (isoString.contains(".")) isoString.substringBefore(".") else isoString
        val vnTimeZone = TimeZone.getTimeZone("GMT+7")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = vnTimeZone
        }
        val date = inputFormat.parse(cleanIso)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = vnTimeZone
        }
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}

// ─── OrderManagementSection ───────────────────────────────────────────────────

@Composable
internal fun OrderManagementSection(
    orders: List<OrderDto>,
    onRefreshOrders: () -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quản lý đơn hàng (${orders.size})",
                style = MaterialTheme.typography.titleMedium,
                color = SoilBrown,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onRefreshOrders,
                modifier = Modifier.size(36.dp).background(Sand, CircleShape)
            ) {
                Text("🔄", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(message = "Không có đơn hàng nào")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(orders.reversed()) { order ->
                    OrderCard(order = order, onUpdateStatus = onUpdateOrderStatus)
                }
            }
        }
    }
}

@Composable
internal fun OrderCard(
    order: OrderDto,
    onUpdateStatus: (String, String) -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { showDetails = !showDetails }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Đơn hàng #${order.id.takeLast(8)}", style = MaterialTheme.typography.titleSmall, color = SoilBrown, fontWeight = FontWeight.Bold)
                    Text(order.createdAt, style = MaterialTheme.typography.labelSmall, color = SandDark)
                }
                val statusColor = when {
                    order.status.contains("pending", true) -> Color(0xFFF57C00)
                    order.status.contains("success", true) || order.status.contains("completed", true) -> MossGreen
                    order.status.contains("shipping", true) -> Color(0xFF2196F3)
                    else -> Color.Red
                }
                Box(
                    modifier = Modifier.background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(order.status, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Khách hàng: ${order.recipientName}", style = MaterialTheme.typography.bodyMedium, color = SoilBrown)
                Text(formatCurrency(order.totalPrice), style = MaterialTheme.typography.titleMedium, color = MossGreen, fontWeight = FontWeight.Bold)
            }

            if (showDetails) {
                Spacer(modifier = Modifier.height(12.dp))
                androidx.compose.material3.Divider(color = Sand.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Địa chỉ: ${order.recipientAddress}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                Text("SĐT: ${order.recipientPhone}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                
                if (!order.message.isNullOrBlank()) {
                    Text("Lời nhắn: ${order.message}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Sản phẩm:", style = MaterialTheme.typography.labelMedium, color = SoilBrown, fontWeight = FontWeight.Bold)
                order.items.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.productName} x${item.quantity}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                        Text(formatCurrency(item.subTotal), style = MaterialTheme.typography.bodySmall, color = SoilBrown)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (order.status.contains("pending", true)) {
                        Button(
                            onClick = { onUpdateStatus(order.id, "Shipping") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("Giao hàng", fontSize = 11.sp) }
                    }
                    if (order.status.contains("shipping", true)) {
                        Button(
                            onClick = { onUpdateStatus(order.id, "Completed") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("Hoàn tất", fontSize = 11.sp) }
                    }
                    Button(
                        onClick = { onUpdateStatus(order.id, "Cancelled") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("Hủy đơn", fontSize = 11.sp) }
                }
            }
        }
    }
}

// ─── AdminStatisticsSection ───────────────────────────────────────────────────

@Composable
internal fun AdminStatisticsSection(
    orders: List<OrderDto>,
    products: List<ProductDto>,
    categories: List<CategoryDto>,
    dashboardStats: com.example.theflower.data.remote.dtos.DashboardStatsDto? = null
) {
    val topProducts = remember(orders, dashboardStats) {
        dashboardStats?.topProducts?.map { item -> item.name to item.quantitySold } 
            ?: calculateTopProducts(orders).take(5)
    }
    
    val categoryRevenue = remember(orders, products, dashboardStats) {
        calculateCategoryRevenue(orders, products)
    }

    val statusBreakdown = remember(orders) {
        orders.groupBy { it.status }.mapValues { it.value.size }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Thống kê chi tiết", style = MaterialTheme.typography.titleMedium, color = SoilBrown, fontWeight = FontWeight.Bold)
        }
        
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top 5 Sản phẩm bán chạy", style = MaterialTheme.typography.titleSmall, color = SoilBrown, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val maxQty = topProducts.firstOrNull()?.second?.toFloat()?.coerceAtLeast(1f) ?: 1f
                    topProducts.forEach { (name, qty) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(name, style = MaterialTheme.typography.bodySmall, color = SoilBrown, maxLines = 1)
                                Text("$qty sp", style = MaterialTheme.typography.labelSmall, color = MossGreen, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(qty / maxQty)
                                    .height(8.dp)
                                    .background(MossGreen, RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Doanh thu theo danh mục", style = MaterialTheme.typography.titleSmall, color = SoilBrown, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    categoryRevenue.sortedByDescending { it.second }.forEach { (name, revenue) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, style = MaterialTheme.typography.bodySmall, color = SoilBrown)
                            Text(formatCurrency(revenue), style = MaterialTheme.typography.bodySmall, color = MossGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Trạng thái đơn hàng", style = MaterialTheme.typography.titleSmall, color = SoilBrown, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        statusBreakdown.forEach { (status, count) ->
                            val color = when {
                                status.contains("pending", true) -> Color(0xFFF57C00)
                                status.contains("success", true) || status.contains("completed", true) -> MossGreen
                                else -> Color.Red
                            }
                            Column(
                                modifier = Modifier.weight(1f).background(color.copy(alpha = 0.05f), RoundedCornerShape(8.dp)).padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$count", style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
                                Text(status.take(8), style = MaterialTheme.typography.labelSmall, color = SandDark, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun calculateTopProducts(orders: List<OrderDto>): List<Pair<String, Int>> {
    val successOrders = orders.filter { !it.status.contains("cancel", true) }
    val productSales = mutableMapOf<String, Int>()
    successOrders.forEach { order ->
        order.items.forEach { item ->
            productSales[item.productName] = (productSales[item.productName] ?: 0) + item.quantity
        }
    }
    return productSales.toList().sortedByDescending { it.second }
}

internal fun calculateCategoryRevenue(orders: List<OrderDto>, products: List<ProductDto>): List<Pair<String, Double>> {
    val successOrders = orders.filter { !it.status.contains("cancel", true) }
    // Create product to category mapping
    val productToCategory = products.associate { it.name to it.categoryName }
    
    val categoryRevenue = mutableMapOf<String, Double>()
    successOrders.forEach { order ->
        order.items.forEach { item ->
            val catName = productToCategory[item.productName] ?: "Chưa phân loại"
            categoryRevenue[catName] = (categoryRevenue[catName] ?: 0.0) + item.subTotal
        }
    }
    return categoryRevenue.toList()
}
