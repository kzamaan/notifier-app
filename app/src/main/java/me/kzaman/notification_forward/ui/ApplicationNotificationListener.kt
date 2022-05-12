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
//        val intent = Intent("me.kzaman.notification_forward")
//        intent.putExtras(mNotification.extras)
//        sendBroadcast(intent)
//        val mActions: Array<Notification.Action> = mNotification.actions
        Log.d("Notification", extras.getString("android.title").toString())
        Log.d("Notification", extras.getString("android.text").toString())
        Log.d("Notification", sbn.packageName)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("Notification", "Clear Notification")
    }
}