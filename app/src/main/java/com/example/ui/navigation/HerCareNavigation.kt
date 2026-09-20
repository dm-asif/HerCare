package com.example.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.components.HerCareBackground
import com.example.ui.screens.DischargeGuideScreen
import com.example.ui.screens.HealthReportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicineAndStockScreen
import com.example.ui.screens.PeriodHistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CardSoftShadowColor
import com.example.ui.theme.DeepPink
import com.example.ui.theme.LightPink
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.SecondaryText
import com.example.ui.viewmodel.HerCareUiState
import com.example.ui.viewmodel.HerCareViewModel

enum class HerCareTab {
    HOME,
    CALENDAR,
    DISCHARGE_GUIDE,
    MEDICINES_PADS,
    HEALTH_REPORT,
    SETTINGS
}

@Composable
fun HerCareAppContent(
    uiState: HerCareUiState,
    viewModel: HerCareViewModel,
    modifier: Modifier = Modifier
) {
    val language = uiState.language
    var currentTab by remember { mutableStateOf(HerCareTab.HOME) }

    HerCareBackground {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = Color.White.copy(alpha = 0.98f),
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            spotColor = CardSoftShadowColor
                        )
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        val navItemColors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepPink,
                            selectedTextColor = DeepPink,
                            indicatorColor = LightPink,
                            unselectedIconColor = SecondaryText.copy(alpha = 0.8f),
                            unselectedTextColor = SecondaryText.copy(alpha = 0.8f)
                        )

                        // 1. Home
                        NavigationBarItem(
                            selected = currentTab == HerCareTab.HOME,
                            onClick = { currentTab = HerCareTab.HOME },
                            icon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = HerCareStrings.homeTab(language),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = HerCareStrings.homeTab(language),
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == HerCareTab.HOME) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            },
                            colors = navItemColors,
                            modifier = Modifier.testTag("nav_home")
                        )

                        // 2. Calendar / History
                        NavigationBarItem(
                            selected = currentTab == HerCareTab.CALENDAR,
                            onClick = { currentTab = HerCareTab.CALENDAR },
                            icon = {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = HerCareStrings.historyTab(language),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ক্যালেন্ডার" else "Calendar",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == HerCareTab.CALENDAR) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            },
                            colors = navItemColors,
                            modifier = Modifier.testTag("nav_calendar")
                        )

                        // 3. Health Reports
                        NavigationBarItem(
                            selected = currentTab == HerCareTab.HEALTH_REPORT,
                            onClick = { currentTab = HerCareTab.HEALTH_REPORT },
                            icon = {
                                Icon(
                                    Icons.Default.Assessment,
                                    contentDescription = HerCareStrings.reportTab(language),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "রিপোর্ট" else "Reports",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == HerCareTab.HEALTH_REPORT) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            },
                            colors = navItemColors,
                            modifier = Modifier.testTag("nav_report")
                        )

                        // 4. Medicine & Pads
                        NavigationBarItem(
                            selected = currentTab == HerCareTab.MEDICINES_PADS,
                            onClick = { currentTab = HerCareTab.MEDICINES_PADS },
                            icon = {
                                Icon(
                                    Icons.Default.Medication,
                                    contentDescription = HerCareStrings.medicineTab(language),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ওষুধ" else "Medicine",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == HerCareTab.MEDICINES_PADS) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            },
                            colors = navItemColors,
                            modifier = Modifier.testTag("nav_medicine")
                        )

                        // 5. More (Settings & Discharge Guide)
                        NavigationBarItem(
                            selected = currentTab == HerCareTab.SETTINGS || currentTab == HerCareTab.DISCHARGE_GUIDE,
                            onClick = { currentTab = HerCareTab.SETTINGS },
                            icon = {
                                Icon(
                                    Icons.Default.MoreHoriz,
                                    contentDescription = HerCareStrings.settingsTab(language),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "আরও" else "More",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == HerCareTab.SETTINGS || currentTab == HerCareTab.DISCHARGE_GUIDE) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            },
                            colors = navItemColors,
                            modifier = Modifier.testTag("nav_settings")
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (currentTab) {
                HerCareTab.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onPeriodStart = { viewModel.logPeriodStart() },
                        onPeriodEnd = { viewModel.logPeriodEnd(it) },
                        onSaveLog = { dateMillis, flow, pain, symptoms, pads, discharge, notes ->
                            viewModel.saveDailyLog(dateMillis, flow, pain, symptoms, pads, discharge, notes)
                        },
                        onConsumePad = { viewModel.consumeOnePad() },
                        onToggleMedicine = { viewModel.toggleMedicineTaken(it) },
                        onRestockPads = { viewModel.restockPads(it) },
                        onToggleMedicineDose = { med, doseIndex -> viewModel.toggleMedicineDoseTaken(med, doseIndex) },
                        onNavigateToTab = { currentTab = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                HerCareTab.CALENDAR -> {
                    PeriodHistoryScreen(
                        uiState = uiState,
                        onSaveLog = { dateMillis, flow, pain, symptoms, pads, discharge, notes ->
                            viewModel.saveDailyLog(dateMillis, flow, pain, symptoms, pads, discharge, notes)
                        },
                        onAddPeriod = { start, end, flow, notes ->
                            viewModel.addPeriodEntry(start, end, flow, notes)
                        },
                        onDeletePeriod = { viewModel.deletePeriodEntry(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                HerCareTab.DISCHARGE_GUIDE -> {
                    DischargeGuideScreen(
                        language = language,
                        onBackToHome = { currentTab = HerCareTab.HOME },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                HerCareTab.MEDICINES_PADS -> {
                    MedicineAndStockScreen(
                        uiState = uiState,
                        onAddMedicine = { name, dose, hour, minute, freq ->
                            viewModel.addMedicine(name, dose, hour, minute, freq)
                        },
                        onToggleMedicine = { viewModel.toggleMedicineTaken(it) },
                        onDeleteMedicine = { viewModel.deleteMedicine(it) },
                        onConsumePad = { viewModel.consumeOnePad() },
                        onUpdatePadStock = { viewModel.updatePadStock(it) },
                        onAddMedicineFull = { name, dose, freqTimes, scheduledTimesCsv, mealTiming, startDate, endDate ->
                            viewModel.addMedicine(name, dose, freqTimes, scheduledTimesCsv, mealTiming, startDate, endDate)
                        },
                        onToggleMedicineDose = { med, doseIndex ->
                            viewModel.toggleMedicineDoseTaken(med, doseIndex)
                        },
                        onRestockPads = {
                            viewModel.restockPads(it)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                HerCareTab.HEALTH_REPORT -> {
                    HealthReportScreen(
                        uiState = uiState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                HerCareTab.SETTINGS -> {
                    SettingsScreen(
                        uiState = uiState,
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onToggleBiometric = { enabled, pin -> viewModel.toggleBiometric(enabled, pin) },
                        onUpdatePersonalInfo = { name, dob -> viewModel.updatePersonalInfo(name, dob) },
                        onTestMedicine = { viewModel.testMedicineReminder() },
                        onTestPeriod = { viewModel.testPeriodReminder() },
                        onTestIrregular = { viewModel.testIrregularCycleWarning() },
                        onTestPad = { viewModel.testPadPurchaseReminder() },
                        onTestHealth = { viewModel.testHealthWarning() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
