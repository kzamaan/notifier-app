package me.kzaman.notification_forward.data.response

import me.kzaman.notification_forward.data.model.UserDataModel


data class ProfileResponse(
    val code: Int,
    val status: String,
    val data: UserDataModel,
)