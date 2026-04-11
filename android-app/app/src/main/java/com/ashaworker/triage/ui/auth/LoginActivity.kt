package com.ashaworker.triage.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ashaworker.triage.MainActivity
import com.ashaworker.triage.databinding.LoginActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val workerId = binding.etWorkerId.text?.toString()?.trim().orEmpty()
            val pin = binding.etPin.text?.toString()?.trim().orEmpty()
            if (workerId.isBlank() || pin.length != 4) {
                Toast.makeText(this, "Enter valid worker ID and 4-digit PIN", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                binding.btnLogin.isEnabled = false
                val result = viewModel.login(workerId, pin)
                binding.btnLogin.isEnabled = true
                result.onSuccess { profile ->
                    binding.tvWorkerDetails.text = "${profile.name} - ${profile.block}, ${profile.district}"
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }.onFailure {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed. Check network or credentials.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
