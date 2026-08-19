# 初眠系统签 (Chumian System Sign)

Android 系统应用签名器，内置 AOSP 各版本系统签名密钥。

## 功能

- **系统应用签名**：内置 Android 7.0 ~ 12.1 各版本 AOSP 系统密钥（platform/media/shared/testkey）
- **自定义签名**：导入 .jks / .keystore / .p12 证书进行签名
- **生成签名**：应用内生成新的 PKCS12 密钥库
- **APK 信息提取**：真实解析 AndroidManifest.xml，提取包名、版本、签名状态等
- **V1/V2/V3 签名方案**：基于 Google 官方 apksig 库
- **Neumorphism 新拟态 UI**：Material 3 基础上的柔和拟态设计

## 技术栈

- Kotlin + Jetpack Compose (Material 3)
- com.android.tools.build:apksig (官方 APK 签名库)
- BouncyCastle (密钥解析与证书生成)
- ViewModel + StateFlow + Coroutines

## 构建

```bash
./gradlew assembleRelease
```

## 版本信息

- 包名：com.chumian.systemsign
- 最低 SDK：Android 7.0 (API 24)
- 架构：arm64-v8a
