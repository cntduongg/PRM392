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
    private val apiService: TheFlowerApiService
) : INotificationRepository {
    
    /**
     * Get paginated list of notifications
     */
    override suspend fun getNotifications(
        token: String,
        pageNumber: Int,
        pageSize: Int
    ): Result<PaginatedResponse<NotificationDto>> {
        return try {
            val response = apiService.getNotifications(token, pageNumber, pageSize)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ServerError(500, response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Mark single notification as read
     */
    override suspend fun markAsRead(token: String, notificationId: Int): Result<NotificationDto> {
        return try {
            val response = apiService.markNotificationAsRead(token, notificationId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ServerError(500, response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Mark all notifications as read
     */
    override suspend fun markAllAsRead(token: String): Result<Unit> {
        return try {
            val response = apiService.markAllNotificationsAsRead(token)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ServerError(500, response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
