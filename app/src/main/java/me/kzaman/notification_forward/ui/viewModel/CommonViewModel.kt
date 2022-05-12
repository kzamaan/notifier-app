package me.kzaman.notification_forward.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.kzaman.notification_forward.base.BaseViewModel
import me.kzaman.notification_forward.data.response.DefaultResponse
import me.kzaman.notification_forward.data.response.LoginResponse
import me.kzaman.notification_forward.network.Resource
import me.kzaman.notification_forward.repository.CommonRepository
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    val repository: CommonRepository,
) : BaseViewModel(repository) {

    private val _forwardResponse: MutableLiveData<Resource<DefaultResponse>> = MutableLiveData()
    val forwardResponse: LiveData<Resource<DefaultResponse>> = _forwardResponse
    fun forwardNotification(
        androidTitle: String,
    ) = viewModelScope.launch {
        _forwardResponse.value = repository.forwardNotification(androidTitle)
    }

}