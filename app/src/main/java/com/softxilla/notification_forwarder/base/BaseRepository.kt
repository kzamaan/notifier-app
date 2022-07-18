package com.softxilla.notification_forwarder.base

import com.softxilla.notification_forwarder.network.BaseApi
import com.softxilla.notification_forwarder.interfaces.SafeApiCall

abstract class BaseRepository(
    private val api: BaseApi,
) : SafeApiCall {
    suspend fun logout() = safeApiCall {
        api.logout()
    }
}