package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.db.HerCareDatabase
import com.example.data.repository.HerCareRepository
import com.example.domain.CycleCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HerCareBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            // Reschedule active medicine alarms and cycle reminders
            val pendingResult = goAsync()
            val repository = HerCareRepository(HerCareDatabase.getInstance(context))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val profile = repository.getUserProfileOnce()
                    val isBangla = profile?.language == "bn"

                    // Reschedule active medicines with all their scheduled dose times
                    val activeMeds = repository.getActiveMedicinesList()
                    activeMeds.forEach { med ->
                        NotificationHelper.scheduleAllMedicineDoses(
                            context = context,
                            medicineId = med.id,
                            medicineName = med.name,
                            dose = med.dose,
                            mealTiming = med.mealTiming,
                            times = med.getScheduledTimesList(),
                            isBangla = isBangla
                        )
                    }

                    // Reschedule upcoming period reminder
                    val periods = repository.getAllPeriodsList()
                    if (periods.isNotEmpty()) {
                        val summary = CycleCalculator.calculateCycleSummary(
                            periods = periods,
                            fallbackCycleLength = profile?.cycleLengthDays ?: 28,
                            fallbackPeriodDuration = profile?.periodDurationDays ?: 5
                        )
                        NotificationHelper.schedulePeriodReminder(
                            context = context,
                            nextPeriodMillis = summary.nextPeriodDateMillis,
                            isBangla = isBangla
                        )
                    }
                } catch (_: Exception) {
                    // Fail-safe handling
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
