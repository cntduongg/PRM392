package com.example.theflower.di

import android.content.Context
import com.example.theflower.data.remote.api.RetrofitClient
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.local.TokenManager
import com.example.theflower.ui.viewmodels.AppViewModel
import com.example.theflower.data.repository.*
import com.example.theflower.domain.repositories.*

/**
 * Dependency Injection Container
 * Singleton object for managing app dependencies
 * Manages initialization and lazy loading of repositories and services
 */
object DIContainer {
    
    private lateinit var context: Context
    private var tokenManager: TokenManager? = null
    private var appViewModel: AppViewModel? = null
    
    // Repository Implementations (lazy initialized)
    private var authRepository: IAuthRepository? = null
    private var productRepository: IProductRepository? = null
    private var categoryRepository: ICategoryRepository? = null
    private var cartRepository: ICartRepository? = null
    private var orderRepository: IOrderRepository? = null
    private var paymentRepository: IPaymentRepository? = null
    private var notificationRepository: INotificationRepository? = null
    private var chatRepository: IChatRepository? = null
    private var userRepository: IUserRepository? = null
    private var adminRepository: IAdminRepository? = null
    private var storeRepository: IStoreRepository? = null
    
    /**
     * Initialize DI Container with app context
     * Must be called once during app startup
     */
    fun init(appContext: Context, isDevelopment: Boolean = true) {
        context = appContext
        RetrofitClient.initialize(isDevelopment, getTokenManager())
    }

    fun getContext(): Context = context
    
    /**
     * Get or create TokenManager singleton
     * Manages secure token persistence using Android DataStore
     */
    fun getTokenManager(): TokenManager {
        return tokenManager ?: TokenManager(context).also { tokenManager = it }
    }
    
    /**
     * Get the Retrofit API service
     * Used by repository implementations to make API calls
     */
    private fun getApiService(): TheFlowerApiService {
        return RetrofitClient.getApiService()
    }
    
    /**
     * Get or create AppViewModel singleton
     * Manages centralized UI state with StateFlow
     */
    fun getAppViewModel(): AppViewModel {
        return appViewModel ?: AppViewModel().also { appViewModel = it }
    }
    
    // ─────── Repository Getters ─────────────────────────────────────────────
    
    /**
     * Get or create AuthRepository singleton
     * Handles: login, register, logout, token refresh
     */
    fun getAuthRepository(): IAuthRepository {
        return authRepository ?: AuthRepositoryImpl(getApiService()).also { authRepository = it }
    }
    
    /**
     * Get or create ProductRepository singleton
     * Handles: get products, get product details, filtering
     */
    fun getProductRepository(): IProductRepository {
        return productRepository ?: ProductRepositoryImpl(getApiService()).also { productRepository = it }
    }
    
    /**
     * Get or create CategoryRepository singleton
     * Handles: get product categories
     */
    fun getCategoryRepository(): ICategoryRepository {
        return categoryRepository ?: CategoryRepositoryImpl(getApiService()).also { categoryRepository = it }
    }
    
    /**
     * Get or create CartRepository singleton
     * Handles: view cart, add/remove items, update quantities, clear cart
     * Note: Requires authentication token
     */
    fun getCartRepository(): ICartRepository {
        return cartRepository ?: CartRepositoryImpl(getApiService()).also { cartRepository = it }
    }
    
    /**
     * Get or create OrderRepository singleton
     * Handles: view orders, create orders, cancel orders
     * Note: Requires authentication token
     */
    fun getOrderRepository(): IOrderRepository {
        return orderRepository ?: OrderRepositoryImpl(getApiService()).also { orderRepository = it }
    }
    
    /**
     * Get or create PaymentRepository singleton
     * Handles: create payments, check payment status
     * Note: Requires authentication token
     */
    fun getPaymentRepository(): IPaymentRepository {
        return paymentRepository ?: PaymentRepositoryImpl(getApiService()).also { paymentRepository = it }
    }
    
    /**
     * Get or create NotificationRepository singleton
     * Handles: get notifications, mark as read
     * Note: Requires authentication token
     */
    fun getNotificationRepository(): INotificationRepository {
        return notificationRepository ?: NotificationRepositoryImpl(getApiService()).also { notificationRepository = it }
    }
    
    /**
     * Get or create ChatRepository singleton
     * Handles: get conversations, send messages
     * Note: Requires authentication token
     */
    fun getChatRepository(): IChatRepository {
        return chatRepository ?: ChatRepositoryImpl(
            getApiService(), 
            com.example.theflower.BuildConfig.API_BASE_URL
        ).also { chatRepository = it }
    }

    
    /**
     * Get or create UserRepository singleton
     * Handles: get profile, update profile, change password
     * Note: Requires authentication token
     */
    fun getUserRepository(): IUserRepository {
        return userRepository ?: UserRepositoryImpl(getApiService()).also { userRepository = it }
    }

    /**
     * Get or create AdminRepository singleton
     * Handles: admin user/product CRUD
     */
    fun getAdminRepository(): IAdminRepository {
        return adminRepository ?: AdminRepositoryImpl(getApiService()).also { adminRepository = it }
    }

    /**
     * Get or create StoreRepository singleton
     * Handles: fetch stores for users
     */
    fun getStoreRepository(): IStoreRepository {
        return storeRepository ?: StoreRepositoryImpl(getApiService()).also { storeRepository = it }
    }

    /**
     * Reset all singletons (useful for testing or logout)
     */
    fun reset() {
        tokenManager = null
        appViewModel = null
        authRepository = null
        productRepository = null
        categoryRepository = null
        cartRepository = null
        orderRepository = null
        paymentRepository = null
        notificationRepository = null
        chatRepository = null
        userRepository = null
        adminRepository = null
        storeRepository = null
    }
}
