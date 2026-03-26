package com.example.theflower.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.ui.viewmodels.AppViewModel
import com.example.theflower.ui.navigation.ProductFilterBottomSheet
import com.example.theflower.ui.navigation.formatCurrency
import com.example.theflower.ui.theme.*
import com.example.theflower.ui.components.EmptyState

@Composable
fun AllProductsScreen(
    viewModel: AppViewModel = viewModel(),
    onProductClick: (ProductDto) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    if (showFilterSheet) {
        ProductFilterBottomSheet(
            categories = uiState.categories,
            selectedCategoryId = uiState.productFilterCategoryId,
            minPrice = uiState.productFilterMinPrice,
            maxPrice = uiState.productFilterMaxPrice,
            sortBy = uiState.productSortBy,
            sortOrder = uiState.productSortOrder,
            onDismiss = { showFilterSheet = false },
            onApply = { cat, min, max, sBy, sOrder ->
                viewModel.setProductFilterCategory(cat)
                viewModel.setProductPriceRange(min, max)
                viewModel.setProductSort(sBy, sOrder)
            },
            onReset = viewModel::clearProductFilters
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Danh mục hoa",
                style = MaterialTheme.typography.headlineSmall,
                color = SoilBrown
            )
            
            IconButton(onClick = { showFilterSheet = true }) {
                Text(
                    text = if (uiState.productFilterCategoryId != null || uiState.productFilterMinPrice != null || uiState.productFilterMaxPrice != null) "✓" else "🎚️",
                    fontSize = 20.sp
                )
            }
        }

        // Search bar (if needed inside screen, but usually in TopBar)
        // Here we just display the results from uiState.products

        if (uiState.products.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không tìm thấy sản phẩm nào 🌸", color = SandDark)
            }
        } else {
            // Product grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.products) { product ->
                    ProductListItem(
                        product = product,
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: ProductDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFFE8DFD8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌸",
                    fontSize = 40.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = SoilBrown,
                    maxLines = 2,
                    minLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = SandDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatCurrency(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MossGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Xem chi tiết",
                        style = MaterialTheme.typography.labelSmall,
                        color = PaperWhite
                    )
                }
            }
        }
    }
}
