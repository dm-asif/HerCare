package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CyclePhase
import com.example.domain.CycleSummary
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.theme.CoralPeriod
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.FertileGreen
import com.example.ui.theme.MintTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CycleVisualizer(
    summary: CycleSummary,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val phaseColor = when (summary.currentPhase) {
        CyclePhase.MENSTRUAL -> CoralPeriod
        CyclePhase.FOLLICULAR -> EmeraldSecondary
        CyclePhase.FERTILE_OVULATION -> FertileGreen
        CyclePhase.LUTEAL -> MintTertiary
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
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Progress Indicator
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val strokeWidth = 16.dp.toPx()

                    // Background Track
                    drawCircle(
                        color = Color(0xFFE8EEEC),
                        radius = (size.minDimension - strokeWidth) / 2f,
                        style = Stroke(width = strokeWidth)
                    )

                    // Active Cycle Sweep
                    drawArc(
                        color = phaseColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Inner Content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = HerCareStrings.cycleDay(language, summary.currentCycleDay),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (summary.daysUntilNextPeriod > 0) {
                            "${summary.daysUntilNextPeriod}"
                        } else if (summary.daysUntilNextPeriod == 0) {
                            "0"
                        } else {
                            "+${-summary.daysUntilNextPeriod}"
                        },
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = phaseColor
                        )
                    )

                    Text(
                        text = if (summary.daysUntilNextPeriod > 0) {
                            if (language == AppLanguage.BANGLA) "দিন বাকি" else "Days Left"
                        } else if (summary.daysUntilNextPeriod == 0) {
                            if (language == AppLanguage.BANGLA) "আজ প্রত্যাশিত" else "Due Today"
                        } else {
                            if (language == AppLanguage.BANGLA) "দিন বিলম্বিত" else "Days Late"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Phase Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(phaseColor.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = phaseName,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = phaseColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = phaseDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expected Date & Cycle Length pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পরবর্তী পিরিয়ড" else "Next Period",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = nextPeriodFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "গড় সাইকেল" else "Avg Cycle",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (language == AppLanguage.BANGLA) "${summary.averageCycleLength} দিন" else "${summary.averageCycleLength} days",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
