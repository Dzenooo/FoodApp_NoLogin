package com.example.easyfood.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.easyfood.UserSessionManager
import com.example.easyfood.databinding.ActivityLoginBinding
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.db.UserDao
import com.example.easyfood.pojo.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize your database and get userDao
        val db = MealDatabase.getInstance(applicationContext)  // your DB instance
        userDao = db.userDao()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(username, password)
        }

        binding.tvRegisterLink.setOnClickListener {
            // Open RegistrationActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish() // optional
        }
    }


    private fun loginUser(username: String, password: String) {
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                userDao.getUserByUsername(username)
            }

            if (user == null) {
                Toast.makeText(this@LoginActivity, "Username or password is incorrect", Toast.LENGTH_SHORT).show()
            } else {
                if (user.password == password) {
                    UserSessionManager.saveLoggedInUserId(applicationContext, user.id)
                    navigateToHome(user)
                } else {
                    Toast.makeText(this@LoginActivity, "Username or password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToHome(user: User) {
        val intent = Intent(this, MainActivity::class.java) // or your home screen
        intent.putExtra("userId", user.id)
        startActivity(intent)
        finish()
    }
}
