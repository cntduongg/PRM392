package com.example.theflower.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.theflower.data.remote.dtos.CategoryDto
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.ui.components.BottomNavBar
import com.example.theflower.ui.components.NavTab
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown

// ─── CategoryListScreen ───────────────────────────────────────────────────────
// Gọi bởi: MainAppLayout() khi NavTab.CATEGORY

@Composable
internal fun CategoryListScreen(
    modifier: Modifier = Modifier,
    categories: List<CategoryDto>,
    products: List<ProductDto>,
    onCategoryClick: (CategoryDto) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Danh mục sản phẩm",
            style = MaterialTheme.typography.headlineSmall,
            color = SoilBrown
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (categories.isEmpty()) {
            EmptyState(message = "Không có danh mục")
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(categories) { category ->
                CategoryListItem(
                    category = category,
                    productCount = products.count { it.categoryId == category.id },
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

// ─── CategoryListItem ─────────────────────────────────────────────────────────

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
        colors = CardDefaults.cardColors(containerColor = com.example.theflower.ui.theme.Sand)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "» ${category.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold
                )

                if (category.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SandDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "($productCount)",
                style = MaterialTheme.typography.bodyMedium,
                color = SandDark
            )
        }
    }
}

// ─── CategoryProductsScreen ───────────────────────────────────────────────────
// Gọi bởi: AppNavigation() khi route = "category_detail"

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
        bottomBar = {
            Column {
                if (pendingOrder != null) {
                    PendingOrderBar(order = pendingOrder, onPay = onPayPendingOrder)
                }
                BottomNavBar(
                    currentTab = currentTab,
                    cartItemCount = cartItemCount,
                    onTabClick = onTabClick
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    modifier = Modifier.clickable(onClick = onBack),
                    style = MaterialTheme.typography.headlineMedium,
                    color = SoilBrown
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = category?.name ?: "Danh mục",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SoilBrown
                    )
                    Text(
                        text = "${categoryProducts.size} sản phẩm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SandDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (category == null) {
                EmptyState(message = "Không tìm thấy danh mục")
                return@Column
            }

            if (categoryProducts.isEmpty()) {
                EmptyState(message = "Danh mục này chưa có sản phẩm")
                return@Column
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(min = 0.dp)
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
