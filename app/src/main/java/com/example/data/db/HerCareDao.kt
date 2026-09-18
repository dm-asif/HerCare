package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyLog
import com.example.data.model.MedicineEntry
import com.example.data.model.PadStock
import com.example.data.model.PeriodEntry
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)
}

@Dao
interface PeriodDao {
    @Query("SELECT * FROM period_entries ORDER BY startDateMillis DESC")
    fun getAllPeriods(): Flow<List<PeriodEntry>>

    @Query("SELECT * FROM period_entries ORDER BY startDateMillis DESC LIMIT 1")
    fun getLatestPeriod(): Flow<PeriodEntry?>

    @Query("SELECT * FROM period_entries ORDER BY startDateMillis DESC LIMIT 1")
    suspend fun getLatestPeriodOnce(): PeriodEntry?

    @Query("SELECT * FROM period_entries ORDER BY startDateMillis DESC")
    suspend fun getAllPeriodsList(): List<PeriodEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodEntry): Long

    @Update
    suspend fun updatePeriod(period: PeriodEntry)

    @Query("DELETE FROM period_entries WHERE id = :id")
    suspend fun deletePeriod(id: Long)
}

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE dateMillis = :dateMillis")
    fun getLogForDate(dateMillis: Long): Flow<DailyLog?>

    @Query("SELECT * FROM daily_logs WHERE dateMillis = :dateMillis")
    suspend fun getLogForDateOnce(dateMillis: Long): DailyLog?

    @Query("SELECT * FROM daily_logs ORDER BY dateMillis DESC")
    fun getAllLogs(): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis ORDER BY dateMillis ASC")
    fun getLogsBetween(startMillis: Long, endMillis: Long): Flow<List<DailyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: DailyLog)
}

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY hour ASC, minute ASC")
    fun getAllMedicines(): Flow<List<MedicineEntry>>

    @Query("SELECT * FROM medicines WHERE isActive = 1 ORDER BY hour ASC, minute ASC")
    fun getActiveMedicines(): Flow<List<MedicineEntry>>

    @Query("SELECT * FROM medicines WHERE isActive = 1")
    suspend fun getActiveMedicinesList(): List<MedicineEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntry): Long

    @Update
    suspend fun updateMedicine(medicine: MedicineEntry)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicine(id: Long)
}

@Dao
interface PadStockDao {
    @Query("SELECT * FROM pad_stock WHERE id = 1")
    fun getPadStock(): Flow<PadStock?>

    @Query("SELECT * FROM pad_stock WHERE id = 1")
    suspend fun getPadStockOnce(): PadStock?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(padStock: PadStock)
}
