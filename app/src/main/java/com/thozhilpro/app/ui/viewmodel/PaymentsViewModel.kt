package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.CreatePaymentRequest
import com.thozhilpro.app.data.model.Payment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class PaymentsUiState(
    val isLoading: Boolean = false,
    val payments: List<Payment> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    val filteredPayments: List<Payment>
        get() = if (searchQuery.isBlank()) payments
        else payments.filter {
            (it.customer?.fullName?.contains(searchQuery, true) == true) ||
            (it.supplier?.name?.contains(searchQuery, true) == true) ||
            (it.description?.contains(searchQuery, true) == true)
        }
}

@HiltViewModel
class PaymentsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentsUiState())
    val uiState: StateFlow<PaymentsUiState> = _uiState

    init { loadPayments() }

    fun loadPayments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getPayments()
                if (res.isSuccessful) _uiState.value = _uiState.value.copy(isLoading = false, payments = res.body() ?: emptyList())
                else _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed")
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setSearchQuery(q: String) { _uiState.value = _uiState.value.copy(searchQuery = q) }

    fun createPayment(amount: BigDecimal, paymentMode: String, paymentType: String, paymentDate: String, description: String?) {
        viewModelScope.launch {
            try {
                apiService.createPayment(CreatePaymentRequest(amount = amount, paymentMode = paymentMode, paymentType = paymentType, paymentDate = paymentDate, description = description))
                loadPayments()
            } catch (_: Exception) {}
        }
    }

    fun updatePayment(id: Long, amount: BigDecimal, paymentMode: String, paymentType: String, paymentDate: String, description: String?) {
        viewModelScope.launch {
            try {
                apiService.updatePayment(id, CreatePaymentRequest(amount = amount, paymentMode = paymentMode, paymentType = paymentType, paymentDate = paymentDate, description = description))
                loadPayments()
            } catch (_: Exception) {}
        }
    }

    fun deletePayment(id: Long) {
        viewModelScope.launch { try { apiService.deletePayment(id); loadPayments() } catch (_: Exception) {} }
    }
}
