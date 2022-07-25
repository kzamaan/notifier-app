package com.softxilla.notification_forwarder.data.model

import com.google.gson.annotations.SerializedName


data class LocalMessage(
    @SerializedName("id") val id: String,
    @SerializedName("android_title") val androidTitle: String,
    @SerializedName("android_text") val androidText: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("status") val status: Int,
)