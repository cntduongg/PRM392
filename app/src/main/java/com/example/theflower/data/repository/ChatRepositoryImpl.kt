package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.ChatMessageDto
import com.example.theflower.data.remote.dtos.SendChatMessageRequest
import com.example.theflower.data.remote.dtos.SendMessageDto
import com.example.theflower.domain.repositories.IChatRepository
import retrofit2.HttpException

/**
 * Chat Repository Implementation
 * Handles chat message retrieval and sending
 */
class ChatRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IChatRepository {
    
    /**
     * Get all chat conversations
     */
    override suspend fun getChatConversations(): Result<List<ChatMessageDto>> {
        return try {
            val response = apiService.getChatMessages(page = 1, pageSize = 50)
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
     * Get messages from specific conversation
     */
    override suspend fun getConversationMessages(conversationId: String): Result<List<ChatMessageDto>> {
        return try {
            val response = apiService.getChatMessages(page = 1, pageSize = 100)
            if (response.success && response.data != null) {
                Result.success(
                    response.data.filter { it.conversationId == null || it.conversationId == conversationId }
                )
            } else {
                Result.failure(ApiException.NotFound("Conversation not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Send chat message
     */
    override suspend fun sendChatMessage(
        request: SendChatMessageRequest
    ): Result<ChatMessageDto> {
        return try {
            val response = apiService.sendChatMessage(SendMessageDto(request.message))
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
}
