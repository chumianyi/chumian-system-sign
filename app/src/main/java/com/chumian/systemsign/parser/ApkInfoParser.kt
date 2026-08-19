package com.chumian.systemsign.parser

import android.util.Log
import com.chumian.systemsign.data.ApkInfo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.ZipFile

class ApkInfoParser {

    fun parseApk(apkFile: File): ApkInfo {
        return try {
            ZipFile(apkFile).use { zip ->
                val manifestEntry = zip.getEntry("AndroidManifest.xml")
                val manifestBytes = manifestEntry?.let {
                    zip.getInputStream(it).use { ins -> ins.readBytes() }
                } ?: ByteArray(0)

                val info = parseBinaryManifest(manifestBytes)

                // Check signature
                val isSigned = checkSignature(zip)
                val scheme = detectSignatureScheme(zip)

                // Try to get icon
                val iconPath = findIcon(zip, info.iconPath)

                info.copy(
                    filePath = apkFile.absolutePath,
                    isSigned = isSigned,
                    signatureScheme = scheme,
                    iconPath = iconPath
                )
            }
        } catch (e: Exception) {
            Log.e("ApkInfoParser", "解析APK失败", e)
            ApkInfo(filePath = apkFile.absolutePath)
        }
    }

    private fun parseBinaryManifest(bytes: ByteArray): ApkInfo {
        if (bytes.isEmpty()) return ApkInfo()

        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        // Read AXML header
        val magic = buffer.int
        if (magic != 0x00080003) return ApkInfo()

        val fileSize = buffer.int

        var packageName = ""
        var appName = ""
        var versionCode = 0L
        var versionName = ""
        var minSdk = 0
        var targetSdk = 0
        var iconResourceId = 0

        // Parse string pool
        val stringPoolOffset = buffer.position()
        val spMagic = buffer.int
        val spSize = buffer.int
        val spStringCount = buffer.int
        val spStyleCount = buffer.int
        val spFlags = buffer.int
        val spStringsStart = buffer.int
        val spStylesStart = buffer.int

        val stringOffsets = IntArray(spStringCount) { buffer.int }

        val isUtf8 = (spFlags and 0x100) != 0
        val stringsStart = stringPoolOffset + spStringsStart

        val stringPool = mutableListOf<String>()
        for (i in 0 until spStringCount) {
            val strOffset = stringsStart + stringOffsets[i]
            val str = if (isUtf8) {
                readUtf8String(bytes, strOffset)
            } else {
                readUtf16String(bytes, strOffset)
            }
            stringPool.add(str)
        }

        // Skip to resource IDs (after string pool chunk)
        buffer.position(stringPoolOffset + spSize)

        // Resource IDs chunk
        if (buffer.position() < bytes.size) {
            val resMagic = buffer.int
            if (resMagic == 0x00080180) {
                val resSize = buffer.int
                // Skip resource IDs for now
                buffer.position(buffer.position() + resSize - 8)
            } else {
                buffer.position(buffer.position() - 4)
            }
        }

        // Parse chunks
        while (buffer.position() < bytes.size - 8) {
            val chunkStart = buffer.position()
            val chunkType = buffer.short.toInt()
            val headerSize = buffer.short.toInt()
            val chunkSize = buffer.int

            if (chunkSize <= 0) break

            when (chunkType) {
                0x0100 -> { // Start namespace
                    // skip
                }
                0x0102 -> { // Start element
                    buffer.position(chunkStart + headerSize)
                    val lineNum = buffer.int
                    val comment = buffer.int
                    val nsUri = buffer.int
                    val name = buffer.int
                    val attrStart = buffer.short.toInt()
                    val attrSize = buffer.short.toInt()
                    val attrCount = buffer.short.toInt()

                    val elementName = if (name >= 0 && name < stringPool.size) stringPool[name] else ""

                    buffer.position(chunkStart + headerSize + attrStart)
                    for (j in 0 until attrCount) {
                        val attrNs = buffer.int
                        val attrNameIdx = buffer.int
                        val attrRawValue = buffer.int
                        val attrSize2 = buffer.short.toInt()
                        val attrRes0 = buffer.get().toInt()
                        val attrDataType = buffer.get().toInt()
                        val attrData = buffer.int

                        val attrName = if (attrNameIdx >= 0 && attrNameIdx < stringPool.size) stringPool[attrNameIdx] else ""

                        when (elementName) {
                            "manifest" -> {
                                when (attrName) {
                                    "package" -> packageName = getStringValue(stringPool, attrRawValue, attrDataType, attrData)
                                    "versionCode" -> versionCode = attrData.toLong()
                                    "versionName" -> versionName = getStringValue(stringPool, attrRawValue, attrDataType, attrData)
                                }
                            }
                            "uses-sdk" -> {
                                when (attrName) {
                                    "minSdkVersion" -> minSdk = attrData
                                    "targetSdkVersion" -> targetSdk = attrData
                                }
                            }
                            "application" -> {
                                when (attrName) {
                                    "label" -> appName = getStringValue(stringPool, attrRawValue, attrDataType, attrData)
                                    "icon" -> iconResourceId = attrData
                                }
                            }
                        }
                    }
                }
            }

            buffer.position(chunkStart + chunkSize)
        }

        return ApkInfo(
            packageName = packageName,
            appName = appName.ifEmpty { packageName.substringAfterLast('.') },
            versionCode = versionCode,
            versionName = versionName,
            minSdk = minSdk,
            targetSdk = targetSdk,
            iconPath = if (iconResourceId != 0) "@res/icon_$iconResourceId" else null
        )
    }

