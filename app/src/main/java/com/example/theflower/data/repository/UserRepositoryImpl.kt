package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.IUserRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * User Repository Implementation
 * Handles user profile operations
 */
class UserRepositoryImpl(
    private val apiService: TheFlowerApiService,
    private val token: String
) : IUserRepository {
    
    private val authHeader get() = token
    
    /**
     * Get user profile details
     */
    override suspend fun getUserProfile(): Result<UserProfileDto> {
        return try {
            val response = apiService.getUserProfile(authHeader)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound(message = "User profile not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Update user profile information
     */
    override suspend fun updateUserProfile(
        fullName: String,
        phoneNumber: String,
        address: String,
        avatar: String?
    ): Result<UserProfileDto> {
        return try {
            val request = UpdateProfileRequest(
                fullName = fullName,
                phoneNumber = phoneNumber,
                address = address,
                avatar = avatar
            )
            val response = apiService.updateUserProfile(authHeader, request)
            
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
     * Change user password
     */
    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val request = mapOf(
                "oldPassword" to oldPassword,
                "newPassword" to newPassword
            )
            val response = apiService.changePassword(authHeader, request)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ValidationError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
}
