package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.model.Tenant
import com.thozhilpro.app.data.model.User
import com.thozhilpro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.currentUser
    val tenant: StateFlow<Tenant?> = authRepository.currentTenant

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
