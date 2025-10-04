package com.zjgsu.ifm_preview.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zjgsu.ifm_preview.core.constants.AppConstants
import com.zjgsu.ifm_preview.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeViews()
        checkLoginStatus()
    }
    
    private fun initializeViews() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString()
            
            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }
        
        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString()
            
            if (validateInput(email, password)) {
                performRegistration(email, password)
            }
        }
        
        binding.textSkip.setOnClickListener {
            navigateToMainActivity()
        }
    }
    
    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences(AppConstants.Preferences.SHARED_PREFS_NAME, MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean(AppConstants.Preferences.KEY_USER_LOGGED_IN, false)
        
        if (isLoggedIn) {
            navigateToMainActivity()
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.editTextEmail.error = "请输入邮箱"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.error = "邮箱格式不正确"
            return false
        }
        
        if (password.isEmpty()) {
            binding.editTextPassword.error = "请输入密码"
            return false
        }
        
        if (password.length < 6) {
            binding.editTextPassword.error = "密码至少6位"
            return false
        }
        
        return true
    }
    
    private fun performLogin(email: String, password: String) {
        binding.buttonLogin.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE
        
        android.os.Handler().postDelayed({
            if (email == "test@example.com" && password == "123456") {
                saveLoginStatus(email)
                navigateToMainActivity()
            } else {
                showError("登录失败，请检查邮箱和密码")
            }
            
            binding.buttonLogin.isEnabled = true
            binding.progressBar.visibility = android.view.View.GONE
        }, 1500)
    }
    
    private fun performRegistration(email: String, password: String) {
        binding.buttonRegister.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE
        
        android.os.Handler().postDelayed({
            saveLoginStatus(email)
            navigateToMainActivity()
            
            binding.buttonRegister.isEnabled = true
            binding.progressBar.visibility = android.view.View.GONE
        }, 1500)
    }
    
    private fun saveLoginStatus(email: String) {
        val sharedPreferences = getSharedPreferences(AppConstants.Preferences.SHARED_PREFS_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(AppConstants.Preferences.KEY_USER_LOGGED_IN, true)
        editor.putString(AppConstants.Preferences.KEY_USER_EMAIL, email)
        editor.putString(AppConstants.Preferences.KEY_USER_NAME, email.substringBefore("@"))
        editor.apply()
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
