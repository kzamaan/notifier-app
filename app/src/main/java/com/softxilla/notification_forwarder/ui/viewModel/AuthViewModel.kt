package com.softxilla.notification_forwarder.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.softxilla.notification_forwarder.base.BaseViewModel
import com.softxilla.notification_forwarder.data.response.LoginResponse
import com.softxilla.notification_forwarder.network.Resource
import com.softxilla.notification_forwarder.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : BaseViewModel(repository) {


    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>> = _loginResponse


    fun userLogin(
        userName: String,
        password: String,
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(userName, password)
    }
}