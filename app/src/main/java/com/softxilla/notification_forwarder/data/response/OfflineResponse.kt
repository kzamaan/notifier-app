package com.softxilla.notification_forwarder.data.response

data class OfflineResponse(
    val status: Boolean,
    val message: String,
    val offlineIds: ArrayList<String>
)