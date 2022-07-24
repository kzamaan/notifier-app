package com.softxilla.notification_forwarder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.softxilla.notification_forwarder.data.response.OnlineResponse
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import com.softxilla.notification_forwarder.database.SharedPreferenceManager
import com.softxilla.notification_forwarder.network.NetworkHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MessageReceiver : BroadcastReceiver() {

    companion object {
        // operator codes
        const val BKASH = "BKASH"
        const val NAGAD = "NAGAD"
        const val UPAY = "UPAY"
        const val ROCKET = "ROCKET"
    }

    @Inject
    lateinit var prefManager: SharedPreferenceManager

    override fun onReceive(mContext: Context, intent: Intent?) {
        val helper = NetworkHelper(mContext)
        if (intent?.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            val smsMessages: Array<SmsMessage> = getMessagesFromIntent(intent)
            val databaseHelper = MessageDatabaseHelper(mContext)
            for (message in smsMessages) {
                val originatingAddress = message.originatingAddress.toString().trim()
                val messageBody = message.messageBody.toString().trim()

                Log.d("SMS", "SMS Received1: $messageBody")
                Log.d("SMS", "SMS Received2: $originatingAddress")

                val postObject: MutableMap<String, String> = HashMap()
                postObject["android_title"] = originatingAddress
                postObject["android_text"] = messageBody
                postObject["msg_from"] = prefManager.getUserPhone()

                val matchTitle = matchMessageTitle(originatingAddress)
                if (matchTitle) {
                    val lastMessage = databaseHelper.saveMsgSQLite(originatingAddress, messageBody)
                    postObject["app_id"] = lastMessage.toString()
                    if (helper.isNetworkConnected()) {
                        sendNotificationPost(postObject, mContext)
                    }
                    Log.d("Match Title", "Match Title: $originatingAddress")
                } else {
                    Log.d("Match Title", "Not Match Title: $originatingAddress")
                }
            }
        }
    }

    private fun matchMessageTitle(title: String): Boolean {
        val stringUpper = title.uppercase(Locale.ROOT).trim()
        return stringUpper == BKASH || stringUpper == NAGAD || stringUpper == UPAY || stringUpper == ROCKET
    }

    private fun sendNotificationPost(postObject: Map<String, String>, mContext: Context) {
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
    }
}