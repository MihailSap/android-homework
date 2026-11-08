package ru.sapegin.androidhomework.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import java.util.Calendar

class FavoriteClassAlarmScheduler(
    context: Context
) {

    private val appContext = context.applicationContext

    fun schedule(
        reminderTime: String,
        fullName: String
    ) {
        val time = parseTime(reminderTime)
            ?: throw IllegalArgumentException(
                "Укажите время в формате HH:mm"
            )

        val (hour, minute) = time

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            throw IllegalArgumentException(
                "Укажите время позже текущего. Напоминание устанавливается на сегодня."
            )
        }

        val alarmManager = appContext.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            throw SecurityException("Необходимо разрешить установку точных будильников")
        }

        val alarmIntent = Intent(
            appContext,
            FavoriteClassAlarmReceiver::class.java
        ).apply {
            action = ACTION_FAVORITE_CLASS_REMINDER

            putExtra(
                EXTRA_FULL_NAME,
                fullName.ifBlank { "Владелец профиля" }
            )

            putExtra(
                EXTRA_REMINDER_TIME,
                reminderTime
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            ALARM_REQUEST_CODE,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun parseTime(value: String): Pair<Int, Int>? {
        if (!isValidReminderTime(value)) {
            return null
        }

        val parts = value.split(":")

        return parts[0].toInt() to parts[1].toInt()
    }

    companion object {

        const val ACTION_FAVORITE_CLASS_REMINDER = "ru.sapegin.androidhomework.FAVORITE_CLASS_REMINDER"

        const val EXTRA_FULL_NAME = "extra_full_name"

        const val EXTRA_REMINDER_TIME = "extra_reminder_time"

        private const val ALARM_REQUEST_CODE = 1001

        fun isValidReminderTime(value: String): Boolean {
            return Regex(
                "^(?:[01]\\d|2[0-3]):[0-5]\\d$"
            ).matches(value)
        }

        fun canScheduleExactAlarms(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                return true
            }

            val alarmManager = context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

            return alarmManager.canScheduleExactAlarms()
        }

        fun requestExactAlarmPermission(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                return
            }

            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            ).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        }
    }
}