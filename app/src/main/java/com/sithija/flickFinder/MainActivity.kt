package com.sithija.flickFinder

import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.sithija.flickFinder.databinding.ActivityMainBinding
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding and set content view ONCE
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "MainActivity onCreate started")
        try {
            enableEdgeToEdge()
            Log.d(TAG, "Main layout set")

            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            mAuth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase initialized in MainActivity")

            // Setup navigation AFTER setting content view
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            // Connect BottomNavigationView with NavController
            binding.bottomNavigation.setupWithNavController(navController)

            // Setup fullscreen
            setupFullscreen()


        } catch (e: Exception) {
            Log.e(TAG, "Critical error in MainActivity: ${e.message}", e)
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



    private fun navigateToLogin() {
        try {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Login: ${e.message}", e)
            finishAffinity() // Emergency exit
        }
    }

    private fun navigateToDashboard() {
        try {
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to Dashboard: ${e.message}", e)
            navigateToLogin() // Fallback to login
        }
    }
}