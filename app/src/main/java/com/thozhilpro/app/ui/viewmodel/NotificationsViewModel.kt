package com.thozhilpro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.model.NotificationMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationMessage> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init { loadNotifications() }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = apiService.getNotifications()
                if (res.isSuccessful) _uiState.value = NotificationsUiState(notifications = res.body() ?: emptyList())
                else _uiState.value = NotificationsUiState(error = "Failed")
            } catch (e: Exception) { _uiState.value = NotificationsUiState(error = e.message) }
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch { try { apiService.markNotificationRead(id); loadNotifications() } catch (_: Exception) {} }
    }

    fun markAllRead() {
        viewModelScope.launch { try { apiService.markAllNotificationsRead(); loadNotifications() } catch (_: Exception) {} }
    }
}
