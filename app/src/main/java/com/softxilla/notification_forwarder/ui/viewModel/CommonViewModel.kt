package com.softxilla.notification_forwarder.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.softxilla.notification_forwarder.base.BaseViewModel
import com.softxilla.notification_forwarder.data.response.DefaultResponse
import com.softxilla.notification_forwarder.data.response.OfflineResponse
import com.softxilla.notification_forwarder.network.Resource
import com.softxilla.notification_forwarder.repository.CommonRepository
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    val repository: CommonRepository,
) : BaseViewModel(repository) {

    private val _syncOfflineResponse: MutableLiveData<Resource<OfflineResponse>> = MutableLiveData()
    val syncOfflineResponse: LiveData<Resource<OfflineResponse>> = _syncOfflineResponse
    fun syncOfflineNotification(
        msgFrom: String,
        messages: String
    ) = viewModelScope.launch {
        _syncOfflineResponse.value = Resource.Loading
        _syncOfflineResponse.value = repository.syncOfflineNotification(msgFrom, messages)
    }
}