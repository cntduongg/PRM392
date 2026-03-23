package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.ChangeUserPasswordDto
import com.example.theflower.data.remote.dtos.UpdateProfileRequest
import com.example.theflower.data.remote.dtos.UserProfileDto
import com.example.theflower.domain.repositories.IUserRepository
import retrofit2.HttpException

/**
 * User Repository Implementation
 * Handles user profile operations
 */
class UserRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IUserRepository {
    
    /**
     * Get user profile details
     */
    override suspend fun getUserProfile(): Result<UserProfileDto> {
        return try {
            val response = apiService.getUserProfile()
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound("User profile not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Update user profile information
     */
    override suspend fun updateUserProfile(request: UpdateProfileRequest): Result<UserProfileDto> {
        return try {
            val response = apiService.updateUserProfile(request)
            
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
     * Change user password
     */
    override suspend fun changePassword(request: ChangeUserPasswordDto): Result<Unit> {
        return try {
            val response = apiService.changePassword(request)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
