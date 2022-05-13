package me.kzaman.notification_forwarder.repository

import me.kzaman.notification_forwarder.base.BaseRepository
import me.kzaman.notification_forwarder.network.api.AuthApi
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