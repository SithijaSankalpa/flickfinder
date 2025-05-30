package com.sithija.flickFinder.ui.UserProfile

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sithija.flickFinder.LoginActivity
import com.sithija.flickFinder.R

class UserProfileFragment : Fragment() {

    private val viewModel: UserProfileViewModel by viewModels()

    private lateinit var progressBar: ProgressBar
    private lateinit var ivProfilePicture: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var llWatchlist: LinearLayout
    private lateinit var llCollection: LinearLayout
    private lateinit var llRatings: LinearLayout
    private lateinit var btnLogout: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
        observeViewModel()

        viewModel.loadUserProfile()
    }

    private fun initializeViews(view: View) {
        progressBar = view.findViewById(R.id.progress_bar)
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture)
        tvUserName = view.findViewById(R.id.tv_user_name)
        llWatchlist = view.findViewById(R.id.ll_watchlist)
        llCollection = view.findViewById(R.id.ll_collection)
        llRatings = view.findViewById(R.id.ll_ratings)
        btnLogout = view.findViewById(R.id.btn_logout)
    }

    private fun setupClickListeners() {
        llWatchlist.setOnClickListener {
            Toast.makeText(requireContext(), "Watchlist clicked", Toast.LENGTH_SHORT).show()
        }
        llCollection.setOnClickListener {
            Toast.makeText(requireContext(), "Collection clicked", Toast.LENGTH_SHORT).show()
        }
        llRatings.setOnClickListener {
            Toast.makeText(requireContext(), "Ratings clicked", Toast.LENGTH_SHORT).show()
        }
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.userName.observe(viewLifecycleOwner, Observer { name ->
            tvUserName.text = name
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.logoutEvent.observe(viewLifecycleOwner, Observer { shouldLogout ->
            if (shouldLogout == true) redirectToLogin()
        })
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        fun newInstance(): Fragment {

            return TODO("")
        }
    }
}