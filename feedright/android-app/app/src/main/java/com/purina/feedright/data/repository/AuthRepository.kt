package com.purina.feedright.data.repository

import android.content.SharedPreferences
import com.purina.feedright.data.remote.AuthRequest
import com.purina.feedright.data.remote.FeedRightApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling authentication
 */
@Singleton
class AuthRepository @Inject constructor(
    private val api: FeedRightApi,
    private val sharedPreferences: SharedPreferences
) {

    suspend fun login(phoneNumber: String, pin: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(AuthRequest(phoneNumber, pin))

            // Save token and salesman info
            sharedPreferences.edit().apply {
                putString("auth_token", response.token)
                putString("salesman_id", response.salesman.id)
                putString("salesman_name", response.salesman.name)
                apply()
            }

            Result.success(response.salesman.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString("auth_token", null) != null
    }

    fun getSalesmanId(): String? {
        return sharedPreferences.getString("salesman_id", null)
    }

    fun getSalesmanName(): String? {
        return sharedPreferences.getString("salesman_name", null)
    }

    fun logout() {
        sharedPreferences.edit().apply {
            remove("auth_token")
            remove("salesman_id")
            remove("salesman_name")
            apply()
        }
    }
}
