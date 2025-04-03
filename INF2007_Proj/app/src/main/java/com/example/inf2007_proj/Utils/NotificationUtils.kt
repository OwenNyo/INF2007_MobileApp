package com.example.inf2007_proj.Utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.inf2007_proj.DataModels.MedicationEntity
import java.util.*

object NotificationUtils {

    private const val TAG = "NotificationUtils"

    private fun scheduleNotification(context: Context, time: String, message: String, uniqueId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            Log.d(TAG, "Exact alarm permission not granted. Redirecting user to settings.")
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            uniqueId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeParts = time.split(":").map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeParts[0])
            set(Calendar.MINUTE, timeParts[1])
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Log.d(TAG, "Notification scheduled for time: $time with message: \"$message\", uniqueId: $uniqueId")
    }

    fun scheduleDailyNotifications(context: Context, medications: List<MedicationEntity>) {
        if (medications.isEmpty()) {
            Log.d(TAG, "No medications for today. Skipping notifications.")
            return
        }

        // Collect all dose times from today's medications
        val doseTimes = mutableSetOf<String>() // Use a set to ensure distinct times
        medications.forEach { medication ->
            listOf(
                medication.firstDoseTime,
                medication.secondDoseTime,
                medication.thirdDoseTime
            ).filterNotNull() // Ignore null values
                .filter { it != "NA" } // Ignore placeholder values
                .forEach { doseTimes.add(it.trim()) } // Add to the set after trimming
        }

        // Log collected times
        Log.d(TAG, "Collected dose times: $doseTimes")

        // Schedule notifications for all distinct times
        doseTimes.forEachIndexed { index, time ->
            scheduleNotification(
                context,
                time,
                "It's time to take your medication!",
                index + 1 // Unique ID for each notification
            )
        }

        Log.d(TAG, "Notifications scheduled for the following times: $doseTimes")
    }
}
