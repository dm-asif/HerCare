package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object NotificationHelper {

    const val CHANNEL_MEDICINE = "hercare_medicine_channel"
    const val CHANNEL_PERIOD = "hercare_period_channel"
    const val CHANNEL_IRREGULARITY = "hercare_irregularity_channel"
    const val CHANNEL_PAD = "hercare_pad_channel"
    const val CHANNEL_HEALTH = "hercare_health_channel"

    const val ACTION_SHOW_NOTIFICATION = "com.aistudio.hercare.ACTION_SHOW_NOTIFICATION"

    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val EXTRA_CHANNEL_ID = "extra_channel_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_TYPE = "extra_type"

    const val TYPE_MEDICINE = "MEDICINE"
    const val TYPE_PERIOD = "PERIOD"
    const val TYPE_IRREGULAR = "IRREGULAR"
    const val TYPE_PAD = "PAD"
    const val TYPE_HEALTH = "HEALTH"

    const val ID_TEST_MEDICINE = 9001
    const val ID_TEST_PERIOD = 9002
    const val ID_TEST_IRREGULAR = 9003
    const val ID_TEST_PAD = 9004
    const val ID_TEST_HEALTH = 9005

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_MEDICINE,
                    "💊 HerCare Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders to take scheduled medicines according to your personal routine."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                },
                NotificationChannel(
                    CHANNEL_PERIOD,
                    "🩸 HerCare Period & Cycle Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Upcoming period arrival alerts and cycle phase updates."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 150, 250)
                },
                NotificationChannel(
                    CHANNEL_IRREGULARITY,
                    "🔴 Irregular Cycle & Health Warnings",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Clinically grounded cycle irregularity warnings and doctor referral notices."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 400, 200, 400)
                },
                NotificationChannel(
                    CHANNEL_PAD,
                    "🧻 Pad Stock Low Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts to restock sanitary pads before your expected period starts."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 200, 100, 200)
                },
                NotificationChannel(
                    CHANNEL_HEALTH,
                    "⚠️ Health Warnings & Medical Guides",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Educational health advice, symptom warnings, and medical recommendations."
                    enableVibration(true)
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun showNotificationNow(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String,
        type: String = TYPE_HEALTH
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NOTIFICATION_TYPE", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Attempt to load official HerCare logo for large icon
        val largeIcon = try {
            BitmapFactory.decodeResource(context.resources, R.drawable.hercare_icon)
        } catch (_: Exception) {
            null
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.hercare_icon)
            .apply {
                if (largeIcon != null) setLargeIcon(largeIcon)
            }
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun scheduleExactAlarm(
        context: Context,
        requestCode: Int,
        triggerMillis: Long,
        channelId: String,
        title: String,
        message: String,
        type: String
    ) {
        if (triggerMillis <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, HerCareNotificationReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
            putExtra(EXTRA_NOTIFICATION_ID, requestCode)
            putExtra(EXTRA_CHANNEL_ID, channelId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_TYPE, type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // On Android 12+ (API 31+), verify exact alarm capability
                if (alarmManager.canScheduleExactAlarms()) {
                    val showIntent = Intent(context, MainActivity::class.java)
                    val showPendingIntent = PendingIntent.getActivity(
                        context,
                        requestCode,
                        showIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // On Android 10 (API 29) & Android 11 (API 30), setAlarmClock works reliably without special permissions
                val showIntent = Intent(context, MainActivity::class.java)
                val showPendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    showIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            // Fallback if exact alarm permission is restricted
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HerCareNotificationReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleMedicineDoseAlarm(
        context: Context,
        medicineId: Long,
        doseIndex: Int,
        medicineName: String,
        dose: String,
        mealTiming: String,
        hour: Int,
        minute: Int,
        isBangla: Boolean = false
    ) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val mealText = when (mealTiming.uppercase()) {
            "BEFORE_MEAL" -> if (isBangla) "খাবারের আগে" else "Before meal"
            "AFTER_MEAL" -> if (isBangla) "খাবারের পরে" else "After meal"
            "WITH_MEAL" -> if (isBangla) "খাবারের সাথে" else "With meal"
            else -> ""
        }

        val timeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
        val title = if (isBangla) "💊 ওষুধ নেওয়ার সময়: $medicineName" else "💊 Medicine Reminder: $medicineName"
        val body = if (isBangla) {
            "ডোজ: $dose${if (mealText.isNotEmpty()) " ($mealText)" else ""} • সময়: $timeStr। নিয়মিত ওষুধ গ্রহণ আপনাকে সুস্থ রাখবে।"
        } else {
            "Dose: $dose${if (mealText.isNotEmpty()) " ($mealText)" else ""} • Time: $timeStr. Don't forget your scheduled medication!"
        }

        val requestCode = (10000 + (medicineId * 10) + doseIndex).toInt()

        scheduleExactAlarm(
            context = context,
            requestCode = requestCode,
            triggerMillis = cal.timeInMillis,
            channelId = CHANNEL_MEDICINE,
            title = title,
            message = body,
            type = TYPE_MEDICINE
        )
    }

    fun scheduleAllMedicineDoses(
        context: Context,
        medicineId: Long,
        medicineName: String,
        dose: String,
        mealTiming: String,
        times: List<String>,
        isBangla: Boolean = false
    ) {
        times.forEachIndexed { index, timeStr ->
            val parts = timeStr.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 8
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            scheduleMedicineDoseAlarm(
                context = context,
                medicineId = medicineId,
                doseIndex = index,
                medicineName = medicineName,
                dose = dose,
                mealTiming = mealTiming,
                hour = h,
                minute = m,
                isBangla = isBangla
            )
        }
    }

    fun cancelAllMedicineDoses(context: Context, medicineId: Long) {
        // Cancel all possible dose slots (0..9) plus legacy code
        for (i in 0..9) {
            cancelAlarm(context, (10000 + (medicineId * 10) + i).toInt())
        }
        cancelAlarm(context, (1000 + (medicineId % 10000)).toInt())
    }

    fun scheduleMedicineAlarm(
        context: Context,
        medicineId: Long,
        medicineName: String,
        dose: String,
        hour: Int,
        minute: Int,
        isBangla: Boolean = false
    ) {
        scheduleMedicineDoseAlarm(
            context = context,
            medicineId = medicineId,
            doseIndex = 0,
            medicineName = medicineName,
            dose = dose,
            mealTiming = "AFTER_MEAL",
            hour = hour,
            minute = minute,
            isBangla = isBangla
        )
    }

    fun schedulePeriodReminder(
        context: Context,
        nextPeriodMillis: Long,
        isBangla: Boolean = false
    ) {
        val twoDaysBefore = nextPeriodMillis - (2 * 24 * 60 * 60 * 1000L)
        val cal = Calendar.getInstance().apply {
            timeInMillis = twoDaysBefore
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        }

        val title = if (isBangla) "🩸 পিরিয়ডের পূর্বাভাস রিমাইন্ডার" else "🩸 Upcoming Period Reminder"
        val message = if (isBangla) {
            "আপনার পরবর্তী পিরিয়ড আনুমানিক ২ দিনের মধ্যে শুরু হতে পারে। প্রয়োজনীয় প্রস্তুতি ও প্যাড মজুত রাখুন।"
        } else {
            "Your next period is expected in approximately 2 days. Keep supplies ready and care for yourself."
        }

        if (cal.timeInMillis > System.currentTimeMillis()) {
            scheduleExactAlarm(
                context = context,
                requestCode = 2001,
                triggerMillis = cal.timeInMillis,
                channelId = CHANNEL_PERIOD,
                title = title,
                message = message,
                type = TYPE_PERIOD
            )
        }
    }

    fun checkAndSchedulePadAlert(
        context: Context,
        daysUntilPeriod: Int,
        remainingPads: Int,
        isBangla: Boolean = false
    ) {
        if (daysUntilPeriod in 0..2 && remainingPads <= 3) {
            val title = if (isBangla) "🧻 প্যাড মজুত শেষ হওয়ার সতর্কতা" else "🧻 Pad Stock Low Alert"
            val body = if (isBangla) {
                "আপনার পিরিয়ড আর $daysUntilPeriod দিন পর শুরু হতে পারে এবং মাত্র $remainingPads টি প্যাড রয়েছে। এখনই নতুন প্যাড সংগ্রহ করুন!"
            } else {
                "Period expected in $daysUntilPeriod days and you have only $remainingPads pads left. Please restock now!"
            }
            showNotificationNow(
                context = context,
                notificationId = 3001,
                channelId = CHANNEL_PAD,
                title = title,
                message = body,
                type = TYPE_PAD
            )
        }
    }

    fun triggerTestMedicine(context: Context, isBangla: Boolean) {
        val title = if (isBangla) "💊 ওষুধ রিমাইন্ডার: আয়রন ও ফলিক এসিড" else "💊 Medicine Reminder: Iron & Folic Acid"
        val body = if (isBangla) "নির্ধারিত ডোজ (১টি ট্যাবলেট, খাবারের পর) গ্রহণের সময় হয়েছে। সুস্থ থাকুন!" else "Scheduled dose (1 Tablet, after meal) is due now. Stay healthy!"
        showNotificationNow(context, ID_TEST_MEDICINE, CHANNEL_MEDICINE, title, body, TYPE_MEDICINE)
    }

    fun triggerTestPeriod(context: Context, isBangla: Boolean) {
        val title = if (isBangla) "🩸 পিরিয়ড পূর্বাভাস রিমাইন্ডার" else "🩸 Upcoming Period Reminder"
        val body = if (isBangla) "আপনার পিরিয়ড আগামী ২ দিনের মধ্যে শুরু হতে পারে। প্যাড মজুত রাখুন।" else "Your period is expected in ~2 days. Keep supplies ready!"
        showNotificationNow(context, ID_TEST_PERIOD, CHANNEL_PERIOD, title, body, TYPE_PERIOD)
    }

    fun triggerTestIrregular(context: Context, isBangla: Boolean) {
        val title = if (isBangla) "🔴 অনিয়মিত সাইকেল সতর্কতা" else "🔴 Irregular Cycle Warning"
        val body = if (isBangla) "সাম্প্রতিক সাইকেল স্বাভাবিক ২১–৩৫ দিনের পরিসীমার বাইরে। প্রয়োজনে চিকিৎসকের পরামর্শ নিন।" else "Cycle pattern outside 21–35 day normal range. Consult a healthcare provider if persistent."
        showNotificationNow(context, ID_TEST_IRREGULAR, CHANNEL_IRREGULARITY, title, body, TYPE_IRREGULAR)
    }

    fun triggerTestPad(context: Context, isBangla: Boolean) {
        val title = if (isBangla) "🧻 প্যাড শেষ হওয়ার সতর্কতা" else "🧻 Low Pad Stock Reminder"
        val body = if (isBangla) "আর মাত্র ২টি প্যাড রয়েছে এবং পিরিয়ড আসন্ন। দ্রুত নতুন প্যাড কিনে রাখুন!" else "Only 2 pads remaining with period approaching. Please restock soon!"
        showNotificationNow(context, ID_TEST_PAD, CHANNEL_PAD, title, body, TYPE_PAD)
    }

    fun triggerTestHealth(context: Context, isBangla: Boolean) {
        val title = if (isBangla) "⚠️ স্বাস্থ্য পরামর্শ সতর্কতা" else "⚠️ Health Advisory Warning"
        val body = if (isBangla) "অস্বাভাবিক স্রাব বা তীব্র পেলভিক ব্যথায় অবহেলা করবেন না। রেজিস্টার্ড চিকিৎসকের পরামর্শ নিন।" else "Unusual discharge or persistent pelvic pain should be evaluated by a healthcare professional."
        showNotificationNow(context, ID_TEST_HEALTH, CHANNEL_HEALTH, title, body, TYPE_HEALTH)
    }
}
