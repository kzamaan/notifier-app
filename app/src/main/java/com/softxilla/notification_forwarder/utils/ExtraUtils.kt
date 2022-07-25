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
import com.softxilla.notification_forwarder.data.response.OfflineResponse
import com.softxilla.notification_forwarder.data.response.OnlineResponse
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import org.json.JSONArray
import org.json.JSONObject

fun sendNotificationPost(
    mContext: Context,
    postObject: Map<String, String>
): Boolean {
    val url = "https://softxilla.com/api/store-notification"
    val requestQueue = Volley.newRequestQueue(mContext)
    val jsonObjRequest: StringRequest =
        object : StringRequest(Method.POST, url,
            Response.Listener {
                Log.d("Response", it.toString())
                val response = Gson().fromJson(it.toString(), OnlineResponse::class.java)
                if (response.status) {
                    Log.d("Response", "Success")
                    val databaseHelper = MessageDatabaseHelper(mContext)
                    databaseHelper.updateMessageStatus(response.appId)
                } else {
                    Log.d("Response", "Failed")
                }
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
    return true
}

fun syncOfflineMessageToDatabase(
    mContext: Context,
    msgFrom: String,
    isActivity: Boolean = false
): Boolean {
    val loadingUtils = LoadingUtils(mContext)
    val messageDatabaseHelper = MessageDatabaseHelper(mContext)
    val messages = messageDatabaseHelper.getUnSyncedMessage()
    val jsonArray = JSONArray()
    if (messages.moveToFirst()) {
        loadingUtils.isLoading(true)
        do {
            val messageObject = JSONObject()
            val rowId = messages.getColumnIndex(MessageDatabaseHelper.ID)
            val androidTitle = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TITLE)
            val androidText = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TEXT)
            val createdAt = messages.getColumnIndex(MessageDatabaseHelper.CREATED_AT)
            messageObject.put("offline_id", messages.getString(rowId))
            messageObject.put("android_title", messages.getString(androidTitle))
            messageObject.put("android_text", messages.getString(androidText))
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
                    loadingUtils.isLoading(false)
                    val offline = Gson().fromJson(it.toString(), OfflineResponse::class.java)
                    updateDatabaseStatus(mContext, offline)
                }, Response.ErrorListener { error ->
                    loadingUtils.isLoading(false)
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
        if (isActivity) {
            Toast.makeText(mContext, "No Message to Sync", Toast.LENGTH_SHORT).show()
        }
    }
    return true
}

fun updateDatabaseStatus(mContext: Context, offlineResponse: OfflineResponse) {
    Log.d("offlineResponse", offlineResponse.toString())
    val databaseHelper = MessageDatabaseHelper(mContext)
    if (offlineResponse.status) {
        Toast.makeText(mContext, offlineResponse.message, Toast.LENGTH_SHORT).show()
        println("offlineResponse object: $offlineResponse")
        offlineResponse.offlineIds.forEach {
            Log.d("offlineId", it)
            databaseHelper.updateMessageStatus(it.toInt())
        }
    } else {
        Toast.makeText(mContext, offlineResponse.message, Toast.LENGTH_SHORT).show()
        println("offlineResponse object: $offlineResponse")
    }
}