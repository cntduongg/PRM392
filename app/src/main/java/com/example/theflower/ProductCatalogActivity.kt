package com.example.theflower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.di.DIContainer
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.MyApplicationTheme
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown
import kotlin.math.roundToInt

private enum class SortOption(val label: String) {
    NAME_ASC("A-Z"),
    NAME_DESC("Z-A"),
    PRICE_ASC("Gia tang"),
    PRICE_DESC("Gia giam")
}

class ProductCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DIContainer.init(applicationContext, isDevelopment = true)

        setContent {
            MyApplicationTheme {
                ProductCatalogScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
private fun ProductCatalogScreen(onBack: () -> Unit) {
    val productRepository = remember { DIContainer.getProductRepository() }

    var allProducts by remember { mutableStateOf<List<ProductDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedLetter by remember { mutableStateOf("ALL") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedSort by remember { mutableStateOf(SortOption.NAME_ASC) }

    var minPriceBound by remember { mutableStateOf(0f) }
    var maxPriceBound by remember { mutableStateOf(0f) }
    var selectedPriceRange by remember { mutableStateOf(0f..0f) }

    val letters = remember { listOf("ALL") + ('A'..'Z').map { it.toString() } }
    val sortOptions = remember { SortOption.entries }
    val categories = remember(allProducts) {
        listOf("ALL") + allProducts.map { it.categoryName }.distinct().sorted()
    }

    LaunchedEffect(Unit) {
        isLoading = true
        productRepository.getProducts(pageNumber = 1, pageSize = 300)
            .onSuccess { response ->
                val items = response.items.orEmpty()
                allProducts = items

                val min = items.minOfOrNull { it.price }?.toFloat() ?: 0f
                val max = items.maxOfOrNull { it.price }?.toFloat() ?: 0f
                minPriceBound = min
                maxPriceBound = max
                selectedPriceRange = min..max
                errorMessage = null
            }
            .onFailure {
                errorMessage = it.message ?: "Khong the tai danh sach san pham"
            }
        isLoading = false
    }

    val displayedProducts = remember(
        allProducts,
        searchQuery,
        selectedLetter,
        selectedCategory,
        selectedPriceRange,
        selectedSort
    ) {
        val filtered = allProducts.filter { product ->
            val matchSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.categoryName.contains(searchQuery, ignoreCase = true)

            val matchLetter = selectedLetter == "ALL" ||
                product.name.startsWith(selectedLetter, ignoreCase = true)

            val matchCategory = selectedCategory == "ALL" ||
                product.categoryName.equals(selectedCategory, ignoreCase = true)

            val price = product.price.toFloat()
            val matchPrice = price in selectedPriceRange.start..selectedPriceRange.endInclusive

            matchSearch && matchLetter && matchCategory && matchPrice
        }

        when (selectedSort) {
            SortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
            SortOption.PRICE_ASC -> filtered.sortedBy { it.price }
            SortOption.PRICE_DESC -> filtered.sortedByDescending { it.price }
        }
    }

    Scaffold(containerColor = PaperWhite) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "San pham cho khach hang",
                    style = MaterialTheme.typography.headlineSmall,
                    color = SoilBrown
                )
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Sand),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Dong", color = SoilBrown)
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tim theo ten hoac danh muc") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Loc theo chu cai", style = MaterialTheme.typography.titleSmall, color = SoilBrown)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(letters) { letter ->
                    FilterChip(
                        selected = selectedLetter == letter,
                        onClick = { selectedLetter = letter },
                        label = { Text(letter) }
                    )
                }
            }

            Text("Loc theo danh muc", style = MaterialTheme.typography.titleSmall, color = SoilBrown)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Text("Sap xep", style = MaterialTheme.typography.titleSmall, color = SoilBrown)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sortOptions) { option ->
                    FilterChip(
                        selected = selectedSort == option,
                        onClick = { selectedSort = option },
                        label = { Text(option.label) }
                    )
                }
            }

            Text("Loc theo gia", style = MaterialTheme.typography.titleSmall, color = SoilBrown)
            if (maxPriceBound > minPriceBound) {
                RangeSlider(
                    value = selectedPriceRange,
                    onValueChange = { selectedPriceRange = it },
                    valueRange = minPriceBound..maxPriceBound
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tu: ${formatCurrency(selectedPriceRange.start.toDouble())}", color = SandDark)
                Text("Den: ${formatCurrency(selectedPriceRange.endInclusive.toDouble())}", color = SandDark)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Ket qua: ${displayedProducts.size} san pham",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = {
                        searchQuery = ""
                        selectedLetter = "ALL"
                        selectedCategory = "ALL"
                        selectedSort = SortOption.NAME_ASC
                        selectedPriceRange = minPriceBound..maxPriceBound
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Sand)
                ) {
                    Text("Reset", color = SoilBrown)
                }
            }

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator()
                    }
                }

                !errorMessage.isNullOrBlank() -> {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                displayedProducts.isEmpty() -> {
                    Text("Khong co san pham phu hop", color = SandDark)
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(displayedProducts) { product ->
                            ProductInfoCard(product = product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductInfoCard(product: ProductDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Sand)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium, color = SoilBrown)
            Text("Danh muc: ${product.categoryName}", style = MaterialTheme.typography.bodySmall, color = SandDark)
            Text("Gia: ${formatCurrency(product.price)}", style = MaterialTheme.typography.titleSmall, color = MossGreen)
            Text("Ton kho: ${product.stock}", style = MaterialTheme.typography.bodySmall, color = SandDark)
            if (!product.description.isNullOrBlank()) {
                Text(
                    text = product.description!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoilBrown,
                    maxLines = 3
                )
            }
        }
    }
}

private fun formatCurrency(value: Double): String = "${value.roundToInt()} VND"
