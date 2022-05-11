package me.kzaman.notification_forward.data.response

import com.google.gson.annotations.SerializedName

data class DefaultResponse(
    @SerializedName("response_code") val responseCode: Int?,
    @SerializedName("message") val message: String,
)
