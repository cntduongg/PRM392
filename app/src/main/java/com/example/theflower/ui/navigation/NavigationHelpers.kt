package com.example.theflower.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.theflower.ui.theme.MossGreen
import com.example.theflower.ui.theme.PlaceholderGray
import com.example.theflower.ui.theme.Sand
import com.example.theflower.ui.theme.SandDark
import com.example.theflower.ui.theme.SoilBrown
import kotlin.math.roundToInt

@Composable
internal fun botanicalOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Sand,
    unfocusedContainerColor = Sand,
    disabledContainerColor = Sand,
    focusedTextColor = SoilBrown,
    unfocusedTextColor = SoilBrown,
    cursorColor = MossGreen,
    focusedLabelColor = PlaceholderGray,
    unfocusedLabelColor = PlaceholderGray,
    focusedBorderColor = MossGreen,
    unfocusedBorderColor = SandDark
)

@Composable
internal fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = SandDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun ErrorNote(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.fillMaxWidth()
    )
}

internal fun formatCurrency(value: Double): String {
    return "\u20AB${value.roundToInt()}"
}

internal fun formatProductInfoLines(content: String): List<String> {
    val normalized = content
        .replace("\r\n", "\n")
        .replace("•", "\n")
        .replace(";", "\n")
        .replace("|", "\n")

    return normalized
        .split('\n')
        .map { it.trim().trimStart('-', '*', '•').trim() }
        .filter { it.isNotEmpty() }
        .ifEmpty { listOf(content.trim()) }
}
