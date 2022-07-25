package com.softxilla.notification_forwarder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import com.softxilla.notification_forwarder.database.SharedPreferenceManager
import com.softxilla.notification_forwarder.network.NetworkHelper
import com.softxilla.notification_forwarder.utils.sendNotificationPost
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
                        sendNotificationPost(mContext, postObject)
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
}