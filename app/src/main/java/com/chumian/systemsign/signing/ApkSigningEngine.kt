package com.chumian.systemsign.signing

import android.content.Context
import android.util.Log
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import com.chumian.systemsign.data.SigningConfig
import com.chumian.systemsign.data.SigningResult
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Collections

class ApkSigningEngine(private val context: Context) {

    init {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    fun signApk(
        inputApk: File,
        outputApk: File,
        config: SigningConfig,
        onLog: (String) -> Unit = {}
    ): SigningResult {
        val logs = mutableListOf<String>()
        return try {
            logs.add("开始签名: ${inputApk.name}")
            onLog("开始签名: ${inputApk.name}")

            val signerConfigs = when (config.mode) {
                com.chumian.systemsign.data.SigningMode.SYSTEM -> {
                    logs.add("使用系统密钥: ${config.androidVersion} / ${config.keyType}")
                    onLog("使用系统密钥: ${config.androidVersion} / ${config.keyType}")
                    val (privateKey, cert) = loadSystemKey(config.androidVersion, config.keyType)
                    listOf(
                        ApkSigner.SignerConfig.Builder(
                            config.keyType,
                            privateKey,
                            listOf(cert)
                        ).build()
                    )
                }
                com.chumian.systemsign.data.SigningMode.CUSTOM -> {
                    logs.add("使用自定义密钥: ${config.customKeystorePath}")
                    onLog("使用自定义密钥: ${config.customKeystorePath}")
                    val (privateKey, cert) = loadCustomKey(config)
                    listOf(
                        ApkSigner.SignerConfig.Builder(
                            config.keyAlias.ifEmpty { "key" },
                            privateKey,
                            listOf(cert)
                        ).build()
                    )
                }
                else -> throw IllegalArgumentException("不支持的签名模式")
            }

            logs.add("配置签名方案: v1=${config.v1Enabled}, v2=${config.v2Enabled}, v3=${config.v3Enabled}")
            onLog("配置签名方案: v1=${config.v1Enabled}, v2=${config.v2Enabled}, v3=${config.v3Enabled}")

            val signer = ApkSigner.Builder(signerConfigs)
                .setInputApk(inputApk)
                .setOutputApk(outputApk)
                .setV1SigningEnabled(config.v1Enabled)
                .setV2SigningEnabled(config.v2Enabled)
                .setV3SigningEnabled(config.v3Enabled)
                .setOtherSignersSignaturesPreserved(false)
                .build()

            logs.add("执行签名中...")
            onLog("执行签名中...")
            signer.sign()

            logs.add("签名完成: ${outputApk.absolutePath}")
            onLog("签名完成: ${outputApk.absolutePath}")

            // Verify
            logs.add("验证签名...")
            onLog("验证签名...")
            val verifier = ApkVerifier.Builder(outputApk).build()
            val result = verifier.verify()
            if (result.isVerified) {
                logs.add("签名验证通过")
                onLog("签名验证通过")
            } else {
                logs.add("警告: 签名验证可能存在问题")
                onLog("警告: 签名验证可能存在问题")
            }

            SigningResult(
                success = true,
                outputPath = outputApk.absolutePath,
                message = "签名成功",
                logs = logs
            )
        } catch (e: Exception) {
            logs.add("签名失败: ${e.message}")
            onLog("签名失败: ${e.message}")
            Log.e("ApkSigningEngine", "签名失败", e)
            SigningResult(
                success = false,
                message = "签名失败: ${e.message}",
                logs = logs
            )
        }
    }

    private fun loadSystemKey(androidVersion: String, keyType: String): Pair<PrivateKey, X509Certificate> {
        val keyDir = "keys/$androidVersion"
        val pk8Path = "$keyDir/$keyType.pk8"
        val pemPath = "$keyDir/$keyType.x509.pem"

        // Load private key from .pk8
        val pk8Bytes = context.assets.open(pk8Path).use { it.readBytes() }
        val privateKey = parsePk8PrivateKey(pk8Bytes)

        // Load certificate from .x509.pem
        val pemBytes = context.assets.open(pemPath).use { it.readBytes() }
        val cert = parsePemCertificate(pemBytes)

        return Pair(privateKey, cert)
    }

    private fun parsePk8PrivateKey(pk8Bytes: ByteArray): PrivateKey {
        val keyInfo = PrivateKeyInfo.getInstance(pk8Bytes)
        val converter = JcaPEMKeyConverter().setProvider("BC")
        return converter.getPrivateKey(keyInfo)
    }

    private fun parsePemCertificate(pemBytes: ByteArray): X509Certificate {
        val parser = PEMParser(InputStreamReader(ByteArrayInputStream(pemBytes)))
        val obj = parser.readObject()
        parser.close()
        val certFactory = CertificateFactory.getInstance("X.509", "BC")
        return if (obj is org.bouncycastle.asn1.x509.Certificate) {
            certFactory.generateCertificate(ByteArrayInputStream(obj.encoded)) as X509Certificate
        } else {
            certFactory.generateCertificate(ByteArrayInputStream(pemBytes)) as X509Certificate
        }
    }

    private fun loadCustomKey(config: SigningConfig): Pair<PrivateKey, X509Certificate> {
        val keystoreFile = File(config.customKeystorePath!!)
        val keystoreType = when {
            config.customKeystorePath.endsWith(".p12") || config.customKeystorePath.endsWith(".pfx") -> "PKCS12"
            else -> "PKCS12"
        }

        val keyStore = KeyStore.getInstance(keystoreType)
        FileInputStream(keystoreFile).use { fis ->
            keyStore.load(fis, config.storePassword.toCharArray())
        }

        val alias = if (keyStore.containsAlias(config.keyAlias)) {
            config.keyAlias
        } else {
            val aliases = Collections.list(keyStore.aliases())
            if (aliases.isEmpty()) throw Exception("Keystore 中没有找到密钥别名")
            aliases[0]
        }

        val privateKey = keyStore.getKey(alias, config.keyPassword.toCharArray()) as PrivateKey
        val cert = keyStore.getCertificate(alias) as X509Certificate

        return Pair(privateKey, cert)
    }

    fun generateKeystore(
        outputFile: File,
        alias: String,
        storePassword: String,
        keyPassword: String,
        validityYears: Int,
        dn: Map<String, String>
    ): Boolean {
        return try {
            val keyStore = KeyStore.getInstance("PKCS12")
            keyStore.load(null, storePassword.toCharArray())

            val keyPairGen = java.security.KeyPairGenerator.getInstance("RSA", "BC")
            keyPairGen.initialize(2048)
            val keyPair = keyPairGen.generateKeyPair()

            val dnStr = dn.entries.joinToString(", ") { "${it.key}=${it.value}" }
            val cert = generateCertificate(keyPair, alias, dnStr, validityYears * 365)

            keyStore.setKeyEntry(
                alias,
                keyPair.private,
                keyPassword.toCharArray(),
                arrayOf(cert)
            )

            outputFile.outputStream().use { fos ->
                keyStore.store(fos, storePassword.toCharArray())
            }
            true
        } catch (e: Exception) {
            Log.e("ApkSigningEngine", "生成Keystore失败", e)
            false
        }
    }

    private fun generateCertificate(
        keyPair: java.security.KeyPair,
        alias: String,
        dn: String,
        validityDays: Int
    ): X509Certificate {
        val startDate = java.util.Date()
        val endDate = java.util.Date(startDate.time + validityDays * 86400000L)
        val serialNumber = java.math.BigInteger.valueOf(System.currentTimeMillis())

        val certBuilder = org.bouncycastle.cert.X509v3CertificateBuilder(
            org.bouncycastle.asn1.x500.X500Name(dn),
            serialNumber,
            startDate,
            endDate,
            org.bouncycastle.asn1.x500.X500Name(dn),
            org.bouncycastle.cert.jcajce.JcaSubjectPublicKeyInfo.from(keyPair.public)
        )

        val signer = org.bouncycastle.operator.jcajce.JcaContentSignerBuilder("SHA256WithRSA")
            .setProvider("BC")
            .build(keyPair.private)

        val certHolder = certBuilder.build(signer)
        return org.bouncycastle.cert.jcajce.JcaX509CertificateConverter()
            .setProvider("BC")
            .getCertificate(certHolder)
    }
}
