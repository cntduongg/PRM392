package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.IChatRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
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
    override suspend fun getChatConversations(token: String): Result<List<ChatMessageDto>> {
        return try {
            val response = apiService.getChatConversations(token)
            
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
    override suspend fun getConversationMessages(token: String, conversationId: Int): Result<List<ChatMessageDto>> {
        return try {
            val response = apiService.getConversationMessages(token, conversationId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
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
        token: String,
        request: SendChatMessageRequest
    ): Result<ChatMessageDto> {
        return try {
            val response = apiService.sendChatMessage(token, request)
            
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
