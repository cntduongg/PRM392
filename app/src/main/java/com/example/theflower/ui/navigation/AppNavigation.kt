package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.theflower.data.remote.dtos.CartDto
import com.example.theflower.data.remote.dtos.AdminUserDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.ui.components.BottomNavBar
import com.example.theflower.ui.components.NavTab
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown
import com.example.theflower.ui.theme.WarmPeach
import com.example.theflower.ui.viewmodels.AppViewModel
import kotlin.math.roundToInt

@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        if (!uiState.errorMessage.isNullOrBlank()) {
            snackBarHostState.showSnackbar(uiState.errorMessage!!)
            viewModel.clearError()
        }
    }

    if (!uiState.isLoggedIn) {
        AuthFlow(
            currentScreen = uiState.currentScreen,
            onLogin = viewModel::login,
            onRegister = viewModel::register,
            onGoToRegister = viewModel::navigateToRegister,
            onGoToLogin = viewModel::navigateToLogin,
            snackbarHostState = snackBarHostState
        )
        return
    }

    when (uiState.currentScreen) {
        "detail" -> ProductDetailApiScreen(
            product = uiState.selectedProductDto,
            onBack = viewModel::navigateBack,
            onAddToCart = { productId, quantity -> viewModel.addToCart(productId, quantity) }
        )

        "orders" -> OrdersApiScreen(
            orders = uiState.orders,
            onBack = viewModel::navigateBack
        )

        "admin_dashboard" -> AdminDashboardScreen(
            users = uiState.adminUsers,
            products = uiState.products,
            onBack = viewModel::navigateBack,
            onRefreshUsers = viewModel::loadAdminUsers,
            onRefreshProducts = viewModel::loadProducts,
            onCreateUser = viewModel::createAdminUser,
            onUpdateUser = viewModel::updateAdminUser,
            onDeleteUser = viewModel::deleteAdminUser,
            onCreateProduct = viewModel::createAdminProduct,
            onUpdateProduct = viewModel::updateAdminProduct,
            onDeleteProduct = viewModel::deleteAdminProduct
        )

        else -> MainAppLayout(
            currentTab = uiState.currentTab,
            products = uiState.products,
            searchQuery = uiState.searchQuery,
            cart = uiState.cart,
            userName = uiState.userName,
            userEmail = uiState.userEmail,
            userRole = uiState.userRole,
            checkoutAddress = uiState.checkoutAddress,
            onTabClick = viewModel::selectTab,
            onSearchChange = viewModel::updateSearchQuery,
            onProductClick = viewModel::navigateToProductDetail,
            onAddToCart = { productId -> viewModel.addToCart(productId) },
            onRemoveCartItem = viewModel::removeCartItem,
            onCheckoutAddressChange = viewModel::updateCheckoutAddress,
            onCreateOrder = viewModel::createOrder,
            onViewOrders = viewModel::navigateToOrders,
            onOpenAdminDashboard = viewModel::navigateToAdminDashboard,
            onRefreshProducts = viewModel::loadProducts,
            onRefreshCart = viewModel::loadCart,
            onLogout = viewModel::logout,
            snackbarHostState = snackBarHostState
        )
    }
}

@Composable
private fun AuthFlow(
    currentScreen: String,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onGoToRegister: () -> Unit,
    onGoToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        containerColor = PaperWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(PaperWhite)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            HeroHeader(
                title = "The Flower",
                subtitle = "Tặng hoa — tặng cả cảm xúc"
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (currentScreen == "register") {
                RegisterForm(
                    onRegister = onRegister,
                    onGoToLogin = onGoToLogin
                )
            } else {
                LoginForm(
                    onLogin = onLogin,
                    onGoToRegister = onGoToRegister
                )
            }
        }
    }
}

