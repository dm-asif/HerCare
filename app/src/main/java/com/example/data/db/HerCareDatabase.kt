package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DailyLog
import com.example.data.model.MedicineEntry
import com.example.data.model.PadStock
import com.example.data.model.PeriodEntry
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        PeriodEntry::class,
        DailyLog::class,
        MedicineEntry::class,
        PadStock::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HerCareDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun periodDao(): PeriodDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun medicineDao(): MedicineDao
    abstract fun padStockDao(): PadStockDao

    companion object {
        @Volatile
        private var INSTANCE: HerCareDatabase? = null

        fun getInstance(context: Context): HerCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HerCareDatabase::class.java,
                    "hercare_local_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
