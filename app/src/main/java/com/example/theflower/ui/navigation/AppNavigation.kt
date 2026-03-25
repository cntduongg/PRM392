package com.example.theflower.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.theflower.ProductCatalogActivity
import com.example.theflower.ui.components.AppDrawerContent
import com.example.theflower.ui.components.AppNotification
import com.example.theflower.ui.components.AppTopBar
import com.example.theflower.ui.components.NavTab
import com.example.theflower.ui.components.NotificationModal
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.viewmodels.AdminChatViewModel
import com.example.theflower.ui.viewmodels.AppViewModel
import com.example.theflower.ui.viewmodels.ChatViewModel
import com.example.theflower.ui.screens.chat.ChatScreen
import com.example.theflower.ui.screens.payment.PaymentSuccessScreen
import com.example.theflower.ui.screens.payment.PaymentCancelScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════════════
// AppNavigation.kt  –  Entry point & routing
//
// Route map:
//   !isLoggedIn          → AuthNavScreens.kt   :: AuthFlow
//   "detail"             → ProductNavScreens.kt :: ProductDetailApiScreen
//   "orders"             → OrdersNavScreen.kt   :: OrdersApiScreen
//   "category_detail"    → CategoryNavScreens.kt :: CategoryProductsScreen
//   "admin_dashboard"    → AdminNavScreens.kt   :: AdminDashboardScreen
//   else (tabs)          → MainAppLayout dispatches by NavTab
//     NavTab.HOME        → ProductNavScreens.kt :: ProductListApiScreen
//     NavTab.CATEGORY    → CategoryNavScreens.kt :: CategoryListScreen
//     NavTab.CART        → CartNavScreen.kt     :: CartApiScreen
//     NavTab.PROFILE     → ProfileNavScreen.kt  :: ProfileApiScreen
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val navigationEvent by viewModel.navigationEvent.collectAsState()

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            if (event is com.example.theflower.ui.viewmodels.NavigationEvent.OpenUrl) {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(event.url))
                context.startActivity(intent)
            }
            viewModel.captureNavigationEvent()
        }
    }

    val visibleErrorMessage = uiState.errorMessage?.takeUnless {
        it.contains("Unauthorized", ignoreCase = true)
    }

    // ── Not logged in ──────────────────────────────────────────────────────────
    if (!uiState.isLoggedIn) {
        AuthFlow(
            currentScreen = uiState.currentScreen,
            errorMessage = visibleErrorMessage,
            onLogin = viewModel::login,
            onRegister = viewModel::register,
            onGoToRegister = viewModel::navigateToRegister,
            onGoToLogin = viewModel::navigateToLogin,
            snackbarHostState = snackBarHostState
        )
        return
    }

    // ── Logged-in route dispatch ───────────────────────────────────────────────
    when (uiState.currentScreen) {

        "detail" -> ProductDetailApiScreen(
            product = uiState.selectedProductDto,
            onBack = viewModel::navigateBack,
            pendingOrder = uiState.pendingOrder,
            onAddToCart = { productId, quantity -> viewModel.addToCart(productId, quantity) },
            onPayPendingOrder = viewModel::createPaymentForPendingOrder
        )

        "orders" -> OrdersApiScreen(
            orders = uiState.orders,
            pendingOrder = uiState.pendingOrder,
            onBack = viewModel::navigateBack,
            onPayPendingOrder = viewModel::createPaymentForPendingOrder
        )

        "category_detail" -> CategoryProductsScreen(
            category = uiState.selectedCategory,
            products = uiState.products,
            currentTab = uiState.currentTab,
            cartItemCount = uiState.cart?.totalItems ?: 0,
            pendingOrder = uiState.pendingOrder,
            onBack = viewModel::navigateBack,
            onTabClick = viewModel::selectTab,
            onProductClick = viewModel::navigateToProductDetail,
            onAddToCart = { productId -> viewModel.addToCart(productId) },
            onPayPendingOrder = viewModel::createPaymentForPendingOrder
        )

        "admin_dashboard" -> {
            val adminChatVm: AdminChatViewModel = viewModel()
            
            // Client-side sorting for Admin
            val sortedUsers = uiState.adminUsers.let { users ->
                when (uiState.adminUserSortBy) {
                    "Name" -> if (uiState.adminUserSortOrder == "asc") users.sortedBy { it.username } else users.sortedByDescending { it.username }
                    "Email" -> if (uiState.adminUserSortOrder == "asc") users.sortedBy { it.email } else users.sortedByDescending { it.email }
                    "Role" -> if (uiState.adminUserSortOrder == "asc") users.sortedBy { it.role } else users.sortedByDescending { it.role }
                    else -> users
                }
            }
            
            val sortedProducts = uiState.products.let { prods ->
                when (uiState.adminProductSortBy) {
                    "Name" -> if (uiState.adminProductSortOrder == "asc") prods.sortedBy { it.name } else prods.sortedByDescending { it.name }
                    "Price" -> if (uiState.adminProductSortOrder == "asc") prods.sortedBy { it.price } else prods.sortedByDescending { it.price }
                    "Stock" -> if (uiState.adminProductSortOrder == "asc") prods.sortedBy { it.stock } else prods.sortedByDescending { it.stock }
                    else -> prods
                }
            }
            
            val sortedCategories = uiState.categories.let { cats ->
                when (uiState.adminCategorySortBy) {
                    "Name" -> if (uiState.adminCategorySortOrder == "asc") cats.sortedBy { it.name } else cats.sortedByDescending { it.name }
                    else -> cats
                }
            }

            AdminDashboardScreen(
                users = sortedUsers,
                products = sortedProducts,
                orders = uiState.orders,
                categories = sortedCategories,
                errorMessage = uiState.errorMessage,
                adminChatVm = adminChatVm,
                onBack = viewModel::navigateBack,
                onRefreshUsers = viewModel::loadAdminUsers,
                onRefreshProducts = viewModel::loadProducts,
                onRefreshOrders = viewModel::loadOrders,
                onRefreshCategories = viewModel::loadCategories,
                onCreateUser = viewModel::createAdminUser,
                onUpdateUser = viewModel::updateAdminUser,
                onDeleteUser = viewModel::deleteAdminUser,
                onCreateProduct = viewModel::createAdminProduct,
                onUpdateProduct = viewModel::updateAdminProduct,
                onDeleteProduct = viewModel::deleteAdminProduct,
                onProductSort = viewModel::setAdminProductSort,
                onCreateCategory = viewModel::createAdminCategory,
                onUpdateCategory = viewModel::updateAdminCategory,
                onDeleteCategory = viewModel::deleteAdminCategory,
                onCategorySort = viewModel::setAdminCategorySort,
                onUserSort = viewModel::setAdminUserSort
            )
        }
        
        "chat" -> {
            val chatVm: ChatViewModel = viewModel()
            ChatScreen(
                viewModel = chatVm,
                onBack = viewModel::navigateBack
            )
        }

        "payment_result" -> {
            val success = uiState.lastPaymentResult ?: false
            val orderId = uiState.lastPaymentOrderId ?: ""
            if (success) {
                PaymentSuccessScreen(
                    orderId = orderId,
                    onBackHome = { viewModel.selectTab(NavTab.HOME) },
                    onViewOrder = { viewModel.navigateToOrders() }
                )
            } else {
                PaymentCancelScreen(
                    orderId = orderId,
                    onRetryPayment = { viewModel.selectTab(NavTab.CART) },
                    onBackHome = { viewModel.selectTab(NavTab.HOME) }
                )
            }
        }

        // Default: drawer layout
        else -> MainAppLayout(
            currentTab = uiState.currentTab,
            products = uiState.products,
            searchQuery = uiState.searchQuery,
            cart = uiState.cart,
            pendingOrder = uiState.pendingOrder,
            errorMessage = visibleErrorMessage,
            userName = uiState.userName,
            userEmail = uiState.userEmail,
            userPhone = uiState.userPhone,
            userAddress = uiState.userAddress,
            categories = uiState.categories,
            userRole = uiState.userRole,
            checkoutAddress = uiState.checkoutAddress,
            onTabClick = viewModel::selectTab,
            onSearchChange = viewModel::updateSearchQuery,
            onProductClick = viewModel::navigateToProductDetail,
            onCategoryClick = viewModel::navigateToCategoryDetail,
            onAddToCart = { productId -> viewModel.addToCart(productId) },
            onRemoveCartItem = viewModel::removeCartItem,
            onUpdateCartItem = viewModel::updateCartItemQuantity,
            onClearCart = viewModel::clearCart,
            onCheckoutAddressChange = viewModel::updateCheckoutAddress,
            onCreateOrder = viewModel::createOrder,
            onPayPendingOrder = viewModel::createPaymentForPendingOrder,
            onViewOrders = viewModel::navigateToOrders,
            onChatClick = viewModel::navigateToChat,
            onOpenAdminDashboard = viewModel::navigateToAdminDashboard,
            onOpenCustomerProductActivity = {
                context.startActivity(Intent(context, ProductCatalogActivity::class.java))
            },
            onRefreshProducts = viewModel::loadProducts,
            onRefreshCart = viewModel::loadCart,
            onUpdateProfile = viewModel::updateProfile,
            onChangePassword = viewModel::changePassword,
            onLogout = viewModel::logout,
            snackbarHostState = snackBarHostState
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MainAppLayout  –  Drawer scaffold + tab dispatch
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MainAppLayout(
    currentTab: NavTab,
    products: List<com.example.theflower.data.remote.dtos.ProductDto>,
    searchQuery: String,
    cart: com.example.theflower.data.remote.dtos.CartDto?,
    pendingOrder: com.example.theflower.data.remote.dtos.OrderDto?,
    errorMessage: String?,
    userName: String,
    userEmail: String,
    userPhone: String,
    userAddress: String,
    categories: List<com.example.theflower.data.remote.dtos.CategoryDto>,
    userRole: String,
    checkoutAddress: String,
    onTabClick: (NavTab) -> Unit,
    onSearchChange: (String) -> Unit,
    onProductClick: (com.example.theflower.data.remote.dtos.ProductDto) -> Unit,
    onCategoryClick: (com.example.theflower.data.remote.dtos.CategoryDto) -> Unit,
    onAddToCart: (String) -> Unit,
    onRemoveCartItem: (String) -> Unit,
    onUpdateCartItem: (String, Int) -> Unit,
    onClearCart: () -> Unit,
    onCheckoutAddressChange: (String) -> Unit,
    onCreateOrder: () -> Unit,
    onPayPendingOrder: () -> Unit,
    onViewOrders: () -> Unit,
    onChatClick: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onOpenCustomerProductActivity: () -> Unit,
    onRefreshProducts: () -> Unit,
    onRefreshCart: () -> Unit,
    onUpdateProfile: (String, String, String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onLogout: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ── Notification state ─────────────────────────────────────────────────────
    var showNotifications by remember { mutableStateOf(false) }
    // Demo notifications built from orders — in production replace with real push data
    val notifications = remember(pendingOrder) {
        buildList {
            if (pendingOrder != null) {
                add(
                    AppNotification(
                        id = pendingOrder.id,
                        title = "Đơn hàng chờ thanh toán",
                        body = "Đơn #${pendingOrder.id.take(8)} • ${pendingOrder.totalPrice} VNĐ",
                        icon = "⏳",
                        isRead = false,
                        timestamp = "Vừa xong"
                    )
                )
            }
        }
    }

    // ── Notification modal ─────────────────────────────────────────────────────
    if (showNotifications) {
        NotificationModal(
            notifications = notifications,
            onDismiss = { showNotifications = false },
            onMarkAllRead = { showNotifications = false }
        )
    }

    val filteredProducts = if (searchQuery.isBlank()) products else products.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
            it.categoryName.contains(searchQuery, ignoreCase = true)
    }

    val topBarTitle = when (currentTab) {
        NavTab.HOME -> "🌿 The Flower"
        NavTab.CATEGORY -> "Danh mục"
        NavTab.CART -> "Giỏ hàng"
        NavTab.PROFILE -> "Tài khoản"
    }

    val showSearch = currentTab == NavTab.HOME
    val cartCount = cart?.totalItems ?: 0

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                userName = userName,
                userEmail = userEmail,
                userRole = userRole,
                currentTab = currentTab,
                cartItemCount = cartCount,
                categories = categories,
                onTabClick = onTabClick,
                onCategoryClick = { category -> onCategoryClick(category) },
                onViewOrders = onViewOrders,
                onChatClick = onChatClick,
                onOpenAdminDashboard = onOpenAdminDashboard,
                onLogout = onLogout,
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = PaperWhite,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AppTopBar(
                    title = topBarTitle,
                    cartItemCount = cartCount,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCartClick = if (currentTab != NavTab.CART) {
                        { onTabClick(NavTab.CART) }
                    } else null,
                    showSearch = showSearch,
                    searchQuery = searchQuery,
                    onSearchChange = onSearchChange,
                    notifications = notifications,
                    onNotificationClick = { showNotifications = true }
                )
            },
            bottomBar = {
                if (pendingOrder != null) {
                    PendingOrderBar(order = pendingOrder, onPay = onPayPendingOrder)
                }
            }
        ) { padding ->
            when (currentTab) {

                NavTab.HOME -> ProductListApiScreen(
                    modifier = Modifier.padding(padding),
                    title = "",  // title shown in TopBar
                    searchQuery = searchQuery,
                    products = filteredProducts,
                    onSearchChange = onSearchChange,
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart,
                    onRefresh = onRefreshProducts
                )

                NavTab.CATEGORY -> CategoryListScreen(
                    modifier = Modifier.padding(padding),
                    categories = categories,
                    products = products,
                    onCategoryClick = onCategoryClick
                )

                NavTab.CART -> CartApiScreen(
                    modifier = Modifier.padding(padding),
                    cart = cart,
                    checkoutAddress = checkoutAddress,
                    onCheckoutAddressChange = onCheckoutAddressChange,
                    onCreateOrder = onCreateOrder,
                    onRemoveItem = onRemoveCartItem,
                    onUpdateItemQuantity = onUpdateCartItem,
                    onClearCart = onClearCart,
                    onRefresh = onRefreshCart
                )

                NavTab.PROFILE -> ProfileApiScreen(
                    modifier = Modifier.padding(padding),
                    errorMessage = errorMessage?.takeUnless {
                        it.contains("Unauthorized", ignoreCase = true)
                    },
                    userName = userName,
                    userEmail = userEmail,
                    userPhone = userPhone,
                    userAddress = userAddress,
                    userRole = userRole,
                    onOpenCustomerProductActivity = onOpenCustomerProductActivity,
                    onViewOrders = onViewOrders,
                    onOpenAdminDashboard = onOpenAdminDashboard,
                    onUpdateProfile = onUpdateProfile,
                    onChangePassword = onChangePassword,
                    onLogout = onLogout
                )
            }
        }
    }
}
