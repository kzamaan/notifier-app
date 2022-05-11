package me.kzaman.notification_forward.data.model

import com.google.gson.annotations.SerializedName

data class TokenDataModel(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_at") val expireAt: String,
)

data class UserDataModel(
    @SerializedName("id") val id: String,
    @SerializedName("user_uid") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("user_type") val userType: String,
    @SerializedName("sbu_id") val sbuId: String,
    @SerializedName("sbu_name") val sbuName: String,
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("role_name") val roleName: String,
    @SerializedName("visible_name") val visibleName: String,
    @SerializedName("mobile") val phone: String,
)