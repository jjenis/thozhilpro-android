package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.CreateCustomerRequest
import com.thozhilpro.app.data.model.Customer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class CustomersUiState(
    val isLoading: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    val filteredCustomers: List<Customer>
        get() = if (searchQuery.isBlank()) customers
        else customers.filter { it.fullName.contains(searchQuery, true) || it.phone.contains(searchQuery) }
}

@HiltViewModel
class CustomersViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomersUiState())
    val uiState: StateFlow<CustomersUiState> = _uiState

    init { loadCustomers() }

    fun loadCustomers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getCustomers()
                if (res.isSuccessful) _uiState.value = _uiState.value.copy(isLoading = false, customers = res.body() ?: emptyList())
                else _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load")
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setSearchQuery(q: String) { _uiState.value = _uiState.value.copy(searchQuery = q) }

    fun createCustomer(firstName: String, lastName: String, phone: String, email: String?, address: String?, previousBalance: BigDecimal?) {
        viewModelScope.launch {
            try {
                apiService.createCustomer(CreateCustomerRequest(firstName, lastName, phone, email, address, previousBalance))
                loadCustomers()
            } catch (_: Exception) {}
        }
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            try { apiService.deleteCustomer(id); loadCustomers() } catch (_: Exception) {}
        }
    }
}
