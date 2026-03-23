package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.CategoryDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.ui.components.AppTopBar
import com.example.theflower.ui.components.NavTab
import com.example.theflower.ui.theme.*

// ─── CategoryListScreen ───────────────────────────────────────────────────────
// Called by: MainAppLayout() when NavTab.CATEGORY

@Composable
internal fun CategoryListScreen(
    modifier: Modifier = Modifier,
    categories: List<CategoryDto>,
    products: List<ProductDto>,
    onCategoryClick: (CategoryDto) -> Unit
) {
    if (categories.isEmpty()) {
        EmptyState(message = "Chưa có danh mục nào\nVui lòng thử lại sau 🌸")
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                productCount = products.count { it.categoryId == category.id },
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

// ─── CategoryCard (grid cell) ─────────────────────────────────────────────────

@Composable
private fun CategoryCard(
    category: CategoryDto,
    productCount: Int,
    onClick: () -> Unit
) {
    // Alternating warm tones
    val bgColor = if (productCount % 2 == 0) Sand else WarmPeach

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🌺", fontSize = 34.sp)
                Column {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = SoilBrown,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (category.description.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            category.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = SandDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Product count badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MossGreen)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "$productCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = PaperWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─── CategoryListItem (kept for any list-mode usage) ─────────────────────────

@Composable
internal fun CategoryListItem(
    category: CategoryDto,
    productCount: Int,
    onClick: () -> Unit
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
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("🌺", fontSize = 24.sp)
                Column {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = SoilBrown,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (category.description.isNotBlank()) {
                        Text(
                            category.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = SandDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MossGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "$productCount sản phẩm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MossGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─── CategoryProductsScreen ───────────────────────────────────────────────────
// Called by: AppNavigation() when route = "category_detail"

@Composable
internal fun CategoryProductsScreen(
    category: CategoryDto?,
    products: List<ProductDto>,
    currentTab: NavTab,
    cartItemCount: Int,
    pendingOrder: OrderDto?,
    onBack: () -> Unit,
    onTabClick: (NavTab) -> Unit,
    onProductClick: (ProductDto) -> Unit,
    onAddToCart: (String) -> Unit,
    onPayPendingOrder: () -> Unit
) {
    val categoryProducts = products.filter { product ->
        category != null && product.categoryId == category.id
    }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            AppTopBar(
                title = category?.name ?: "Danh mục",
                cartItemCount = cartItemCount,
                onMenuClick = onBack,
                onCartClick = { onTabClick(NavTab.CART) }
            )
        },
        bottomBar = {
            if (pendingOrder != null) {
                PendingOrderBar(order = pendingOrder, onPay = onPayPendingOrder)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "${categoryProducts.size} sản phẩm",
                style = MaterialTheme.typography.bodyMedium,
                color = SandDark
            )
            Spacer(Modifier.height(10.dp))

            if (category == null) {
                EmptyState(message = "Không tìm thấy danh mục")
                return@Column
            }

            if (categoryProducts.isEmpty()) {
                EmptyState(message = "Danh mục này chưa có sản phẩm 🌱")
                return@Column
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categoryProducts) { product ->
                    ProductListItem(
                        product = product,
                        onClick = { onProductClick(product) },
                        onAdd = { onAddToCart(product.id) }
                    )
                }
            }
        }
    }
}
