package com.softxilla.notification_forwarder.data.response

import com.google.gson.annotations.SerializedName

data class OnlineResponse(
    val status: Boolean,
    val message: String,
    @SerializedName("app_id") val appId: Int
)