@Composable
private fun MainAppLayout(
    currentTab: NavTab,
    products: List<ProductDto>,
    searchQuery: String,
    cart: CartDto?,
    userName: String,
    userEmail: String,
    userRole: String,
    checkoutAddress: String,
    onTabClick: (NavTab) -> Unit,
    onSearchChange: (String) -> Unit,
    onProductClick: (ProductDto) -> Unit,
    onAddToCart: (Int) -> Unit,
    onRemoveCartItem: (Int) -> Unit,
    onCheckoutAddressChange: (String) -> Unit,
    onCreateOrder: () -> Unit,
    onViewOrders: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onRefreshProducts: () -> Unit,
    onRefreshCart: () -> Unit,
    onLogout: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val filteredProducts = if (searchQuery.isBlank()) {
        products
    } else {
        products.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.categoryName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = PaperWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(currentTab = currentTab, onTabClick = onTabClick)
        }
    ) { padding ->
        when (currentTab) {
            NavTab.HOME,
            NavTab.CATEGORY -> ProductListApiScreen(
                modifier = Modifier.padding(padding),
                title = if (currentTab == NavTab.HOME) "Sản phẩm nổi bật" else "Danh mục sản phẩm",
                searchQuery = searchQuery,
                products = filteredProducts,
                onSearchChange = onSearchChange,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onRefresh = onRefreshProducts
            )

            NavTab.CART -> CartApiScreen(
                modifier = Modifier.padding(padding),
                cart = cart,
                checkoutAddress = checkoutAddress,
                onCheckoutAddressChange = onCheckoutAddressChange,
                onCreateOrder = onCreateOrder,
                onRemoveItem = onRemoveCartItem,
                onRefresh = onRefreshCart
            )

            NavTab.PROFILE -> ProfileApiScreen(
                modifier = Modifier.padding(padding),
                userName = userName,
                userEmail = userEmail,
                userRole = userRole,
                onViewOrders = onViewOrders,
                onOpenAdminDashboard = onOpenAdminDashboard,
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun HeroHeader(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🌸", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SandDark)
        }
    }
}

@Composable
private fun LoginForm(
    onLogin: (String, String) -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Đăng nhập", style = MaterialTheme.typography.titleLarge, color = SoilBrown)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onLogin(email.trim(), password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Đăng nhập")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Chưa có tài khoản? Đăng ký",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoToRegister),
                style = MaterialTheme.typography.bodyMedium,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RegisterForm(
    onRegister: (String, String, String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tạo tài khoản", style = MaterialTheme.typography.titleLarge, color = SoilBrown)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên hiển thị") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onRegister(name.trim(), email.trim(), password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Đăng ký")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Đã có tài khoản? Đăng nhập",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoToLogin),
                style = MaterialTheme.typography.bodyMedium,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProductListApiScreen(
    modifier: Modifier = Modifier,
    title: String,
    searchQuery: String,
    products: List<ProductDto>,
    onSearchChange: (String) -> Unit,
    onProductClick: (ProductDto) -> Unit,
    onAddToCart: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Button(
                onClick = onRefresh,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand)
            ) {
                Text("Làm mới", color = SoilBrown)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Tìm sản phẩm") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (products.isEmpty()) {
            EmptyState(message = "Không có sản phẩm phù hợp")
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(products) { product ->
                ProductListItem(
                    product = product,
                    onClick = { onProductClick(product) },
                    onAdd = { onAddToCart(product.id) }
                )
            }
        }
    }
}

@Composable
private fun ProductListItem(
    product: ProductDto,
    onClick: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(WarmPeach, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌷")
            }

            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, color = SoilBrown)
                Text(product.categoryName, style = MaterialTheme.typography.bodySmall, color = SandDark)
                Spacer(modifier = Modifier.height(3.dp))
                Text(formatCurrency(product.price), style = MaterialTheme.typography.titleMedium, color = MossGreen)
            }

            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Thêm")
            }
        }
    }
}

@Composable
private fun ProductDetailApiScreen(
    product: ProductDto?,
    onBack: () -> Unit,
    onAddToCart: (Int, Int) -> Unit
) {
    var quantityText by remember { mutableStateOf("1") }

    if (product == null) {
        EmptyState(message = "Không có dữ liệu sản phẩm")
        return
    }

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

            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Sand)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(product.name, style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(product.categoryName, style = MaterialTheme.typography.bodyMedium, color = SandDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(formatCurrency(product.price), style = MaterialTheme.typography.titleLarge, color = MossGreen)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(product.description ?: "Không có mô tả", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it.filter(Char::isDigit) },
                singleLine = true,
                label = { Text("Số lượng") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val quantity = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    onAddToCart(product.id, quantity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Thêm vào giỏ")
            }
        }
    }
}

