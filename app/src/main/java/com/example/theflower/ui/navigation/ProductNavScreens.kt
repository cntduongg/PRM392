package com.example.theflower.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown
import com.example.theflower.ui.theme.WarmPeach

// ─── ProductListApiScreen ────────────────────────────────────────────────────
// Gọi bởi: MainAppLayout() khi NavTab.HOME

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
            colors = botanicalOutlinedTextFieldColors(),
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

// ─── ProductListItem ─────────────────────────────────────────────────────────

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
                Text("\uD83C\uDF37")
            }

            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, color = SoilBrown)
                Text(product.categoryName, style = MaterialTheme.typography.bodySmall, color = SandDark)
                val summaryText = product.briefDescription
                    ?.takeIf { it.isNotBlank() }
                    ?: product.fullDescription?.takeIf { it.isNotBlank() }
                if (summaryText != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = summaryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = SandDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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

// ─── ProductDetailApiScreen ───────────────────────────────────────────────────
// Gọi bởi: AppNavigation() khi route = "detail"

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

    Scaffold(
        containerColor = PaperWhite,
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sand)
            ) {
                Text("Quay lại", color = SoilBrown)
            }

            Spacer(modifier = Modifier.height(14.dp))

            ProductImageHero(imageUrl = product.image, productName = product.name)

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
                    ProductInfoSection(
                        title = "Mô tả ngắn",
                        content = product.briefDescription ?: product.categoryName
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProductInfoSection(
                        title = "Mô tả chi tiết",
                        content = product.fullDescription ?: product.description
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProductInfoSection(
                        title = "Điểm nổi bật của hoa",
                        content = product.technicalSpecifications
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it.filter(Char::isDigit) },
                singleLine = true,
                label = { Text("Số lượng") },
                colors = botanicalOutlinedTextFieldColors(),
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

// ─── ProductImageHero ─────────────────────────────────────────────────────────

@Composable
internal fun ProductImageHero(
    imageUrl: String?,
    productName: String
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = productName,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, SandDark.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(WarmPeach),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "\uD83C\uDF38",
            style = MaterialTheme.typography.displaySmall
        )
    }
}

// ─── ProductThumbnail ─────────────────────────────────────────────────────────
// Dùng trong CartNavScreen

@Composable
internal fun ProductThumbnail(
    imageUrl: String?,
    label: String
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = label,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(WarmPeach)
        )
        return
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WarmPeach),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "\uD83C\uDF38")
    }
}

// ─── ProductInfoSection ───────────────────────────────────────────────────────

@Composable
internal fun ProductInfoSection(
    title: String,
    content: String?
) {
    if (content.isNullOrBlank()) return

    val formattedLines = remember(content) { formatProductInfoLines(content) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = SoilBrown,
            fontWeight = FontWeight.SemiBold
        )
        formattedLines.forEach { line ->
            Text(
                text = if (formattedLines.size == 1) line else "• $line",
                style = MaterialTheme.typography.bodyMedium,
                color = SandDark
            )
        }
    }
}
