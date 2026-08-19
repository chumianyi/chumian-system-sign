package com.chumian.systemsign.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chumian.systemsign.ui.NeuCard
import com.chumian.systemsign.ui.NeuColors

@Composable
fun SettingsScreen(
    dark: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    var darkMode by remember { mutableStateOf(dark) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "设置",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NeuColors.text(dark)
        )
        Spacer(modifier = Modifier.height(16.dp))

        NeuCard(dark = dark) {
            Text("外观", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DarkMode, "深色模式", tint = NeuColors.textSecondary(dark))
                Spacer(modifier = Modifier.width(12.dp))
                Text("深色模式", fontSize = 15.sp, color = NeuColors.text(dark), modifier = Modifier.weight(1f))
                Switch(
                    checked = darkMode,
                    onCheckedChange = {
                        darkMode = it
                        onDarkModeChange(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeuCard(dark = dark) {
            Text("签名默认设置", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
            Spacer(modifier = Modifier.height(12.dp))
            SettingRow("默认 V1 签名", "启用 JAR 签名方案", dark)
            SettingRow("默认 V2 签名", "启用 APK 签名方案 v2", dark)
            SettingRow("默认 V3 签名", "启用密钥轮换 (实验性)", dark)
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeuCard(dark = dark) {
            Text("关于", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
            Spacer(modifier = Modifier.height(12.dp))
            AboutRow("应用名称", "初眠系统签", dark)
            AboutRow("版本", "v1.0.0", dark)
            AboutRow("包名", "com.chumian.systemsign", dark)
            AboutRow("最低 SDK", "Android 7.0 (API 24)", dark)
            AboutRow("目标 SDK", "Android 14 (API 34)", dark)
            AboutRow("架构", "arm64-v8a", dark)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "内置 AOSP 各版本系统签名密钥，支持系统应用签名、自定义签名与证书生成。",
                fontSize = 12.sp,
                color = NeuColors.textSecondary(dark)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingRow(title: String, desc: String, dark: Boolean) {
    var checked by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, color = NeuColors.text(dark))
            Text(desc, fontSize = 11.sp, color = NeuColors.textSecondary(dark))
        }
        Switch(checked = checked, onCheckedChange = { checked = it })
    }
}

@Composable
fun AboutRow(label: String, value: String, dark: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(label, fontSize = 13.sp, color = NeuColors.textSecondary(dark), modifier = Modifier.width(90.dp))
        Text(value, fontSize = 13.sp, color = NeuColors.text(dark), fontWeight = FontWeight.Medium)
    }
}
