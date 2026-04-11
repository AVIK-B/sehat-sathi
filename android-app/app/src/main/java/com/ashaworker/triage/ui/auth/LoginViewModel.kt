package com.ashaworker.triage.ui.auth

import androidx.lifecycle.ViewModel
import com.ashaworker.triage.data.model.LoginResponse
import com.ashaworker.triage.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun login(workerId: String, pin: String): Result<LoginResponse> {
        return authRepository.login(workerId, pin)
    }
}
