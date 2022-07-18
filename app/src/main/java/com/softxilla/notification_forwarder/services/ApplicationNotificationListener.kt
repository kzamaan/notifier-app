package com.softxilla.notification_forwarder.services


import android.R.attr
import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ApplicationNotificationListener : NotificationListenerService() {

    companion object {
        const val NAGAD_AGENT = "com.konasl.nagad.agent"
        const val NAGAD_PACK_NAME = "com.konasl.nagad"
        const val BKASH_PACK_NAME = "com.bkash.customerapp"
        const val SAMSUNG_MESSAGE = "com.samsung.android.messaging"
        const val GOOGLE_MESSAGE = "com.google.android.apps.messaging"

        const val NAGAD_CODE = 5
        const val BKASH_CODE = 6
        const val MESSAGE_CODE = 7
        const val OTHER_NOTIFICATIONS_CODE = 10 // We ignore all notification with code == 10

    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        val packageName = sbn!!.packageName
        val notificationCode = matchNotificationCode(sbn)

        val intent = Intent("com.softxilla.notification_forwarder")
        intent.putExtra("Notification Code", notificationCode)

        val mNotification: Notification = sbn.notification
        val extras: Bundle = mNotification.extras
        val androidTitle = extras.getString("android.title").toString()
        val androidText = extras.getString("android.text").toString()
        val androidSubText = extras.getString("android.subText").toString()
        val androidBigText = extras.getString("android.bigText").toString()
        val androidInfoText = extras.getString("android.infoText").toString()

        val packageManager = applicationContext.packageManager
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = appInfo.loadLabel(packageManager).toString()

        Log.d("notification_title", androidTitle)
        Log.d("notification_text", androidText)

        if (notificationCode != OTHER_NOTIFICATIONS_CODE || appName == "Messages") {
            Log.d("notification_text", "Notification Matched")
            val postObject: MutableMap<String, String> = HashMap()
            postObject["app_name"] = appName
            postObject["package_name"] = packageName
            postObject["android_title"] = androidTitle
            postObject["android_text"] = androidText
            postObject["android_sub_text"] = androidSubText
            postObject["android_big_text"] = androidBigText
            postObject["android_info_text"] = androidInfoText

            // saveOnFirebase(postObject)
            sendNotificationPost(postObject)
            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("Notification", "Clear Notification")
        val notificationCode = matchNotificationCode(sbn!!)

        if (notificationCode != OTHER_NOTIFICATIONS_CODE) {
            val activeNotifications = this.activeNotifications
            if (activeNotifications != null && activeNotifications.isNotEmpty()) {
                for (i in activeNotifications.indices) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        val intent = Intent("com.softxilla.notification_forwarder")
                        intent.putExtra("Notification Code", notificationCode)
                        sendBroadcast(intent)
                        break
                    }
                }
            }
        }
    }

    private fun matchNotificationCode(sbn: StatusBarNotification): Int {
        return when (sbn.packageName) {
            NAGAD_AGENT -> NAGAD_CODE
            NAGAD_PACK_NAME -> NAGAD_CODE
            BKASH_PACK_NAME -> BKASH_CODE
            SAMSUNG_MESSAGE -> MESSAGE_CODE
            GOOGLE_MESSAGE -> MESSAGE_CODE
            else -> OTHER_NOTIFICATIONS_CODE
        }
    }

    private fun sendNotificationPost(postObject: Map<String, String>) {

        val url = "https://softxilla.com/api/store-notification"

        // Instantiate the RequestQueue.
        Log.d("d", "Got to sendPost")
        val requestQueue = Volley.newRequestQueue(this)
        Log.d("d", "Data: " + attr.data.toString())
        Log.d("d", "Url: $url")


        val jsonObjRequest: StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener {
                    Log.d("Response", it.toString())
                }, Response.ErrorListener { error ->
                    VolleyLog.d("volley", "Error: " + error.message)
                    error.printStackTrace()
                }) {
                override fun getBodyContentType(): String {
                    return "application/x-www-form-urlencoded; charset=UTF-8"
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    var params: Map<String, String> = HashMap()
                    params = postObject
                    return params
                }
            }
        requestQueue.add(jsonObjRequest)
    }

    private fun saveOnFirebase(postObject: Map<String, String>) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val key = current.format(formatter)
        val database =
            FirebaseDatabase.getInstance("https://notification-forwarder-22-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val notificationRef = database.getReference("notifications")
        notificationRef.child(key).setValue(postObject)
    }
}