package com.sithija.flickFinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var mAuth: FirebaseAuth

    private val TAG = "DashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        Log.d(TAG, "DashboardActivity onCreate started")

        try {
            setContentView(R.layout.activity_main)
            Log.d(TAG, "Dashboard layout set")


            mAuth = FirebaseAuth.getInstance()


            if (mAuth.currentUser == null) {
                Log.e(TAG, "User not authenticated, redirecting to login")
                navigateToLogin()
                return
            }

            Log.d(TAG, "User authenticated: ${mAuth.currentUser?.email}")

            if (savedInstanceState == null) {
                loadHomeFragment()
            }

            setupBottomNavigation()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)

            navigateToLogin()
        }
    }

    private fun loadHomeFragment() {
        try {
            Log.d(TAG, "Loading Home Fragment")


            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

            if (navHostFragment != null) {
                val navController = navHostFragment.navController

                navController.navigate(R.id.nav_home)
                Log.d(TAG, "Home Fragment loaded via NavController")
            } else {
                Log.e(TAG, "NavHostFragment not found")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error loading Home Fragment: ${e.message}", e)
        }
    }

    private fun setupBottomNavigation() {
        try {
            bottomNav = findViewById(R.id.bottomNavigation)
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {

                        Log.d(TAG, "Home navigation selected")
                        navController.navigate(R.id.homeFragment)
                        true
                    }
                    R.id.nav_movies -> {

                        Log.d(TAG, "Movies navigation selected")
                        navController.navigate(R.id.movieListFragment)
                        true
                    }

                    R.id.nav_profile -> {

                        Log.d(TAG, "Profile navigation selected")

                        navController.navigate(R.id.userProfileFragment)
                        true
                    }
                    else -> false
                }
            }
            Log.d(TAG, "Bottom navigation setup complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up bottom navigation: ${e.message}", e)
        }
    }

    private fun navigateToLogin() {
        try {
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to login: ${e.message}", e)
            Toast.makeText(this, "Unable to navigate to login screen", Toast.LENGTH_LONG).show()
            finishAffinity() // Close the app if navigation fails
        }
    }
}