package com.github.hahmadfaiq21.customnotification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.cancel(notificationId)
    }

    companion object {
        const val NOTIFICATION_ID = "notificationId"
    }
}