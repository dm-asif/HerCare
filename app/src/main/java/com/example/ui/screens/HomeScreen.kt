package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineEntry
import com.example.domain.CycleCalculator
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.components.CycleVisualizer
import com.example.ui.components.HerCareHeader
import com.example.ui.components.IrregularWarningCard
import com.example.ui.components.LogFlowDialog
import com.example.ui.navigation.HerCareTab
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CardSoftShadowColor
import com.example.ui.theme.CoralPeriod
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepPink
import com.example.ui.theme.HeroGradientEnd
import com.example.ui.theme.HeroGradientStart
import com.example.ui.theme.LightPink
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.HerCareUiState

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
    onNavigateToTab: ((HerCareTab) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val language = uiState.language
    val summary = uiState.cycleSummary

    var showLogDialog by remember { mutableStateOf(false) }
    var showRestockDialog by remember { mutableStateOf(false) }

    val todayMillis = CycleCalculator.getStartOfDayMillis(System.currentTimeMillis())
    val todayLog = uiState.dailyLogs.find { it.dateMillis == todayMillis }

    val latestPeriod = uiState.periods.firstOrNull()
    val isPeriodOngoing = latestPeriod != null && latestPeriod.endDateMillis == null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. App Header (Official Logo, Slogan, Language Pill, Notification)
        item {
            HerCareHeader(
                userName = uiState.userProfile.name,
                currentLanguage = language,
                onLanguageToggle = onLanguageChange,
                onNotificationClick = {
                    onNavigateToTab?.invoke(HerCareTab.SETTINGS)
                }
            )
        }

        // 2. Cycle Visualizer Card (Ring, Days Left, Next Period, Woman Art)
        if (summary != null) {
            item {
                CycleVisualizer(
                    summary = summary,
                    language = language,
                    onPhaseClick = {
                        onNavigateToTab?.invoke(HerCareTab.CALENDAR)
                    }
                )
            }
        }

        // 3. Period Started / Ongoing Action Card (Reference Card Design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = CardSoftShadowColor)
                    .testTag("period_action_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Circular droplet badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(LightPink, SoftRose.copy(alpha = 0.4f)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = DeepPink,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isPeriodOngoing) {
                                    if (language == AppLanguage.BANGLA) "পিরিয়ড চলছে" else "Period Ongoing"
                                } else {
                                    if (language == AppLanguage.BANGLA) "পিরিয়ড শুরু" else "Period Started"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = PrimaryText
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) "রক্তপ্রবাহ ও উপসর্গ রেকর্ড করুন" else "Track your flow and symptoms",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                color = SecondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Action Pill Button
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (isPeriodOngoing) {
                            Button(
                                onClick = { onPeriodEnd(latestPeriod!!.id) },
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepPink
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("period_end_button")
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "সমাপ্ত >" else "End Period >",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        } else {
                            Button(
                                onClick = onPeriodStart,
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepPink
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("period_start_button")
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "শুরু করুন >" else "Start >",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        // Log Button
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = LightPink,
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { showLogDialog = true }
                                .testTag("log_today_button")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "লগ" else "Log",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = DeepPink
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Pad Stock Tracker Card (Reference Card Design)
        item {
            val stock = uiState.padStock.totalAvailable
            val isOutOfStock = stock <= 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = CardSoftShadowColor)
                    .testTag("pad_stock_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Circular icon badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isOutOfStock) Modifier.background(DangerRed.copy(alpha = 0.12f))
                                    else Modifier.background(Brush.linearGradient(listOf(LightPink, SoftRose.copy(alpha = 0.35f))))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧻", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "প্যাড স্টক ট্র্যাকার" else "Pad Stock Tracker",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = PrimaryText
                            )

                            if (isOutOfStock) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "স্টক শেষ" else "Out of Stock",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = DangerRed
                                )
                            } else {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "$stock টি প্যাড অবশিষ্ট" else "$stock Pads Remaining",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = if (stock <= 3) DangerRed else SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Action Pill Button (+ 1 Pad Used > / Restock >)
                    if (isOutOfStock) {
                        Button(
                            onClick = { showRestockDialog = true },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("home_restock_button")
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "স্টক করুন >" else "RESTOCK >",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White
                            )
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = onConsumePad,
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DeepPink),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("consume_pad_quick_button")
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "+ ১টি ব্যবহৃত >" else "+ 1 Pad Used >",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color.White
                                )
                            }

                            // Quick restock button
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = LightPink,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { showRestockDialog = true }
                                    .testTag("quick_restock_icon_button")
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Restock",
                                        tint = DeepPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Six Feature Shortcuts Grid (Directly matching UI reference)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Medicine Tracker, Calendar, Discharge Guide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureShortcutCard(
                        title = if (language == AppLanguage.BANGLA) "ওষুধ ট্র্যাকার" else "Medicine Tracker",
                        subtitle = if (language == AppLanguage.BANGLA) "নিয়মিত রাখুন >" else "Stay on track >",
                        icon = Icons.Default.Medication,
                        onClick = { onNavigateToTab?.invoke(HerCareTab.MEDICINES_PADS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_medicine_tracker")
                    )

                    FeatureShortcutCard(
                        title = if (language == AppLanguage.BANGLA) "ক্যালেন্ডার" else "Calendar",
                        subtitle = if (language == AppLanguage.BANGLA) "সাইকেল দেখুন >" else "View your cycle >",
                        icon = Icons.Default.CalendarMonth,
                        onClick = { onNavigateToTab?.invoke(HerCareTab.CALENDAR) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_calendar")
                    )

                    FeatureShortcutCard(
                        title = if (language == AppLanguage.BANGLA) "ডিসচার্জ গাইড" else "Discharge Guide",
                        subtitle = if (language == AppLanguage.BANGLA) "লক্ষণ জানুন >" else "Know what's normal >",
                        icon = Icons.Default.WaterDrop,
                        onClick = { onNavigateToTab?.invoke(HerCareTab.DISCHARGE_GUIDE) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_discharge_guide")
                    )
                }

                // Row 2: Symptoms, Personal Report, Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureShortcutCard(
                        title = if (language == AppLanguage.BANGLA) "উপসর্গ" else "Symptoms",
                        subtitle = if (language == AppLanguage.BANGLA) "স্বাস্থ্য ট্র্যাক >" else "Track your health >",
                        icon = Icons.Default.FavoriteBorder,
                        onClick = { showLogDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_symptoms")
                    )

                    FeatureShortcutCard(
                        title = if (language == AppLanguage.BANGLA) "ব্যক্তিগত রিপোর্ট" else "Personal Report",
                        subtitle = if (language == AppLanguage.BANGLA) "বিশ্লেষণ দেখুন >" else "See your insights >",
                        icon = Icons.Default.BarChart,
                        onClick = { onNavigateToTab?.invoke(HerCareTab.HEALTH_REPORT) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_personal_report")
                    )

                    FeatureShortcutCard(
                        title = if (language == AppLanguage.BANGLA) "সেটিংস" else "Settings",
                        subtitle = if (language == AppLanguage.BANGLA) "অ্যাপ পরিচালনা >" else "Manage your app >",
                        icon = Icons.Default.Settings,
                        onClick = { onNavigateToTab?.invoke(HerCareTab.SETTINGS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_settings")
                    )
                }
            }
        }

        // 6. Irregular Warning Card (if applicable)
        if (summary?.activeWarning != null) {
            item {
                IrregularWarningCard(
                    warning = summary.activeWarning,
                    language = language,
                    modifier = Modifier.testTag("irregular_warning_card")
                )
            }
        }

        // 7. Pad Low Stock Warning Banner
        if (summary?.shouldWarnPadStock == true) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), spotColor = CardSoftShadowColor)
                        .testTag("low_pad_stock_warning_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6F6)),
                    border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DangerRed.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧻", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "প্যাড কেনার তাগিদ" else "Low Pad Stock Warning",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = DangerRed
                            )
                            Text(
                                text = HerCareStrings.padLowStockNotification(
                                    lang = language,
                                    days = summary.daysUntilNextPeriod,
                                    pads = uiState.padStock.totalAvailable
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = PrimaryText
                            )
                        }
                    }
                }
            }
        }

        // 8. Today's Medicines Checklist Widget
        if (uiState.medicines.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = CardSoftShadowColor),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(LightPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalPharmacy,
                                        contentDescription = "Medicines",
                                        tint = DeepPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = HerCareStrings.medicineTitle(language),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = DeepPink
                                )
                            }

                            Text(
                                text = if (language == AppLanguage.BANGLA) "সব দেখুন >" else "View all >",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryPink
                                ),
                                modifier = Modifier.clickable {
                                    onNavigateToTab?.invoke(HerCareTab.MEDICINES_PADS)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        uiState.medicines.filter { it.isActive }.take(4).forEach { med ->
                            val times = med.getScheduledTimesList()
                            val isFullyTaken = med.isFullyTakenToday(todayMillis)

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFFCF8F9),
                                border = BorderStroke(0.8.dp, CardBorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${med.name} (${med.dose})",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            ),
                                            color = PrimaryText
                                        )
                                        val timesSummary = times.joinToString(", ")
                                        Text(
                                            text = if (language == AppLanguage.BANGLA) "সময়: $timesSummary" else "Times: $timesSummary",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = SecondaryText
                                        )
                                    }

                                    Checkbox(
                                        checked = isFullyTaken,
                                        onCheckedChange = { onToggleMedicine(med) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = DeepPink,
                                            uncheckedColor = SoftRose
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 9. Today's Recorded Log Summary (If already logged today)
        if (todayLog != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), spotColor = CardSoftShadowColor),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Logged",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "আজকের স্বাস্থ্য তথ্য নথিভুক্ত হয়েছে" else "Today's Log Recorded",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = DeepPink
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA) {
                                "রক্তপ্রবাহ: ${todayLog.flowLevel} • ব্যথা: ${todayLog.painLevel} • ব্যবহৃত প্যাড: ${todayLog.padsUsed}"
                            } else {
                                "Flow: ${todayLog.flowLevel} • Pain: ${todayLog.painLevel} • Pads Used: ${todayLog.padsUsed}"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = PrimaryText
                        )

                        if (todayLog.symptomsCsv.isNotBlank()) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "উপসর্গ: ${todayLog.symptomsCsv}" else "Symptoms: ${todayLog.symptomsCsv}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = SecondaryText,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
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
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = HerCareStrings.restockPads(language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepPink
                    )
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
                        color = SecondaryText
                    )
                    Text(
                        text = HerCareStrings.restockDialogPrompt(language),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimaryText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 20).forEach { qty ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (addCountText == qty.toString()) LightPink else Color(0xFFF9F9F9),
                                border = BorderStroke(
                                    1.dp,
                                    if (addCountText == qty.toString()) DeepPink else CardBorderColor
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { addCountText = qty.toString() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "+$qty",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (addCountText == qty.toString()) DeepPink else PrimaryText
                                        )
                                    )
                                }
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
                        shape = RoundedCornerShape(12.dp),
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
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPink),
                    modifier = Modifier.testTag("confirm_restock_button")
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "মজুত আপডেট করুন" else "Update Stock",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRestockDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Text(HerCareStrings.cancel(language), color = SecondaryText)
                }
            }
        )
    }
}

/**
 * Reusable feature shortcut card styled according to the UI reference
 */
@Composable
private fun FeatureShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(115.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp), spotColor = CardSoftShadowColor)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // Icon circular badge
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(LightPink),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = DeepPink,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        lineHeight = 14.sp
                    ),
                    color = PrimaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
