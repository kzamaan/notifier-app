package me.kzaman.notification_forwarder.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import me.kzaman.notification_forwarder.base.BaseViewModel
import me.kzaman.notification_forwarder.data.response.ProfileResponse
import me.kzaman.notification_forwarder.network.Resource
import me.kzaman.notification_forwarder.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : BaseViewModel(repository) {

    private val _profile: MutableLiveData<Resource<ProfileResponse>> = MutableLiveData()
    val profile: LiveData<Resource<ProfileResponse>> = _profile

    fun getUserProfile() = viewModelScope.launch {
        _profile.value = Resource.Loading
        _profile.value = repository.getUserProfile()
    }

    fun clearAllTable() {}
}