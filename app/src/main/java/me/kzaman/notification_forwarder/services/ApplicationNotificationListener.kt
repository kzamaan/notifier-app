package me.kzaman.notification_forwarder.services


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
import java.util.HashMap;


class ApplicationNotificationListener : NotificationListenerService() {

    companion object {
        const val FACEBOOK_PACK_NAME = "com.facebook.katana"
        const val FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca"
        const val WHATSAPP_PACK_NAME = "com.whatsapp"
        const val INSTAGRAM_PACK_NAME = "com.instagram.android"
        const val GMAIL_PACK_NAME = "com.google.android.gm"

        const val FACEBOOK_CODE = 1
        const val FACEBOOK_MESSENGER_CODE = 2
        const val WHATSAPP_CODE = 3
        const val INSTAGRAM_CODE = 4
        const val GMAIL_CODE = 5
        const val OTHER_NOTIFICATIONS_CODE = 6 // We ignore all notification with code == 4

    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        val intent = Intent("me.kzaman.notification_forwarder")
        val notificationCode = matchNotificationCode(sbn!!)
        intent.putExtra("Notification Code", notificationCode)

        val mNotification: Notification = sbn.notification
        val extras: Bundle = mNotification.extras
        val androidTitle = extras.getString("android.title").toString()
        val androidText = extras.getString("android.text").toString()

        Log.d("Notification title", androidTitle)
        Log.d("Notification text", androidText)
        Log.d("Notification", sbn.packageName)

        val params: MutableMap<String, String> = HashMap()
        params["package"] = sbn.packageName
        params["android_title"] = androidTitle
        params["android_text"] = androidText
        params["code"] = notificationCode.toString()

        sendNotificationPost(params)
        Log.d("d", "Sent broadcast")
        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("Notification", "Clear Notification")
        val notificationCode = matchNotificationCode(sbn!!)

        if (notificationCode != OTHER_NOTIFICATIONS_CODE) {
            val activeNotifications = this.activeNotifications
            if (activeNotifications != null && activeNotifications.isNotEmpty()) {
                for (i in activeNotifications.indices) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        val intent = Intent("me.kzaman.notification_forwarder")
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
            FACEBOOK_PACK_NAME -> {
                FACEBOOK_CODE
            }
            FACEBOOK_MESSENGER_PACK_NAME -> {
                FACEBOOK_MESSENGER_CODE
            }
            INSTAGRAM_PACK_NAME -> {
                INSTAGRAM_CODE
            }
            WHATSAPP_PACK_NAME -> {
                WHATSAPP_CODE
            }
            GMAIL_PACK_NAME -> {
                GMAIL_CODE
            }
            else -> {
                OTHER_NOTIFICATIONS_CODE
            }
        }
    }

    private fun sendNotificationPost(data: Map<String, String>) {

        val url = "https://api.kzaman.me/api/forward-notification"

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
                    params = data
                    return params
                }
            }
        requestQueue.add(jsonObjRequest)
    }
}