package com.sithija.flickFinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.sithija.flickFinder.ui.UserProfile.UserProfileFragment

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


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

        onBackPressed()
        return true
    }
}