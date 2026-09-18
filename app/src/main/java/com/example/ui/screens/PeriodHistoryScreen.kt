package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PeriodEntry
import com.example.domain.CycleCalculator
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.components.LogFlowDialog
import com.example.ui.theme.CoralPeriod
import com.example.ui.theme.FertileGreen
import com.example.ui.theme.SoftPinkPeriod
import com.example.ui.viewmodel.HerCareUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun PeriodHistoryScreen(
    uiState: HerCareUiState,
    onSaveLog: (
        dateMillis: Long,
        flowLevel: String,
        painLevel: String,
        symptoms: List<String>,
        padsUsed: Int,
        dischargeType: String,
        notes: String
    ) -> Unit,
    onAddPeriod: (startMillis: Long, endMillis: Long?, flowIntensity: String, notes: String) -> Unit,
    onDeletePeriod: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = uiState.language
    val context = LocalContext.current

    var calendarMonthOffset by remember { mutableIntStateOf(0) }
    var selectedCalendarDateMillis by remember { mutableStateOf<Long?>(null) }
    var showLogDialogForDate by remember { mutableStateOf<Long?>(null) }

    // Calendar Calculations
    val displayCalendar = remember(calendarMonthOffset) {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, calendarMonthOffset)
        }
    }

    val monthYearFormat = SimpleDateFormat(
        "MMMM yyyy",
        if (language == AppLanguage.BANGLA) Locale("bn") else Locale.ENGLISH
    )
    val monthYearText = monthYearFormat.format(displayCalendar.time)

    val daysInMonth = displayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = displayCalendar.get(Calendar.DAY_OF_WEEK) // 1 is Sunday

    val summary = uiState.cycleSummary
    val dateFormat = SimpleDateFormat(
        "MMM dd, yyyy",
        if (language == AppLanguage.BANGLA) Locale("bn") else Locale.ENGLISH
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পিরিয়ড ট্র্যাকিং ও ইতিহাস" else "Period Tracking & History",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পূর্ববর্তী চক্রসমূহ ও ক্যালেন্ডার" else "Past cycles, flow logs & predictions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Add Historical Period Button
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val startCal = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth, 0, 0, 0)
                                }
                                val endMillis = startCal.timeInMillis + (5 * 24 * 60 * 60 * 1000L)
                                onAddPeriod(startCal.timeInMillis, endMillis, "MEDIUM", "")
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_historical_period_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Period", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == AppLanguage.BANGLA) "যুক্ত করুন" else "+ Period")
                }
            }
        }

        // Interactive Monthly Calendar Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Month Navigation Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { calendarMonthOffset-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                        }

                        Text(
                            text = monthYearText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(onClick = { calendarMonthOffset++ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days of Week Header
                    val weekDays = if (language == AppLanguage.BANGLA) {
                        listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র", "শনি")
                    } else {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekDays.forEach { dayName ->
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days Grid
                    val totalSlots = (firstDayOfWeek - 1) + daysInMonth
                    val rows = (totalSlots + 6) / 7

                    for (rowIndex in 0 until rows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (colIndex in 0..6) {
                                val slotIndex = rowIndex * 7 + colIndex
                                val dayNumber = slotIndex - (firstDayOfWeek - 1) + 1

                                if (dayNumber in 1..daysInMonth) {
                                    val cellCal = Calendar.getInstance().apply {
                                        set(
                                            displayCalendar.get(Calendar.YEAR),
                                            displayCalendar.get(Calendar.MONTH),
                                            dayNumber,
                                            0,
                                            0,
                                            0
                                        )
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val cellMillis = cellCal.timeInMillis

                                    // Check if this date falls within any logged period
                                    val isLoggedPeriodDay = uiState.periods.any { period ->
                                        val start = CycleCalculator.getStartOfDayMillis(period.startDateMillis)
                                        val end = period.endDateMillis?.let { CycleCalculator.getStartOfDayMillis(it) }
                                            ?: (start + (uiState.userProfile.periodDurationDays * 24 * 60 * 60 * 1000L))
                                        cellMillis in start..end
                                    }

                                    // Check if predicted period day
                                    val isPredictedPeriod = summary != null &&
                                            cellMillis >= summary.nextPeriodDateMillis &&
                                            cellMillis < (summary.nextPeriodDateMillis + (summary.averagePeriodDuration * 24 * 60 * 60 * 1000L))

                                    // Check if fertile window
                                    val isFertileDay = summary != null &&
                                            cellMillis in summary.fertileWindowStartMillis..summary.fertileWindowEndMillis

                                    // Check if ovulation day
                                    val isOvulationDay = summary != null &&
                                            TimeUnit.MILLISECONDS.toDays(cellMillis) == TimeUnit.MILLISECONDS.toDays(summary.estimatedOvulationDateMillis)

                                    // Has daily log
                                    val hasDailyLog = uiState.dailyLogs.any { it.dateMillis == cellMillis }

                                    val bgModifier = when {
                                        isLoggedPeriodDay -> Modifier.background(CoralPeriod, CircleShape)
                                        isPredictedPeriod -> Modifier.background(SoftPinkPeriod, CircleShape)
                                        isOvulationDay -> Modifier.background(FertileGreen, CircleShape)
                                        isFertileDay -> Modifier.background(FertileGreen.copy(alpha = 0.2f), CircleShape)
                                        hasDailyLog -> Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                        else -> Modifier
                                    }

                                    val textColor = when {
                                        isLoggedPeriodDay || isOvulationDay -> Color.White
                                        isPredictedPeriod -> CoralPeriod
                                        isFertileDay -> FertileGreen
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .then(bgModifier)
                                            .clickable {
                                                selectedCalendarDateMillis = cellMillis
                                                showLogDialogForDate = cellMillis
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isLoggedPeriodDay || isOvulationDay) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = textColor
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calendar Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        LegendItem(color = CoralPeriod, text = if (language == AppLanguage.BANGLA) "পিরিয়ড" else "Period")
                        LegendItem(color = SoftPinkPeriod, text = if (language == AppLanguage.BANGLA) "ভবিষ্যদ্বাণী" else "Predicted")
                        LegendItem(color = FertileGreen, text = if (language == AppLanguage.BANGLA) "ওভুলেশন" else "Ovulation")
                    }
                }
            }
        }

        // Complete Period History List Header
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "সম্পূর্ণ পিরিয়ড ইতিহাস" else "Complete Period History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (uiState.periods.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "কোনো পিরিয়ডের তথ্য এখনো সংরক্ষিত নেই।" else "No period records found yet. Tap '+ Period' to add.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val sortedPeriods = uiState.periods.sortedByDescending { it.startDateMillis }
            items(sortedPeriods) { period ->
                val durationDays = period.endDateMillis?.let {
                    (TimeUnit.MILLISECONDS.toDays(it - period.startDateMillis) + 1).toInt()
                } ?: uiState.userProfile.periodDurationDays

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
                                    .background(CoralPeriod.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Period",
                                    tint = CoralPeriod,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = dateFormat.format(Date(period.startDateMillis)) +
                                            if (period.endDateMillis != null) " - ${dateFormat.format(Date(period.endDateMillis))}" else " (Active)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "স্থায়িত্বকাল: $durationDays দিন • তীব্রতা: ${period.flowIntensity}" else "Duration: $durationDays days • Flow: ${period.flowIntensity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { onDeletePeriod(period.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete record",
                                tint = MaterialTheme.colorScheme.outline
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

    // Modal Dialog when date is tapped in the calendar
    showLogDialogForDate?.let { dateMillis ->
        LogFlowDialog(
            initialDateMillis = dateMillis,
            language = language,
            onDismiss = { showLogDialogForDate = null },
            onSave = { flow, pain, symptoms, pads, discharge, notes ->
                onSaveLog(
                    dateMillis,
                    flow,
                    pain,
                    symptoms,
                    pads,
                    discharge,
                    notes
                )
                showLogDialogForDate = null
            }
        )
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}
