package me.kzaman.notification_forward.repository

import me.kzaman.notification_forward.base.BaseRepository
import me.kzaman.notification_forward.network.api.AuthApi
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