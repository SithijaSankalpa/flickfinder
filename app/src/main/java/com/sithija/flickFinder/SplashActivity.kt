package com.sithija.flickFinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 3000 // 3 seconds
    private lateinit var mAuth: FirebaseAuth
    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val appLogo: ImageView = findViewById(R.id.appLogo)
        val appTitle: TextView = findViewById(R.id.appTitle)
        val appSlogan: TextView = findViewById(R.id.appSlogan)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        appLogo.startAnimation(fadeIn)
        appTitle.startAnimation(slideUp)
        appSlogan.startAnimation(fadeIn)
        progressBar.startAnimation(scaleUp)

        Log.d(TAG, "SplashActivity onCreate started")
        try {
            // Setup fullscreen
            setupFullscreen()

            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            mAuth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase initialized in SplashActivity")

            // Set up splash screen timeout
            Handler(Looper.getMainLooper()).postDelayed({
                // Always navigate to login after splash screen

                checkUserAuthenticationStatus()
            }, SPLASH_DELAY)

        } catch (e: Exception) {
            Log.e(TAG, "Critical error in SplashActivity: ${e.message}", e)
            // Emergency fallback - always go to login on error
            navigateToLogin()
        }
    }

    private fun setupFullscreen() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.hide(WindowInsets.Type.statusBars())
                Log.d(TAG, "Android R+ fullscreen setup complete")
            } else {
                @Suppress("DEPRECATION")
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                Log.d(TAG, "Legacy fullscreen setup complete")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting fullscreen: ${e.message}", e)
            // Non-critical error, continue execution
        }
    }
    private fun checkUserAuthenticationStatus() {
        try {
            val currentUser = mAuth.currentUser

            if (currentUser != null) {
                // User is already logged in
                Log.d(TAG, "User is already logged in, user ID: ${currentUser.uid}")
                navigateToDashboard()
            } else {
                // User is not logged in
                Log.d(TAG, "User is not logged in, redirecting to login")
                navigateToLogin()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking authentication status: ${e.message}", e)
            // If there's an error, navigate to login as fallback
            navigateToLogin()
        }
    }
    private fun navigateToDashboard() {
        try {
            Log.d(TAG, "Navigating to Dashboard")
            val intent = Intent(this@SplashActivity, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
            Log.d(TAG, "Successfully navigated to Dashboard")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Dashboard: ${e.message}", e)
            // If navigation fails, try login as fallback
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        try {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Login: ${e.message}", e)
            finishAffinity() // Emergency exit
        }
    }
}