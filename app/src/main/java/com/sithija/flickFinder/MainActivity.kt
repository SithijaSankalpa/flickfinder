package com.sithija.flickFinder

import android.os.Build
import android.os.Bundle
import android.content.Intent

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        Log.d(TAG, "MainActivity onCreate started")
        try {
            enableEdgeToEdge()
            Log.d(TAG, "Main layout set")

            FirebaseApp.initializeApp(this)
            mAuth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase initialized in MainActivity")

            if (!isFirestoreInitialized) {
                try {
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                    FirebaseFirestore.getInstance().firestoreSettings = settings
                    isFirestoreInitialized = true

                    Log.d(TAG, "Firestore persistence enabled")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to enable Firestore persistence", e)
                }
            }

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController


            binding.bottomNavigation.setupWithNavController(navController)


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
    companion object {
        private var isFirestoreInitialized = false
    }

}