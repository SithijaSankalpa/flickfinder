package com.sithija.flickFinder

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "LoginActivity onCreate started")

        try {

            mAuth = FirebaseAuth.getInstance()


            setupUI()
            setupClickListeners()


            if (intent.getBooleanExtra("REGISTRATION_SUCCESS", false)) {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing login screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())


        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        Log.d(TAG, "UI components initialized")
    }

    private fun setupClickListeners() {

        btnLogin.setOnClickListener {
            loginUser()
        }


        tvRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun loginUser() {
        try {

            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()


            if (!validateInputs(email, password)) {
                return
            }


            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            Log.d(TAG, "Attempting to login with email: $email")


            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true

                    if (task.isSuccessful) {
                        Log.d(TAG, "Login successful, user ID: ${mAuth.currentUser?.uid}")
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    } else {
                        Log.e(TAG, "Login failed", task.exception)
                        val errorMessage = task.exception?.message ?: "Authentication failed"
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { exception ->
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    Log.e(TAG, "Login failure", exception)
                    Toast.makeText(this@LoginActivity, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
            Log.e(TAG, "Critical error in login process: ${e.message}", e)
            Toast.makeText(this, "Login error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        when {
            TextUtils.isEmpty(email) -> {
                etUsername.error = "Email is required"
                etUsername.requestFocus()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etUsername.error = "Please enter a valid email"
                etUsername.requestFocus()
                return false
            }
            TextUtils.isEmpty(password) -> {
                etPassword.error = "Password is required"
                etPassword.requestFocus()
                return false
            }
        }
        return true
    }

    private fun navigateToRegister() {
        try {
            Log.d(TAG, "Navigating to Register")
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            // Don't finish() here - user should be able to come back with back button
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to register: ${e.message}", e)
            Toast.makeText(this, "Error opening register screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDashboard() {
        try {
            Log.d(TAG, "Navigating to Dashboard")
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish() // Prevent going back to login with back button
            Log.d(TAG, "Successfully navigated to Dashboard")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Dashboard: ${e.message}", e)
            Toast.makeText(this, "Error opening Dashboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        etUsername.error = null
        etPassword.error = null
    }
}