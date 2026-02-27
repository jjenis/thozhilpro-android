package com.thozhilpro.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.thozhilpro.app.data.model.Tenant
import com.thozhilpro.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "thozhilpro_prefs")

class PreferencesManager(
    private val context: Context,
    private val gson: Gson
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_KEY = stringPreferencesKey("user")
        private val TENANT_KEY = stringPreferencesKey("tenant")
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _currentTenant = MutableStateFlow<Tenant?>(null)
    val currentTenant: StateFlow<Tenant?> = _currentTenant

    init {
        runBlocking {
            val prefs = context.dataStore.data.first()
            prefs[USER_KEY]?.let {
                _currentUser.value = gson.fromJson(it, User::class.java)
            }
            prefs[TENANT_KEY]?.let {
                _currentTenant.value = gson.fromJson(it, Tenant::class.java)
            }
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.first()
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }.first()
    }

    suspend fun saveTokens(token: String, refreshToken: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveAuthData(token: String, refreshToken: String, user: User, tenant: Tenant) {
        _currentUser.value = user
        _currentTenant.value = tenant
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            it[REFRESH_TOKEN_KEY] = refreshToken
            it[USER_KEY] = gson.toJson(user)
            it[TENANT_KEY] = gson.toJson(tenant)
        }
    }

    suspend fun saveUser(user: User) {
        _currentUser.value = user
        context.dataStore.edit {
            it[USER_KEY] = gson.toJson(user)
        }
    }

    suspend fun saveTenant(tenant: Tenant) {
        _currentTenant.value = tenant
        context.dataStore.edit {
            it[TENANT_KEY] = gson.toJson(tenant)
        }
    }

    suspend fun clearAuthData() {
        _currentUser.value = null
        _currentTenant.value = null
        context.dataStore.edit { it.clear() }
    }

    suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
