package me.kzaman.notification_forward.services

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import me.kzaman.notification_forward.ui.fragments.ApplicationListFragment


class ApplicationNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val mNotification: Notification = sbn!!.notification
        val extras: Bundle = mNotification.extras
        val androidTitle = extras.getString("android.title").toString()
        val androidText = extras.getString("android.text").toString()

        Log.d("Notification title", androidTitle)
        Log.d("Notification text", androidText)
        Log.d("Notification", sbn.packageName)

        ApplicationListFragment().displayNotificationDetail(androidTitle)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("Notification", "Clear Notification")
    }
}