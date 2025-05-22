package com.sithija.flickFinder

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class BookMyGameApplication : Application() {
    private val TAG = "FlickFinder"

    override fun onCreate() {
        super.onCreate()

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)

            // Enable Firebase database persistence (offline capabilities)
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)

            Log.d(TAG, "Firebase initialized in Application class")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase in Application class: ${e.message}", e)
        }
    }
}