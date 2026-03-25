package com.example.theflower.data.repository

import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.ChatMessageDto
import com.example.theflower.data.remote.dtos.ConversationSummaryDto
import com.example.theflower.data.remote.dtos.SendMessageRequest
import com.example.theflower.domain.repositories.ChatConnectionStatus
import com.example.theflower.domain.repositories.IChatRepository
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ChatRepositoryImpl(
    private val apiService: TheFlowerApiService,
    private val baseUrl: String
) : IChatRepository {

    private var hubConnection: HubConnection? = null
    
    private val _messages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    override val messages: StateFlow<List<ChatMessageDto>> = _messages.asStateFlow()

    private var activeUserId: String? = null

    private val _connectionStatus = MutableStateFlow(ChatConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ChatConnectionStatus> = _connectionStatus.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override suspend fun connect(accessToken: String) {
        val currentState = hubConnection?.connectionState
        if (currentState == HubConnectionState.CONNECTED || currentState == HubConnectionState.CONNECTING) {
            Timber.d("SignalR already connecting or connected")
            return
        }

        _connectionStatus.value = ChatConnectionStatus.CONNECTING
        
        val hubUrl = if (baseUrl.endsWith("/")) "${baseUrl}hub/chat" else "$baseUrl/hub/chat"
        Timber.d("Connecting to SignalR at $hubUrl")
        
        try {
            hubConnection = HubConnectionBuilder.create(hubUrl)
                .withAccessTokenProvider(io.reactivex.rxjava3.core.Single.just(accessToken))
                .build()

            // Handle incoming messages
            hubConnection?.on("ReceiveMessage", { message: ChatMessageDto ->
                Timber.d("Received message: ${message.message} (isFromAdmin: ${message.isFromAdmin})")
                val currentList = _messages.value.toMutableList()
                if (currentList.none { it.id == message.id }) {
                    currentList.add(message)
                    _messages.value = currentList
                }
            }, ChatMessageDto::class.java)

            hubConnection?.on("ReceiveUserMessage", { data: Map<String, Any> ->
                Timber.d("Admin received ReceiveUserMessage event")
            }, Map::class.java)

            hubConnection?.onClosed { exception ->
                if (exception != null) {
                    Timber.e(exception, "SignalR Connection Closed with error")
                } else {
                    Timber.d("SignalR Connection Closed gracefully")
                }
                _connectionStatus.value = ChatConnectionStatus.DISCONNECTED
            }

            hubConnection?.start()?.blockingAwait()
            
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                _connectionStatus.value = ChatConnectionStatus.CONNECTED
                Timber.i("SignalR Connected successfully to $hubUrl")
            } else {
                _connectionStatus.value = ChatConnectionStatus.ERROR
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to connect to SignalR")
            _connectionStatus.value = ChatConnectionStatus.ERROR
        }
    }

    override suspend fun disconnect() {
        hubConnection?.stop()?.blockingAwait()
        hubConnection = null
        _connectionStatus.value = ChatConnectionStatus.DISCONNECTED
    }

    override suspend fun sendMessage(text: String) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            hubConnection?.send("SendMessage", text)
        } else {
            // Fallback to REST
            try {
                val response = apiService.sendChatMessage(SendMessageRequest(text))
                if (response.success && response.data != null) {
                    val currentList = _messages.value.toMutableList()
                    currentList.add(response.data)
                    _messages.value = currentList
                }
            } catch (e: Exception) {
                Timber.e(e, "REST Send fallback failed")
            }
        }
    }

    override suspend fun sendAdminReply(targetUserId: String, text: String) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            try {
                hubConnection?.send("SendMessageToUser", targetUserId, text)
                return
            } catch (e: Exception) {
                Timber.w("Failed to send SignalR admin reply, falling back to REST: ${e.message}")
            }
        }
        
        // Fallback to REST
        try {
            val response = apiService.sendAdminReply(com.example.theflower.data.remote.dtos.AdminReplyRequest(targetUserId, text))
            if (response.success && response.data != null) {
                val currentList = _messages.value.toMutableList()
                currentList.add(response.data)
                _messages.value = currentList
                Timber.d("Admin reply sent via REST successfully")
            }
        } catch (e: Exception) {
            Timber.e(e, "REST Admin reply fallback failed")
        }
    }

    override suspend fun loadHistory(page: Int, pageSize: Int): List<ChatMessageDto> {
        return try {
            val response = apiService.getChatMessages(page, pageSize)
            if (response.success) {
                val history = response.data ?: emptyList()
                // Update message list with history if it's page 1
                if (page == 1) {
                    _messages.value = history
                }
                history
            } else emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Failed to load chat history")
            emptyList()
        }
    }

    override suspend fun getConversations(): List<ConversationSummaryDto> {
        return try {
            val response = apiService.getConversations()
            if (response.success) response.data ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun loadMessagesForUser(userId: String, page: Int, pageSize: Int): List<ChatMessageDto> {
        return try {
            val response = apiService.getMessagesForUser(userId, page, pageSize)
            if (response.success) {
                val history = response.data ?: emptyList()
                if (page == 1) {
                    _messages.value = history
                }
                history
            } else emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Failed to load messages for user: $userId")
            emptyList()
        }
    }

    override fun setActiveUserId(userId: String?) {
        activeUserId = userId
        if (userId == null) {
            // Clear messages when exiting chat view
            _messages.value = emptyList()
        }
    }

    override suspend fun clearChat(): Result<Unit> {
        return try {
            val response = apiService.deleteChatMessages()
            if (response.success) {
                _messages.value = emptyList()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear chat history")
            Result.failure(e)
        }
    }

    override suspend fun clearUserChat(userId: String): Result<Unit> {
        return try {
            val response = apiService.deleteUserChatMessages(userId)
            if (response.success) {
                // If we are currently viewing this user's messages, clear them in UI
                if (activeUserId == userId) {
                    _messages.value = emptyList()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear user chat history: $userId")
            Result.failure(e)
        }
    }
}
