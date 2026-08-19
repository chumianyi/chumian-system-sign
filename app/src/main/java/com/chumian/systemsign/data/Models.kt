package com.chumian.systemsign.data

data class ApkInfo(
    val packageName: String = "",
    val appName: String = "",
    val versionCode: Long = 0,
    val versionName: String = "",
    val minSdk: Int = 0,
    val targetSdk: Int = 0,
    val isSigned: Boolean = false,
    val signatureScheme: String = "未知",
    val iconPath: String? = null,
    val filePath: String = ""
)

data class SigningConfig(
    val mode: SigningMode = SigningMode.SYSTEM,
    val androidVersion: String = "android-12.0.0_r1",
    val keyType: String = "platform",
    val customKeystorePath: String? = null,
    val storePassword: String = "",
    val keyAlias: String = "",
    val keyPassword: String = "",
    val v1Enabled: Boolean = true,
    val v2Enabled: Boolean = true,
    val v3Enabled: Boolean = false
)

enum class SigningMode {
    SYSTEM, CUSTOM, GENERATE
}

data class SigningResult(
    val success: Boolean,
    val outputPath: String = "",
    val message: String = "",
    val logs: List<String> = emptyList()
)

data class KeystoreInfo(
    val name: String,
    val path: String,
    val alias: String,
    val createdAt: Long,
    val validityYears: Int
)

data class SignHistory(
    val id: Long = System.currentTimeMillis(),
    val appName: String,
    val packageName: String,
    val timestamp: Long,
    val keyType: String,
    val androidVersion: String,
    val status: String,
    val outputPath: String
)
