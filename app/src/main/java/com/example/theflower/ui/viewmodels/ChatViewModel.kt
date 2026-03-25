package com.example.theflower.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theflower.data.local.TokenManager
import com.example.theflower.data.remote.dtos.ChatMessageDto
import com.example.theflower.domain.repositories.ChatConnectionStatus
import com.example.theflower.domain.repositories.IChatRepository
import com.example.theflower.di.DIContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class ChatViewModel(
    private val chatRepository: IChatRepository = DIContainer.getChatRepository(),
    private val tokenManager: TokenManager = DIContainer.getTokenManager()
) : ViewModel() {

    val messages: StateFlow<List<ChatMessageDto>> = chatRepository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val connectionStatus: StateFlow<ChatConnectionStatus> = chatRepository.connectionStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatConnectionStatus.DISCONNECTED)

    private var isInitialized = false

    init {
        initChat()
    }

    fun initChat() {
        if (isInitialized) return
        isInitialized = true
        
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().orEmpty()
            if (token.isNotBlank()) {
                // Connect SignalR
                chatRepository.connect(token)
                // Load history
                chatRepository.loadHistory(page = 1)
            } else {
                Timber.e("No access token available for chat")
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            chatRepository.sendMessage(text)
        }
    }

    fun refreshHistory() {
        viewModelScope.launch {
            chatRepository.loadHistory(page = 1)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatRepository.clearChat()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Anti-leak: disconnect from SignalR when VM is destroyed
        viewModelScope.launch {
            chatRepository.disconnect()
        }
    }
}
