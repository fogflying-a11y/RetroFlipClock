package com.retroflip.clock.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retroflip.clock.R
import kotlin.math.sin

// ── ABS Plastic Material Palette ──
private val pageBackgroundColor = Color(0xFF050505)
private val cardBaseColor = Color(0xFF111111)
private val cardBorderColor = Color(0xFF242424)
private val topHighlightColor = Color(0x0AFFFFFF)  // rgba(255,255,255,0.04)
private val bottomShadowColor = Color(0x99000000)   // rgba(0,0,0,0.6)
private val dividerLineColor = Color(0xFF0A0A0A)
private val dividerHighlightColor = Color(0xFF1A1A1A)
private val digitColor = Color(0xFFE8E8E8)
private val hingeLightColor = Color(0xFF3A3A3A)
private val hingeDarkColor = Color(0xFF1A1A1A)
private val colonBrassColor = Color(0xFFD6B35A)

private val flipFont = FontFamily(
    Font(R.font.tickingtimebombbb, FontWeight.Black)
)

// ─────────────────────────────────────────────────────────────
// FlipCard — fully responsive: size comes from parent constraints
// No fixed width/height. fontSize is derived from BOTH available
// width and height inside BoxWithConstraints.
// ─────────────────────────────────────────────────────────────
@Composable
fun FlipCard(
    value: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    onFlip: (() -> Unit)? = null
) {
    var previousValue by remember { mutableStateOf(value) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(value) {
        if (previousValue != value) {
            onFlip?.invoke()
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            previousValue = value
        }
    }

    val progress = animProgress.value

    BoxWithConstraints(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(6.dp),
                ambientColor = Color.Black.copy(alpha = 0.6f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
    ) {
        // ── All dimensions derived from parent constraints ──
        val cardHeight = maxHeight
        val halfHeight = cardHeight / 2f

        // ── Solari Udine proportions ──
        // TickingTimebombBB actual glyph width ≈ 0.63 × fontSize per digit
        // 2 digits ≈ 1.26 × fontSize → fontSize = cardWidth × 0.96 / 1.26
        val fontSizeFromWidth = (maxWidth * 0.96f) / 1.26f
        val fontSizeFromHeight = cardHeight * 0.92f
        val fontSizeDp = minOf(fontSizeFromWidth, fontSizeFromHeight)
            .coerceIn(24.dp, 800.dp)
        val fontSize = fontSizeDp.value.sp

        val shadowAlpha = if (progress in 0.02f..0.98f) {
            sin(progress * Math.PI.toFloat()) * 0.45f
        } else 0f

        val topFlapAngle = progress * 100f
        val bottomFlapAngle = (1f - progress) * 100f

        // ── Card base: ABS plastic panel ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp))
                .background(cardBaseColor)
                .drawBehind {
                    // Top highlight strip
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(topHighlightColor, Color.Transparent),
                            startY = 0f,
                            endY = size.height * 0.15f
                        )
                    )
                    // Bottom shadow
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, bottomShadowColor),
                            startY = size.height * 0.7f,
                            endY = size.height
                        )
                    )
                    // Border
                    drawRect(
                        color = cardBorderColor,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                    )
                }
        )

        // ── Static NEW top half ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfHeight)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .clipToBounds()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x1A000000)),
                            startY = size.height * 0.55f,
                            endY = size.height
                        )
                    )
                },
            contentAlignment = Alignment.TopCenter
        ) {
            FlipDigit(
                text = value,
                fontSize = fontSize,
                cardHeight = cardHeight,
                fontWeight = fontWeight,
                modifier = Modifier.wrapContentHeight(align = Alignment.Top, unbounded = true)
            )
        }

        // ── Static OLD bottom half ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfHeight)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .clipToBounds()
                .drawBehind {
                    drawLine(
                        color = dividerHighlightColor,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.2.dp.toPx()
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0x14000000), Color.Transparent),
                            startY = 0f,
                            endY = size.height * 0.3f
                        )
                    )
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            FlipDigit(
                text = previousValue,
                fontSize = fontSize,
                cardHeight = cardHeight,
                fontWeight = fontWeight,
                modifier = Modifier.wrapContentHeight(align = Alignment.Bottom, unbounded = true)
            )
        }

        // ── Folding OLD top flap ──
        if (progress in 0f..0.99f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(halfHeight)
                    .align(Alignment.TopCenter)
                    .clipToBounds()
                    .graphicsLayer {
                        this.rotationX = topFlapAngle
                        this.transformOrigin = TransformOrigin(0.5f, 1f)
                        this.cameraDistance = 12f * density
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                FlipDigit(
                    text = previousValue,
                    fontSize = fontSize,
                    cardHeight = cardHeight,
                    modifier = Modifier.wrapContentHeight(align = Alignment.Top, unbounded = true)
                )
                val backAlpha = (progress * 0.35f).coerceAtMost(0.35f)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = backAlpha))
                )
            }
        }

        // ── Unfolding NEW bottom flap ──
        if (progress in 0.01f..1f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(halfHeight)
                    .align(Alignment.BottomCenter)
                    .clipToBounds()
                    .graphicsLayer {
                        this.rotationX = bottomFlapAngle
                        this.transformOrigin = TransformOrigin(0.5f, 0f)
                        this.cameraDistance = 12f * density
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                FlipDigit(
                    text = value,
                    fontSize = fontSize,
                    cardHeight = cardHeight,
                    modifier = Modifier.wrapContentHeight(align = Alignment.Bottom, unbounded = true)
                )
                val backAlpha = ((1f - progress) * 0.35f).coerceAtMost(0.35f)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = backAlpha))
                )
            }
        }

        // ── Shadow overlay during flip ──
        if (shadowAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight * 0.25f)
                    .align(Alignment.Center)
                    .drawBehind {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = shadowAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                    }
            )
        }

        // ── Center divider (mechanical axis) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.Center)
                .background(dividerLineColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .align(Alignment.Center)
                .offset(y = (-1).dp)
                .background(dividerHighlightColor)
        )

        // ── Hinge pins (left & right) ──
        HingePin(modifier = Modifier.align(Alignment.CenterStart).offset(x = (-3).dp))
        HingePin(modifier = Modifier.align(Alignment.CenterEnd).offset(x = 3.dp))
    }
}

@Composable
private fun HingePin(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(hingeLightColor, hingeDarkColor),
                    center = Offset(0.3f, 0.3f)
                )
            )
    )
}

@Composable
fun ColonSeparator(
    color: Color = colonBrassColor,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Brass indicator dot with glow
        Box(contentAlignment = Alignment.Center) {
            // Glow layer (blur simulation)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
            )
            // Mid glow
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.3f))
            )
            // Core dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.8f))
            )
        }
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.8f))
            )
        }
    }
}

@Composable
private fun FlipDigit(
    text: String,
    fontSize: TextUnit,
    cardHeight: Dp,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(cardHeight),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text,
            fontSize = fontSize,
            fontFamily = flipFont,
            fontWeight = FontWeight.Black,
            color = digitColor,
            textAlign = TextAlign.Center,
            lineHeight = fontSize,
            maxLines = 1,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}
