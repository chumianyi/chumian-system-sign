package com.chumian.systemsign.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun Modifier.neumorphic(
    backgroundColor: Color,
    shadowDark: Color,
    shadowLight: Color,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
    pressed: Boolean = false
): Modifier = this.drawBehind {
    val radiusPx = cornerRadius.toPx()
    val elevPx = elevation.toPx()
    val roundRect = RoundRect(
        0f, 0f, size.width, size.height,
        CornerRadius(radiusPx, radiusPx)
    )
    val shape = Outline.Rounded(roundRect)
    if (!pressed) {
        drawOutline(
            outline = shape,
            brush = Brush.linearGradient(
                colors = listOf(shadowDark.copy(alpha = 0.5f), shadowDark.copy(alpha = 0f)),
                start = Offset(elevPx, elevPx),
                end = Offset.Zero
            ),
            style = Stroke(width = elevPx * 2)
        )
        drawOutline(
            outline = shape,
            brush = Brush.linearGradient(
                colors = listOf(shadowLight.copy(alpha = 0.7f), shadowLight.copy(alpha = 0f)),
                start = Offset(-elevPx, -elevPx),
                end = Offset.Zero
            ),
            style = Stroke(width = elevPx * 2)
        )
    }
}.background(backgroundColor, RoundedCornerShape(cornerRadius))

@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .padding(elevation)
            .neumorphic(
                NeuColors.background(dark),
                NeuColors.shadowDark(dark),
                NeuColors.shadowLight(dark),
                cornerRadius,
                elevation
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun NeuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    text: String,
    enabled: Boolean = true,
    color: Color? = null
) {
    var pressed by remember { mutableStateOf(false) }
    val bg = color ?: if (enabled) NeuColors.accent(dark) else NeuColors.shadowDark(dark).copy(alpha = 0.5f)
    Box(
        modifier = modifier
            .padding(6.dp)
            .neumorphic(
                bg,
                bg.copy(alpha = 0.7f),
                bg.copy(alpha = 0.3f),
                16.dp,
                5.dp,
                pressed
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled
            ) {
                if (enabled) onClick()
            }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NeuOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    text: String
) {
    var pressed by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .padding(6.dp)
            .neumorphic(
                NeuColors.background(dark),
                NeuColors.shadowDark(dark),
                NeuColors.shadowLight(dark),
                16.dp,
                4.dp,
                pressed
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick()
                pressed = false
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = NeuColors.text(dark),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
