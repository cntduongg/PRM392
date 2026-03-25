package com.example.theflower.data.remote.api

import com.example.theflower.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Enhanced Retrofit Client with Token Management
 */
object RetrofitClient {
    

    // Biến kết nối với BE local ( Điện thoại vật lí với Máy chạy BE phải cùng mạng wifi)
    private val BASE_URL_DEV = com.example.theflower.BuildConfig.API_BASE_URL
    
    // Thay xxxxxxxxx thành ipv4 của wifi, thay yyyy là port chạy ở be
    //private const val BASE_URL_DEV = "http://xxxxxxxxx:yyyy/"

    //private const val BASE_URL_DEV = "http://10.0.2.2:5134/"
    private const val BASE_URL_PROD = "https://api.theflower.com/"
    private const val TIMEOUT_SECONDS = 30L
    
    private var isDevelopment = true
    private var tokenManager: TokenManager? = null

    private var retrofit: Retrofit? = null
    private var apiService: TheFlowerApiService? = null
    
    fun initialize(isDev: Boolean = true, tokenManager: TokenManager? = null) {
        isDevelopment = isDev
        this.tokenManager = tokenManager
        retrofit = buildRetrofit()
        apiService = retrofit!!.create(TheFlowerApiService::class.java)
    }
    
    private fun buildRetrofit(): Retrofit {
        val baseUrl = if (isDevelopment) BASE_URL_DEV else BASE_URL_PROD
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(buildOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private fun buildOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

        if (isDevelopment && BASE_URL_DEV.startsWith("https://")) {
            applyDevelopmentTlsBypass(httpClient)
        }
        
        // Add logging interceptor for debugging
        if (isDevelopment) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            httpClient.addInterceptor(loggingInterceptor)
        }
        
        // Add authorization interceptor
        tokenManager?.let { httpClient.addInterceptor(AuthInterceptor(it)) }
        
        return httpClient.build()
    }

    private fun applyDevelopmentTlsBypass(httpClient: OkHttpClient.Builder) {
        val trustAllManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<out X509Certificate>, authType: String) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())

        httpClient.sslSocketFactory(sslContext.socketFactory, trustAllManager)
        httpClient.hostnameVerifier { hostname, _ ->
            hostname == "10.0.2.2" || hostname == "localhost"
        }
    }
    
    fun getApiService(): TheFlowerApiService {
        return apiService ?: throw IllegalStateException(
            "RetrofitClient not initialized. Call initialize() first."
        )
    }
    
    fun setDevelopmentMode(isDev: Boolean) {
        if (isDevelopment != isDev) {
            isDevelopment = isDev
            retrofit = buildRetrofit()
            apiService = retrofit!!.create(TheFlowerApiService::class.java)
        }
    }
}

/**
 * Interceptor to automatically add Bearer token to all protected requests.
 * Token is read directly from TokenManager â no need to pass it manually.
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth header for public endpoints
        if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        // OkHttp runs on a background thread, so runBlocking is safe here
        val token = runBlocking { tokenManager.getAccessToken() }
        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        return chain.proceed(newRequest)
    }

    private fun isPublicEndpoint(path: String): Boolean {
        return path.contains("/auth/register") ||
               path.contains("/auth/login") ||
               path.contains("/auth/refresh-token") ||
               path.contains("/products") ||
               path.contains("/categories")
    }
}

