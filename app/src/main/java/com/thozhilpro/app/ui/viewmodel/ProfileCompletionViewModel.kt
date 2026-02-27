package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileCompletionUiState(
    val firstName: String = "",
    val lastName: String = "",
    val companyName: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileCompletionUiState())
    val uiState: StateFlow<ProfileCompletionUiState> = _uiState

    fun refreshData() {
        val user = authRepository.currentUser.value
        val tenant = authRepository.currentTenant.value
        _uiState.value = _uiState.value.copy(
            firstName = user?.firstName ?: "",
            lastName = user?.lastName ?: "",
            companyName = tenant?.companyName ?: "",
            phone = tenant?.phone ?: ""
        )
    }

    fun completeProfile(firstName: String, lastName: String, companyName: String, phone: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.completeProfile(firstName, lastName, companyName, phone)
            result.fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}
