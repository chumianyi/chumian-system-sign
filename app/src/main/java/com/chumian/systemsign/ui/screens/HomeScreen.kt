package com.chumian.systemsign.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chumian.systemsign.data.SigningMode
import com.chumian.systemsign.ui.NeuButton
import com.chumian.systemsign.ui.NeuCard
import com.chumian.systemsign.ui.NeuColors

@Composable
fun HomeScreen(
    dark: Boolean,
    onModeSelected: (SigningMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "初眠系统签",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = NeuColors.text(dark)
        )
        Text(
            text = "Chumian System Sign v1.0.0",
            fontSize = 13.sp,
            color = NeuColors.textSecondary(dark)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "内置 AOSP 各版本系统签名密钥",
            fontSize = 13.sp,
            color = NeuColors.textSecondary(dark)
        )
        Spacer(modifier = Modifier.height(32.dp))

        ModeCard(
            dark = dark,
            icon = Icons.Default.Security,
            title = "系统应用签名",
            desc = "使用 AOSP 系统密钥签名\n支持 Android 7.0 ~ 12.1",
            color = Color(0xFF5B8DEF),
            onClick = { onModeSelected(SigningMode.SYSTEM) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        ModeCard(
            dark = dark,
            icon = Icons.Default.VpnKey,
            title = "自定义签名",
            desc = "导入 .jks / .keystore / .p12\n使用自己的证书签名",
            color = Color(0xFF7B68EE),
            onClick = { onModeSelected(SigningMode.CUSTOM) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        ModeCard(
            dark = dark,
            icon = Icons.Default.AddCircle,
            title = "生成签名",
            desc = "应用内生成新的 Keystore\n设置有效期与组织信息",
            color = Color(0xFF4CAF50),
            onClick = { onModeSelected(SigningMode.GENERATE) }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ModeCard(
    dark: Boolean,
    icon: ImageVector,
    title: String,
    desc: String,
    color: Color,
    onClick: () -> Unit
) {
    NeuCard(
        dark = dark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .neumorphicSmall(color, dark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeuColors.text(dark)
                )
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = NeuColors.textSecondary(dark),
                    lineHeight = 16.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "进入",
                tint = NeuColors.textSecondary(dark)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        NeuButton(
            onClick = onClick,
            dark = dark,
            text = "开始使用",
            color = color,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun Modifier.neumorphicSmall(color: Color, dark: Boolean): Modifier =
    this.background(color, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
