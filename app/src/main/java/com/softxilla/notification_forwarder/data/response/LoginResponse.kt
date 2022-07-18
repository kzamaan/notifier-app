package com.softxilla.notification_forwarder.data.response

import com.softxilla.notification_forwarder.data.model.TokenDataModel
import com.softxilla.notification_forwarder.data.model.UserDataModel
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("response_code") var responseCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") var data: DataModel,
)

data class DataModel(
    @SerializedName("token") val token: TokenDataModel,
    @SerializedName("user") val user: UserDataModel? = null,
)