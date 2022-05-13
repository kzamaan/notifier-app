package me.kzaman.notification_forwarder.base

import me.kzaman.notification_forwarder.network.BaseApi
import me.kzaman.notification_forwarder.interfaces.SafeApiCall

abstract class BaseRepository(
    private val api: BaseApi,
) : SafeApiCall {
    suspend fun logout() = safeApiCall {
        api.logout()
    }
}