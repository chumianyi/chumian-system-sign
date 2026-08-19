package com.chumian.systemsign.ui.screens

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "签名历史",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NeuColors.text(dark)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            NeuCard(dark = dark) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = NeuColors.textSecondary(dark),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("暂无签名记录", fontSize = 15.sp, color = NeuColors.textSecondary(dark))
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { item ->
                    HistoryItem(dark, item, onInstall, onDelete)
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
    onDelete: (Long) -> Unit
) {
    NeuCard(dark = dark) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (item.status == "成功") NeuColors.success(dark) else NeuColors.error(dark),
                            androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (item.status == "成功") Icons.Default.Check else Icons.Default.Close,
                        null,
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.appName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
                    Text(item.packageName, fontSize = 11.sp, color = NeuColors.textSecondary(dark))
                }
                Text(item.status, fontSize = 12.sp, color = if (item.status == "成功") NeuColors.success(dark) else NeuColors.error(dark))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "密钥: ${item.keyType} | ${item.androidVersion}",
                fontSize = 11.sp,
                color = NeuColors.textSecondary(dark)
            )
            Text(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp)),
                fontSize = 11.sp,
                color = NeuColors.textSecondary(dark)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                if (item.status == "成功") {
                    TextButton(onClick = { onInstall(item.outputPath) }) {
                        Icon(Icons.Default.InstallMobile, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("安装", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onDelete(item.id) }) {
                    Icon(Icons.Default.Delete, "删除", tint = NeuColors.error(dark), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private fun Modifier.background(color: androidx.compose.ui.graphics.Color, shape: androidx.compose.ui.graphics.Shape): Modifier =
    this.then(androidx.compose.foundation.background(color, shape))
