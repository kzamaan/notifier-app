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
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import com.softxilla.notification_forwarder.network.NetworkHelper
import org.json.JSONArray
import org.json.JSONObject


class ApplicationNotificationListener : NotificationListenerService() {

    companion object {
        const val SAMSUNG_MESSAGE = "com.samsung.android.messaging"
        const val GOOGLE_MESSAGE = "com.google.android.apps.messaging"
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

            val helper = NetworkHelper(applicationContext)
            val messageDatabaseHelper = MessageDatabaseHelper(applicationContext)
            if (helper.isNetworkConnected()) {
                sendNotificationPost(postObject)
                Log.d("status", "Online, Internet Available")
                val messages = messageDatabaseHelper.getUnSyncedMessage()
                val jsonArray = JSONArray()
                if (messages.moveToFirst()) {
                    do {
                        val messageObject = JSONObject()
                        val rowId = messages.getColumnIndex(MessageDatabaseHelper.ID)
                        val locAppName = messages.getColumnIndex(MessageDatabaseHelper.APP_NAME)
                        val locPackage = messages.getColumnIndex(MessageDatabaseHelper.PACKAGE_NAME)
                        val locTitle = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TITLE)
                        val locText = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TEXT)
                        val createdAt = messages.getColumnIndex(MessageDatabaseHelper.CREATED_AT)
                        messageObject.put("app_name", messages.getString(locAppName))
                        messageObject.put("package_name", messages.getString(locPackage))
                        messageObject.put("android_title", messages.getString(locTitle))
                        messageObject.put("android_text", messages.getString(locText))
                        messageObject.put("created_at", messages.getString(createdAt))
                        jsonArray.put(messageObject)
                        messageDatabaseHelper.updateMessageStatus(messages.getInt(rowId))
                    } while (messages.moveToNext())

                    val offlineMessageObject: MutableMap<String, String> = HashMap()
                    offlineMessageObject["messages"] = jsonArray.toString()
                    syncOfflineMessage(offlineMessageObject)
                }
            } else {
                messageDatabaseHelper.storeMessagesSQLite(
                    appName,
                    packageName,
                    androidTitle,
                    androidText
                )
                Log.d("status", "Offline, No Internet")
            }
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
                    return postObject
                }
            }
        requestQueue.add(jsonObjRequest)
    }

    private fun syncOfflineMessage(postObject: Map<String, String>) {
        val url = "https://softxilla.com/api/sync-offline-message"
        val requestQueue = Volley.newRequestQueue(this)
        Log.d("d", "Data: " + attr.data.toString())
        Log.d("d", "postObject: $postObject")

        val jsonObjRequest: StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener {
                    Log.d("sync_offline", it.toString())
                }, Response.ErrorListener { error ->
                    VolleyLog.d("volley", "Error: " + error.message)
                    error.printStackTrace()
                }) {
                override fun getBodyContentType(): String {
                    return "application/x-www-form-urlencoded; charset=UTF-8"
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return postObject
                }
            }
        requestQueue.add(jsonObjRequest)
    }
}