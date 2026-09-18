package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val birthDateMillis: Long = 0L,
    val cycleLengthDays: Int = 28,
    val periodDurationDays: Int = 5,
    val isBiometricEnabled: Boolean = false,
    val pinCode: String? = null,
    val language: String = "en",
    val onboardingCompleted: Boolean = false
)

@Entity(tableName = "period_entries")
data class PeriodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDateMillis: Long,
    val endDateMillis: Long? = null, // null if currently active
    val flowIntensity: String = "MEDIUM", // LIGHT, MEDIUM, HEAVY, VERY_HEAVY
    val notes: String = ""
)

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey val dateMillis: Long, // Start of day timestamp
    val flowLevel: String = "NONE", // NONE, LIGHT, MEDIUM, HEAVY, VERY_HEAVY
    val painLevel: String = "NONE", // NONE, MILD, MODERATE, SEVERE
    val symptomsCsv: String = "", // Comma-separated: CRAMPS,HEADACHE,BLOATING,etc.
    val padsUsed: Int = 0,
    val dischargeType: String = "NONE", // NONE, CLEAR_WATERY, WHITE_STICKY, EGG_WHITE, YELLOW_GREEN, GRAY_FROTHY, BROWN_SPOTTING
    val notes: String = ""
)

@Entity(tableName = "medicines")
data class MedicineEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dose: String,
    val hour: Int = 8,
    val minute: Int = 0,
    val frequency: String = "1", // "1", "2", "3", "4", "AS_NEEDED"
    val frequencyTimesPerDay: Int = 1, // 1 to 4
    val scheduledTimesCsv: String = "08:00", // comma-separated HH:mm, e.g. "08:00,20:00"
    val mealTiming: String = "AFTER_MEAL", // BEFORE_MEAL, AFTER_MEAL, WITH_MEAL, ANY_TIME
    val startDateMillis: Long = System.currentTimeMillis(),
    val endDateMillis: Long? = null,
    val isActive: Boolean = true,
    val lastTakenDateMillis: Long? = null,
    val takenDosesCsv: String = "" // e.g. "0,1" for taken dose indices today
) {
    fun getScheduledTimesList(): List<String> {
        if (scheduledTimesCsv.isNotBlank()) {
            return scheduledTimesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
        return listOf(String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute))
    }

    fun isDoseTakenToday(doseIndex: Int, todayStartMillis: Long): Boolean {
        if (lastTakenDateMillis == null || lastTakenDateMillis < todayStartMillis) return false
        val indices = takenDosesCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
        return indices.contains(doseIndex)
    }

    fun isDoseTakenToday(todayStartMillis: Long, doseIndex: Int): Boolean {
        return isDoseTakenToday(doseIndex, todayStartMillis)
    }

    fun isFullyTakenToday(todayStartMillis: Long): Boolean {
        val times = getScheduledTimesList()
        if (times.isEmpty()) return false
        return times.indices.all { isDoseTakenToday(it, todayStartMillis) }
    }
}

@Entity(tableName = "pad_stock")
data class PadStock(
    @PrimaryKey val id: Int = 1,
    val totalAvailable: Int = 15,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)
