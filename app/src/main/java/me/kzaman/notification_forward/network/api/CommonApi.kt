package me.kzaman.notification_forward.network.api

import me.kzaman.notification_forward.data.response.DefaultResponse
import me.kzaman.notification_forward.data.response.ProfileResponse
import me.kzaman.notification_forward.network.BaseApi
import retrofit2.http.GET

interface CommonApi : BaseApi {

    @GET("user")
    suspend fun getUserProfile(): ProfileResponse

    @GET("mobile/get-sales-user-wise-all-customer-list")
    suspend fun getAllCustomers(): DefaultResponse
}