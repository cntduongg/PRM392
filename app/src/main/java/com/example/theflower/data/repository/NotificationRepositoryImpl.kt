package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.NotificationBadgeDto
import com.example.theflower.data.remote.dtos.NotificationDto
import com.example.theflower.data.remote.dtos.PaginatedResponse
import com.example.theflower.domain.repositories.INotificationRepository
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
            val response = apiService.getNotifications(token)
            if (response.success) {
                val items = response.data.orEmpty()
                val fromIndex = ((pageNumber - 1) * pageSize).coerceAtLeast(0)
                val toIndex = (fromIndex + pageSize).coerceAtMost(items.size)
                val pageItems = if (fromIndex < toIndex) items.subList(fromIndex, toIndex) else emptyList()
                Result.success(
                    PaginatedResponse(
                        items = pageItems,
                        pageNumber = pageNumber,
                        pageSize = pageSize,
                        totalItems = items.size,
                        totalPages = if (items.isEmpty()) 0 else (items.size + pageSize - 1) / pageSize
                    )
                )
            } else {
                Result.failure(ApiException.ServerError(500, response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    override suspend fun getBadge(token: String): Result<NotificationBadgeDto> {
        return try {
            val response = apiService.getNotificationBadge(token)
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
    override suspend fun markAsRead(token: String, notificationId: String): Result<NotificationDto> {
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
