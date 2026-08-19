package com.chumian.systemsign.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chumian.systemsign.data.*
import com.chumian.systemsign.parser.ApkInfoParser
import com.chumian.systemsign.signing.ApkSigningEngine
import com.chumian.systemsign.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val parser = ApkInfoParser()
    private val signer = ApkSigningEngine(application)

    private val _apkInfo = MutableStateFlow<ApkInfo?>(null)
    val apkInfo: StateFlow<ApkInfo?> = _apkInfo.asStateFlow()

    private val _signingConfig = MutableStateFlow(SigningConfig())
    val signingConfig: StateFlow<SigningConfig> = _signingConfig.asStateFlow()

    private val _isSigning = MutableStateFlow(false)
    val isSigning: StateFlow<Boolean> = _isSigning.asStateFlow()

    private val _signingLogs = MutableStateFlow<List<String>>(emptyList())
    val signingLogs: StateFlow<List<String>> = _signingLogs.asStateFlow()

    private val _signingResult = MutableStateFlow<SigningResult?>(null)
    val signingResult: StateFlow<SigningResult?> = _signingResult.asStateFlow()

    private val _history = MutableStateFlow<List<SignHistory>>(emptyList())
    val history: StateFlow<List<SignHistory>> = _history.asStateFlow()

    private val _keystores = MutableStateFlow<List<KeystoreInfo>>(emptyList())
    val keystores: StateFlow<List<KeystoreInfo>> = _keystores.asStateFlow()

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private var tempApkFile: File? = null

    val androidVersions = listOf(
        "android-7.0.0_r1" to "Android 7.0 (Nougat)",
        "android-8.0.0_r1" to "Android 8.0 (Oreo)",
        "android-8.1.0_r1" to "Android 8.1 (Oreo)",
        "android-9.0.0_r1" to "Android 9 (Pie)",
        "android-10.0.0_r1" to "Android 10 (Q)",
        "android-11.0.0_r1" to "Android 11 (R)",
        "android-12.0.0_r1" to "Android 12 (S)",
        "android-12.1.0_r1" to "Android 12.1 (S_V2)"
    )

    val keyTypes = listOf("platform", "media", "shared", "testkey")

    fun importApk(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tempDir = FileUtils.getTempDir(getApplication())
                val tempFile = File(tempDir, "imported_${System.currentTimeMillis()}.apk")
                FileUtils.copyUriToFile(getApplication(), uri, tempFile)
                tempApkFile = tempFile
                val info = parser.parseApk(tempFile)
                _apkInfo.value = info
            }
        }
    }

    fun updateConfig(config: SigningConfig) {
        _signingConfig.value = config
    }

    fun startSigning() {
        val apk = tempApkFile ?: return
        val config = _signingConfig.value

        viewModelScope.launch {
            _isSigning.value = true
            _signingLogs.value = emptyList()
            _signingResult.value = null

            withContext(Dispatchers.IO) {
                val outputDir = FileUtils.getOutputDir(getApplication())
                val outputName = FileUtils.generateOutputName(apk.name, config.keyType)
                val outputFile = File(outputDir, outputName)

                val result = signer.signApk(apk, outputFile, config) { log ->
                    _signingLogs.value = _signingLogs.value + log
                }

                _signingResult.value = result

                if (result.success) {
                    val historyItem = SignHistory(
                        appName = _apkInfo.value?.appName ?: "未知",
                        packageName = _apkInfo.value?.packageName ?: "",
                        timestamp = System.currentTimeMillis(),
                        keyType = config.keyType,
                        androidVersion = config.androidVersion,
                        status = "成功",
                        outputPath = result.outputPath
                    )
                    _history.value = listOf(historyItem) + _history.value
                }
            }

            _isSigning.value = false
        }
    }

    fun generateKeystore(
        name: String,
        storePassword: String,
        keyPassword: String,
        alias: String,
        validityYears: Int,
        orgUnit: String,
        org: String,
        city: String,
        province: String,
        countryCode: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val keystoreDir = FileUtils.getKeystoreDir(getApplication())
                val outputFile = File(keystoreDir, "$name.p12")

                val dn = mapOf(
                    "CN" to name,
                    "OU" to orgUnit,
                    "O" to org,
                    "L" to city,
                    "ST" to province,
                    "C" to countryCode
                )

                val success = signer.generateKeystore(
                    outputFile, alias, storePassword, keyPassword, validityYears, dn
                )

                if (success) {
                    val info = KeystoreInfo(
                        name = name,
                        path = outputFile.absolutePath,
                        alias = alias,
                        createdAt = System.currentTimeMillis(),
                        validityYears = validityYears
                    )
                    _keystores.value = _keystores.value + info
                    onComplete(true, outputFile.absolutePath)
                } else {
                    onComplete(false, "")
                }
            }
        }
    }

    fun importCustomKeystore(uri: Uri, password: String, alias: String, keyPassword: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val keystoreDir = FileUtils.getKeystoreDir(getApplication())
                val destFile = File(keystoreDir, "imported_${System.currentTimeMillis()}.p12")
                FileUtils.copyUriToFile(getApplication(), uri, destFile)

                val currentConfig = _signingConfig.value
                _signingConfig.value = currentConfig.copy(
                    customKeystorePath = destFile.absolutePath,
                    storePassword = password,
                    keyAlias = alias,
                    keyPassword = keyPassword
                )
            }
        }
    }

    fun setDarkMode(dark: Boolean) {
        _darkMode.value = dark
    }

    fun clearApk() {
        _apkInfo.value = null
        tempApkFile?.delete()
        tempApkFile = null
        _signingResult.value = null
        _signingLogs.value = emptyList()
    }

    fun deleteHistory(id: Long) {
        _history.value = _history.value.filter { it.id != id }
    }

    fun deleteKeystore(path: String) {
        File(path).delete()
        _keystores.value = _keystores.value.filter { it.path != path }
    }
}
