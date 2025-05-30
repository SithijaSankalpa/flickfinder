package com.sithija.flickFinder.ui.UserProfile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> = _logoutEvent

    private val TAG = "UserProfileViewModel"

    fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _logoutEvent.value = true
            return
        }

        _loading.value = true

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                _loading.value = false
                if (document.exists()) {
                    val userName = document.getString("name") ?: document.getString("username")
                    _userName.value = userName ?: extractNameFromEmail(currentUser.email)
                } else {
                    _userName.value = currentUser.displayName ?: extractNameFromEmail(currentUser.email)
                }
            }
            .addOnFailureListener { exception ->
                _loading.value = false
                Log.e(TAG, "Failed to fetch user profile: ${exception.message}", exception)
                _userName.value = currentUser.displayName ?: extractNameFromEmail(currentUser.email)
            }
    }

    fun performLogout() {
        auth.signOut()
        _logoutEvent.value = true
    }

    private fun extractNameFromEmail(email: String?): String {
        return if (!email.isNullOrEmpty()) {
            val namePart = email.substringBefore("@")
            namePart.replace(Regex("[._]"), " ")
                .split(" ")
                .joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }
        } else {
            "User"
        }
    }
}