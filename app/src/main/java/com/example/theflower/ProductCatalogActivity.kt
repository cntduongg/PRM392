package com.example.theflower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.theflower.di.DIContainer
import com.example.theflower.ui.screens.product.ProductCatalogScreen
import com.example.theflower.ui.theme.MyApplicationTheme

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
