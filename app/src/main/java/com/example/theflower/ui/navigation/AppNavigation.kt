package com.example.theflower.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.theflower.ProductCatalogActivity
import com.example.theflower.ui.components.BottomNavBar
import com.example.theflower.ui.components.NavTab
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.viewmodels.AppViewModel

// ═══════════════════════════════════════════════════════════════════════════════
// AppNavigation.kt  –  Entry point & routing only
//
// Route map:
//   !isLoggedIn          → AuthNavScreens.kt   :: AuthFlow
//   "detail"             → ProductNavScreens.kt :: ProductDetailApiScreen
//   "orders"             → OrdersNavScreen.kt   :: OrdersApiScreen
//   "category_detail"    → CategoryNavScreens.kt :: CategoryProductsScreen
//   "admin_dashboard"    → AdminNavScreens.kt   :: AdminDashboardScreen
//   else (tabs)          → MainAppLayout (below) dispatches by NavTab
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

    val shouldHideUnauthorizedMessage =
        uiState.errorMessage?.contains("Unauthorized", ignoreCase = true) == true
    val visibleErrorMessage = uiState.errorMessage?.takeUnless {
        it.contains("Unauthorized", ignoreCase = true)
    }

    LaunchedEffect(uiState.errorMessage) {
        if (!uiState.errorMessage.isNullOrBlank() && !shouldHideUnauthorizedMessage) {
            snackBarHostState.showSnackbar(uiState.errorMessage!!)
        }
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

        "admin_dashboard" -> AdminDashboardScreen(
            users = uiState.adminUsers,
            products = uiState.products,
            orders = uiState.orders,
            errorMessage = uiState.errorMessage,
            onBack = viewModel::navigateBack,
            onRefreshUsers = viewModel::loadAdminUsers,
            onRefreshProducts = viewModel::loadProducts,
            onRefreshOrders = viewModel::loadOrders,
            onCreateUser = viewModel::createAdminUser,
            onUpdateUser = viewModel::updateAdminUser,
            onDeleteUser = viewModel::deleteAdminUser,
            onCreateProduct = viewModel::createAdminProduct,
            onUpdateProduct = viewModel::updateAdminProduct,
            onDeleteProduct = viewModel::deleteAdminProduct
        )

        // Default: main tab layout
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
// MainAppLayout  –  Bottom-tab scaffold + tab dispatch
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
    onOpenAdminDashboard: () -> Unit,
    onOpenCustomerProductActivity: () -> Unit,
    onRefreshProducts: () -> Unit,
    onRefreshCart: () -> Unit,
    onUpdateProfile: (String, String, String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
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
            Column {
                if (pendingOrder != null) {
                    PendingOrderBar(order = pendingOrder, onPay = onPayPendingOrder)
                }
                BottomNavBar(
                    currentTab = currentTab,
                    cartItemCount = cart?.totalItems ?: 0,
                    onTabClick = onTabClick
                )
            }
        }
    ) { padding ->
        when (currentTab) {

            NavTab.HOME -> ProductListApiScreen(
                modifier = Modifier.padding(padding),
                title = "Sản phẩm nổi bật",
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
