package com.softxilla.notification_forwarder.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.softxilla.notification_forwarder.data.model.OfflineResponse
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat


fun syncOfflineMessageToDatabase(mContext: Context, msgFrom: String): Boolean {
    var isSyncToDatabase = false
    val messageDatabaseHelper = MessageDatabaseHelper(mContext)
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
            messageObject.put("offline_id", messages.getString(rowId))
            messageObject.put("app_name", messages.getString(locAppName))
            messageObject.put("package_name", messages.getString(locPackage))
            messageObject.put("android_title", messages.getString(locTitle))
            messageObject.put("android_text", messages.getString(locText))
            messageObject.put("created_at", messages.getString(createdAt))
            jsonArray.put(messageObject)
        } while (messages.moveToNext())

        val postObject: MutableMap<String, String> = HashMap()
        postObject["messages"] = jsonArray.toString()
        postObject["msg_from"] = msgFrom
        Log.d("offlineMessage", postObject.toString())
        val url = "https://softxilla.com/api/sync-offline-message"
        val requestQueue = Volley.newRequestQueue(mContext)

        val jsonObjRequest: StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener {
                    val offline = Gson().fromJson(it.toString(), OfflineResponse::class.java)
                    updateDatabaseStatus(mContext, offline)
                    isSyncToDatabase = true
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
    } else {
        isSyncToDatabase = false
        Toast.makeText(mContext, "No Message to Sync", Toast.LENGTH_SHORT).show()
    }
    return isSyncToDatabase
}

fun updateDatabaseStatus(mContext: Context, offlineResponse: OfflineResponse) {
    Log.d("offlineResponse", offlineResponse.toString())
    val databaseHelper = MessageDatabaseHelper(mContext)
    if (offlineResponse.status) {
        println("offlineResponse object: $offlineResponse")
        offlineResponse.offlineIds.forEach {
            Log.d("offlineId", it)
            databaseHelper.updateMessageStatus(it.toInt())
        }
    } else {
        Toast.makeText(mContext, "Nothing to sync", Toast.LENGTH_SHORT).show()
        println("offlineResponse object: $offlineResponse")
    }
}