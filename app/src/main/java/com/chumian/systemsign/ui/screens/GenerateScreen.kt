package com.chumian.systemsign.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chumian.systemsign.ui.NeuButton
import com.chumian.systemsign.ui.NeuCard
import com.chumian.systemsign.ui.NeuColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    dark: Boolean,
    onGenerate: (String, String, String, String, Int, String, String, String, String, String, (Boolean, String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("my-key") }
    var storePassword by remember { mutableStateOf("") }
    var keyPassword by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("key0") }
    var validity by remember { mutableStateOf("25") }
    var orgUnit by remember { mutableStateOf("") }
    var org by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("CN") }
    var resultMsg by remember { mutableStateOf("") }

    val textColor = NeuColors.text(dark)
    val secondaryColor = NeuColors.textSecondary(dark)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "生成签名证书",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        NeuCard(dark = dark) {
            Text("基本信息", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(12.dp))

            Text("密钥名称", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Key 别名", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = alias, onValueChange = { alias = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Store 密码", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = storePassword, onValueChange = { storePassword = it },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Key 密码", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = keyPassword, onValueChange = { keyPassword = it },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("有效期 (年)", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = validity, onValueChange = { validity = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        NeuCard(dark = dark) {
            Text("组织信息 (DN)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(12.dp))

            Text("组织单位 (OU)", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = orgUnit, onValueChange = { orgUnit = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("组织 (O)", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = org, onValueChange = { org = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("城市 (L)", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = city, onValueChange = { city = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("省份 (ST)", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = province, onValueChange = { province = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("国家代码 (C)", fontSize = 12.sp, color = secondaryColor)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = countryCode, onValueChange = { countryCode = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        NeuButton(
            onClick = {
                onGenerate(name, storePassword, keyPassword, alias, validity.toIntOrNull() ?: 25,
                    orgUnit, org, city, province, countryCode
                ) { success, path ->
                    resultMsg = if (success) "生成成功: $path" else "生成失败"
                }
            },
            dark = dark,
            text = "生成 Keystore",
            modifier = Modifier.fillMaxWidth()
        )

        if (resultMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(resultMsg, fontSize = 12.sp, color = secondaryColor)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