@Composable
private fun CartApiScreen(
    modifier: Modifier = Modifier,
    cart: CartDto?,
    checkoutAddress: String,
    onCheckoutAddressChange: (String) -> Unit,
    onCreateOrder: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    val items = cart?.items.orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Giỏ hàng", style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Button(
                onClick = onRefresh,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand)
            ) {
                Text("Làm mới", color = SoilBrown)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (items.isEmpty()) {
            EmptyState(message = "Giỏ hàng đang trống")
            return
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Sand)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.productName, style = MaterialTheme.typography.titleSmall, color = SoilBrown)
                            Text("Số lượng: ${item.quantity}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                            Text(formatCurrency(item.totalPrice), style = MaterialTheme.typography.titleSmall, color = MossGreen)
                        }
                        Button(
                            onClick = { onRemoveItem(item.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                        ) {
                            Text("Xóa")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Sand), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Tổng thanh toán", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                Spacer(modifier = Modifier.height(4.dp))
                Text(formatCurrency(cart?.totalPrice ?: 0.0), style = MaterialTheme.typography.headlineSmall, color = MossGreen)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = checkoutAddress,
                    onValueChange = onCheckoutAddressChange,
                    label = { Text("Địa chỉ giao hàng") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onCreateOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
                ) {
                    Text("Đặt hàng (COD)")
                }
            }
        }
    }
}

