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
    private val apiService: TheFlowerApiService,
    private val token: String
) : IChatRepository {
    
    private val authHeader get() = token
    
    /**
     * Get all chat conversations
     */
    override suspend fun getChatConversations(): Result<List<ChatMessageDto>> {
        return try {
            val response = apiService.getChatConversations(authHeader)
            
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
     * Get messages from specific conversation
     */
    override suspend fun getConversationMessages(conversationId: Int): Result<List<ChatMessageDto>> {
        return try {
            val response = apiService.getConversationMessages(authHeader, conversationId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound(message = "Conversation not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Send chat message
     */
    override suspend fun sendChatMessage(
        conversationId: Int,
        message: String
    ): Result<ChatMessageDto> {
        return try {
            val request = SendChatMessageRequest(
                conversationId = conversationId,
                message = message
            )
            val response = apiService.sendChatMessage(authHeader, request)
            
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
}
