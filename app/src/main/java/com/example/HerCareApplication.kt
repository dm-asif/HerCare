package com.example

import android.app.Application
import com.example.data.db.HerCareDatabase
import com.example.data.repository.HerCareRepository
import com.example.notifications.NotificationHelper

class HerCareApplication : Application() {

    lateinit var database: HerCareDatabase
        private set

    lateinit var repository: HerCareRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = HerCareDatabase.getInstance(this)
        repository = HerCareRepository(database)

        // Initialize local notification channels immediately
        NotificationHelper.createNotificationChannels(this)
    }
}
