package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.CreateSupplierRequest
import com.thozhilpro.app.data.model.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class SuppliersUiState(
    val isLoading: Boolean = false,
    val suppliers: List<Supplier> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    val filteredSuppliers: List<Supplier>
        get() = if (searchQuery.isBlank()) suppliers
        else suppliers.filter { it.name.contains(searchQuery, true) || it.phone.contains(searchQuery) }
}

@HiltViewModel
class SuppliersViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(SuppliersUiState())
    val uiState: StateFlow<SuppliersUiState> = _uiState

    init { loadSuppliers() }

    fun loadSuppliers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getSuppliers()
                if (res.isSuccessful) _uiState.value = _uiState.value.copy(isLoading = false, suppliers = res.body() ?: emptyList())
                else _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed")
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setSearchQuery(q: String) { _uiState.value = _uiState.value.copy(searchQuery = q) }

    fun createSupplier(name: String, contactPerson: String?, phone: String, email: String?, address: String?, balance: BigDecimal?) {
        viewModelScope.launch {
            try { apiService.createSupplier(CreateSupplierRequest(name, contactPerson, phone, email, address, balance)); loadSuppliers() } catch (_: Exception) {}
        }
    }

    fun deleteSupplier(id: Long) {
        viewModelScope.launch { try { apiService.deleteSupplier(id); loadSuppliers() } catch (_: Exception) {} }
    }
}
