package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.local.PreferencesManager
import com.thozhilpro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val companyName: String = "",
    val phone: String = "",
    val currency: String = "INR",
    val gstNumber: String = "",
    val gstPercentage: String = "18",
    val address: String = "",
    val paymentDelayDays: String = "10",
    val stockAgeDays: String = "10",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init { loadSettings() }

    fun loadSettings() {
        viewModelScope.launch {
            try {
                val res = apiService.getSettings()
                if (res.isSuccessful && res.body() != null) {
                    val t = res.body()!!
                    _uiState.value = SettingsUiState(
                        companyName = t.companyName ?: "",
                        phone = t.phone ?: "",
                        currency = t.currency ?: "INR",
                        gstNumber = t.gstNumber ?: "",
                        gstPercentage = t.gstPercentage?.toPlainString() ?: "18",
                        address = t.address ?: "",
                        paymentDelayDays = (t.paymentDelayAlertDays ?: 10).toString(),
                        stockAgeDays = (t.stockAgeAlertDays ?: 10).toString()
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.value = when (field) {
            "companyName" -> _uiState.value.copy(companyName = value)
            "phone" -> _uiState.value.copy(phone = value)
            "currency" -> _uiState.value.copy(currency = value)
            "gstNumber" -> _uiState.value.copy(gstNumber = value)
            "gstPercentage" -> _uiState.value.copy(gstPercentage = value)
            "address" -> _uiState.value.copy(address = value)
            "paymentDelayDays" -> _uiState.value.copy(paymentDelayDays = value)
            "stockAgeDays" -> _uiState.value.copy(stockAgeDays = value)
            else -> _uiState.value
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveSuccess = false, error = null)
            try {
                val s = _uiState.value
                val req = mapOf(
                    "companyName" to s.companyName,
                    "phone" to s.phone,
                    "currency" to s.currency,
                    "gstNumber" to s.gstNumber,
                    "gstPercentage" to s.gstPercentage,
                    "address" to s.address,
                    "paymentDelayAlertDays" to s.paymentDelayDays,
                    "stockAgeAlertDays" to s.stockAgeDays
                )
                val res = apiService.updateSettings(req)
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                } else {
                    _uiState.value = _uiState.value.copy(isSaving = false, error = "Failed to save")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.changePassword(currentPassword, newPassword)
            result.fold(
                onSuccess = { onResult(it) },
                onFailure = { onResult(it.message ?: "Failed") }
            )
        }
    }
}
