package com.sithija.flickFinder.ui.UserProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val location: String = "",
    val profileImageUrl: String = ""
)

class UserProfileViewModel : ViewModel() {

    // LiveData for user profile
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // LiveData for logout status
    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> = _logoutStatus

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // TODO: Replace this with actual data loading from:
                // - Room Database
                // - SharedPreferences
                // - API call
                // - Firebase/any other backend service

                // Simulating API call delay
                kotlinx.coroutines.delay(1000)

                // Sample data - replace with actual data loading
                val profile = getUserProfileFromStorage()
                _userProfile.value = profile

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Failed to load profile: ${e.message}"
            }
        }
    }

    fun updateProfile(updatedProfile: UserProfile) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // TODO: Save updated profile to:
                // - Room Database
                // - SharedPreferences
                // - API call
                // - Firebase/any other backend service

                saveUserProfileToStorage(updatedProfile)
                _userProfile.value = updatedProfile

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Failed to update profile: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // TODO: Perform logout operations:
                // - Clear user session from SharedPreferences
                // - Clear cached data from Room Database
                // - Revoke authentication tokens
                // - Clear any other user-related data

                clearUserData()

                _isLoading.value = false
                _logoutStatus.value = true

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Logout failed: ${e.message}"
            }
        }
    }

    private fun getUserProfileFromStorage(): UserProfile {
        // TODO: Implement actual data retrieval
        // This is sample data - replace with your actual implementation

        // Example using SharedPreferences:
        /*
        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return UserProfile(
            id = sharedPrefs.getString("user_id", "") ?: "",
            name = sharedPrefs.getString("user_name", "") ?: "",
            email = sharedPrefs.getString("user_email", "") ?: "",
            phoneNumber = sharedPrefs.getString("user_phone", "") ?: "",
            dateOfBirth = sharedPrefs.getString("user_dob", "") ?: "",
            location = sharedPrefs.getString("user_location", "") ?: "",
            profileImageUrl = sharedPrefs.getString("user_image_url", "") ?: ""
        )
        */

        // Sample data for now
        return UserProfile(
            id = "1",
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "+1 234 567 8900",
            dateOfBirth = "January 15, 1990",
            location = "New York, USA",
            profileImageUrl = ""
        )
    }

    private fun saveUserProfileToStorage(profile: UserProfile) {
        // TODO: Implement actual data saving

        // Example using SharedPreferences:
        /*
        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("user_id", profile.id)
        editor.putString("user_name", profile.name)
        editor.putString("user_email", profile.email)
        editor.putString("user_phone", profile.phoneNumber)
        editor.putString("user_dob", profile.dateOfBirth)
        editor.putString("user_location", profile.location)
        editor.putString("user_image_url", profile.profileImageUrl)
        editor.apply()
        */
    }

    private fun clearUserData() {
        // TODO: Implement actual data clearing

        // Example using SharedPreferences:
        /*
        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
        */

        // Clear any cached user data
        _userProfile.value = UserProfile()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun resetLogoutStatus() {
        _logoutStatus.value = false
    }
}