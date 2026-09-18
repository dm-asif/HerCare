package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HerCareNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, 100)
        val channelId = intent.getStringExtra(NotificationHelper.EXTRA_CHANNEL_ID) ?: NotificationHelper.CHANNEL_HEALTH
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: "HerCare"
        val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE) ?: "You have a health reminder."
        val type = intent.getStringExtra(NotificationHelper.EXTRA_TYPE) ?: NotificationHelper.TYPE_HEALTH

        NotificationHelper.showNotificationNow(
            context = context,
            notificationId = notificationId,
            channelId = channelId,
            title = title,
            message = message,
            type = type
        )
    }
}
