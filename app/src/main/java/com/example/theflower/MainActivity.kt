package com.example.theflower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
}
