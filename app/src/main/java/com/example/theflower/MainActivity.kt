package com.example.theflower

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.theflower.utils.NotificationHelper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.theflower.di.DIContainer
import com.example.theflower.ui.navigation.AppNavigation
import com.example.theflower.ui.theme.MyApplicationTheme
import com.example.theflower.ui.viewmodels.AppViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * Main Activity - Entry point for the application
 * 
 * Responsibilities:
 * - Initialize shared dependencies (TokenManager, AppViewModel)
 * - Set up Compose UI
 * - Handle navigation based on AppViewModel state
 * - Display loading/error states
 */
class MainActivity : ComponentActivity() {
    
    private val appViewModel: AppViewModel by lazy {
        DIContainer.getAppViewModel()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize DI container with application context
        DIContainer.init(applicationContext, isDevelopment = true)
        
        NotificationHelper.createNotificationChannel(this)
        checkNotificationPermission()

        handleIntent(intent)
        
        setupLifecycleObserver()
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by appViewModel.uiState.collectAsState()
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .systemBarsPadding()
                ) {
                    // Main navigation content
                    AppNavigation(viewModel = appViewModel)
                    
                    // Show loading indicator if needed
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    // TODO: Add error dialog/snackbar for uiState.errorMessage
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up if needed
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val action = intent?.action
        val data = intent?.data
        
        if (Intent.ACTION_VIEW == action && data != null) {
            if (data.scheme == "theflower" && data.host == "payment_result") {
                // handle the deep link for payment success
                val success = data.getQueryParameter("success")?.toBoolean() ?: false
                val orderId = data.getQueryParameter("orderId") ?: ""
                
                appViewModel.onPaymentResult(success, orderId)
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun setupLifecycleObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                // App went to background
                val uiState = appViewModel.uiState.value
                val cart = uiState.cart
                if (uiState.isLoggedIn && cart != null && (cart.totalItems ?: 0) > 0) {
                    NotificationHelper.showNotification(
                        applicationContext,
                        "Giỏ hàng đang chờ bạn! 🛒",
                        "Bạn vẫn còn ${cart.totalItems} sản phẩm trong giỏ hàng. Hãy hoàn tất thanh toán ngay nhé!"
                    )
                }
            }
        })
    }
}
