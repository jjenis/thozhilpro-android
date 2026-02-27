package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.CreateExpenseRequest
import com.thozhilpro.app.data.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class ExpensesUiState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    val filteredExpenses: List<Expense>
        get() = if (searchQuery.isBlank()) expenses
        else expenses.filter { it.type.contains(searchQuery, true) || (it.description?.contains(searchQuery, true) == true) }
}

@HiltViewModel
class ExpensesViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState

    init { loadExpenses() }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getExpenses()
                if (res.isSuccessful) _uiState.value = _uiState.value.copy(isLoading = false, expenses = res.body() ?: emptyList())
                else _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed")
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setSearchQuery(q: String) { _uiState.value = _uiState.value.copy(searchQuery = q) }

    fun createExpense(type: String, amount: BigDecimal, expenseDate: String, description: String?) {
        viewModelScope.launch {
            try { apiService.createExpense(CreateExpenseRequest(type, amount, description, expenseDate)); loadExpenses() } catch (_: Exception) {}
        }
    }

    fun updateExpense(id: Long, type: String, amount: BigDecimal, expenseDate: String, description: String?) {
        viewModelScope.launch {
            try { apiService.updateExpense(id, CreateExpenseRequest(type, amount, description, expenseDate)); loadExpenses() } catch (_: Exception) {}
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch { try { apiService.deleteExpense(id); loadExpenses() } catch (_: Exception) {} }
    }
}