    private fun getStringValue(
        stringPool: List<String>,
        rawValue: Int,
        dataType: Int,
        data: Int
    ): String {
        return if (rawValue >= 0 && rawValue < stringPool.size) {
            stringPool[rawValue]
        } else if (dataType == 0x03 && data >= 0 && data < stringPool.size) {
            stringPool[data]
        } else {
            ""
        }
    }

    private fun readUtf8String(bytes: ByteArray, offset: Int): String {
        var pos = offset
        // Skip length
        var len = bytes[pos].toInt() and 0x7F
        pos++
        if (len == 0x7F) {
            len = ((bytes[pos].toInt() and 0xFF) shl 8) or (bytes[pos + 1].toInt() and 0xFF)
            pos += 2
        }
        // Skip second length
        var len2 = bytes[pos].toInt() and 0x7F
        pos++
        if (len2 == 0x7F) {
            len2 = ((bytes[pos].toInt() and 0xFF) shl 8) or (bytes[pos + 1].toInt() and 0xFF)
            pos += 2
        }
        val strBytes = bytes.copyOfRange(pos, pos + len2)
        return String(strBytes, Charsets.UTF_8)
    }

    private fun readUtf16String(bytes: ByteArray, offset: Int): String {
        var pos = offset
        var len = (bytes[pos].toInt() and 0xFF) or ((bytes[pos + 1].toInt() and 0xFF) shl 8)
        pos += 2
        if (len == 0x7FFF) {
            len = ((bytes[pos].toInt() and 0xFF) or ((bytes[pos + 1].toInt() and 0xFF) shl 8) or
                    ((bytes[pos + 2].toInt() and 0xFF) shl 16) or ((bytes[pos + 3].toInt() and 0xFF) shl 24))
            pos += 4
        }
        val strBytes = bytes.copyOfRange(pos, pos + len * 2)
        return String(strBytes, Charsets.UTF_16LE)
    }

    private fun checkSignature(zip: ZipFile): Boolean {
        val metaInfEntries = zip.entries().toList().filter {
            it.name.startsWith("META-INF/") && (it.name.endsWith(".RSA") ||
                    it.name.endsWith(".DSA") || it.name.endsWith(".EC") ||
                    it.name.endsWith(".SF"))
        }
        return metaInfEntries.isNotEmpty()
    }

    private fun detectSignatureScheme(zip: ZipFile): String {
        val schemes = mutableListOf<String>()
        val entries = zip.entries().toList()

        if (entries.any { it.name.startsWith("META-INF/") && it.name.endsWith(".RSA") }) {
            schemes.add("v1")
        }

        // v2/v3 are in APK Signing Block, can't easily detect from zip entries
        // We'll check for the presence of MANIFEST.MF which indicates v1
        if (entries.any { it.name == "META-INF/MANIFEST.MF" }) {
            if (!schemes.contains("v1")) schemes.add("v1")
        }

        return if (schemes.isEmpty()) "未签名" else schemes.joinToString("+")
    }

    private fun findIcon(zip: ZipFile, iconRef: String?): String? {
        // Try common icon paths
        val iconCandidates = listOf(
            "res/mipmap-xxhdpi-v4/ic_launcher.png",
            "res/mipmap-xhdpi-v4/ic_launcher.png",
            "res/mipmap-hdpi-v4/ic_launcher.png",
            "res/drawable-xxhdpi-v4/ic_launcher.png",
            "res/drawable-xhdpi-v4/ic_launcher.png",
            "res/drawable-hdpi-v4/ic_launcher.png",
            "res/drawable/ic_launcher.png"
        )

        for (candidate in iconCandidates) {
            if (zip.getEntry(candidate) != null) return candidate
        }

        // Find any png in res that looks like an icon
        val pngEntries = zip.entries().toList().filter {
            it.name.startsWith("res/") && it.name.endsWith(".png") &&
                    (it.name.contains("ic_launcher") || it.name.contains("icon") || it.name.contains("app_icon"))
        }
        return pngEntries.firstOrNull()?.name
    }

    fun extractIcon(apkFile: File, iconPath: String?): ByteArray? {
        if (iconPath == null) return null
        return try {
            ZipFile(apkFile).use { zip ->
                zip.getEntry(iconPath)?.let { entry ->
                    zip.getInputStream(entry).use { it.readBytes() }
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
