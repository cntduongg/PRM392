package com.example.theflower.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theflower.data.remote.dtos.ChatMessageDto
import com.example.theflower.domain.repositories.ChatConnectionStatus
import com.example.theflower.ui.theme.*
import com.example.theflower.ui.viewmodels.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val status by viewModel.connectionStatus.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            var showClearConfirm by remember { mutableStateOf(false) }
            
            ChatTopBar(
                status = status,
                onBack = onBack,
                onRefresh = viewModel::refreshHistory,
                onClear = { showClearConfirm = true }
            )

            if (showClearConfirm) {
                AlertDialog(
                    onDismissRequest = { showClearConfirm = false },
                    title = { Text("Xác nhận") },
                    text = { Text("Bạn có chắc chắn muốn xóa toàn bộ lịch sử trò chuyện không?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.clearHistory()
                            showClearConfirm = false
                        }) { Text("Xóa", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearConfirm = false }) { Text("Hủy") }
                    },
                    containerColor = PaperWhite
                )
            }
        },
        bottomBar = {
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (messages.isEmpty() && status == ChatConnectionStatus.CONNECTED) {
                EmptyChatState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(message = message)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    status: ChatConnectionStatus,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onClear: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Hỗ trợ trực tuyến",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoilBrown,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (status) {
                        ChatConnectionStatus.CONNECTED -> Color(0xFF4CAF50)
                        ChatConnectionStatus.CONNECTING -> Color(0xFFFFC107)
                        else -> Color.Red
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = when (status) {
                            ChatConnectionStatus.CONNECTED -> "Đang trực tuyến"
                            ChatConnectionStatus.CONNECTING -> "Đang kết nối..."
                            else -> "Mất kết nối"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = SandDark
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp, color = SoilBrown)
            }
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Text("🔄", fontSize = 18.sp)
            }
            IconButton(onClick = onClear) {
                Text("🗑", fontSize = 18.sp, color = Color.Red.copy(alpha = 0.7f))
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PaperWhite)
    )
}

@Composable
private fun ChatBubble(message: ChatMessageDto) {
    val isUser = !message.isFromAdmin
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isUser) {
                // Admin avatar (initial)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Sand),
                    contentAlignment = Alignment.Center
                ) {
                    Text("AD", fontSize = 12.sp, color = SoilBrown, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                color = if (isUser) MossGreen else Sand.copy(alpha = 0.5f),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.message,
                    modifier = Modifier.padding(12.dp),
                    color = if (isUser) PaperWhite else SoilBrown,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Text(
            text = formatTime(message.sentAt),
            style = MaterialTheme.typography.labelSmall,
            color = SandDark.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp, start = if (isUser) 0.dp else 40.dp)
        )
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = PaperWhite,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Nhập tin nhắn...", color = PlaceholderGray) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 120.dp),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Sand.copy(alpha = 0.3f),
                    unfocusedContainerColor = Sand.copy(alpha = 0.3f),
                    focusedBorderColor = MossGreen,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = SoilBrown,
                    unfocusedTextColor = SoilBrown,
                    cursorColor = MossGreen
                )
            )
            
            FloatingActionButton(
                onClick = onSend,
                containerColor = MossGreen,
                contentColor = PaperWhite,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Text("➤", fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun EmptyChatState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("💬", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Bắt đầu trò chuyện với chúng tôi",
            style = MaterialTheme.typography.titleMedium,
            color = SoilBrown
        )
        Text(
            "Chúng tôi sẽ phản hồi sớm nhất có thể",
            style = MaterialTheme.typography.bodySmall,
            color = SandDark
        )
    }
}

private fun formatTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return ""
    return try {
        // Strip milliseconds if present for simpler parsing
        val cleanIso = if (isoString.contains(".")) isoString.substringBefore(".") else isoString
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+7")
        }
        val date = inputFormat.parse(cleanIso)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+7")
        }
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}
