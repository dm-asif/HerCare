package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.components.IrregularWarningCard
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CardSoftShadowColor
import com.example.ui.theme.DeepPink
import com.example.ui.theme.LightPink
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftRose
import com.example.ui.theme.CoralPeriod
import com.example.ui.theme.FertileGreen
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.HerCareUiState

@Composable
fun HealthReportScreen(
    uiState: HerCareUiState,
    modifier: Modifier = Modifier
) {
    val language = uiState.language
    val summary = uiState.cycleSummary
    val profile = uiState.userProfile
    val context = LocalContext.current

    val age = ((System.currentTimeMillis() - profile.birthDateMillis) / (365.25 * 24 * 60 * 60 * 1000L)).toInt()

    // Aggregate symptoms
    val symptomFrequencies = mutableMapOf<String, Int>()
    uiState.dailyLogs.forEach { log ->
        if (log.symptomsCsv.isNotBlank()) {
            log.symptomsCsv.split(",").map { it.trim() }.forEach { sym ->
                if (sym.isNotEmpty()) {
                    symptomFrequencies[sym] = (symptomFrequencies[sym] ?: 0) + 1
                }
            }
        }
    }

    // Aggregate flow
    val flowFrequencies = mutableMapOf<String, Int>()
    uiState.dailyLogs.forEach { log ->
        if (log.flowLevel.isNotBlank()) {
            flowFrequencies[log.flowLevel] = (flowFrequencies[log.flowLevel] ?: 0) + 1
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = HerCareStrings.healthReportTitle(language),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = DeepPink
                    )
                    Text(
                        text = if (language == AppLanguage.BANGLA) "নথিভুক্ত উপাত্ত ও চক্রের বিশ্লেষণ" else "Aggregated insights from your personal cycle logs",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }

                // Share / Export Button for Doctor Consultation
                Button(
                    onClick = {
                        val shareText = buildString {
                            appendLine("--- HerCare Personal Health Summary ---")
                            appendLine("User: ${profile.name} (Age: $age)")
                            appendLine("Average Cycle Length: ${summary?.averageCycleLength ?: 28} days (Normal: 21-35)")
                            appendLine("Average Period Duration: ${summary?.averagePeriodDuration ?: 5} days (Normal: 2-7)")
                            appendLine("Total Periods Recorded: ${uiState.periods.size}")
                            appendLine("Total Daily Logs: ${uiState.dailyLogs.size}")
                            if (summary?.activeWarning != null) {
                                appendLine("Active Medical Advisory: ${summary.activeWarning.titleEn}")
                                appendLine(summary.activeWarning.doctorAdvisoryEn)
                            }
                            appendLine("Disclaimer: Observational cycle data only. Not a medical diagnosis.")
                        }

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share with Doctor"))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPink),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("share_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "শেয়ার" else "Share",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }

        // Active Red Warning if any
        if (summary?.activeWarning != null) {
            item {
                IrregularWarningCard(
                    warning = summary.activeWarning,
                    language = language
                )
            }
        }

        // Key Cycle Metrics Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "চক্রের মূল পরিসংখ্যান" else "Primary Cycle Metrics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = DeepPink
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox(
                            title = if (language == AppLanguage.BANGLA) "গড় সাইকেল" else "Avg Cycle",
                            value = "${summary?.averageCycleLength ?: 28} " + if (language == AppLanguage.BANGLA) "দিন" else "days",
                            subtext = if (language == AppLanguage.BANGLA) "স্বাভাবিক: ২১–৩৫ দিন" else "Normal: 21–35 days",
                            color = DeepPink
                        )

                        MetricBox(
                            title = if (language == AppLanguage.BANGLA) "গড় পিরিয়ড" else "Avg Period",
                            value = "${summary?.averagePeriodDuration ?: 5} " + if (language == AppLanguage.BANGLA) "দিন" else "days",
                            subtext = if (language == AppLanguage.BANGLA) "স্বাভাবিক: ২–৭ দিন" else "Normal: 2–7 days",
                            color = PrimaryPink
                        )

                        MetricBox(
                            title = if (language == AppLanguage.BANGLA) "নিয়মিততা" else "Regularity",
                            value = if (summary?.activeWarning == null) {
                                if (language == AppLanguage.BANGLA) "স্বাভাবিক" else "Regular"
                            } else {
                                if (language == AppLanguage.BANGLA) "অনিয়মিত" else "Irregular"
                            },
                            subtext = if (summary?.activeWarning == null) "Consistent" else "Review Needed",
                            color = if (summary?.activeWarning == null) FertileGreen else WarningRed
                        )
                    }
                }
            }
        }

        // Symptoms Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "উপসর্গের পুনরাবৃত্তি (Symptoms Summary)" else "Symptoms & PMS Frequency",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = DeepPink
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (symptomFrequencies.isEmpty()) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "এখনো কোনো উপসর্গ নথিভুক্ত করা হয়নি।" else "No symptoms logged yet. Use 'Log Today' to track patterns.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    } else {
                        symptomFrequencies.entries.sortedByDescending { it.value }.take(5).forEach { (sym, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(DeepPink)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = sym,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = PrimaryText
                                    )
                                }
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "$count বার" else "$count times",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = DeepPink
                                )
                            }
                        }
                    }
                }
            }
        }

        // Doctor Consultation Recommendation Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = LightPink.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, SoftRose.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DeepPink.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocalHospital,
                            contentDescription = "Doctor Advice",
                            tint = DeepPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "চিকিৎসকের সাথে পরামর্শের গাইড" else "When to Consult a Healthcare Professional",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = DeepPink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) {
                                "পিরিয়ড যদি টানা ৩ মাসের বেশি সময় ২১ দিনের কম বা ৩৫ দিনের বেশি হয়, রক্তক্ষরণ ৭ দিনের বেশি স্থায়ী হয়, অথবা তীব্র পেলভিক ব্যথা থাকে—তবে এই রিপোর্টটি একজন অভিজ্ঞ স্ত্রীরোগ বিশেষজ্ঞকে (Gynecologist) দেখান।"
                            } else {
                                "If cycles consistently fall outside 21–35 days for over 3 months, bleeding extends beyond 7 days, or severe pelvic cramps occur, share these recorded trends with a gynecologist."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryText,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Observational Non-Diagnosis Disclaimer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Info",
                    tint = SecondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = HerCareStrings.nonDiagnosisDisclaimer(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricBox(title: String, value: String, subtext: String, color: Color) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtext,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = SecondaryText
        )
    }
}
