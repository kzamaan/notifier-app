package me.kzaman.notification_forward.ui

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


class ApplicationNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val mNotification: Notification = sbn!!.notification
        val extras: Bundle = mNotification.extras
        Log.d("Notification title", extras.getString("android.title").toString())
        Log.d("Notification text", extras.getString("android.text").toString())
        Log.d("Notification", sbn.packageName)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("Notification", "Clear Notification")
    }
}