package com.chumian.systemsign.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun copyUriToFile(context: Context, uri: Uri, destFile: File): File {
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return destFile
    }

    fun getOutputDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), "signed_apks")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getTempDir(context: Context): File {
        val dir = File(context.cacheDir, "temp")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getKeystoreDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), "keystores")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun generateOutputName(originalName: String, keyType: String): String {
        val baseName = originalName.removeSuffix(".apk")
        return "${baseName}_signed_${keyType}.apk"
    }
}
