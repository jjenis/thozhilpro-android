package com.thozhilpro.app.data.repository

import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.local.PreferencesManager
import com.thozhilpro.app.data.model.*
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    val currentUser: StateFlow<User?> = preferencesManager.currentUser
    val currentTenant: StateFlow<Tenant?> = preferencesManager.currentTenant

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                preferencesManager.saveAuthData(data.token, data.refreshToken, data.user, data.tenant)
                Result.success(data)
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.register(RegisterRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                preferencesManager.saveAuthData(data.token, data.refreshToken, data.user, data.tenant)
                Result.success(data)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun firebaseLogin(idToken: String): Result<LoginResponse> {
        return try {
            val response = apiService.firebaseLogin(FirebaseLoginRequest(idToken))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                preferencesManager.saveAuthData(data.token, data.refreshToken, data.user, data.tenant)
                Result.success(data)
            } else {
                Result.failure(Exception("Firebase login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun firebaseRegister(idToken: String): Result<LoginResponse> {
        return try {
            val response = apiService.firebaseRegister(FirebaseLoginRequest(idToken))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                preferencesManager.saveAuthData(data.token, data.refreshToken, data.user, data.tenant)
                Result.success(data)
            } else {
                Result.failure(Exception("Firebase register failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeProfile(firstName: String, lastName: String, companyName: String, phone: String): Result<CompleteProfileResponse> {
        return try {
            val response = apiService.completeProfile(CompleteProfileRequest(firstName, lastName, companyName, phone))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                preferencesManager.saveUser(data.user)
                preferencesManager.saveTenant(data.tenant)
                Result.success(data)
            } else {
                Result.failure(Exception("Profile completion failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshSubscriptionStatus(): Result<Unit> {
        return try {
            val response = apiService.getSubscriptionStatus()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val tenant = preferencesManager.currentTenant.value
                if (tenant != null) {
                    tenant.status = data["status"]?.toString() ?: "ACTIVE"
                    tenant.subscriptionEndsAt = data["subscriptionEndsAt"]?.toString()
                    preferencesManager.saveTenant(tenant)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to refresh subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        return try {
            val response = apiService.changePassword(mapOf("currentPassword" to currentPassword, "newPassword" to newPassword))
            if (response.isSuccessful) {
                Result.success("Password changed successfully")
            } else {
                Result.failure(Exception("Failed to change password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        preferencesManager.clearAuthData()
    }

    suspend fun isLoggedIn(): Boolean = preferencesManager.isLoggedIn()
}
