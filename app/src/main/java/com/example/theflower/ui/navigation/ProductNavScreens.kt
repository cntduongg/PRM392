package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.ui.theme.*

// ─── ProductListApiScreen ────────────────────────────────────────────────────
// Called by: MainAppLayout() when NavTab.HOME

@Composable
internal fun ProductListApiScreen(
    modifier: Modifier = Modifier,
    title: String,
    searchQuery: String,
    products: List<ProductDto>,
    onSearchChange: (String) -> Unit,
    onProductClick: (ProductDto) -> Unit,
    onAddToCart: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        // Refresh row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onRefresh,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("↻ Làm mới", color = SoilBrown, style = MaterialTheme.typography.labelMedium)
            }
        }

        if (products.isEmpty()) {
            EmptyState(message = "Không có sản phẩm phù hợp\nHãy thử từ khoá khác 🌸")
            return
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onAdd = { onAddToCart(product.id) }
                )
            }
        }
    }
}

// ─── ProductCard (grid card) ─────────────────────────────────────────────────

@Composable
internal fun ProductCard(
    product: ProductDto,
    onClick: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .clickable(onClick = onClick)
            .shadow(3.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image / placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(WarmPeach),
                contentAlignment = Alignment.Center
            ) {
                if (!product.image.isNullOrBlank()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("🌸", fontSize = 44.sp)
                }
                // Category badge overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoilBrown.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = product.categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = PaperWhite
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatCurrency(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MossGreen,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.weight(1f))
                
                Button(
                    onClick = onAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+ Thêm vào giỏ", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// ─── ProductListItem (used in CategoryProductsScreen) ────────────────────────

@Composable
internal fun ProductListItem(
    product: ProductDto,
    onClick: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Sand),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarmPeach),
                contentAlignment = Alignment.Center
            ) {
                if (!product.image.isNullOrBlank()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("🌸", fontSize = 28.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(product.categoryName, style = MaterialTheme.typography.labelSmall, color = SandDark)
                Spacer(Modifier.height(4.dp))
                Text(
                    formatCurrency(product.price),
                    style = MaterialTheme.typography.titleSmall,
                    color = MossGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Thêm", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

// ─── ProductDetailApiScreen ───────────────────────────────────────────────────
// Called by: AppNavigation() when route = "detail"

@Composable
internal fun ProductDetailApiScreen(
    product: ProductDto?,
    onBack: () -> Unit,
    pendingOrder: OrderDto?,
    onAddToCart: (String, Int) -> Unit,
    onPayPendingOrder: () -> Unit
) {
    var quantityText by remember { mutableStateOf("1") }

    if (product == null) {
        EmptyState(message = "Không có dữ liệu sản phẩm")
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (pendingOrder != null) 130.dp else 80.dp)
        ) {
            // Hero image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    if (!product.image.isNullOrBlank()) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(listOf(WarmPeach, Sand))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌸", fontSize = 80.sp)
                        }
                    }
                    // Gradient overlay bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, PaperWhite)
                                )
                            )
                    )
                    // Back button
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PaperWhite.copy(alpha = 0.9f))
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", fontSize = 18.sp, color = SoilBrown)
                    }
                }
            }

            // Content
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // Category badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MossGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            product.categoryName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MossGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = SoilBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        formatCurrency(product.price),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MossGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = SandDark.copy(alpha = 0.3f))
                    Spacer(Modifier.height(16.dp))

                    // Info sections
                    ProductInfoSection(title = "Mô tả", content = product.briefDescription ?: product.fullDescription ?: product.description)
                    Spacer(Modifier.height(12.dp))
                    ProductInfoSection(title = "Chi tiết", content = product.fullDescription ?: product.description)
                    Spacer(Modifier.height(12.dp))
                    ProductInfoSection(title = "Điểm nổi bật", content = product.technicalSpecifications)

                    Spacer(Modifier.height(24.dp))

                    // Quantity selector
                    Text("Số lượng", style = MaterialTheme.typography.titleSmall, color = SoilBrown, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PillQuantityButton(label = "−") {
                            val q = quantityText.toIntOrNull() ?: 1
                            if (q > 1) quantityText = (q - 1).toString()
                        }
                        Text(
                            quantityText,
                            style = MaterialTheme.typography.headlineSmall,
                            color = SoilBrown,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.widthIn(min = 36.dp),
                        )
                        PillQuantityButton(label = "+") {
                            val q = quantityText.toIntOrNull() ?: 1
                            quantityText = (q + 1).toString()
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        // Sticky bottom: pending order + add to cart
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(PaperWhite)
        ) {
            if (pendingOrder != null) {
                PendingOrderBar(order = pendingOrder, onPay = onPayPendingOrder)
            }
            Button(
                onClick = {
                    val quantity = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    onAddToCart(product.id, quantity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MossGreen)
            ) {
                Text("🛒  Thêm vào giỏ hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── PillQuantityButton ───────────────────────────────────────────────────────

@Composable
private fun PillQuantityButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.5.dp, MossGreen, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleLarge,
            color = MossGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── ProductImageHero ─────────────────────────────────────────────────────────

@Composable
internal fun ProductImageHero(imageUrl: String?, productName: String) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = productName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(18.dp))
        )
        return
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(WarmPeach, Sand))),
        contentAlignment = Alignment.Center
    ) {
        Text("🌸", style = MaterialTheme.typography.displaySmall)
    }
}

// ─── ProductThumbnail ─────────────────────────────────────────────────────────

@Composable
internal fun ProductThumbnail(imageUrl: String?, label: String) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        return
    }
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WarmPeach),
        contentAlignment = Alignment.Center
    ) {
        Text("🌸", fontSize = 28.sp)
    }
}

// ─── ProductInfoSection ───────────────────────────────────────────────────────

@Composable
internal fun ProductInfoSection(title: String, content: String?) {
    if (content.isNullOrBlank()) return
    val formattedLines = remember(content) { formatProductInfoLines(content) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = SoilBrown,
            fontWeight = FontWeight.SemiBold
        )
        formattedLines.forEach { line ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formattedLines.size > 1) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MossGreen)
                    )
                }
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SandDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
