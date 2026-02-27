package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.CreateItemRequest
import com.thozhilpro.app.data.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class ItemsUiState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    val filteredItems: List<Item>
        get() = if (searchQuery.isBlank()) items
        else items.filter { it.name.contains(searchQuery, true) || (it.category?.contains(searchQuery, true) == true) }
}

@HiltViewModel
class ItemsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(ItemsUiState())
    val uiState: StateFlow<ItemsUiState> = _uiState

    init { loadItems() }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getItems()
                if (res.isSuccessful) _uiState.value = _uiState.value.copy(isLoading = false, items = res.body() ?: emptyList())
                else _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed")
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setSearchQuery(q: String) { _uiState.value = _uiState.value.copy(searchQuery = q) }

    fun createItem(name: String, category: String?, unit: String?, quantity: Int, purchaseRate: BigDecimal?, wholesalePrice: BigDecimal?, retailPrice: BigDecimal?, location: String?) {
        viewModelScope.launch {
            try { apiService.createItem(CreateItemRequest(name, category, unit, quantity, purchaseRate, wholesalePrice, retailPrice, location)); loadItems() } catch (_: Exception) {}
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch { try { apiService.deleteItem(id); loadItems() } catch (_: Exception) {} }
    }
}
