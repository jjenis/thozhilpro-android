package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.Sale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SalesUiState(
    val isLoading: Boolean = false,
    val sales: List<Sale> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SalesViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState

    init { loadSales() }

    fun loadSales() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getSales()
                if (res.isSuccessful) _uiState.value = SalesUiState(sales = res.body() ?: emptyList())
                else _uiState.value = SalesUiState(error = "Failed to load")
            } catch (e: Exception) { _uiState.value = SalesUiState(error = e.message) }
        }
    }

    fun deleteSale(id: Long) {
        viewModelScope.launch { try { apiService.deleteSale(id); loadSales() } catch (_: Exception) {} }
    }
}
