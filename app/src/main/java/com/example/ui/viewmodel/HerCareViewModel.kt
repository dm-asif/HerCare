package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.HerCareApplication
import com.example.data.model.DailyLog
import com.example.data.model.MedicineEntry
import com.example.data.model.PadStock
import com.example.data.model.PeriodEntry
import com.example.data.model.UserProfile
import com.example.domain.CycleCalculator
import com.example.domain.CycleSummary
import com.example.localization.AppLanguage
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HerCareUiState(
    val userProfile: UserProfile = UserProfile(),
    val language: AppLanguage = AppLanguage.ENGLISH,
    val periods: List<PeriodEntry> = emptyList(),
    val cycleSummary: CycleSummary? = null,
    val dailyLogs: List<DailyLog> = emptyList(),
    val medicines: List<MedicineEntry> = emptyList(),
    val padStock: PadStock = PadStock(),
    val isAppLocked: Boolean = false,
    val isOnboardingComplete: Boolean = false,
    val isLoaded: Boolean = false
)

class HerCareViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as HerCareApplication).repository

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage

    val userProfile = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val periods = repository.allPeriods.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyLogs = repository.allDailyLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val medicines = repository.allMedicines.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val padStock = repository.padStock.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PadStock()
    )

    private data class InventoryAndSettings(
        val medicines: List<MedicineEntry>,
        val padStock: PadStock?,
        val isLocked: Boolean,
        val language: AppLanguage
    )

    private val inventoryAndSettingsFlow = combine(
        repository.allMedicines,
        repository.padStock,
        _isAppLocked,
        _currentLanguage
    ) { meds, stock, locked, lang ->
        InventoryAndSettings(meds, stock, locked, lang)
    }

    val uiState: StateFlow<HerCareUiState> = combine(
        repository.userProfile,
        repository.allPeriods,
        repository.allDailyLogs,
        inventoryAndSettingsFlow
    ) { profile, periodsList, logsList, inv ->
        val user = profile ?: UserProfile()
        val effectiveLang = if (profile != null) AppLanguage.fromCode(profile.language) else inv.language

        val summary = CycleCalculator.calculateCycleSummary(
            periods = periodsList,
            fallbackCycleLength = user.cycleLengthDays,
            fallbackPeriodDuration = user.periodDurationDays,
            recentDailyLogs = logsList,
            currentPadsCount = inv.padStock?.totalAvailable ?: 15
        )

        HerCareUiState(
            userProfile = user,
            language = effectiveLang,
            periods = periodsList,
            cycleSummary = summary,
            dailyLogs = logsList,
            medicines = inv.medicines,
            padStock = inv.padStock ?: PadStock(),
            isAppLocked = inv.isLocked && user.isBiometricEnabled,
            isOnboardingComplete = user.onboardingCompleted,
            isLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HerCareUiState(isLoaded = false)
    )

    init {
        viewModelScope.launch {
            val profile = repository.getUserProfileOnce()
            if (profile != null) {
                _currentLanguage.value = AppLanguage.fromCode(profile.language)
                if (profile.isBiometricEnabled) {
                    _isAppLocked.value = true
                }
            }
        }
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun updatePersonalInfo(name: String, birthDateMillis: Long) {
        viewModelScope.launch {
            val current = repository.getUserProfileOnce() ?: UserProfile()
            repository.saveUserProfile(
                current.copy(
                    name = name,
                    birthDateMillis = birthDateMillis
                )
            )
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
        viewModelScope.launch {
            val current = repository.getUserProfileOnce() ?: UserProfile()
            repository.saveUserProfile(current.copy(language = lang.code))
        }
    }

    fun completeOnboarding(
        name: String,
        birthDateMillis: Long,
        lastPeriodStartMillis: Long,
        cycleLength: Int,
        periodDuration: Int,
        initialPads: Int,
        lang: AppLanguage
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                id = 1,
                name = name,
                birthDateMillis = birthDateMillis,
                cycleLengthDays = cycleLength,
                periodDurationDays = periodDuration,
                language = lang.code,
                onboardingCompleted = true
            )
            repository.saveUserProfile(profile)

            // Save initial period
            val endMillis = lastPeriodStartMillis + (periodDuration * 24 * 60 * 60 * 1000L)
            repository.addPeriod(
                PeriodEntry(
                    startDateMillis = lastPeriodStartMillis,
                    endDateMillis = if (endMillis <= System.currentTimeMillis()) endMillis else null,
                    flowIntensity = "MEDIUM"
                )
            )

            // Save initial pad stock
            repository.savePadStock(PadStock(id = 1, totalAvailable = initialPads))

            // Schedule period reminder
            val nextPeriodExpected = lastPeriodStartMillis + (cycleLength * 24 * 60 * 60 * 1000L)
            NotificationHelper.schedulePeriodReminder(
                context = getApplication(),
                nextPeriodMillis = nextPeriodExpected,
                isBangla = lang == AppLanguage.BANGLA
            )
        }
    }

    fun logPeriodStart(dateMillis: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val normalizedDate = CycleCalculator.getStartOfDayMillis(dateMillis)
            repository.addPeriod(
                PeriodEntry(
                    startDateMillis = normalizedDate,
                    endDateMillis = null,
                    flowIntensity = "MEDIUM"
                )
            )

            // Reschedule next period reminder
            val profile = repository.getUserProfileOnce()
            val cycleLen = profile?.cycleLengthDays ?: 28
            NotificationHelper.schedulePeriodReminder(
                context = getApplication(),
                nextPeriodMillis = normalizedDate + (cycleLen * 24 * 60 * 60 * 1000L),
                isBangla = profile?.language == "bn"
            )
        }
    }

    fun logPeriodEnd(periodId: Long, endMillis: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val periods = repository.getAllPeriodsList()
            val target = periods.find { it.id == periodId }
            if (target != null) {
                repository.updatePeriod(target.copy(endDateMillis = CycleCalculator.getStartOfDayMillis(endMillis)))
            }
        }
    }

    fun saveDailyLog(
        dateMillis: Long,
        flowLevel: String,
        painLevel: String,
        symptoms: List<String>,
        padsUsed: Int,
        dischargeType: String,
        notes: String
    ) {
        viewModelScope.launch {
            val normalizedDate = CycleCalculator.getStartOfDayMillis(dateMillis)
            val log = DailyLog(
                dateMillis = normalizedDate,
                flowLevel = flowLevel,
                painLevel = painLevel,
                symptomsCsv = symptoms.joinToString(","),
                padsUsed = padsUsed,
                dischargeType = dischargeType,
                notes = notes
            )
            repository.saveDailyLog(log)

            // If pads used were recorded, update pad stock
            if (padsUsed > 0) {
                val stock = repository.getPadStockOnce() ?: PadStock(totalAvailable = 15)
                val remaining = (stock.totalAvailable - padsUsed).coerceAtLeast(0)
                repository.savePadStock(stock.copy(totalAvailable = remaining, lastUpdatedMillis = System.currentTimeMillis()))
            }
        }
    }

    fun addPeriodEntry(startMillis: Long, endMillis: Long?, flowIntensity: String, notes: String) {
        viewModelScope.launch {
            repository.addPeriod(
                PeriodEntry(
                    startDateMillis = startMillis,
                    endDateMillis = endMillis,
                    flowIntensity = flowIntensity,
                    notes = notes
                )
            )
        }
    }

    fun deletePeriodEntry(id: Long) {
        viewModelScope.launch {
            repository.deletePeriod(id)
        }
    }

    fun addMedicine(
        name: String,
        dose: String,
        frequencyTimesPerDay: Int,
        scheduledTimesCsv: String,
        mealTiming: String,
        startDateMillis: Long = System.currentTimeMillis(),
        endDateMillis: Long? = null
    ) {
        viewModelScope.launch {
            val primaryTime = scheduledTimesCsv.split(",").firstOrNull()?.trim() ?: "08:00"
            val parts = primaryTime.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 8
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0

            val med = MedicineEntry(
                name = name,
                dose = dose,
                hour = h,
                minute = m,
                frequency = frequencyTimesPerDay.toString(),
                frequencyTimesPerDay = frequencyTimesPerDay,
                scheduledTimesCsv = scheduledTimesCsv,
                mealTiming = mealTiming,
                startDateMillis = startDateMillis,
                endDateMillis = endDateMillis,
                isActive = true
            )
            val medId = repository.addMedicine(med)

            val profile = repository.getUserProfileOnce()
            NotificationHelper.scheduleAllMedicineDoses(
                context = getApplication(),
                medicineId = medId,
                medicineName = name,
                dose = dose,
                mealTiming = mealTiming,
                times = med.getScheduledTimesList(),
                isBangla = profile?.language == "bn"
            )
        }
    }

    fun addMedicine(
        name: String,
        dose: String,
        hour: Int,
        minute: Int,
        frequency: String,
        startDateMillis: Long = System.currentTimeMillis(),
        endDateMillis: Long? = null
    ) {
        val timeFormatted = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
        addMedicine(
            name = name,
            dose = dose,
            frequencyTimesPerDay = 1,
            scheduledTimesCsv = timeFormatted,
            mealTiming = "AFTER_MEAL",
            startDateMillis = startDateMillis,
            endDateMillis = endDateMillis
        )
    }

    fun toggleMedicineTaken(medicine: MedicineEntry) {
        toggleMedicineDoseTaken(medicine, 0)
    }

    fun toggleMedicineDoseTaken(medicine: MedicineEntry, doseIndex: Int) {
        viewModelScope.launch {
            val todayStart = CycleCalculator.getStartOfDayMillis(System.currentTimeMillis())
            val isCurrentDay = medicine.lastTakenDateMillis?.let {
                CycleCalculator.getStartOfDayMillis(it) == todayStart
            } ?: false

            val currentTaken = if (isCurrentDay) {
                medicine.takenDosesCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableSet()
            } else {
                mutableSetOf()
            }

            if (currentTaken.contains(doseIndex)) {
                currentTaken.remove(doseIndex)
            } else {
                currentTaken.add(doseIndex)
            }

            val updated = medicine.copy(
                lastTakenDateMillis = if (currentTaken.isEmpty()) null else System.currentTimeMillis(),
                takenDosesCsv = currentTaken.sorted().joinToString(",")
            )
            repository.updateMedicine(updated)
        }
    }

    fun deleteMedicine(id: Long) {
        viewModelScope.launch {
            NotificationHelper.cancelAllMedicineDoses(getApplication(), id)
            repository.deleteMedicine(id)
        }
    }

    fun consumeOnePad() {
        viewModelScope.launch {
            val currentStock = repository.getPadStockOnce()?.totalAvailable ?: 0
            if (currentStock <= 0) return@launch // Prevent negative stock

            val remaining = repository.consumePad()
            val summary = uiState.value.cycleSummary
            val isBangla = uiState.value.language == AppLanguage.BANGLA
            if (summary != null) {
                NotificationHelper.checkAndSchedulePadAlert(
                    context = getApplication(),
                    daysUntilPeriod = summary.daysUntilNextPeriod,
                    remainingPads = remaining,
                    isBangla = isBangla
                )
            }
        }
    }

    fun restockPads(addedCount: Int) {
        viewModelScope.launch {
            repository.restockPads(addedCount)
        }
    }

    fun updatePadStock(newCount: Int) {
        viewModelScope.launch {
            repository.savePadStock(
                PadStock(
                    id = 1,
                    totalAvailable = newCount.coerceAtLeast(0),
                    lastUpdatedMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun toggleBiometric(enabled: Boolean, pinCode: String? = null) {
        viewModelScope.launch {
            val current = repository.getUserProfileOnce() ?: UserProfile()
            repository.saveUserProfile(current.copy(isBiometricEnabled = enabled, pinCode = pinCode))
            if (!enabled) {
                _isAppLocked.value = false
            }
        }
    }

    // ==========================================
    // 🔔 TEST EVERY REQUIRED LOCAL NOTIFICATION
    // ==========================================
    fun testMedicineReminder() {
        val isBn = uiState.value.language == AppLanguage.BANGLA
        val title = if (isBn) "💊 ওষুধ খাওয়ার সময় হয়েছে: প্যারাসিটামল" else "💊 Medicine Reminder: Paracetamol"
        val msg = if (isBn) "৫০০ মিগ্রা ট্যাবলেট গ্রহণের সময় হয়েছে। সুস্থ থাকুন!" else "It's time to take your scheduled dose (500mg). Stay healthy!"
        NotificationHelper.showNotificationNow(
            context = getApplication(),
            notificationId = NotificationHelper.ID_TEST_MEDICINE,
            channelId = NotificationHelper.CHANNEL_MEDICINE,
            title = title,
            message = msg,
            type = NotificationHelper.TYPE_MEDICINE
        )
    }

    fun testPeriodReminder() {
        val isBn = uiState.value.language == AppLanguage.BANGLA
        val title = if (isBn) "🩸 পিরিয়ডের পূর্বাভাস রিমাইন্ডার" else "🩸 Upcoming Period Reminder"
        val msg = if (isBn) "আপনার পিরিয়ড আনুমানিক ২ দিনের মধ্যে শুরু হতে পারে। প্যাড মজুত রাখুন।" else "Your period is expected in ~2 days. Keep pads ready and stay hydrated."
        NotificationHelper.showNotificationNow(
            context = getApplication(),
            notificationId = NotificationHelper.ID_TEST_PERIOD,
            channelId = NotificationHelper.CHANNEL_PERIOD,
            title = title,
            message = msg,
            type = NotificationHelper.TYPE_PERIOD
        )
    }

    fun testIrregularCycleWarning() {
        val isBn = uiState.value.language == AppLanguage.BANGLA
        val title = if (isBn) "🔴 অনিয়মিত সাইকেল সতর্কতা" else "🔴 Irregular Cycle Pattern Alert"
        val msg = if (isBn) "সাম্প্রতিক সাইকেলে অস্বাভাবিক তারতম্য দেখা গেছে। এই ধারা বজায় থাকলে চিকিৎসকের পরামর্শ নিন।" else "Unusual cycle variation detected. If this pattern continues, consult a healthcare provider."
        NotificationHelper.showNotificationNow(
            context = getApplication(),
            notificationId = NotificationHelper.ID_TEST_IRREGULAR,
            channelId = NotificationHelper.CHANNEL_IRREGULARITY,
            title = title,
            message = msg,
            type = NotificationHelper.TYPE_IRREGULAR
        )
    }

    fun testPadPurchaseReminder() {
        val isBn = uiState.value.language == AppLanguage.BANGLA
        val title = if (isBn) "🧻 প্যাড শেষ হওয়ার সতর্কতা" else "🧻 Low Pad Stock Alert"
        val msg = if (isBn) "পিরিয়ড ২ দিনের মধ্যে এবং মাত্র ৩টি প্যাড অবশিষ্ট আছে। শীঘ্রই প্যাড কিনে রাখুন।" else "Period expected in 2 days and only 3 pads left. Please purchase pads soon!"
        NotificationHelper.showNotificationNow(
            context = getApplication(),
            notificationId = NotificationHelper.ID_TEST_PAD,
            channelId = NotificationHelper.CHANNEL_PAD,
            title = title,
            message = msg,
            type = NotificationHelper.TYPE_PAD
        )
    }

    fun testHealthWarning() {
        val isBn = uiState.value.language == AppLanguage.BANGLA
        val title = if (isBn) "⚠️ স্বাস্থ্য পরামর্শ ও গাইড" else "⚠️ Health Advisory & Warning"
        val msg = if (isBn) "তীব্র পেলভিক ব্যথা বা অস্বাভাবিক স্রাব থাকলে অবিলম্বে বিশেষজ্ঞ ডাক্তারের শরণাপন্ন হোন।" else "Persistent pelvic pain or concerning discharge warrants professional evaluation. Consult a doctor."
        NotificationHelper.showNotificationNow(
            context = getApplication(),
            notificationId = NotificationHelper.ID_TEST_HEALTH,
            channelId = NotificationHelper.CHANNEL_HEALTH,
            title = title,
            message = msg,
            type = NotificationHelper.TYPE_HEALTH
        )
    }
}
