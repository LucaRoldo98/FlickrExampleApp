package com.example.knowitmoc.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedGrayRectangle(height: Dp, width : Dp, modifier : Modifier = Modifier, animate : Boolean = true) {

    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 700
                0.7f at 500
                0.9f at 800
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    val randomGray = Color.DarkGray.copy(alpha = (0.5 + Math.random() * 0.5).toFloat())

    Box(modifier = modifier
        .height(height)
        .width(width)
        .clip(RoundedCornerShape(4.dp))
        .background(if (animate) randomGray.copy(alpha = alpha) else randomGray)
    ) {

    }
}