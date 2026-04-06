package com.memorymatch.ui.lobby

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.memorymatch.R
import com.memorymatch.databinding.ActivityLoginBinding
import com.memorymatch.model.AppDatabase
import com.memorymatch.model.User
import com.memorymatch.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.btnLoginSubmit.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.login_error_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().getUser(username)
                if (user == null) {
                    // Create new user if not exists (Simplified for this requirement)
                    db.userDao().insertUser(User(username, password))
                    SessionManager.login(this@LoginActivity, username)
                    Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
                    finish()
                } else if (user.password == password) {
                    // Log in existing user
                    SessionManager.login(this@LoginActivity, username)
                    Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
