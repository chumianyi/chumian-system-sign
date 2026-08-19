package com.chumian.systemsign.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chumian.systemsign.data.ApkInfo
import com.chumian.systemsign.data.SigningConfig
import com.chumian.systemsign.data.SigningMode
import com.chumian.systemsign.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigningScreen(
    dark: Boolean,
    mode: SigningMode,
    apkInfo: ApkInfo?,
    config: SigningConfig,
    isSigning: Boolean,
    logs: List<String>,
    result: com.chumian.systemsign.data.SigningResult?,
    androidVersions: List<Pair<String, String>>,
    keyTypes: List<String>,
    onImportApk: (Uri) -> Unit,
    onImportKeystore: (Uri, String, String, String) -> Unit,
    onConfigChange: (SigningConfig) -> Unit,
    onStartSigning: () -> Unit,
    onClear: () -> Unit,
    onInstall: (String) -> Unit,
    onShare: (String) -> Unit
) {
    var expandedVersion by remember { mutableStateOf(false) }
    var expandedKey by remember { mutableStateOf(false) }
    var storePassword by remember { mutableStateOf("") }
    var keyAlias by remember { mutableStateOf("") }
    var keyPassword by remember { mutableStateOf("") }

    val apkPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { onImportApk(it) } }

    val keystorePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImportKeystore(it, storePassword, keyAlias, keyPassword) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = when (mode) {
                SigningMode.SYSTEM -> "系统应用签名"
                SigningMode.CUSTOM -> "自定义签名"
                SigningMode.GENERATE -> "生成签名"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NeuColors.text(dark)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // APK Import
        NeuCard(dark = dark) {
            Text("APK 文件", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
            Spacer(modifier = Modifier.height(8.dp))
            if (apkInfo != null) {
                ApkInfoCard(dark, apkInfo)
                Spacer(modifier = Modifier.height(8.dp))
                NeuOutlinedButton(onClick = onClear, dark = dark, text = "重新选择")
            } else {
                NeuButton(
                    onClick = { apkPicker.launch("application/vnd.android.package-archive") },
                    dark = dark,
                    text = "选择 APK 文件"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System key config
        if (mode == SigningMode.SYSTEM) {
            NeuCard(dark = dark) {
                Text("系统密钥配置", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
                Spacer(modifier = Modifier.height(12.dp))

                Text("Android 版本", fontSize = 13.sp, color = NeuColors.textSecondary(dark))
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedVersion,
                    onExpandedChange = { expandedVersion = it }
                ) {
                    OutlinedTextField(
                        value = androidVersions.find { it.first == config.androidVersion }?.second ?: config.androidVersion,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVersion) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NeuColors.text(dark),
                            unfocusedTextColor = NeuColors.text(dark)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedVersion,
                        onDismissRequest = { expandedVersion = false }
                    ) {
                        androidVersions.forEach { (tag, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    onConfigChange(config.copy(androidVersion = tag))
                                    expandedVersion = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("密钥类型", fontSize = 13.sp, color = NeuColors.textSecondary(dark))
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedKey,
                    onExpandedChange = { expandedKey = it }
                ) {
                    OutlinedTextField(
                        value = config.keyType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKey) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NeuColors.text(dark),
                            unfocusedTextColor = NeuColors.text(dark)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedKey,
                        onDismissRequest = { expandedKey = false }
                    ) {
                        keyTypes.forEach { key ->
                            DropdownMenuItem(
                                text = { Text(key) },
                                onClick = {
                                    onConfigChange(config.copy(keyType = key))
                                    expandedKey = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Custom keystore config
        if (mode == SigningMode.CUSTOM) {
            NeuCard(dark = dark) {
                Text("自定义证书", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = storePassword,
                    onValueChange = { storePassword = it },
                    label = { Text("Store 密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NeuColors.text(dark),
                        unfocusedTextColor = NeuColors.text(dark)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keyAlias,
                    onValueChange = { keyAlias = it },
                    label = { Text("Key 别名") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NeuColors.text(dark),
                        unfocusedTextColor = NeuColors.text(dark)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keyPassword,
                    onValueChange = { keyPassword = it },
                    label = { Text("Key 密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NeuColors.text(dark),
                        unfocusedTextColor = NeuColors.text(dark)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeuButton(
                    onClick = { keystorePicker.launch("*/*") },
                    dark = dark,
                    text = "导入证书文件"
                )
                if (config.customKeystorePath != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "已导入: ${config.customKeystorePath.substringAfterLast('/')}",
                        fontSize = 12.sp,
                        color = NeuColors.success(dark)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Signing options
        if (mode != SigningMode.GENERATE) {
            NeuCard(dark = dark) {
                Text("签名方案", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = config.v1Enabled,
                        onCheckedChange = { onConfigChange(config.copy(v1Enabled = it)) }
                    )
                    Text("V1 (JAR 签名)", color = NeuColors.text(dark))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = config.v2Enabled,
                        onCheckedChange = { onConfigChange(config.copy(v2Enabled = it)) }
                    )
                    Text("V2 (APK 签名方案)", color = NeuColors.text(dark))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = config.v3Enabled,
                        onCheckedChange = { onConfigChange(config.copy(v3Enabled = it)) }
                    )
                    Text("V3 (密钥轮换)", color = NeuColors.text(dark))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Sign button
            NeuButton(
                onClick = onStartSigning,
                dark = dark,
                text = if (isSigning) "签名中..." else "开始签名",
                enabled = apkInfo != null && !isSigning && (mode != SigningMode.CUSTOM || config.customKeystorePath != null),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress & logs
        if (isSigning || logs.isNotEmpty()) {
            NeuCard(dark = dark) {
                Text("签名日志", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeuColors.text(dark))
                Spacer(modifier = Modifier.height(8.dp))
                if (isSigning) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    logs.forEach { log ->
                        Text(
                            text = log,
                            fontSize = 11.sp,
                            color = NeuColors.textSecondary(dark),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Result
        if (result != null && result.success) {
            NeuCard(dark = dark) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, "成功", tint = NeuColors.success(dark))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("签名成功!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeuColors.success(dark))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("输出: ${result.outputPath.substringAfterLast('/')}", fontSize = 12.sp, color = NeuColors.textSecondary(dark))
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    NeuButton(
                        onClick = { onInstall(result.outputPath) },
                        dark = dark,
                        text = "安装",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NeuOutlinedButton(
                        onClick = { onShare(result.outputPath) },
                        dark = dark,
                        text = "分享"
                    )
                }
            }
        } else if (result != null && !result.success) {
            NeuCard(dark = dark) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, "失败", tint = NeuColors.error(dark))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("签名失败", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeuColors.error(dark))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(result.message, fontSize = 12.sp, color = NeuColors.textSecondary(dark))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ApkInfoCard(dark: Boolean, info: ApkInfo) {
    Column {
        InfoRow("应用名", info.appName, dark)
        InfoRow("包名", info.packageName, dark)
        InfoRow("版本名", info.versionName, dark)
        InfoRow("版本号", info.versionCode.toString(), dark)
        InfoRow("最低SDK", "API ${info.minSdk}", dark)
        InfoRow("目标SDK", "API ${info.targetSdk}", dark)
        InfoRow(
            "签名状态",
            if (info.isSigned) "已签名 (${info.signatureScheme})" else "未签名",
            dark
        )
    }
}

@Composable
fun InfoRow(label: String, value: String, dark: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = NeuColors.textSecondary(dark),
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value.ifEmpty { "-" },
            fontSize = 13.sp,
            color = NeuColors.text(dark),
            fontWeight = FontWeight.Medium
        )
    }
}
