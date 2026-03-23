package com.example.theflower.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theflower.data.local.TokenManager
import com.example.theflower.data.remote.dtos.ChatMessageDto
import com.example.theflower.data.remote.dtos.ConversationSummaryDto
import com.example.theflower.domain.repositories.ChatConnectionStatus
import com.example.theflower.domain.repositories.IChatRepository
import com.example.theflower.di.DIContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class AdminChatViewModel(
    private val chatRepository: IChatRepository = DIContainer.getChatRepository(),
    private val tokenManager: TokenManager = DIContainer.getTokenManager()
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationSummaryDto>>(emptyList())
    val conversations: StateFlow<List<ConversationSummaryDto>> = _conversations.asStateFlow()

    private val _selectedUserId = MutableStateFlow<String?>(null)
    val selectedUserId: StateFlow<String?> = _selectedUserId.asStateFlow()

    val messages: StateFlow<List<ChatMessageDto>> = chatRepository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val connectionStatus: StateFlow<ChatConnectionStatus> = chatRepository.connectionStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatConnectionStatus.DISCONNECTED)

    init {
        initChat()
    }

    private fun initChat() {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().orEmpty()
            if (token.isNotBlank()) {
                chatRepository.connect(token)
                loadConversations()
            } else {
                Timber.e("No access token available for admin chat")
            }
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            val list = chatRepository.getConversations()
            _conversations.value = list
        }
    }

    fun selectUser(userId: String) {
        _selectedUserId.value = userId
        chatRepository.setActiveUserId(userId)
        viewModelScope.launch {
            chatRepository.loadMessagesForUser(userId = userId, page = 1)
        }
    }

    fun clearSelectedUser() {
        _selectedUserId.value = null
        chatRepository.setActiveUserId(null)
    }

    fun sendReply(text: String) {
        val targetUserId = _selectedUserId.value
        if (targetUserId.isNullOrBlank() || text.isBlank()) return
        
        viewModelScope.launch {
            chatRepository.sendAdminReply(targetUserId, text)
            // Refresh conversation list so the last message updates
            loadConversations()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Anti-leak
        viewModelScope.launch {
            chatRepository.setActiveUserId(null)
            chatRepository.disconnect()
        }
    }
}
