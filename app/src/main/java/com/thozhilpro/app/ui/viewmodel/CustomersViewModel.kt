package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.*
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
    val error: String? = null,
    val customerDetail: CustomerDetail? = null,
    val showingDetail: Boolean = false,
    val showingDelayedOnly: Boolean = false,
    val settlementResult: SettlementResponse? = null,
    val settlementError: String? = null
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
            _uiState.value = _uiState.value.copy(isLoading = true, showingDelayedOnly = false)
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

    fun updateCustomer(id: Long, firstName: String, lastName: String, phone: String, email: String?, address: String?, idType: String?, idNumber: String?) {
        viewModelScope.launch {
            try {
                apiService.updateCustomer(id, CreateCustomerRequest(firstName, lastName, phone, email, address))
                loadCustomers()
                if (_uiState.value.showingDetail) loadCustomerDetail(id)
            } catch (_: Exception) {}
        }
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            try { apiService.deleteCustomer(id); loadCustomers() } catch (_: Exception) {}
        }
    }

    fun loadCustomerDetail(customerId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getCustomerDetail(customerId)
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, customerDetail = res.body(), showingDetail = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load customer detail")
                }
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun goBackToList() {
        _uiState.value = _uiState.value.copy(showingDetail = false, customerDetail = null, settlementResult = null, settlementError = null)
    }

    fun settleBills(request: SettlementRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, settlementError = null)
            try {
                val res = apiService.settleBills(request)
                if (res.isSuccessful) {
                    val body = res.body()
                    if (body?.error != null) {
                        _uiState.value = _uiState.value.copy(isLoading = false, settlementError = body.error)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, settlementResult = body)
                        loadCustomerDetail(request.customerId)
                        loadCustomers()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, settlementError = "Settlement failed")
                }
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, settlementError = e.message) }
        }
    }

    fun clearSettlementResult() {
        _uiState.value = _uiState.value.copy(settlementResult = null, settlementError = null)
    }

    fun loadDelayedPaymentCustomers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showingDelayedOnly = true)
            try {
                val res = apiService.getDelayedPaymentCustomers()
                if (res.isSuccessful) _uiState.value = _uiState.value.copy(isLoading = false, customers = res.body() ?: emptyList())
                else _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load")
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }
}
