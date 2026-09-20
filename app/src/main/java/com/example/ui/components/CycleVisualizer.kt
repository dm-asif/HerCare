package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.CyclePhase
import com.example.domain.CycleSummary
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CardSoftShadowColor
import com.example.ui.theme.DeepPink
import com.example.ui.theme.HeroGradientEnd
import com.example.ui.theme.HeroGradientStart
import com.example.ui.theme.LightPink
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CycleVisualizer(
    summary: CycleSummary,
    language: AppLanguage,
    onPhaseClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val phaseColor = when (summary.currentPhase) {
        CyclePhase.MENSTRUAL -> PrimaryPink
        CyclePhase.FOLLICULAR -> SoftRose
        CyclePhase.FERTILE_OVULATION -> DeepPink
        CyclePhase.LUTEAL -> DeepPink
    }

    val phaseName = when (summary.currentPhase) {
        CyclePhase.MENSTRUAL -> HerCareStrings.phaseMenstrual(language)
        CyclePhase.FOLLICULAR -> HerCareStrings.phaseFollicular(language)
        CyclePhase.FERTILE_OVULATION -> HerCareStrings.phaseFertile(language)
        CyclePhase.LUTEAL -> HerCareStrings.phaseLuteal(language)
    }

    val phaseDesc = when (summary.currentPhase) {
        CyclePhase.MENSTRUAL -> HerCareStrings.phaseDescMenstrual(language)
        CyclePhase.FOLLICULAR -> HerCareStrings.phaseDescFollicular(language)
        CyclePhase.FERTILE_OVULATION -> HerCareStrings.phaseDescFertile(language)
        CyclePhase.LUTEAL -> HerCareStrings.phaseDescLuteal(language)
    }

    val progressFraction = (summary.currentCycleDay.toFloat() / summary.averageCycleLength.toFloat()).coerceIn(0.05f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 1000),
        label = "cycleProgress"
    )

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", if (language == AppLanguage.BANGLA) Locale("bn") else Locale.ENGLISH)
    val nextPeriodFormatted = dateFormat.format(Date(summary.nextPeriodDateMillis))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp), spotColor = CardSoftShadowColor)
            .testTag("cycle_visualizer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            HeroGradientStart,
                            HeroGradientEnd.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Pill: Cycle Day
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, CardBorderColor),
                        shadowElevation = 0.5.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = DeepPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = HerCareStrings.cycleDay(language, summary.currentCycleDay),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = DeepPink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Center Row: Left Ring + Right Info with Gentle Illustration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. Interactive Animated Cycle Ring
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(140.dp)) {
                            val strokeWidth = 14.dp.toPx()
                            val centerOffset = Offset(size.width / 2f, size.height / 2f)
                            val radius = (size.minDimension - strokeWidth) / 2f

                            // Soft pink track ring
                            drawCircle(
                                color = Color(0xFFFFEBF2),
                                radius = radius,
                                center = centerOffset,
                                style = Stroke(width = strokeWidth)
                            )

                            // Active animated pink gradient sweep
                            val sweepAngle = animatedProgress * 360f
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(SoftRose, PrimaryPink, DeepPink),
                                    center = centerOffset
                                ),
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            // Indicator glowing dot at arc endpoint
                            val endAngleRad = Math.toRadians((-90.0 + sweepAngle))
                            val dotX = centerOffset.x + (radius * cos(endAngleRad)).toFloat()
                            val dotY = centerOffset.y + (radius * sin(endAngleRad)).toFloat()

                            // White ring under bead
                            drawCircle(
                                color = Color.White,
                                radius = 7.dp.toPx(),
                                center = Offset(dotX, dotY)
                            )
                            // Glowing pink bead
                            drawCircle(
                                color = DeepPink,
                                radius = 5.dp.toPx(),
                                center = Offset(dotX, dotY)
                            )
                        }

                        // Ring Center Typography
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (summary.daysUntilNextPeriod > 0) {
                                    "${summary.daysUntilNextPeriod}"
                                } else if (summary.daysUntilNextPeriod == 0) {
                                    "0"
                                } else {
                                    "+${-summary.daysUntilNextPeriod}"
                                },
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 36.sp,
                                    letterSpacing = (-1).sp
                                ),
                                color = DeepPink
                            )

                            Text(
                                text = if (summary.daysUntilNextPeriod > 0) {
                                    if (language == AppLanguage.BANGLA) "দিন বাকি" else "Days Left"
                                } else if (summary.daysUntilNextPeriod == 0) {
                                    if (language == AppLanguage.BANGLA) "আজ প্রত্যাশিত" else "Due Today"
                                } else {
                                    if (language == AppLanguage.BANGLA) "দিন বিলম্বিত" else "Days Late"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = SecondaryText
                            )
                        }
                    }

                    // 2. Right Info & Delicate Illustration Overlay
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        // Artistic serene silhouette illustration from drawable
                        Image(
                            painter = painterResource(id = R.drawable.img_cycle_hero),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(110.dp)
                                .align(Alignment.CenterEnd)
                                .clip(RoundedCornerShape(20.dp)),
                            alpha = 0.85f
                        )

                        // Foreground text layout
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "আপনার সাইকেল" else "Your Cycle",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = DeepPink
                            )

                            Text(
                                text = if (language == AppLanguage.BANGLA) "পরবর্তী পিরিয়ড" else "Next Period",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = SecondaryText
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Date Pill Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.95f),
                                border = BorderStroke(1.dp, CardBorderColor),
                                shadowElevation = 0.5.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = PrimaryPink,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = nextPeriodFormatted,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = PrimaryText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Avg Cycle Row with Waterdrop
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = PrimaryPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "গড় সাইকেল:" else "Avg Cycle",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryText
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "${summary.averageCycleLength} দিন" else "${summary.averageCycleLength} days",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Phase Banner Capsule (Clickable for details)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onPhaseClick() }
                        .testTag("cycle_phase_banner"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 0.5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LightPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = DeepPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = phaseName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = DeepPink
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = DeepPink,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = phaseDesc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                ),
                                color = SecondaryText,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
