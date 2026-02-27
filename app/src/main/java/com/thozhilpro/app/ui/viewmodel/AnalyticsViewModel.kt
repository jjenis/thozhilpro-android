package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.AnalyticsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val data: AnalyticsResponse? = null,
    val startDate: String = LocalDate.now().withDayOfMonth(1).toString(),
    val endDate: String = LocalDate.now().toString(),
    val error: String? = null
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    init { loadAnalytics() }

    fun setStartDate(d: String) { _uiState.value = _uiState.value.copy(startDate = d) }
    fun setEndDate(d: String) { _uiState.value = _uiState.value.copy(endDate = d) }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getAnalytics(_uiState.value.startDate, _uiState.value.endDate)
                if (res.isSuccessful) _uiState.value = _uiState.value.copy(isLoading = false, data = res.body())
                else _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed")
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }
}
