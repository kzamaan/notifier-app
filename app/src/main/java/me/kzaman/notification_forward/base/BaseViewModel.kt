package me.kzaman.notification_forward.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel(
    private val repository: BaseRepository,
) : ViewModel() {

    suspend fun logout() = withContext(Dispatchers.IO) { repository.logout() }
}