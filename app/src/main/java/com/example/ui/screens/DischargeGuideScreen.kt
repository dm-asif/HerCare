package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.theme.FertileGreen
import com.example.ui.theme.FertileGreenContainer
import com.example.ui.theme.WarningRed
import com.example.ui.theme.WarningRedContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DischargeGuideScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    // Interactive Symptom Checker States
    var selectedColor by remember { mutableStateOf("CLEAR_WHITE") }
    var selectedOdor by remember { mutableStateOf("NORMAL") }
    var selectedTexture by remember { mutableStateOf("SLIPPERY_STICKY") }
    var hasDiscomfort by remember { mutableStateOf(false) }

    val hasConcerningSigns = selectedColor in listOf("YELLOW_GREEN", "GRAY_FROTHY", "BLOODY") ||
            selectedOdor in listOf("FISHY", "FOUL") ||
            selectedTexture in listOf("CURD_LIKE", "FROTHY") ||
            hasDiscomfort

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = HerCareStrings.dischargeGuideTitle(language),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (language == AppLanguage.BANGLA) "চিকিৎসাগত তথ্য ও যোনিস্রাব বিশ্লেষণ" else "Medically sourced guide on healthy vs. concerning discharge",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. Normal & Healthy Discharge Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FertileGreenContainer.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, FertileGreen.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FertileGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Normal",
                                tint = FertileGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = HerCareStrings.normalDischargeHeader(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = FertileGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = HerCareStrings.normalDischargeBody(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1B4D2E),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Normal Varieties Cards
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DischargeFeatureBullet(
                            title = if (language == AppLanguage.BANGLA) "স্বচ্ছ ও ভেজা / পিচ্ছিল" else "Clear, Slippery & Wet",
                            desc = if (language == AppLanguage.BANGLA) "ডিমের শ্বেতাংশের মতো প্রসারিত হয়। সাধারণত ওভুলেশনের সময় ঘটে এবং এটি উর্বরতার সর্বোচ্চ লক্ষণ।" else "Stretchy like raw egg whites. Typically occurs around ovulation; signals peak fertility."
                        )
                        DischargeFeatureBullet(
                            title = if (language == AppLanguage.BANGLA) "সাদা ও ঘন / আঠালো" else "White & Thick / Sticky",
                            desc = if (language == AppLanguage.BANGLA) "সাইকেলের শুরুতে বা শেষে স্বাভাবিক। এতে কোনো চুলকানি বা তীব্র দুর্গন্ধ থাকে না।" else "Common at the start or end of the menstrual cycle. Normal without itching or odor."
                        )
                    }
                }
            }
        }

        // 2. 🔴 CLEAR DOCTOR WARNING FOR CONCERNING DISCHARGE (Mandatory Feature 4)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WarningRedContainer),
                border = BorderStroke(1.5.dp, WarningRed)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WarningRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Doctor Warning",
                                tint = WarningRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = HerCareStrings.concerningDischargeHeader(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = WarningRed
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = HerCareStrings.concerningDischargeWarning(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5C1D1D),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(WarningRed.copy(alpha = 0.1f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocalHospital,
                            contentDescription = "Hospital",
                            tint = WarningRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "চিকিৎসকের পরামর্শ গ্রহণ করুন: সঠিক ল্যাব টেস্ট ও সঠিক প্রেসক্রিপশনের জন্য বিশেষজ্ঞ ডাক্তার দেখান।" else "Consult a Doctor: Only a licensed gynecologist can diagnose and treat infections properly.",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = WarningRed
                        )
                    }
                }
            }
        }

        // 3. Interactive Discharge Self-Assessment
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MedicalServices,
                            contentDescription = "Assessment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = HerCareStrings.dischargeSymptomChecker(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Color Selector
                    Text(
                        text = if (language == AppLanguage.BANGLA) "রঙ (Color):" else "Color:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedColor == "CLEAR_WHITE",
                            onClick = { selectedColor = "CLEAR_WHITE" },
                            label = { Text(if (language == AppLanguage.BANGLA) "স্বচ্ছ বা সাদা" else "Clear / White") }
                        )
                        FilterChip(
                            selected = selectedColor == "YELLOW_GREEN",
                            onClick = { selectedColor = "YELLOW_GREEN" },
                            label = { Text(if (language == AppLanguage.BANGLA) "হলুদ বা সবুজ (অস্বাভাবিক)" else "Yellow / Green (Abnormal)") }
                        )
                        FilterChip(
                            selected = selectedColor == "GRAY_FROTHY",
                            onClick = { selectedColor = "GRAY_FROTHY" },
                            label = { Text(if (language == AppLanguage.BANGLA) "ধূসর / ফেনা জাতীয়" else "Gray / Frothy") }
                        )
                        FilterChip(
                            selected = selectedColor == "BLOODY",
                            onClick = { selectedColor = "BLOODY" },
                            label = { Text(if (language == AppLanguage.BANGLA) "রক্তাক্ত বা বাদামি" else "Bloody / Brown") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Odor Selector
                    Text(
                        text = if (language == AppLanguage.BANGLA) "গন্ধ (Odor):" else "Odor:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedOdor == "NORMAL",
                            onClick = { selectedOdor = "NORMAL" },
                            label = { Text(if (language == AppLanguage.BANGLA) "গন্ধহীন বা হালকা" else "Mild / Odorless") }
                        )
                        FilterChip(
                            selected = selectedOdor == "FISHY",
                            onClick = { selectedOdor = "FISHY" },
                            label = { Text(if (language == AppLanguage.BANGLA) "আঁশটে বা তীব্র গন্ধ" else "Strong Fishy Odor") }
                        )
                        FilterChip(
                            selected = selectedOdor == "FOUL",
                            onClick = { selectedOdor = "FOUL" },
                            label = { Text(if (language == AppLanguage.BANGLA) "পচা বা অপ্রীতিকর গন্ধ" else "Foul / Unpleasant") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Accompanying Discomfort Toggle
                    Text(
                        text = if (language == AppLanguage.BANGLA) "অস্বস্তি বা শারীরিক উপসর্গ (Discomfort):" else "Associated Discomfort:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !hasDiscomfort,
                            onClick = { hasDiscomfort = false },
                            label = { Text(if (language == AppLanguage.BANGLA) "কোনো ব্যথা/চুলকানি নেই" else "No Itching or Pain") }
                        )
                        FilterChip(
                            selected = hasDiscomfort,
                            onClick = { hasDiscomfort = true },
                            label = { Text(if (language == AppLanguage.BANGLA) "চুলকানি/জ্বালাপোড়া/তলপেটে ব্যথা" else "Itching, Soreness or Pelvic Pain") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Assessment Result Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (hasConcerningSigns) WarningRedContainer else FertileGreenContainer.copy(alpha = 0.5f)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = if (hasConcerningSigns) {
                                    if (language == AppLanguage.BANGLA) "⚠️ সতর্কতামূলক লক্ষণ চিহ্নিত হয়েছে" else "⚠️ Concerning Symptoms Identified"
                                } else {
                                    if (language == AppLanguage.BANGLA) "✅ স্বাভাবিক শারীরিক বৈশিষ্ট্যের সাথে সামঞ্জস্যপূর্ণ" else "✅ Consistent with Normal Physiology"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (hasConcerningSigns) WarningRed else FertileGreen
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (hasConcerningSigns) {
                                    if (language == AppLanguage.BANGLA) {
                                        "আপনার নির্বাচিত লক্ষণসমূহ (যেমন অস্বাভাবিক রঙ, গন্ধ বা চুলকানি) সাধারণ সংক্রমণের ইঙ্গিত দিতে পারে। সঠিক স্বাস্থ্য সুরক্ষার জন্য অনুগ্রহ করে একজন অভিজ্ঞ চিকিৎসকের পরামর্শ নিন।"
                                    } else {
                                        "Your selections indicate symptoms (such as abnormal color, strong odor, or pelvic discomfort) that may require medical evaluation. Please consult a doctor."
                                    }
                                } else {
                                    if (language == AppLanguage.BANGLA) {
                                        "স্বচ্ছ/সাদা এবং গন্ধহীন স্রাব হরমোনের স্বাভাবিক পরিবর্তনের ফল। সুষম খাবার গ্রহণ ও স্বাস্থ্যবিধি মেনে চলুন।"
                                    } else {
                                        "Clear/white, non-foul discharge without pain is typical of normal hormonal shifts across your menstrual cycle."
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 4. Strict Medical Disclaimer (Never Diagnose)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Medical Disclaimer",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = HerCareStrings.nonDiagnosisDisclaimer(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DischargeFeatureBullet(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1B4D2E)
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF2C5B3E),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
