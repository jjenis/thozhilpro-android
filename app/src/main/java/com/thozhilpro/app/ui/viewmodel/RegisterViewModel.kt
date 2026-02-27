package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            val result = authRepository.register(email, password)
            result.fold(
                onSuccess = { _uiState.value = RegisterUiState(isSuccess = true) },
                onFailure = { _uiState.value = RegisterUiState(error = it.message ?: "Registration failed") }
            )
        }
    }

    fun setError(msg: String) {
        _uiState.value = RegisterUiState(error = msg)
    }
}
