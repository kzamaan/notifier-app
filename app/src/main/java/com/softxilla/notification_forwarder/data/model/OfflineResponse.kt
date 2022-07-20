package com.softxilla.notification_forwarder.data.model

data class OfflineResponse(
    val status: Boolean,
    val message: String,
    val offlineIds: ArrayList<String>
)