@Composable
private fun OrdersApiScreen(
    orders: List<OrderDto>,
    onBack: () -> Unit
) {
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
            Text("Đơn hàng của tôi", style = MaterialTheme.typography.headlineSmall, color = SoilBrown)
            Spacer(modifier = Modifier.height(10.dp))

            if (orders.isEmpty()) {
                EmptyState(message = "Chưa có đơn hàng")
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(orders) { order ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Sand)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Đơn #${order.id}", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                                StatusPill(order.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Phương thức: ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall)
                            Text("Thanh toán: ${order.paymentStatus}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(formatCurrency(order.totalPrice), style = MaterialTheme.typography.titleSmall, color = MossGreen)
                            Text(order.recipientAddress, style = MaterialTheme.typography.bodySmall, color = SandDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    Box(
        modifier = Modifier
            .background(MossGreen.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = MossGreen,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProfileApiScreen(
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String,
    userRole: String,
    onViewOrders: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Tài khoản", style = MaterialTheme.typography.headlineSmall, color = SoilBrown)

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Sand)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("👤 ${if (userName.isBlank()) "Người dùng" else userName}", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                Spacer(modifier = Modifier.height(4.dp))
                Text("✉️ ${if (userEmail.isBlank()) "(chưa có)" else userEmail}", style = MaterialTheme.typography.bodyMedium, color = SandDark)
            }
        }

        Button(
            onClick = onViewOrders,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Xem đơn hàng")
        }

        if (userRole.equals("ADMIN", ignoreCase = true)) {
            Button(
                onClick = onOpenAdminDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoilBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Admin Dashboard")
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Sand),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Đăng xuất", color = SoilBrown)
        }
    }
}

@Composable
private fun AdminDashboardScreen(
    users: List<AdminUserDto>,
    products: List<ProductDto>,
    onBack: () -> Unit,
    onRefreshUsers: () -> Unit,
    onRefreshProducts: () -> Unit,
    onCreateUser: (String, String, String, String, String, String) -> Unit,
    onUpdateUser: (Int, String, String, String, String, String, String) -> Unit,
    onDeleteUser: (Int) -> Unit,
    onCreateProduct: (String, String, String, String, String, String, String, String) -> Unit,
    onUpdateProduct: (Int, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteProduct: (Int) -> Unit
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
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("User Management") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Product Management") })
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (selectedTab == 0) {
                UserManagementSection(
                    users = users,
                    onRefreshUsers = onRefreshUsers,
                    onCreateUser = onCreateUser,
                    onUpdateUser = onUpdateUser,
                    onDeleteUser = onDeleteUser
                )
            } else {
                ProductManagementSection(
                    products = products,
                    onRefreshProducts = onRefreshProducts,
                    onCreateProduct = onCreateProduct,
                    onUpdateProduct = onUpdateProduct,
                    onDeleteProduct = onDeleteProduct
                )
            }
        }
    }
}

@Composable
private fun UserManagementSection(
    users: List<AdminUserDto>,
    onRefreshUsers: () -> Unit,
    onCreateUser: (String, String, String, String, String, String) -> Unit,
    onUpdateUser: (Int, String, String, String, String, String, String) -> Unit,
    onDeleteUser: (Int) -> Unit
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
        ) {
            Text("Làm mới", color = SoilBrown)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (Admin/Customer)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    onCreateUser(username, email, password, phone, address, role)
                    username = ""
                    email = ""
                    password = ""
                    phone = ""
                    address = ""
                    role = "Customer"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Tạo user")
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
    if (users.isEmpty()) {
        EmptyState(message = "Không có người dùng")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(users) { user ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Sand)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(user.username, style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                        StatusPill(user.role)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = SandDark)
                    Text("Role: ${user.role}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    if (!user.phoneNumber.isNullOrBlank()) {
                        Text("Phone: ${user.phoneNumber}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    }
                    if (!user.address.isNullOrBlank()) {
                        Text("Address: ${user.address}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onUpdateUser(
                                    user.userId,
                                    user.username,
                                    user.email,
                                    user.phoneNumber.orEmpty(),
                                    user.address.orEmpty(),
                                    user.role,
                                    ""
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoilBrown)
                        ) {
                            Text("Cập nhật")
                        }
                        Button(
                            onClick = { onDeleteUser(user.userId) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                        ) {
                            Text("Xóa")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductManagementSection(
    products: List<ProductDto>,
    onRefreshProducts: () -> Unit,
    onCreateProduct: (String, String, String, String, String, String, String, String) -> Unit,
    onUpdateProduct: (Int, String, String, String, String, String, String, String, String) -> Unit,
    onDeleteProduct: (Int) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("0") }
    var briefDescription by remember { mutableStateOf("") }
    var fullDescription by remember { mutableStateOf("") }
    var technicalSpecifications by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Quản lý sản phẩm", style = MaterialTheme.typography.titleMedium, color = SoilBrown)
        Button(
            onClick = onRefreshProducts,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Sand)
        ) {
            Text("Làm mới", color = SoilBrown)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Tên sản phẩm") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Giá") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = categoryId, onValueChange = { categoryId = it }, label = { Text("CategoryId") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = briefDescription, onValueChange = { briefDescription = it }, label = { Text("Mô tả ngắn") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fullDescription, onValueChange = { fullDescription = it }, label = { Text("Mô tả chi tiết") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = technicalSpecifications, onValueChange = { technicalSpecifications = it }, label = { Text("Thông số kỹ thuật") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    onCreateProduct(
                        productName,
                        price,
                        categoryId,
                        stock,
                        briefDescription,
                        fullDescription,
                        technicalSpecifications,
                        imageUrl
                    )
                    productName = ""
                    price = ""
                    categoryId = ""
                    stock = "0"
                    briefDescription = ""
                    fullDescription = ""
                    technicalSpecifications = ""
                    imageUrl = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("Tạo sản phẩm")
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
    if (products.isEmpty()) {
        EmptyState(message = "Không có sản phẩm")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(products) { product ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Sand)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(product.name, style = MaterialTheme.typography.titleMedium, color = SoilBrown)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Danh mục: ${product.categoryName}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    Text("Giá: ${formatCurrency(product.price)}", style = MaterialTheme.typography.bodySmall, color = MossGreen)
                    Text("Kho: ${product.stock}", style = MaterialTheme.typography.bodySmall, color = SandDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onUpdateProduct(
                                    product.id,
                                    product.name,
                                    product.price.toString(),
                                    product.categoryId.toString(),
                                    product.stock.toString(),
                                    product.briefDescription.orEmpty(),
                                    product.fullDescription.orEmpty(),
                                    product.technicalSpecifications.orEmpty(),
                                    product.image.orEmpty()
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoilBrown)
                        ) {
                            Text("Sửa")
                        }
                        Button(
                            onClick = { onDeleteProduct(product.id) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SandDark)
                        ) {
                            Text("Xóa")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = SandDark,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatCurrency(value: Double): String {
    return "₫${value.roundToInt()}"
}
