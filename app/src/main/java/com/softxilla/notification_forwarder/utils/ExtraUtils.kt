package com.softxilla.notification_forwarder.utils

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.softxilla.notification_forwarder.data.response.OnlineResponse
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import com.softxilla.notification_forwarder.di.AppModule.BASE_URL

fun sendNotificationPost(
    mContext: Context,
    postObject: Map<String, String>
): Boolean {
    val url = "${BASE_URL}store-notification"
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