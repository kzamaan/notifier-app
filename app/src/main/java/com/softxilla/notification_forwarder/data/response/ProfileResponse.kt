package com.softxilla.notification_forwarder.data.response

import com.softxilla.notification_forwarder.data.model.UserDataModel


data class ProfileResponse(
    val code: Int,
    val status: String,
    val data: UserDataModel,
)