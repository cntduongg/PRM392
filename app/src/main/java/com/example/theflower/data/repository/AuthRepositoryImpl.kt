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
    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val backendRequest = request.copy(username = request.username.ifBlank { request.fullName })
            val response = apiService.register(backendRequest)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Login user with email and password
     */
    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.Unauthorized(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Refresh access token using refresh token
     */
    override suspend fun refreshToken(request: RefreshTokenRequest): Result<AuthResponse> {
        return Result.failure(ApiException.TokenExpired("Refresh token endpoint chưa được backend hỗ trợ."))
    }
    
    /**
     * Logout user and invalidate tokens
     */
    override suspend fun logout(token: String): Result<Unit> {
        return Result.success(Unit)
    }
}
