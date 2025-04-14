package com.mobileuqac.routines.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mobileuqac.routines.receiver.NotificationReceiver
import java.util.Date
import java.util.Calendar

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val NOTIFICATION_REQUEST_CODE = NotificationReceiver.NOTIFICATION_ID // Réutiliser l'ID
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(delayMillis: Long, message: String, titre: String, id: Int) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_MESSAGE, message)
            putExtra(NotificationReceiver.NOTIFICATION_TITLE, titre)
            putExtra(NotificationReceiver.NOTIFICATION_ID, id)
        }
        Log.d("Receiver", "recu")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + delayMillis

        // Planifier l'alarme
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotificationAt(date: Date, titre: String, message: String, id: Int) {

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_MESSAGE, message)
            putExtra(NotificationReceiver.NOTIFICATION_TITLE, titre)
            putExtra(NotificationReceiver.NOTIFICATION_ID, id.toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("Alarmeee", date.time.toString())
        // Planifier l'alarme
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            date.time,
            pendingIntent
        )
    }

    fun cancelNotification(id: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // FLAG_NO_CREATE pour éviter de créer un nouvel Intent si inexistant
        )
        pendingIntent?.cancel()
        alarmManager.cancel(pendingIntent)
    }
}