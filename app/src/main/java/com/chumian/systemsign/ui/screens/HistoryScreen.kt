package com.chumian.systemsign.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chumian.systemsign.data.SignHistory
import com.chumian.systemsign.ui.NeuCard
import com.chumian.systemsign.ui.NeuColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    dark: Boolean,
    history: List<SignHistory>,
    onInstall: (String) -> Unit,
    onDelete: (Long) -> Unit
) {
    val textColor = NeuColors.text(dark)
    val secondaryColor = NeuColors.textSecondary(dark)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "签名历史",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (history.isEmpty()) {
            NeuCard(dark = dark) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = secondaryColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("暂无签名记录", fontSize = 14.sp, color = secondaryColor)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history) { item ->
                    HistoryItem(dark, item, onInstall, onDelete, textColor, secondaryColor)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    dark: Boolean,
    item: SignHistory,
    onInstall: (String) -> Unit,
    onDelete: (Long) -> Unit,
    textColor: androidx.compose.ui.graphics.Color,
    secondaryColor: androidx.compose.ui.graphics.Color
) {
    NeuCard(dark = dark) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            if (item.status == "成功") NeuColors.success(dark) else NeuColors.error(dark),
                            androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (item.status == "成功") Icons.Default.Check else Icons.Default.Close,
                        null,
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.appName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Text(item.packageName, fontSize = 10.sp, color = secondaryColor)
                }
                Text(item.status, fontSize = 11.sp, color = if (item.status == "成功") NeuColors.success(dark) else NeuColors.error(dark))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "密钥: ${item.keyType} | ${item.androidVersion}",
                fontSize = 10.sp,
                color = secondaryColor
            )
            Text(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp)),
                fontSize = 10.sp,
                color = secondaryColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                if (item.status == "成功") {
                    TextButton(onClick = { onInstall(item.outputPath) }, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Icon(Icons.Default.InstallMobile, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("安装", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onDelete(item.id) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "删除", tint = NeuColors.error(dark), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
