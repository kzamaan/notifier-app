package me.kzaman.notification_forward.repository

import me.kzaman.notification_forward.base.BaseRepository
import me.kzaman.notification_forward.network.api.CommonApi
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: CommonApi,
) : BaseRepository(api) {

    suspend fun getUserProfile() = safeApiCall { api.getUserProfile() }
}