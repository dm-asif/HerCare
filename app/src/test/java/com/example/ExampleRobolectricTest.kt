package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PeriodEntry
import com.example.domain.CycleCalculator
import com.example.domain.IrregularitySeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies HerCare app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HerCare", appName)
    }

    @Test
    fun `cycle calculator detects normal cycle`() {
        val now = System.currentTimeMillis()
        val periods = listOf(
            PeriodEntry(startDateMillis = now - TimeUnit.DAYS.toMillis(14), endDateMillis = now - TimeUnit.DAYS.toMillis(9))
        )
        val summary = CycleCalculator.calculateCycleSummary(
            periods = periods,
            fallbackCycleLength = 28,
            fallbackPeriodDuration = 5,
            currentDateMillis = now
        )
        assertEquals(15, summary.currentCycleDay)
        assertEquals(14, summary.daysUntilNextPeriod)
        assertEquals(null, summary.activeWarning)
    }

    @Test
    fun `cycle calculator detects prolonged bleeding irregularity warning`() {
        val now = System.currentTimeMillis()
        val periods = listOf(
            PeriodEntry(
                startDateMillis = now - TimeUnit.DAYS.toMillis(10),
                endDateMillis = now - TimeUnit.DAYS.toMillis(1) // 10 days of bleeding (> 7)
            )
        )
        val summary = CycleCalculator.calculateCycleSummary(
            periods = periods,
            currentDateMillis = now
        )
        assertNotNull(summary.activeWarning)
        assertEquals(IrregularitySeverity.RED_WARNING, summary.activeWarning?.severity)
    }

    @Test
    fun `medicine multi-dose scheduling parses times and tracks doses`() {
        val now = System.currentTimeMillis()
        val todayStart = CycleCalculator.getStartOfDayMillis(now)
        val med = com.example.data.model.MedicineEntry(
            name = "Iron Supplement",
            dose = "1 Tablet",
            frequencyTimesPerDay = 3,
            scheduledTimesCsv = "08:00,14:00,20:00",
            mealTiming = "AFTER_MEAL",
            lastTakenDateMillis = now,
            takenDosesCsv = "0,2"
        )
        val times = med.getScheduledTimesList()
        assertEquals(3, times.size)
        assertEquals("08:00", times[0])
        assertEquals("14:00", times[1])
        assertEquals("20:00", times[2])

        assertEquals(true, med.isDoseTakenToday(0, todayStart))
        assertEquals(false, med.isDoseTakenToday(1, todayStart))
        assertEquals(true, med.isDoseTakenToday(2, todayStart))
        assertEquals(false, med.isFullyTakenToday(todayStart))
    }

    @Test
    fun `pad stock zero and restock functionality`() {
        val emptyStock = com.example.data.model.PadStock(totalAvailable = 0)
        assertEquals(0, emptyStock.totalAvailable)
        val restocked = emptyStock.copy(totalAvailable = emptyStock.totalAvailable + 10)
        assertEquals(10, restocked.totalAvailable)
    }
}
