package me.kzaman.notification_forwarder.data.response

import me.kzaman.notification_forwarder.data.model.UserDataModel


data class ProfileResponse(
    val code: Int,
    val status: String,
    val data: UserDataModel,
)