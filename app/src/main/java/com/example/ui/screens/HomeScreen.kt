package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.MedicineEntry
import com.example.domain.CycleCalculator
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.components.CycleVisualizer
import com.example.ui.components.HerCareHeader
import com.example.ui.components.IrregularWarningCard
import com.example.ui.components.LogFlowDialog
import com.example.ui.theme.CoralPeriod
import com.example.ui.theme.WarningRed
import com.example.ui.theme.WarningRedContainer
import com.example.ui.viewmodel.HerCareUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.mutableIntStateOf

@Composable
fun HomeScreen(
    uiState: HerCareUiState,
    onLanguageChange: (AppLanguage) -> Unit,
    onPeriodStart: () -> Unit,
    onPeriodEnd: (Long) -> Unit,
    onSaveLog: (
        dateMillis: Long,
        flowLevel: String,
        painLevel: String,
        symptoms: List<String>,
        padsUsed: Int,
        dischargeType: String,
        notes: String
    ) -> Unit,
    onConsumePad: () -> Unit,
    onToggleMedicine: (MedicineEntry) -> Unit,
    onRestockPads: (Int) -> Unit = {},
    onToggleMedicineDose: (MedicineEntry, Int) -> Unit = { med, _ -> onToggleMedicine(med) },
    modifier: Modifier = Modifier
) {
    val language = uiState.language
    val summary = uiState.cycleSummary

    var showLogDialog by remember { mutableStateOf(false) }
    var showRestockDialog by remember { mutableStateOf(false) }

    val todayMillis = CycleCalculator.getStartOfDayMillis(System.currentTimeMillis())
    val todayLog = uiState.dailyLogs.find { it.dateMillis == todayMillis }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HerCareHeader(
                userName = uiState.userProfile.name,
                currentLanguage = language,
                onLanguageToggle = onLanguageChange
            )
        }

        // Cycle Visualizer Card
        if (summary != null) {
            item {
                CycleVisualizer(
                    summary = summary,
                    language = language
                )
            }
        }

        // Active Period Control & Log Actions
        item {
            val latestPeriod = uiState.periods.firstOrNull()
            val isPeriodOngoing = latestPeriod != null && latestPeriod.endDateMillis == null

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isPeriodOngoing) {
                    Button(
                        onClick = { onPeriodEnd(latestPeriod!!.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("period_end_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "End Period", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = HerCareStrings.logPeriodEnd(language),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    Button(
                        onClick = onPeriodStart,
                        colors = ButtonDefaults.buttonColors(containerColor = CoralPeriod),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("period_start_button")
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = "Start Period", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = HerCareStrings.logPeriodStart(language),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showLogDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("log_today_button")
                ) {
                    Icon(Icons.Default.EditCalendar, contentDescription = "Log", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "আজকের লগ" else "Log Today",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // 🔴 IRREGULAR CYCLE WARNING CARD (Mandatory Feature 2)
        if (summary?.activeWarning != null) {
            item {
                IrregularWarningCard(
                    warning = summary.activeWarning,
                    language = language,
                    modifier = Modifier.testTag("irregular_warning_card")
                )
            }
        }

        // 🧻 PAD STOCK LOW ALERT BANNER
        if (summary?.shouldWarnPadStock == true) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarningRedContainer),
                    border = BorderStroke(1.dp, WarningRed)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🧻", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "প্যাড কেনার তাগিদ" else "Low Pad Stock Warning",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = WarningRed
                            )
                            Text(
                                text = HerCareStrings.padLowStockNotification(
                                    lang = language,
                                    days = summary.daysUntilNextPeriod,
                                    pads = uiState.padStock.totalAvailable
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Pad Stock Quick Widget
        item {
            val stock = uiState.padStock.totalAvailable
            val isOutOfStock = stock <= 0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isOutOfStock) WarningRedContainer else MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧻", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = HerCareStrings.padStockTitle(language),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isOutOfStock) {
                                Text(
                                    text = HerCareStrings.outOfStock(language),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = WarningRed
                                )
                            } else {
                                Text(
                                    text = HerCareStrings.remainingPads(language, stock),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (stock <= 3) WarningRed else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (isOutOfStock) {
                        Button(
                            onClick = { showRestockDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("home_restock_button")
                        ) {
                            Text(
                                text = HerCareStrings.restockButton(language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    } else {
                        Button(
                            onClick = onConsumePad,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("consume_pad_quick_button")
                        ) {
                            Text(
                                text = HerCareStrings.logPadUsed(language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Today's Medicines Checklist Widget
        if (uiState.medicines.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalPharmacy,
                                contentDescription = "Medicines",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = HerCareStrings.medicineTitle(language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        uiState.medicines.filter { it.isActive }.take(4).forEach { med ->
                            val times = med.getScheduledTimesList()
                            val isFullyTaken = med.isFullyTakenToday(todayMillis)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${med.name} (${med.dose})",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    val timesSummary = times.joinToString(", ")
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "সময়: $timesSummary" else "Times: $timesSummary",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Checkbox(
                                    checked = isFullyTaken,
                                    onCheckedChange = { onToggleMedicine(med) },
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Recorded Log Summary (If already logged today)
        if (todayLog != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Logged",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "আজকের স্বাস্থ্য তথ্য নথিভুক্ত হয়েছে" else "Today's Log Recorded",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA) {
                                "রক্তপ্রবাহ: ${todayLog.flowLevel} • ব্যথা: ${todayLog.painLevel} • ব্যবহৃত প্যাড: ${todayLog.padsUsed}"
                            } else {
                                "Flow: ${todayLog.flowLevel} • Pain: ${todayLog.painLevel} • Pads Used: ${todayLog.padsUsed}"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (todayLog.symptomsCsv.isNotBlank()) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "উপসর্গ: ${todayLog.symptomsCsv}" else "Symptoms: ${todayLog.symptomsCsv}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showLogDialog) {
        LogFlowDialog(
            initialDateMillis = System.currentTimeMillis(),
            language = language,
            onDismiss = { showLogDialog = false },
            onSave = { flow, pain, symptoms, pads, discharge, notes ->
                onSaveLog(
                    System.currentTimeMillis(),
                    flow,
                    pain,
                    symptoms,
                    pads,
                    discharge,
                    notes
                )
                showLogDialog = false
            }
        )
    }

    if (showRestockDialog) {
        var addCountText by remember { mutableStateOf("10") }
        AlertDialog(
            onDismissRequest = { showRestockDialog = false },
            title = {
                Text(
                    text = HerCareStrings.restockPads(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "বর্তমান মজুত: ${uiState.padStock.totalAvailable} টি"
                        else
                            "Current Stock: ${uiState.padStock.totalAvailable} pads",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = HerCareStrings.restockDialogPrompt(language),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 20).forEach { qty ->
                            OutlinedButton(
                                onClick = { addCountText = qty.toString() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                            ) {
                                Text("+$qty")
                            }
                        }
                    }

                    OutlinedTextField(
                        value = addCountText,
                        onValueChange = { newVal ->
                            if (newVal.all { it.isDigit() } && newVal.length <= 3) {
                                addCountText = newVal
                            }
                        },
                        label = {
                            Text(if (language == AppLanguage.BANGLA) "যোগ করার সংখ্যা" else "Quantity to Add")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val added = addCountText.toIntOrNull() ?: 0
                        if (added > 0) {
                            onRestockPads(added)
                        }
                        showRestockDialog = false
                    },
                    modifier = Modifier.testTag("confirm_restock_button")
                ) {
                    Text(if (language == AppLanguage.BANGLA) "মজুত আপডেট করুন" else "Update Stock")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRestockDialog = false }) {
                    Text(HerCareStrings.cancel(language))
                }
            }
        )
    }
}
