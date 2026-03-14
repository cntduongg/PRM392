package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.IAuthRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * Auth Repository Implementation
 * Handles authentication operations: login, register, token refresh, logout
 */
class AuthRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IAuthRepository {
    
    /**
     * Register new user account
     */
    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String?
    ): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(
                email = email,
                password = password,
                fullName = fullName,
                phoneNumber = phoneNumber
            )
            val response = apiService.register(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ValidationError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Login user with email and password
     */
    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response = apiService.login(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.Unauthorized(message = "Invalid credentials"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Refresh access token using refresh token
     */
    override suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            val request = RefreshTokenRequest(refreshToken = refreshToken)
            val response = apiService.refreshToken(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.TokenExpired(message = "Token refresh failed"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Logout user and invalidate tokens
     */
    override suspend fun logout(token: String): Result<Unit> {
        return try {
            val authHeader = token
            val response = apiService.logout(authHeader)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            // Treat 401/403 as success for logout (already logged out)
            if (e.code() in listOf(401, 403)) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.handleException(e))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
}
