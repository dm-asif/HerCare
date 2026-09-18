package com.example.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineEntry
import com.example.domain.CycleCalculator
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.theme.WarningRed
import com.example.ui.theme.WarningRedContainer
import com.example.ui.viewmodel.HerCareUiState
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicineAndStockScreen(
    uiState: HerCareUiState,
    onAddMedicine: (name: String, dose: String, hour: Int, minute: Int, frequency: String) -> Unit,
    onToggleMedicine: (MedicineEntry) -> Unit,
    onDeleteMedicine: (Long) -> Unit,
    onConsumePad: () -> Unit,
    onUpdatePadStock: (Int) -> Unit,
    onAddMedicineFull: ((
        name: String,
        dose: String,
        frequencyTimes: Int,
        scheduledTimesCsv: String,
        mealTiming: String,
        startDateMillis: Long,
        endDateMillis: Long?
    ) -> Unit)? = null,
    onToggleMedicineDose: (MedicineEntry, Int) -> Unit = { med, _ -> onToggleMedicine(med) },
    onRestockPads: (Int) -> Unit = { onUpdatePadStock(uiState.padStock.totalAvailable + it) },
    modifier: Modifier = Modifier
) {
    val language = uiState.language
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    var showAddMedicineDialog by remember { mutableStateOf(false) }
    var showAdjustStockDialog by remember { mutableStateOf(false) }

    val todayMillis = CycleCalculator.getStartOfDayMillis(System.currentTimeMillis())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Row: Medicines vs Pads
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💊", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = HerCareStrings.medicineRoutineTab(language),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                modifier = Modifier.testTag("medicine_tab")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧻", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = HerCareStrings.padStockTrackerTab(language),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                modifier = Modifier.testTag("pad_stock_tab")
            )
        }

        if (selectedTab == 0) {
            // MEDICINE ROUTINE CONTENT
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header & Add Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = HerCareStrings.medicineTitle(language),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) "ওষুধের সময়সূচী ও নির্ভরযোগ্য রিমাইন্ডার" else "Prescription schedule & persistent reminders",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showAddMedicineDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("add_medicine_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = HerCareStrings.addMedicine(language),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Mandatory Non-Prescription Medical Disclaimer
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Disclaimer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = HerCareStrings.medicineDisclaimer(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Medicines List
                if (uiState.medicines.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "কোনো ওষুধ যুক্ত করা হয়নি। নতুন ওষুধ যোগ করতে উপরের বোতামে চাপুন।" else "No medicines added yet. Tap '+ Add Medicine' to schedule reminders.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(24.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(uiState.medicines) { med ->
                        val times = med.getScheduledTimesList()
                        val isFullyTakenToday = med.isFullyTakenToday(todayMillis)

                        val mealLabel = when (med.mealTiming.uppercase()) {
                            "BEFORE_MEAL" -> HerCareStrings.mealBefore(language)
                            "WITH_MEAL" -> HerCareStrings.mealWith(language)
                            "ANY_TIME" -> HerCareStrings.mealAny(language)
                            else -> HerCareStrings.mealAfter(language)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isFullyTakenToday) MaterialTheme.colorScheme.secondaryContainer
                                                    else MaterialTheme.colorScheme.primaryContainer
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isFullyTakenToday) Icons.Default.Check else Icons.Default.LocalPharmacy,
                                                contentDescription = "Med",
                                                tint = if (isFullyTakenToday) MaterialTheme.colorScheme.onSecondaryContainer
                                                else MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = med.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "${HerCareStrings.medicineDose(language)}: ${med.dose} • $mealLabel",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteMedicine(med.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Dose Times & Status row
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "আজকের ডোজের সময়সূচী:" else "Today's Dose Schedule:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    times.forEachIndexed { doseIndex, timeStr ->
                                        val isThisDoseTaken = med.isDoseTakenToday(todayMillis, doseIndex)

                                        FilterChip(
                                            selected = isThisDoseTaken,
                                            onClick = { onToggleMedicineDose(med, doseIndex) },
                                            label = {
                                                Text(
                                                    text = if (isThisDoseTaken) "✓ $timeStr" else timeStr,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = if (isThisDoseTaken) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                                    contentDescription = "Dose",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                                selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // PAD STOCK TRACKER CONTENT
            val summary = uiState.cycleSummary
            val remainingPads = uiState.padStock.totalAvailable
            val isOutOfStock = remainingPads <= 0
            val isLowStock = !isOutOfStock && (summary?.shouldWarnPadStock == true || remainingPads <= 3)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Out of Stock or Low Stock Banner
                if (isOutOfStock) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = WarningRedContainer),
                            border = BorderStroke(1.5.dp, WarningRed)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = WarningRed,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = HerCareStrings.outOfStock(language),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = WarningRed
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BANGLA)
                                            "আপনার সব প্যাড শেষ হয়ে গেছে! অনুগ্রহ করে অবিলম্বে নতুন প্যাড রিস্টক করুন।"
                                        else
                                            "You have run out of sanitary pads! Please restock your inventory immediately.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                } else if (isLowStock) {
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
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = WarningRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "প্যাড কেনার তাগিদ (সতর্কতা)" else "Low Pad Stock Alert",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = WarningRed
                                    )
                                    Text(
                                        text = HerCareStrings.padLowStockNotification(
                                            lang = language,
                                            days = summary?.daysUntilNextPeriod ?: 2,
                                            pads = remainingPads
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Big Stock Visual Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🧻",
                                fontSize = 56.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isOutOfStock) {
                                Text(
                                    text = HerCareStrings.outOfStock(language),
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = WarningRed
                                )
                            } else {
                                Text(
                                    text = HerCareStrings.remainingPads(language, remainingPads),
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (remainingPads <= 3) WarningRed else MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = if (language == AppLanguage.BANGLA) "বর্তমানে মজুত রয়েছে" else "Available in your inventory",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Action Buttons (When 0, hide +1 Pad Used, show prominent RESTOCK)
                            if (isOutOfStock) {
                                Button(
                                    onClick = { showAdjustStockDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("restock_pads_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningRed)
                                ) {
                                    Text(
                                        text = HerCareStrings.restockButton(language),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = onConsumePad,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("consume_pad_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text(
                                            text = HerCareStrings.logPadUsed(language),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { showAdjustStockDialog = true },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("adjust_stock_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text(
                                            text = HerCareStrings.restockButton(language),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // How Pad Stock Notification Rule Works
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "🔔 স্বয়ংক্রিয় প্যাড সতর্কতা নিয়ম" else "🔔 Automatic Pad Alert Rule",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) {
                                    "আপনার পরবর্তী পিরিয়ড যদি ২ দিন বা তার কম সময়ের মধ্যে প্রত্যাশিত হয় এবং অবশিষ্ট প্যাডের সংখ্যা ৩ বা তার চেয়ে কম থাকে, তবে HerCare স্বয়ংক্রিয়ভাবে আপনাকে নতুন প্যাড সংগ্রহ করার জন্য অ্যান্ড্রয়েড লোকাল নোটিফিকেশন পাঠাবে।"
                                } else {
                                    "If your expected period is 2 days away or less and remaining pads are 3 or fewer, HerCare triggers a reliable local Android notification reminding you to purchase pads in advance."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Medicine Dialog with Full Multi-Dose Time Scheduling
    if (showAddMedicineDialog) {
        var medName by remember { mutableStateOf("") }
        var medDose by remember { mutableStateOf("") }
        var frequencyTimes by remember { mutableIntStateOf(1) }
        var mealTiming by remember { mutableStateOf("AFTER_MEAL") }

        val scheduledTimes = remember {
            mutableStateListOf("08:00")
        }

        fun updateFrequency(count: Int) {
            frequencyTimes = count
            scheduledTimes.clear()
            when (count) {
                1 -> scheduledTimes.addAll(listOf("08:00"))
                2 -> scheduledTimes.addAll(listOf("08:00", "20:00"))
                3 -> scheduledTimes.addAll(listOf("08:00", "14:00", "20:00"))
                4 -> scheduledTimes.addAll(listOf("08:00", "12:00", "16:00", "20:00"))
                else -> scheduledTimes.addAll(listOf("08:00"))
            }
        }

        AlertDialog(
            onDismissRequest = { showAddMedicineDialog = false },
            title = {
                Text(
                    text = HerCareStrings.addMedicine(language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = medName,
                            onValueChange = { medName = it },
                            label = { Text(HerCareStrings.medicineName(language)) },
                            placeholder = { Text(if (language == AppLanguage.BANGLA) "যেমন: আয়রন ট্যাবলেট" else "e.g. Iron Supplement") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = medDose,
                            onValueChange = { medDose = it },
                            label = { Text(HerCareStrings.medicineDose(language)) },
                            placeholder = { Text(if (language == AppLanguage.BANGLA) "যেমন: ১টি ট্যাবলেট বা ৫০০মিগ্রা" else "e.g. 1 Tablet / 500mg") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // Frequency selection
                    item {
                        Column {
                            Text(
                                text = HerCareStrings.medicineFrequency(language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    1 to HerCareStrings.freqDaily(language),
                                    2 to HerCareStrings.freqTwiceDaily(language),
                                    3 to HerCareStrings.freqThreePerDay(language),
                                    4 to HerCareStrings.freqFourPerDay(language)
                                ).forEach { (count, label) ->
                                    FilterChip(
                                        selected = frequencyTimes == count,
                                        onClick = { updateFrequency(count) },
                                        label = {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Default suggestion notice
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⚠️ " + HerCareStrings.defaultTimesSuggestionNote(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // Scheduled Times Edit Row
                    item {
                        Column {
                            Text(
                                text = HerCareStrings.customReminderTimes(language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                scheduledTimes.forEachIndexed { index, timeStr ->
                                    val parts = timeStr.split(":")
                                    val curH = parts.getOrNull(0)?.toIntOrNull() ?: 8
                                    val curM = parts.getOrNull(1)?.toIntOrNull() ?: 0

                                    AssistChip(
                                        onClick = {
                                            TimePickerDialog(
                                                context,
                                                { _, newH, newM ->
                                                    val formatted = String.format(Locale.getDefault(), "%02d:%02d", newH, newM)
                                                    if (index < scheduledTimes.size) {
                                                        scheduledTimes[index] = formatted
                                                    }
                                                },
                                                curH,
                                                curM,
                                                false
                                            ).show()
                                        },
                                        label = {
                                            Text(
                                                text = "${index + 1}. $timeStr",
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit Time",
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Meal timing selection
                    item {
                        Column {
                            Text(
                                text = HerCareStrings.mealTimingLabel(language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "AFTER_MEAL" to HerCareStrings.mealAfter(language),
                                    "BEFORE_MEAL" to HerCareStrings.mealBefore(language),
                                    "WITH_MEAL" to HerCareStrings.mealWith(language),
                                    "ANY_TIME" to HerCareStrings.mealAny(language)
                                ).forEach { (timingKey, timingLabel) ->
                                    FilterChip(
                                        selected = mealTiming == timingKey,
                                        onClick = { mealTiming = timingKey },
                                        label = { Text(timingLabel, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (medName.isNotBlank()) {
                            val timesCsv = scheduledTimes.joinToString(",")
                            if (onAddMedicineFull != null) {
                                onAddMedicineFull(
                                    medName.trim(),
                                    medDose.ifBlank { "1 dose" }.trim(),
                                    frequencyTimes,
                                    timesCsv,
                                    mealTiming,
                                    System.currentTimeMillis(),
                                    null
                                )
                            } else {
                                val first = scheduledTimes.firstOrNull() ?: "08:00"
                                val p = first.split(":")
                                onAddMedicine(
                                    medName.trim(),
                                    medDose.ifBlank { "1 dose" }.trim(),
                                    p.getOrNull(0)?.toIntOrNull() ?: 8,
                                    p.getOrNull(1)?.toIntOrNull() ?: 0,
                                    "$frequencyTimes times/day"
                                )
                            }
                            showAddMedicineDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (language == AppLanguage.BANGLA) "যুক্ত করুন" else "Add Schedule")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddMedicineDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(HerCareStrings.cancel(language))
                }
            }
        )
    }

    // Adjust / Restock Stock Dialog
    if (showAdjustStockDialog) {
        var addCountText by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAdjustStockDialog = false },
            title = {
                Text(
                    text = HerCareStrings.restockPads(language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                        showAdjustStockDialog = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_adjust_stock_button")
                ) {
                    Text(if (language == AppLanguage.BANGLA) "মজুত আপডেট করুন" else "Update Stock")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAdjustStockDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(HerCareStrings.cancel(language))
                }
            }
        )
    }
}
