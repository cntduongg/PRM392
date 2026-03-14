package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.INotificationRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * Notification Repository Implementation
 * Handles notification retrieval and management
 */
class NotificationRepositoryImpl(
    private val apiService: TheFlowerApiService,
    private val token: String
) : INotificationRepository {
    
    private val authHeader get() = token
    
    /**
     * Get paginated list of notifications
     */
    override suspend fun getNotifications(
        pageNumber: Int,
        pageSize: Int
    ): Result<PaginatedResponse<NotificationDto>> {
        return try {
            val response = apiService.getNotifications(authHeader, pageNumber, pageSize)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Mark single notification as read
     */
    override suspend fun markAsRead(notificationId: Int): Result<Unit> {
        return try {
            val response = apiService.markNotificationAsRead(authHeader, notificationId)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Mark all notifications as read
     */
    override suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val response = apiService.markAllNotificationsAsRead(authHeader)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
}
