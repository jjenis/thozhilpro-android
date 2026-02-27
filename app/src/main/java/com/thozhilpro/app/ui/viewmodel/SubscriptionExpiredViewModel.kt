package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionExpiredUiState(
    val isLoading: Boolean = false,
    val plans: List<Map<String, Any>> = emptyList(),
    val selectedPlan: String = "",
    val isYearly: Boolean = false,
    val isProcessing: Boolean = false,
    val isRenewed: Boolean = false,
    val orderId: String? = null,
    val error: String? = null
)

@HiltViewModel
class SubscriptionExpiredViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionExpiredUiState())
    val uiState: StateFlow<SubscriptionExpiredUiState> = _uiState

    init { loadPlans() }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getSubscriptionPlans()
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, plans = res.body() ?: emptyList())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun toggleBillingCycle() {
        _uiState.value = _uiState.value.copy(isYearly = !_uiState.value.isYearly)
    }

    fun selectPlan(plan: String) {
        _uiState.value = _uiState.value.copy(selectedPlan = plan)
    }

    fun startPayment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            try {
                val billingCycle = if (_uiState.value.isYearly) "YEARLY" else "MONTHLY"
                val req = mapOf<String, Any>("plan" to _uiState.value.selectedPlan, "billingCycle" to billingCycle)
                val res = apiService.createSubscriptionOrder(req)
                if (res.isSuccessful && res.body() != null) {
                    val data = res.body()!!
                    val orderId = data["orderId"]?.toString()
                    _uiState.value = _uiState.value.copy(isProcessing = false, orderId = orderId)
                    // Razorpay checkout would be triggered here from the Activity
                } else {
                    _uiState.value = _uiState.value.copy(isProcessing = false, error = "Failed to create order")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isProcessing = false, error = e.message)
            }
        }
    }

    fun verifyPayment(razorpayPaymentId: String, razorpayOrderId: String, razorpaySignature: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            try {
                val req = mapOf<String, Any>(
                    "razorpay_payment_id" to razorpayPaymentId,
                    "razorpay_order_id" to razorpayOrderId,
                    "razorpay_signature" to razorpaySignature
                )
                val res = apiService.verifySubscriptionPayment(req)
                if (res.isSuccessful) {
                    authRepository.refreshSubscriptionStatus()
                    _uiState.value = _uiState.value.copy(isProcessing = false, isRenewed = true)
                } else {
                    _uiState.value = _uiState.value.copy(isProcessing = false, error = "Payment verification failed")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isProcessing = false, error = e.message)
            }
        }
    }

    fun refreshSubscriptionStatus() {
        viewModelScope.launch {
            authRepository.refreshSubscriptionStatus()
            _uiState.value = _uiState.value.copy(isRenewed = true)
        }
    }
}
