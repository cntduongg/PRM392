package com.example.theflower.data.remote.api

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
    
    //private const val BASE_URL_DEV = "http://192.168.1.7:5134/"
    private const val BASE_URL_DEV = "http://10.0.2.2:5134/"
    private const val BASE_URL_PROD = "https://api.theflower.com/"
    private const val TIMEOUT_SECONDS = 30L
    
    private var isDevelopment = true
    
    private var retrofit: Retrofit? = null
    private var apiService: TheFlowerApiService? = null
    
    fun initialize(isDev: Boolean = true) {
        isDevelopment = isDev
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
        httpClient.addInterceptor(AuthInterceptor())
        
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
 * Interceptor to add Bearer token to requests
 */
class AuthInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        // Skip auth for public endpoints
        if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }
        
        // Add token to request (implement async token retrieval if needed)
        // For now, tokens should be pre-retrieved and set in the header by the repository
        val token = originalRequest.header("Authorization")
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        
        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
    
    private fun isPublicEndpoint(path: String): Boolean {
        return path.contains("/auth/register") || 
               path.contains("/auth/login") || 
               path.contains("/products") ||
               path.contains("/categories")
    }
}

