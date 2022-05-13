package me.kzaman.notification_forwarder.repository

import me.kzaman.notification_forwarder.base.BaseRepository
import me.kzaman.notification_forwarder.network.api.CommonApi
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: CommonApi,
) : BaseRepository(api) {

    suspend fun getUserProfile() = safeApiCall { api.getUserProfile() }
}