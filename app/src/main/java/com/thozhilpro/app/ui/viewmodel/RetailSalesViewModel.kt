package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.RetailSale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RetailSalesUiState(
    val isLoading: Boolean = false,
    val retailSales: List<RetailSale> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class RetailSalesViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(RetailSalesUiState())
    val uiState: StateFlow<RetailSalesUiState> = _uiState

    init { loadRetailSales() }

    fun loadRetailSales() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getRetailSales()
                if (res.isSuccessful) _uiState.value = RetailSalesUiState(retailSales = res.body() ?: emptyList())
                else _uiState.value = RetailSalesUiState(error = "Failed to load")
            } catch (e: Exception) { _uiState.value = RetailSalesUiState(error = e.message) }
        }
    }

    fun deleteRetailSale(id: Long) {
        viewModelScope.launch { try { apiService.deleteRetailSale(id); loadRetailSales() } catch (_: Exception) {} }
    }
}
