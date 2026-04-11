package com.ashaworker.triage.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ashaworker.triage.data.model.LoginRequest
import com.ashaworker.triage.data.model.LoginResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) {

    private val sharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun login(workerId: String, pin: String): Result<LoginResponse> {
        val response = apiService.login(LoginRequest(workerId, pin))
        return if (response.isSuccessful && response.body() != null) {
            val payload = response.body()!!
            sharedPreferences.edit()
                .putString("worker_id", payload.workerId)
                .putString("worker_name", payload.name)
                .putString("worker_block", payload.block)
                .putString("worker_district", payload.district)
                .putString("jwt", payload.token)
                .putString("pin", pin)
                .apply()
            Result.success(payload)
        } else {
            val cachedWorkerId = sharedPreferences.getString("worker_id", null)
            val cachedPin = sharedPreferences.getString("pin", null)
            if (cachedWorkerId == workerId && cachedPin == pin) {
                Result.success(
                    LoginResponse(
                        token = sharedPreferences.getString("jwt", "") ?: "",
                        workerId = cachedWorkerId,
                        name = sharedPreferences.getString("worker_name", "") ?: "",
                        block = sharedPreferences.getString("worker_block", "") ?: "",
                        district = sharedPreferences.getString("worker_district", "") ?: "",
                        role = "WORKER"
                    )
                )
            } else {
                Result.failure(IllegalArgumentException("Invalid credentials"))
            }
        }
    }

    fun getWorkerId(): String = sharedPreferences.getString("worker_id", "") ?: ""

    fun getWorkerName(): String = sharedPreferences.getString("worker_name", "") ?: ""

    fun getWorkerArea(): String {
        val block = sharedPreferences.getString("worker_block", "") ?: ""
        val district = sharedPreferences.getString("worker_district", "") ?: ""
        return "$block, $district"
    }
}
