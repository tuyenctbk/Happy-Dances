package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

class DailyDanceReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showFriendlyDanceNotification(context)
        // Reschedule for next day
        val hour = intent.getIntExtra("reminder_hour", 16)
        val minute = intent.getIntExtra("reminder_minute", 0)
        DanceNotificationScheduler.scheduleDailyReminder(context, hour, minute)
    }

    private fun showFriendlyDanceNotification(context: Context) {
        val channelId = "happy_dances_daily_nudge"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Dance Magic Nudge",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Friendly invitations to dance, stretch, and leap!"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val friendlyNudges = listOf(
            "🩰 It's ballet time! Put on your invisible wings and leap today! ✨",
            "🌸 Your magic garden is waiting! Come paint with your toes! 🌈",
            "👑 The Royal Ballroom music is starting! Let's practice pliés! 🏰",
            "⭐ Keep your dance streak glowing! Ready for today's dance story? 🔥",
            "🎵 Time for a dance break! Spin, jump, and have fun! 🌟",
            "🦋 Butterfly wings and reach for the stars! Try our 2-minute 'gentle stretch' warm-up before you dance! 🩰",
            "🦢 Warm up your swan wings! Stretch and bend for 2 minutes before today's dance adventure! ✨"
        )
        val message = friendlyNudges.random()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("🩰 Happy Dances: Time to Dance!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(1001, notification)
        } catch (e: Exception) {
            Log.e("DanceReminderReceiver", "Error posting notification: ${e.message}")
        }
    }
}

object DanceNotificationScheduler {
    private const val ALARM_REQUEST_CODE = 8821

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyDanceReminderReceiver::class.java).apply {
            putExtra("reminder_hour", hour)
            putExtra("reminder_minute", minute)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("DanceNotificationScheduler", "Scheduled reminder for $hour:$minute at ${calendar.time}")
        } catch (e: Exception) {
            Log.e("DanceNotificationScheduler", "Error scheduling reminder: ${e.message}")
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyDanceReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun triggerTestNotification(context: Context) {
        val receiver = DailyDanceReminderReceiver()
        val intent = Intent(context, DailyDanceReminderReceiver::class.java)
        receiver.onReceive(context, intent)
    }
}
