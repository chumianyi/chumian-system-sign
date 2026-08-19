package com.chumian.systemsign

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.chumian.systemsign.data.SigningMode
import com.chumian.systemsign.ui.*
import com.chumian.systemsign.ui.screens.*
import java.io.File

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode by viewModel.darkMode.collectAsState()
            ChumianTheme(darkTheme = darkMode) {
                MainScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {
        var selectedTab by remember { mutableStateOf(0) }
        var selectedMode by remember { mutableStateOf<SigningMode?>(null) }

        val apkInfo by viewModel.apkInfo.collectAsState()
        val config by viewModel.signingConfig.collectAsState()
        val isSigning by viewModel.isSigning.collectAsState()
        val logs by viewModel.signingLogs.collectAsState()
        val result by viewModel.signingResult.collectAsState()
        val history by viewModel.history.collectAsState()
        val keystores by viewModel.keystores.collectAsState()
        val dark by viewModel.darkMode.collectAsState()

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                val tabs = listOf(
                    TabItem("签名", Icons.Default.Edit),
                    TabItem("证书", Icons.Default.VpnKey),
                    TabItem("历史", Icons.Default.History),
                    TabItem("设置", Icons.Default.Settings)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = selectedTab == index
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedTab = index
                                    if (index == 0) selectedMode = null
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedTab) {
                    0 -> {
                        if (selectedMode == null) {
                            HomeScreen(dark = dark) { mode ->
                                selectedMode = mode
                                viewModel.updateConfig(config.copy(mode = mode))
                            }
                        } else if (selectedMode == SigningMode.GENERATE) {
                            GenerateScreen(dark = dark) { name, sp, kp, alias, validity, ou, o, l, st, c, cb ->
                                viewModel.generateKeystore(name, sp, kp, alias, validity, ou, o, l, st, c, cb)
                            }
                        } else {
                            SigningScreen(
                                dark = dark,
                                mode = selectedMode!!,
                                apkInfo = apkInfo,
                                config = config,
                                isSigning = isSigning,
                                logs = logs,
                                result = result,
                                androidVersions = viewModel.androidVersions,
                                keyTypes = viewModel.keyTypes,
                                onImportApk = { viewModel.importApk(it) },
                                onImportKeystore = { uri, sp, ka, kp ->
                                    viewModel.importCustomKeystore(uri, sp, ka, kp)
                                },
                                onConfigChange = { viewModel.updateConfig(it) },
                                onStartSigning = { viewModel.startSigning() },
                                onClear = { viewModel.clearApk() },
                                onInstall = { installApk(it) },
                                onShare = { shareApk(it) }
                            )
                        }
                    }
                    1 -> CertificateScreen(dark, keystores) { viewModel.deleteKeystore(it) }
                    2 -> HistoryScreen(dark, history, { installApk(it) }, { viewModel.deleteHistory(it) })
                    3 -> SettingsScreen(dark) { viewModel.setDarkMode(it) }
                }
            }
        }
    }

    private fun installApk(path: String) {
        val file = File(path)
        if (!file.exists()) return
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun shareApk(path: String) {
        val file = File(path)
        if (!file.exists()) return
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "分享 APK"))
    }

    data class TabItem(val label: String, val icon: ImageVector)
}
