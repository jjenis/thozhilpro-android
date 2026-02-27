package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.DashboardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val stats: DashboardStats? = null,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init { loadStats() }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState(isLoading = true)
            try {
                val response = apiService.getDashboardStats()
                if (response.isSuccessful) {
                    _uiState.value = DashboardUiState(stats = response.body())
                } else {
                    _uiState.value = DashboardUiState(error = "Failed to load dashboard")
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState(error = e.message)
            }
        }
    }
}
