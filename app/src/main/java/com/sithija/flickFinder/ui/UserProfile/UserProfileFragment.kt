package com.sithija.flickFinder.ui.UserProfile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sithija.flickFinder.R
import com.sithija.flickFinder.LoginActivity
import com.sithija.flickFinder.Model.User
import com.sithija.flickFinder.ui.UserProfile.UserProfileViewModel

class UserProfileFragment : Fragment() {

    // ViewModel
    private lateinit var viewModel: UserProfileViewModel

    // UI Components
    private lateinit var ivProfilePicture: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]

        // Initialize UI components
        initializeViews(view)

        // Set up click listeners
        setupClickListeners()

        // Observe ViewModel data
        observeViewModel()

        return view
    }

    private fun initializeViews(view: View) {
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture)
        tvUserName = view.findViewById(R.id.tv_user_name)
        tvUserEmail = view.findViewById(R.id.tv_user_email)

        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        btnSettings = view.findViewById(R.id.btn_settings)
        btnLogout = view.findViewById(R.id.btn_logout)
        progressBar = view.findViewById(R.id.progress_bar)
    }

    private fun setupClickListeners() {
        // Edit Profile Button
        btnEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to edit profile activity or fragment
        }

        // Settings Button
        btnSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to settings activity or fragment
        }

        // Logout Button
        btnLogout.setOnClickListener {
            handleLogout()
        }
    }

    private fun observeViewModel() {
        // Observe user profile data
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            updateUI(User())
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }

        // Observe logout status
        viewModel.logoutStatus.observe(viewLifecycleOwner) { loggedOut ->
            if (loggedOut) {
                navigateToLogin()
                viewModel.resetLogoutStatus()
            }
        }
    }

    private fun updateUI(profile: com.sithija.flickFinder.Model.User) {
        tvUserName.text = profile.username
        tvUserEmail.text = profile.email


        // TODO: Load profile picture using image loading library like Glide or Picasso
        // if (profile.profileImageUrl.isNotEmpty()) {
        //     Glide.with(this).load(profile.profileImageUrl).into(ivProfilePicture)
        // }
    }



    private fun handleLogout() {
        // Show confirmation dialog (optional)
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun navigateToLogin() {
        // Navigate to Login Activity
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finish current activity
        requireActivity().finish()

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    companion object {
        // Factory method to create new instance of fragment
        fun newInstance(): UserProfileFragment {
            return UserProfileFragment()
        }
    }
}