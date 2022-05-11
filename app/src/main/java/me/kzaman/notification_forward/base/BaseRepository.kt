package me.kzaman.notification_forward.base

import me.kzaman.notification_forward.network.BaseApi
import me.kzaman.notification_forward.interfaces.SafeApiCall

abstract class BaseRepository(
    private val api: BaseApi,
) : SafeApiCall {
    suspend fun logout() = safeApiCall {
        api.logout()
    }
}