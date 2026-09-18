package com.example.domain

import com.example.data.model.DailyLog
import com.example.data.model.PeriodEntry
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class CyclePhase {
    MENSTRUAL,
    FOLLICULAR,
    FERTILE_OVULATION,
    LUTEAL
}

enum class IrregularitySeverity {
    NONE,
    CAUTION,
    RED_WARNING
}

data class CycleWarning(
    val severity: IrregularitySeverity,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val doctorAdvisoryEn: String,
    val doctorAdvisoryBn: String,
    val isRedWarning: Boolean
)

data class CycleSummary(
    val currentCycleDay: Int,
    val daysUntilNextPeriod: Int,
    val nextPeriodDateMillis: Long,
    val currentPhase: CyclePhase,
    val estimatedOvulationDateMillis: Long,
    val fertileWindowStartMillis: Long,
    val fertileWindowEndMillis: Long,
    val averageCycleLength: Int,
    val averagePeriodDuration: Int,
    val activeWarning: CycleWarning?,
    val isPeriodCurrentlyActive: Boolean,
    val shouldWarnPadStock: Boolean
)

object CycleCalculator {

    const val DEFAULT_CYCLE_LENGTH = 28
    const val DEFAULT_PERIOD_DURATION = 5
    const val MIN_NORMAL_CYCLE = 21
    const val MAX_NORMAL_CYCLE = 35
    const val MIN_NORMAL_PERIOD = 2
    const val MAX_NORMAL_PERIOD = 7

    fun calculateCycleSummary(
        periods: List<PeriodEntry>,
        fallbackCycleLength: Int = DEFAULT_CYCLE_LENGTH,
        fallbackPeriodDuration: Int = DEFAULT_PERIOD_DURATION,
        recentDailyLogs: List<DailyLog> = emptyList(),
        currentPadsCount: Int = 10,
        currentDateMillis: Long = System.currentTimeMillis()
    ): CycleSummary {
        val sortedPeriods = periods.sortedByDescending { it.startDateMillis }
        val latestPeriod = sortedPeriods.firstOrNull()

        if (latestPeriod == null) {
            // No periods logged yet
            val nextPeriod = currentDateMillis + TimeUnit.DAYS.toMillis(fallbackCycleLength.toLong())
            return CycleSummary(
                currentCycleDay = 1,
                daysUntilNextPeriod = fallbackCycleLength,
                nextPeriodDateMillis = nextPeriod,
                currentPhase = CyclePhase.FOLLICULAR,
                estimatedOvulationDateMillis = nextPeriod - TimeUnit.DAYS.toMillis(14),
                fertileWindowStartMillis = nextPeriod - TimeUnit.DAYS.toMillis(19),
                fertileWindowEndMillis = nextPeriod - TimeUnit.DAYS.toMillis(13),
                averageCycleLength = fallbackCycleLength,
                averagePeriodDuration = fallbackPeriodDuration,
                activeWarning = null,
                isPeriodCurrentlyActive = false,
                shouldWarnPadStock = false
            )
        }

        // Calculate average cycle length from consecutive logged periods if >= 2 periods exist
        val cycleLengths = mutableListOf<Int>()
        for (i in 0 until sortedPeriods.size - 1) {
            val diffMillis = sortedPeriods[i].startDateMillis - sortedPeriods[i + 1].startDateMillis
            val days = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
            if (days in 10..90) {
                cycleLengths.add(days)
            }
        }
        val avgCycleLength = if (cycleLengths.isNotEmpty()) {
            cycleLengths.average().toInt().coerceIn(18, 60)
        } else {
            fallbackCycleLength.coerceIn(21, 35)
        }

        // Calculate average period duration
        val periodDurations = sortedPeriods.mapNotNull { period ->
            period.endDateMillis?.let { end ->
                val days = (TimeUnit.MILLISECONDS.toDays(end - period.startDateMillis) + 1).toInt()
                if (days in 1..20) days else null
            }
        }
        val avgPeriodDuration = if (periodDurations.isNotEmpty()) {
            periodDurations.average().toInt().coerceIn(2, 10)
        } else {
            fallbackPeriodDuration.coerceIn(2, 7)
        }

        // Calculate current cycle day
        val daysSinceStart = TimeUnit.MILLISECONDS.toDays(currentDateMillis - latestPeriod.startDateMillis).toInt()
        val currentCycleDay = (daysSinceStart + 1).coerceAtLeast(1)

        // Expected next period
        val nextPeriodDateMillis = latestPeriod.startDateMillis + TimeUnit.DAYS.toMillis(avgCycleLength.toLong())
        val daysUntilNext = TimeUnit.MILLISECONDS.toDays(nextPeriodDateMillis - currentDateMillis).toInt()

        // Ovulation & fertile window calculation (standard clinical formula: Ovulation occurs ~14 days before next cycle start)
        val estimatedOvulationMillis = nextPeriodDateMillis - TimeUnit.DAYS.toMillis(14)
        val fertileStartMillis = estimatedOvulationMillis - TimeUnit.DAYS.toMillis(5)
        val fertileEndMillis = estimatedOvulationMillis + TimeUnit.DAYS.toMillis(1)

        // Check if period is currently active
        val isPeriodActive = latestPeriod.endDateMillis == null && currentCycleDay <= avgPeriodDuration

        // Determine current cycle phase
        val currentPhase = when {
            isPeriodActive || currentCycleDay <= avgPeriodDuration -> CyclePhase.MENSTRUAL
            currentDateMillis in fertileStartMillis..fertileEndMillis -> CyclePhase.FERTILE_OVULATION
            currentDateMillis < fertileStartMillis -> CyclePhase.FOLLICULAR
            else -> CyclePhase.LUTEAL
        }

        // Evaluate Irregular Cycle Warning
        val warning = evaluateCycleIrregularity(
            cycleLengths = cycleLengths,
            latestCycleLength = if (cycleLengths.isNotEmpty()) cycleLengths.first() else avgCycleLength,
            latestPeriodDuration = periodDurations.firstOrNull() ?: avgPeriodDuration,
            recentLogs = recentDailyLogs
        )

        // Check pad stock rule: expected period <= 2 days away AND remaining pads <= 3
        val shouldWarnPadStock = daysUntilNext <= 2 && currentPadsCount <= 3

        return CycleSummary(
            currentCycleDay = currentCycleDay,
            daysUntilNextPeriod = daysUntilNext,
            nextPeriodDateMillis = nextPeriodDateMillis,
            currentPhase = currentPhase,
            estimatedOvulationDateMillis = estimatedOvulationMillis,
            fertileWindowStartMillis = fertileStartMillis,
            fertileWindowEndMillis = fertileEndMillis,
            averageCycleLength = avgCycleLength,
            averagePeriodDuration = avgPeriodDuration,
            activeWarning = warning,
            isPeriodCurrentlyActive = isPeriodActive,
            shouldWarnPadStock = shouldWarnPadStock
        )
    }

