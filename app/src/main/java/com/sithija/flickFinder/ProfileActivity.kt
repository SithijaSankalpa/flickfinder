package com.sithija.flickFinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sithija.flickFinder.R
import com.sithija.flickFinder.ui.UserProfile.UserProfileFragment

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Load the fragment
        if (savedInstanceState == null) {
            loadUserProfileFragment()
        }
    }

    private fun loadUserProfileFragment() {
        val fragment = UserProfileFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle back button click
        onBackPressed()
        return true
    }
}