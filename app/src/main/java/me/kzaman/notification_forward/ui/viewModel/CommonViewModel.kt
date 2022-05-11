package me.kzaman.notification_forward.ui.viewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import me.kzaman.notification_forward.base.BaseViewModel
import me.kzaman.notification_forward.repository.CommonRepository
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    val repository: CommonRepository,
) : BaseViewModel(repository) {

}