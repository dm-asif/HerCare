package com.example.data.repository

import com.example.data.db.HerCareDatabase
import com.example.data.model.DailyLog
import com.example.data.model.MedicineEntry
import com.example.data.model.PadStock
import com.example.data.model.PeriodEntry
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class HerCareRepository(private val db: HerCareDatabase) {

    // User Profile
    val userProfile: Flow<UserProfile?> = db.userProfileDao().getUserProfile()
    suspend fun getUserProfileOnce(): UserProfile? = db.userProfileDao().getUserProfileOnce()
    suspend fun saveUserProfile(profile: UserProfile) = db.userProfileDao().insertOrUpdate(profile)

    // Period Entries
    val allPeriods: Flow<List<PeriodEntry>> = db.periodDao().getAllPeriods()
    val latestPeriod: Flow<PeriodEntry?> = db.periodDao().getLatestPeriod()
    suspend fun getLatestPeriodOnce(): PeriodEntry? = db.periodDao().getLatestPeriodOnce()
    suspend fun getAllPeriodsList(): List<PeriodEntry> = db.periodDao().getAllPeriodsList()
    suspend fun addPeriod(period: PeriodEntry): Long = db.periodDao().insertPeriod(period)
    suspend fun updatePeriod(period: PeriodEntry) = db.periodDao().updatePeriod(period)
    suspend fun deletePeriod(id: Long) = db.periodDao().deletePeriod(id)

    // Daily Logs
    fun getLogForDate(dateMillis: Long): Flow<DailyLog?> = db.dailyLogDao().getLogForDate(dateMillis)
    suspend fun getLogForDateOnce(dateMillis: Long): DailyLog? = db.dailyLogDao().getLogForDateOnce(dateMillis)
    val allDailyLogs: Flow<List<DailyLog>> = db.dailyLogDao().getAllLogs()
    suspend fun saveDailyLog(log: DailyLog) = db.dailyLogDao().insertOrUpdateLog(log)

    // Medicine Entries
    val allMedicines: Flow<List<MedicineEntry>> = db.medicineDao().getAllMedicines()
    val activeMedicines: Flow<List<MedicineEntry>> = db.medicineDao().getActiveMedicines()
    suspend fun getActiveMedicinesList(): List<MedicineEntry> = db.medicineDao().getActiveMedicinesList()
    suspend fun addMedicine(medicine: MedicineEntry): Long = db.medicineDao().insertMedicine(medicine)
    suspend fun updateMedicine(medicine: MedicineEntry) = db.medicineDao().updateMedicine(medicine)
    suspend fun deleteMedicine(id: Long) = db.medicineDao().deleteMedicine(id)

    // Pad Stock
    val padStock: Flow<PadStock?> = db.padStockDao().getPadStock()
    suspend fun getPadStockOnce(): PadStock? = db.padStockDao().getPadStockOnce()
    suspend fun savePadStock(stock: PadStock) = db.padStockDao().insertOrUpdate(stock)

    suspend fun consumePad(): Int {
        val current = getPadStockOnce() ?: PadStock(totalAvailable = 15)
        val updatedCount = (current.totalAvailable - 1).coerceAtLeast(0)
        savePadStock(current.copy(totalAvailable = updatedCount, lastUpdatedMillis = System.currentTimeMillis()))
        return updatedCount
    }

    suspend fun restockPads(addedCount: Int): Int {
        val current = getPadStockOnce() ?: PadStock(totalAvailable = 0)
        val updatedCount = (current.totalAvailable + addedCount).coerceAtLeast(0)
        savePadStock(current.copy(totalAvailable = updatedCount, lastUpdatedMillis = System.currentTimeMillis()))
        return updatedCount
    }
}
