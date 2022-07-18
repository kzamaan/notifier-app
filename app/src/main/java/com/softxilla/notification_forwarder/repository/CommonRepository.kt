package com.softxilla.notification_forwarder.repository

import com.softxilla.notification_forwarder.base.BaseRepository
import com.softxilla.notification_forwarder.network.api.CommonApi
import javax.inject.Inject

class CommonRepository @Inject constructor(
    private val api: CommonApi,
) : BaseRepository(api) {

    suspend fun forwardNotification(
        androidTitle: String,
    ) = safeApiCall {
        api.forwardNotification(androidTitle)
    }
}