    private fun evaluateCycleIrregularity(
        cycleLengths: List<Int>,
        latestCycleLength: Int,
        latestPeriodDuration: Int,
        recentLogs: List<DailyLog>
    ): CycleWarning? {
        val disclaimerEn = "Note: HerCare provides cycle observation patterns, not a medical diagnosis. Always consult a healthcare professional."
        val disclaimerBn = "সতর্কতা: HerCare সাইকেল পর্যবেক্ষণ পরিসংখ্যান প্রদান করে, কোনো রোগ নির্ণয় করে না। প্রয়োজনে চিকিৎসকের পরামর্শ নিন।"

        // 1. Prolonged bleeding (> 7 days)
        if (latestPeriodDuration > MAX_NORMAL_PERIOD) {
            return CycleWarning(
                severity = IrregularitySeverity.RED_WARNING,
                titleEn = "🔴 Prolonged Period Bleeding Warning",
                titleBn = "🔴 দীর্ঘস্থায়ী পিরিয়ড রক্তস্রাব সতর্কতা",
                descriptionEn = "Your period bleeding has lasted $latestPeriodDuration days. A typical menstrual period lasts 2 to 7 days.",
                descriptionBn = "আপনার পিরিয়ড $latestPeriodDuration দিন ধরে চলছে। সাধারণত একটি স্বাভাবিক পিরিয়ড ২ থেকে ৭ দিন স্থায়ী হয়।",
                doctorAdvisoryEn = "Bleeding extending past 7 days warrants a medical check-up. Please consult a doctor or gynecologist. $disclaimerEn",
                doctorAdvisoryBn = "৭ দিনের অধিক রক্তপাত হলে চিকিৎসকের পরামর্শ নেওয়া আবশ্যক। অনুগ্রহ করে একজন স্ত্রীরোগ বিশেষজ্ঞ দেখান। $disclaimerBn",
                isRedWarning = true
            )
        }

        // 2. Short cycle (< 21 days)
        if (latestCycleLength < MIN_NORMAL_CYCLE && latestCycleLength > 5) {
            return CycleWarning(
                severity = IrregularitySeverity.RED_WARNING,
                titleEn = "🔴 Frequent / Short Cycle Warning",
                titleBn = "🔴 ঘন ঘন / স্বল্পমেয়াদী সাইকেল সতর্কতা",
                descriptionEn = "Your cycle length is $latestCycleLength days, which is shorter than the standard 21–35 day medical range.",
                descriptionBn = "আপনার সাইকেলের দৈর্ঘ্য $latestCycleLength দিন, যা স্বাভাবিক ২১–৩৫ দিনের তুলনায় উল্লেখযোগ্যভাবে কম।",
                doctorAdvisoryEn = "Repeated cycles under 21 days (polymenorrhea pattern) can lead to fatigue or anemia. If this pattern continues, please consult a healthcare professional. $disclaimerEn",
                doctorAdvisoryBn = "বারবার ২১ দিনের কম সময়ে পিরিয়ড হলে রক্তস্বল্পতা হতে পারে। এটি অব্যাহত থাকলে চিকিৎসকের শরণাপন্ন হোন। $disclaimerBn",
                isRedWarning = true
            )
        }

        // 3. Long cycle (> 35 days)
        if (latestCycleLength > MAX_NORMAL_CYCLE) {
            return CycleWarning(
                severity = IrregularitySeverity.RED_WARNING,
                titleEn = "🔴 Infrequent / Long Cycle Warning",
                titleBn = "🔴 বিলম্বিত / দীর্ঘমেয়াদী সাইকেল সতর্কতা",
                descriptionEn = "Your cycle length is $latestCycleLength days, which exceeds the typical 21–35 day physiological range.",
                descriptionBn = "আপনার সাইকেলের দৈর্ঘ্য $latestCycleLength দিন, যা স্বাভাবিক ২১–৩৫ দিনের সর্বোচ্চ সীমার বেশি।",
                doctorAdvisoryEn = "Cycles repeatedly extending beyond 35 days (oligomenorrhea pattern) may indicate hormonal imbalances. Please consult a doctor if this pattern persists. $disclaimerEn",
                doctorAdvisoryBn = "৩৫ দিনের বেশি সময় পর পর পিরিয়ড হওয়া হরমোনজনিত পরিবর্তনের লক্ষণ হতে পারে। এটি বজায় থাকলে চিকিৎসকের পরামর্শ নিন। $disclaimerBn",
                isRedWarning = true
            )
        }

        // 4. Repeated high variation across consecutive cycles (> 8 days difference)
        if (cycleLengths.size >= 2) {
            val maxCycle = cycleLengths.maxOrNull() ?: 0
            val minCycle = cycleLengths.minOrNull() ?: 0
            val variation = maxCycle - minCycle
            if (variation >= 8) {
                return CycleWarning(
                    severity = IrregularitySeverity.RED_WARNING,
                    titleEn = "🔴 High Cycle Variability Warning",
                    titleBn = "🔴 সাইকেলের তারতম্য ও অনিয়ম সতর্কতা",
                    descriptionEn = "Significant variation of $variation days detected between your recorded cycles.",
                    descriptionBn = "আপনার রেকর্ডকৃত সাইকেলগুলোর মধ্যে $variation দিনের উল্লেখযোগ্য তারতম্য পরিলক্ষিত হয়েছে।",
                    doctorAdvisoryEn = "Frequent large shifts between cycle lengths indicate an irregular pattern. If this continues, consult your doctor. $disclaimerEn",
                    doctorAdvisoryBn = "সাইকেলের দৈর্ঘ্যে বড় ধরনের অসামঞ্জস্য অনিয়মিততার প্রমাণ। এটি স্থায়ী হলে চিকিৎসকের পরামর্শ নেওয়া উচিত। $disclaimerBn",
                    isRedWarning = true
                )
            }
        }

        // 5. Consecutive very heavy flow days
        val recentVeryHeavyCount = recentLogs.take(5).count { it.flowLevel == "VERY_HEAVY" }
        if (recentVeryHeavyCount >= 2) {
            return CycleWarning(
                severity = IrregularitySeverity.RED_WARNING,
                titleEn = "🔴 Heavy Flow Alert",
                titleBn = "🔴 অতিরিক্ত রক্তক্ষরণ সতর্কতা",
                descriptionEn = "You have recorded multiple consecutive days with very heavy flow.",
                descriptionBn = "আপনি টানা একাধিক দিন অতি ভারী রক্তপ্রবাহের তথ্য নথিভুক্ত করেছেন।",
                doctorAdvisoryEn = "Persistently very heavy bleeding soaking through pads rapidly can impact iron levels. Please consult a doctor if accompanied by dizziness or severe cramps. $disclaimerEn",
                doctorAdvisoryBn = "টানা খুব ভারী রক্তস্রাব শরীরে আয়রনের ঘাটতি ঘটাতে পারে। দুর্বলতা বা তীব্র ব্যথা থাকলে দ্রুত ডাক্তারের পরামর্শ নিন। $disclaimerBn",
                isRedWarning = true
            )
        }

        return null
    }

    fun getStartOfDayMillis(millis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
