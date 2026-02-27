package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.Purchase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PurchasesUiState(
    val isLoading: Boolean = false,
    val purchases: List<Purchase> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PurchasesViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(PurchasesUiState())
    val uiState: StateFlow<PurchasesUiState> = _uiState

    init { loadPurchases() }

    fun loadPurchases() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getPurchases()
                if (res.isSuccessful) _uiState.value = PurchasesUiState(purchases = res.body() ?: emptyList())
                else _uiState.value = PurchasesUiState(error = "Failed to load")
            } catch (e: Exception) { _uiState.value = PurchasesUiState(error = e.message) }
        }
    }

    fun deletePurchase(id: Long) {
        viewModelScope.launch { try { apiService.deletePurchase(id); loadPurchases() } catch (_: Exception) {} }
    }
}
