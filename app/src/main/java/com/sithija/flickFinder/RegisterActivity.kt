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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG, "RegisterActivity onCreate started")

        try {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance()

            // Setup UI
            setupUI()
            setupClickListeners()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing register screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        // Hide status bar
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.progressBar)

        Log.d(TAG, "UI components initialized")
    }

    private fun setupClickListeners() {
        // Set click listener for Register button
        btnRegister.setOnClickListener {
            registerUser()
        }

        // Set click listener for Login text
        tvLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun registerUser() {
        try {
            // Get input values
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validate inputs
            if (!validateInputs(email, password, confirmPassword)) {
                return
            }

            // Show progress bar
            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled = false

            Log.d(TAG, "Attempting to register with email: $email")

            // Create user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true

                    if (task.isSuccessful) {
                        Log.d(TAG, "Registration successful, user ID: ${mAuth.currentUser?.uid}")

                        // Sign out the user after registration (as per your requirement)
                        mAuth.signOut()

                        // Navigate back to login after successful registration
                        navigateToLoginAfterRegistration()
                    } else {
                        Log.e(TAG, "Registration failed", task.exception)
                        val errorMessage = task.exception?.message ?: "Registration failed"
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { exception ->
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    Log.e(TAG, "Registration failure", exception)
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            btnRegister.isEnabled = true
            Log.e(TAG, "Critical error in registration process: ${e.message}", e)
            Toast.makeText(this, "Registration error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String): Boolean {
        when {
            TextUtils.isEmpty(email) -> {
                etEmail.error = "Email is required"
                etEmail.requestFocus()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Please enter a valid email"
                etEmail.requestFocus()
                return false
            }
            TextUtils.isEmpty(password) -> {
                etPassword.error = "Password is required"
                etPassword.requestFocus()
                return false
            }
            password.length < 6 -> {
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return false
            }
            TextUtils.isEmpty(confirmPassword) -> {
                etConfirmPassword.error = "Please confirm your password"
                etConfirmPassword.requestFocus()
                return false
            }
            password != confirmPassword -> {
                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()
                return false
            }
        }
        return true
    }

    private fun navigateToLogin() {
        try {
            Log.d(TAG, "Navigating back to Login")
            // Simply finish this activity to go back to login
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to login: ${e.message}", e)
            Toast.makeText(this, "Error navigating to login screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLoginAfterRegistration() {
        try {
            Log.d(TAG, "Navigating to Login after successful registration")
            // Create a new intent to login with special flag to indicate successful registration
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.putExtra("REGISTRATION_SUCCESS", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish() // Close register activity
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to login after registration: ${e.message}", e)
            // Fallback - just finish this activity
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Clear any error messages when activity starts
        etEmail.error = null
        etPassword.error = null
        etConfirmPassword.error = null
    }
}