package com.softxilla.notification_forwarder.network.api

import com.softxilla.notification_forwarder.data.response.DefaultResponse
import com.softxilla.notification_forwarder.data.response.OfflineResponse
import com.softxilla.notification_forwarder.data.response.ProfileResponse
import com.softxilla.notification_forwarder.network.BaseApi
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface CommonApi : BaseApi {

    @GET("user")
    suspend fun getUserProfile(): ProfileResponse

    @FormUrlEncoded
    @POST("sync-offline-message")
    suspend fun syncOfflineNotification(
        @Field("msg_from") msgFrom: String,
        @Field("messages") messages: String,
    ): OfflineResponse
}