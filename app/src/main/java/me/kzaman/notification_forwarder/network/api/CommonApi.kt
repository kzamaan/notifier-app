package me.kzaman.notification_forwarder.network.api

import me.kzaman.notification_forwarder.data.response.DefaultResponse
import me.kzaman.notification_forwarder.data.response.ProfileResponse
import me.kzaman.notification_forwarder.network.BaseApi
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface CommonApi : BaseApi {

    @GET("user")
    suspend fun getUserProfile(): ProfileResponse

    @FormUrlEncoded
    @POST("forward-notification")
    suspend fun forwardNotification(
        @Field("androidTitle") androidTitle: String,
    ): DefaultResponse
}