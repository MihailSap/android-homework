package ru.sapegin.androidhomework.data.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class FavoriteClassAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        createNotificationChannel(notificationManager)

        val fullName = intent.getStringExtra(
            FavoriteClassAlarmScheduler.EXTRA_FULL_NAME
        ) ?: "Владелец профиля"

        val reminderTime = intent.getStringExtra(
            FavoriteClassAlarmScheduler.EXTRA_REMINDER_TIME
        ).orEmpty()

        val notificationText =
            "$fullName, начинается любимая пара по мобильной разработке!"

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val contentPendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                NOTIFICATION_REQUEST_CODE,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notificationBuilder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Время любимой пары!")
            .setContentText(notificationText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "$notificationText Время: $reminderTime"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (contentPendingIntent != null) {
            notificationBuilder.setContentIntent(contentPendingIntent)
        }

        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.build()
        )
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Напоминания о занятиях",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о начале любимой пары"
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {

        private const val CHANNEL_ID = "favorite_class_channel"

        private const val NOTIFICATION_ID = 1001

        private const val NOTIFICATION_REQUEST_CODE = 1002
    }
}