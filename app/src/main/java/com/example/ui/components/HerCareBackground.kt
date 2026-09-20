package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import com.example.ui.theme.LightPink
import com.example.ui.theme.VeryLightBackground

/**
 * Global HerCare background providing a calm, soft pastel pink/white wellness atmosphere
 * with subtle artistic waves, soft glow, and delicate background accents.
 */
@Composable
fun HerCareBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Base vertical gradient (Very Light Pink -> White -> Soft Pink wash)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VeryLightBackground,
                        Color.White,
                        Color(0xFFFFF4F8)
                    )
                )
            )

            // 2. Gentle radial ambient glow at top-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF8BBD0).copy(alpha = 0.25f),
                        Color(0xFFFCE4EC).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.9f, height * 0.08f),
                    radius = width * 0.65f
                )
            )

            // 3. Gentle radial ambient glow at bottom-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF48FB1).copy(alpha = 0.15f),
                        Color(0xFFFCE4EC).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.1f, height * 0.85f),
                    radius = width * 0.7f
                )
            )

            // 4. Ultra-subtle abstract top organic wave
            val topWavePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height * 0.09f)
                cubicTo(
                    width * 0.75f, height * 0.13f,
                    width * 0.35f, height * 0.05f,
                    0f, height * 0.10f
                )
                close()
            }
            drawPath(
                path = topWavePath,
                color = Color(0xFFFCE4EC).copy(alpha = 0.35f),
                style = Fill
            )

            // 5. Subtle delicate petal curve on right edge
            val petalPath = Path().apply {
                moveTo(width, height * 0.42f)
                cubicTo(
                    width * 0.94f, height * 0.40f,
                    width * 0.93f, height * 0.46f,
                    width, height * 0.48f
                )
                close()
            }
            drawPath(
                path = petalPath,
                color = Color(0xFFF8BBD0).copy(alpha = 0.25f),
                style = Fill
            )
        }

        // Foreground Screen Content
        content()
    }
}
