package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.MockData
import com.example.myapplication.data.Product
import com.example.myapplication.ui.theme.*

@Composable
fun AllProductsScreen(
    onProductClick: (Product) -> Unit,
    searchQuery: String = ""
) {
    val sortBy = remember { mutableStateOf("recommended") }
    
    val filteredProducts = if (searchQuery.isBlank()) {
        MockData.products
    } else {
        MockData.products.filter { 
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        // Header
        Text(
            text = "Danh mục hoa",
            style = MaterialTheme.typography.headlineSmall,
            color = SoilBrown,
            modifier = Modifier.padding(16.dp)
        )

        // Filter/Sort buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Được đề xuất", "Giá: Thấp → Cao", "Giá: Cao → Thấp", "Đánh giá cao").forEach { filter ->
                FilterChip(
                    selected = filter == "Được đề xuất",
                    onClick = { sortBy.value = filter },
                    label = { Text(filter, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MossGreen,
                        selectedLabelColor = PaperWhite,
                        containerColor = Sand,
                        labelColor = SoilBrown
                    )
                )
            }
        }

        // Product grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredProducts) { product ->
                ProductListItem(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: Product,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        // Product image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Sand),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌸",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        }

        // Product info
        Column(modifier = Modifier.padding(12.dp)) {
            // Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.labelMedium,
                color = SoilBrown,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating
            Text(
                text = "⭐ ${product.rating}",
                style = MaterialTheme.typography.labelSmall,
                color = SandDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Price
            Text(
                text = "₫${product.price}",
                style = MaterialTheme.typography.titleSmall,
                color = MossGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add button
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "+ Thêm",
                    style = MaterialTheme.typography.labelSmall,
                    color = PaperWhite
                )
            }
        }
    }
}
