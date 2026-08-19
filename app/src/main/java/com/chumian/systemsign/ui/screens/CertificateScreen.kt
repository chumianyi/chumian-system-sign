package com.chumian.systemsign.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chumian.systemsign.data.KeystoreInfo
import com.chumian.systemsign.ui.NeuCard
import com.chumian.systemsign.ui.NeuColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CertificateScreen(
    dark: Boolean,
    keystores: List<KeystoreInfo>,
    onDelete: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "证书管理",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NeuColors.text(dark)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (keystores.isEmpty()) {
            NeuCard(dark = dark) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = NeuColors.textSecondary(dark),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "暂无保存的证书",
                        fontSize = 15.sp,
                        color = NeuColors.textSecondary(dark)
                    )
                    Text(
                        "在「生成签名」中创建或在「自定义签名」中导入",
                        fontSize = 12.sp,
                        color = NeuColors.textSecondary(dark)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(keystores) { ks ->
                    KeystoreItem(dark, ks, onDelete)
                }
            }
        }
    }
}

@Composable
fun KeystoreItem(dark: Boolean, ks: KeystoreInfo, onDelete: (String) -> Unit) {
    NeuCard(dark = dark) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        NeuColors.accent(dark),
                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.VpnKey, null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ks.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
                Text("别名: ${ks.alias}", fontSize = 12.sp, color = NeuColors.textSecondary(dark))
                Text("有效期: ${ks.validityYears} 年", fontSize = 12.sp, color = NeuColors.textSecondary(dark))
                Text(
                    "创建: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(ks.createdAt))}",
                    fontSize = 11.sp,
                    color = NeuColors.textSecondary(dark)
                )
            }
            IconButton(onClick = { onDelete(ks.path) }) {
                Icon(Icons.Default.Delete, "删除", tint = NeuColors.error(dark))
            }
        }
    }
}
