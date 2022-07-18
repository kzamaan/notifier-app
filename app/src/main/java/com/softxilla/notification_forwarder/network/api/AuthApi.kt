package com.softxilla.notification_forwarder.network.api

import com.softxilla.notification_forwarder.data.response.LoginResponse
import com.softxilla.notification_forwarder.network.BaseApi
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi : BaseApi {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") password: String,
    ): LoginResponse
}