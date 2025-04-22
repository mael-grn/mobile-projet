package com.mobileuqac.routines.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mobileuqac.routines.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "notification_id"
        const val CHANNEL_ID = "my_channel_id"
        const val CHANNEL_NAME = "My Notifications"
        const val EXTRA_MESSAGE = "notification_message"
        const val NOTIFICATION_TITLE = "notification_title"
    }

    class RoutineActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val routineTitle = intent.getStringExtra("routine_title") ?: "Routine"

            // Ici tu peux marquer la routine comme complétée dans la base de données
            Toast.makeText(context, "$routineTitle marquée comme complétée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Notification reçue !"
        val title = intent.getStringExtra(NOTIFICATION_TITLE) ?: "Nouvelle notification"
        val id = intent.getStringExtra(NOTIFICATION_ID)?.toInt() ?: 123456
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Log.d("Receiver", "recu")

        createNotificationChannel(notificationManager)
        showNotification(context, notificationManager, title, message, id)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications de mon application"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, notificationManager: NotificationManager, title: String, message: String, notificationId: Int) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 👉 Intent pour l'action "Complétée"
        val completeIntent = Intent(context, RoutineActionReceiver::class.java).apply {
            putExtra("routine_title", title)
        }

        val completePendingIntent = PendingIntent.getBroadcast(
            context, 1, completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "Complétée",
                completePendingIntent
            )

        notificationManager.notify(notificationId, builder.build())
    }
}