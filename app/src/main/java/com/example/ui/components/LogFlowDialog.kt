package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.theme.CoralPeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogFlowDialog(
    initialDateMillis: Long,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (
        flowLevel: String,
        painLevel: String,
        symptoms: List<String>,
        padsUsed: Int,
        dischargeType: String,
        notes: String
    ) -> Unit
) {
    var selectedFlow by remember { mutableStateOf("MEDIUM") }
    var selectedPain by remember { mutableStateOf("NONE") }
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var padsCount by remember { mutableIntStateOf(1) }
    var selectedDischarge by remember { mutableStateOf("NONE") }
    var notesText by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", if (language == AppLanguage.BANGLA) Locale("bn") else Locale.ENGLISH)
    val formattedDate = dateFormat.format(Date(initialDateMillis))

    val flowOptions = listOf(
        "NONE" to HerCareStrings.flowNone(language),
        "LIGHT" to HerCareStrings.flowLight(language),
        "MEDIUM" to HerCareStrings.flowMedium(language),
        "HEAVY" to HerCareStrings.flowHeavy(language),
        "VERY_HEAVY" to HerCareStrings.flowVeryHeavy(language)
    )

    val painOptions = listOf(
        "NONE" to HerCareStrings.painNone(language),
        "MILD" to HerCareStrings.painMild(language),
        "MODERATE" to HerCareStrings.painModerate(language),
        "SEVERE" to HerCareStrings.painSevere(language)
    )

    val symptomOptions = listOf(
        "CRAMPS" to HerCareStrings.symptomCramps(language),
        "HEADACHE" to HerCareStrings.symptomHeadache(language),
        "BLOATING" to HerCareStrings.symptomBloating(language),
        "BREAST_TENDERNESS" to HerCareStrings.symptomBreastTenderness(language),
        "FATIGUE" to HerCareStrings.symptomFatigue(language),
        "MOOD_SWINGS" to HerCareStrings.symptomMoodSwings(language),
        "BACKACHE" to HerCareStrings.symptomBackache(language),
        "ACNE" to HerCareStrings.symptomAcne(language),
        "NAUSEA" to HerCareStrings.symptomNausea(language)
    )

    val dischargeOptions = listOf(
        "NONE" to if (language == AppLanguage.BANGLA) "কোনটিই নয়" else "None",
        "CLEAR_WATERY" to if (language == AppLanguage.BANGLA) "স্বচ্ছ ও তরল" else "Clear & Watery",
        "WHITE_STICKY" to if (language == AppLanguage.BANGLA) "সাদা ও আঠালো" else "White & Sticky",
        "EGG_WHITE" to if (language == AppLanguage.BANGLA) "ডিমের শ্বেতাংশের মতো পিচ্ছিল" else "Egg-white / Stretchy",
        "YELLOW_GREEN" to if (language == AppLanguage.BANGLA) "হলুদ/সবুজ (সতর্কতা)" else "Yellow/Green (Alert)",
        "GRAY_FROTHY" to if (language == AppLanguage.BANGLA) "ধূসর/ফেনা জাতীয় (সতর্কতা)" else "Gray/Frothy (Alert)",
        "BROWN_SPOTTING" to if (language == AppLanguage.BANGLA) "বাদামি ফোঁটা রক্তপাত" else "Brown Spotting"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (language == AppLanguage.BANGLA) "দৈনিক লগ যোগ করুন" else "Log Daily Health",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Flow Level Section
                Text(
                    text = if (language == AppLanguage.BANGLA) "রক্তপ্রবাহের মাত্রা (Flow)" else "Day-by-Day Flow",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    flowOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedFlow == key,
                            onClick = { selectedFlow = key },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (key == "VERY_HEAVY") CoralPeriod else MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = if (key == "VERY_HEAVY") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pain Level Section
                Text(
                    text = if (language == AppLanguage.BANGLA) "ব্যথা বা ক্র্যাম্পের তীব্রতা" else "Pain / Cramp Level",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    painOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedPain == key,
                            onClick = { selectedPain = key },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pads Used Counter
                Text(
                    text = if (language == AppLanguage.BANGLA) "আজ ব্যবহৃত প্যাডের সংখ্যা" else "Pads Used Today",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (padsCount > 0) padsCount-- },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease pads")
                    }
                    Text(
                        text = "$padsCount",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = { padsCount++ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase pads")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Discharge Type Section
                Text(
                    text = if (language == AppLanguage.BANGLA) "যোনিস্রাবের ধরন (Vaginal Discharge)" else "Vaginal Discharge Observed",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dischargeOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedDischarge == key,
                            onClick = { selectedDischarge = key },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Symptoms Chips
                Text(
                    text = if (language == AppLanguage.BANGLA) "উপসর্গ বা পিএমএস লক্ষণ" else "Symptoms & PMS",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    symptomOptions.forEach { (key, label) ->
                        val isChecked = selectedSymptoms.contains(key)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                selectedSymptoms = if (isChecked) {
                                    selectedSymptoms - key
                                } else {
                                    selectedSymptoms + key
                                }
                            },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text(if (language == AppLanguage.BANGLA) "ব্যক্তিগত নোট (ঐচ্ছিক)" else "Personal Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        selectedFlow,
                        selectedPain,
                        selectedSymptoms.toList(),
                        padsCount,
                        selectedDischarge,
                        notesText
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("save_log_button")
            ) {
                Text(if (language == AppLanguage.BANGLA) "সংরক্ষণ করুন" else "Save Log")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}
