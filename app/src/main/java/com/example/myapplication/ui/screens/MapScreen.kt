package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.MockData
import com.example.myapplication.ui.theme.*

@Composable
fun MapScreen() {
    val storeLocations = MockData.storeLocations

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(bottom = 60.dp)
    ) {
        // Header
        Text(
            text = "Cửa hàng của chúng tôi",
            style = MaterialTheme.typography.headlineSmall,
            color = SoilBrown,
            modifier = Modifier.padding(16.dp)
        )

        // Map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Sand),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🗺️\nGoogle Maps\n(Sẽ được integrate)",
                style = MaterialTheme.typography.bodyMedium,
                color = SandDark,
                textAlign = TextAlign.Center
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Chi nhánh gần bạn",
            style = MaterialTheme.typography.titleMedium,
            color = SoilBrown,
            modifier = Modifier.padding(16.dp)
        )

        // Store list
        LazyColumn {
            items(storeLocations) { (coordinates, storeName) ->
                StoreLocationCard(
                    coordinates = coordinates,
                    storeName = storeName
                )
            }
        }
    }
}

@Composable
fun StoreLocationCard(
    coordinates: String,
    storeName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Sand),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MossGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍")
                }
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoilBrown,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Tọa độ: $coordinates",
                style = MaterialTheme.typography.bodySmall,
                color = SandDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MossGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "📱 Gọi",
                        style = MaterialTheme.typography.labelSmall,
                        color = PaperWhite
                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WarmPeach),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "🗺️ Chỉ đường",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoilBrown
                    )
                }
            }
        }
    }
}
