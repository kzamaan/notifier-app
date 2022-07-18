package com.softxilla.notification_forwarder.repository

import com.softxilla.notification_forwarder.base.BaseRepository
import com.softxilla.notification_forwarder.network.api.AuthApi
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
) : BaseRepository(api) {

    suspend fun login(
        userName: String,
        password: String,
    ) = safeApiCall {
        api.login(userName, password)
    }
}