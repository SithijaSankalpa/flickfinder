package com.sithija.flickFinder.ui.movieDetails

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sithija.flickFinder.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.sithija.flickFinder.databinding.FragmentMovieDetailsBinding
import kotlinx.coroutines.launch
import com.sithija.flickFinder.Model.Movie
import android.widget.TextView
import android.widget.Toast
import android.app.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sithija.flickFinder.adapter.FeedbackAdapter
import com.sithija.flickFinder.adapter.GenreAdapter
import androidx.recyclerview.widget.LinearLayoutManager

class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var genreAdapter: GenreAdapter
    private val args: MovieDetailsFragmentArgs by navArgs()
    private val viewModel: MovieDetailsViewModel by viewModels {
        MovieDetailsViewModelFactory(requireContext())
    }
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var movieId: String = ""
    private var currentUserRating: Float = 0f
    private lateinit var feedbackAdapter: FeedbackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        movieId = args.movie.id.toString()

        setupFeedbacksRecycler()
        setupUI()
        setupClickListeners()
        setupRecyclerViews()

        viewModel.setMovie(args.movie)
        observeViewModel()
        observeFeedbacks()

        // Load feedbacks and user rating
        viewModel.loadFeedbacks(args.movie.id)
        loadUserRating()
    }

    private fun setupFeedbacksRecycler() {
        feedbackAdapter = FeedbackAdapter(emptyList())
        binding.feedbackRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.feedbackRecycler.adapter = feedbackAdapter
    }

    private fun observeFeedbacks() {
        viewModel.feedbacks.observe(viewLifecycleOwner) { feedbackList ->
            feedbackAdapter.updateData(feedbackList)
        }
    }

    private fun setupUI() {
        selectTab(binding.tabAbout)
    }

    private fun setupRecyclerViews() {
        genreAdapter = GenreAdapter(emptyList())
        binding.recyclerGenres.adapter = genreAdapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRate.setOnClickListener {
            viewModel.onRateClicked()
        }

        binding.btnBookmark.setOnClickListener {
            viewModel.onBookmarkClicked()
        }

        binding.tabAbout.setOnClickListener {
            viewModel.onTabSelected(MovieDetailsViewModel.Tab.ABOUT)
        }

        binding.tabComments.setOnClickListener {
            viewModel.onTabSelected(MovieDetailsViewModel.Tab.COMMENTS)
        }

        binding.tabRecommendations.setOnClickListener {
            viewModel.onTabSelected(MovieDetailsViewModel.Tab.RECOMMENDATIONS)
        }

        binding.btnReadMore.setOnClickListener {
            viewModel.onReadMoreClicked()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movieDetails.collect { movie ->
                movie?.let { displayMovieDetails(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedTab.collect { tab ->
                selectTab(getTabView(tab))
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isBookmarked.collect { isBookmarked ->
                updateBookmarkIcon(isBookmarked)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRated.collect { isRated ->
                updateRateNowIcon(isRated)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isDescriptionExpanded.collect { isExpanded ->
                updateDescriptionState(isExpanded)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contentVisibility.collect { visibility ->
                updateContentVisibility(visibility)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvents.collect { event ->
                event?.let { handleUiEvent(it) }
            }
        }
    }

    private fun displayMovieDetails(movie: Movie) {
        with(binding) {
            val imageUrl = if (!movie.poster_path.isNullOrEmpty()) {
                "https://image.tmdb.org/t/p/w500${movie.poster_path}"
            } else {
                movie.posterUrl
            }

            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(imgMoviePoster)

            txtMovieTitle.text = movie.title
            val duration = movie.duration ?: 0
            val durationText = if (duration > 0) "${duration} mins" else "null mins"
            txtMovieInfo.text = "${movie.release_date} • $durationText"

            txtMovieDescription.text = movie.overview

            updateRatingDisplay(movie.rating)

            movie.genres?.let { genres ->
                if (genres.isNotEmpty()) {
                    genreAdapter.updateGenres(genres)
                    layoutGenres.visibility = View.VISIBLE
                } else {
                    layoutGenres.visibility = View.GONE
                }
            } ?: run {
                layoutGenres.visibility = View.GONE
            }
        }
    }

    private fun selectTab(selectedTab: View) {
        with(binding) {
            listOf(tabAbout, tabComments, tabRecommendations).forEach { tab ->
                (tab as? TextView)?.apply {
                    setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
                    textSize = 16f
                    paintFlags = paintFlags and android.graphics.Paint.FAKE_BOLD_TEXT_FLAG.inv()
                }
            }
        }

        (selectedTab as? TextView)?.apply {
            setTextColor(resources.getColor(R.color.white, requireContext().theme))
            paintFlags = paintFlags or android.graphics.Paint.FAKE_BOLD_TEXT_FLAG
        }
    }

    private fun getTabView(tab: MovieDetailsViewModel.Tab): View {
        return when (tab) {
            MovieDetailsViewModel.Tab.ABOUT -> binding.tabAbout
            MovieDetailsViewModel.Tab.COMMENTS -> binding.tabComments
            MovieDetailsViewModel.Tab.RECOMMENDATIONS -> binding.tabRecommendations
        }
    }

    private fun updateRateNowIcon(isRated: Boolean) {
        val iconRes = if (isRated) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        binding.imgRateIcon.setImageResource(iconRes)
    }

    private fun updateBookmarkIcon(isBookmarked: Boolean) {
        val iconRes = if (isBookmarked) {
            R.drawable.ic_bookmark_filled
        } else {
            R.drawable.ic_bookmark_outline
        }
        binding.btnBookmark.setImageResource(iconRes)
    }

    private fun updateDescriptionState(isExpanded: Boolean) {
        if (isExpanded) {
            binding.txtMovieDescription.maxLines = Int.MAX_VALUE
            binding.btnReadMore.text = "Read less"
        } else {
            binding.txtMovieDescription.maxLines = 3
            binding.btnReadMore.text = "Read more"
        }
    }

    private fun updateContentVisibility(visibility: MovieDetailsViewModel.ContentVisibility) {
        with(binding) {
            // About content
            layoutRatings.visibility = if (visibility.showRatings) View.VISIBLE else View.GONE
            txtMovieDescription.visibility = if (visibility.showDescription) View.VISIBLE else View.GONE
            btnReadMore.visibility = if (visibility.showReadMore) View.VISIBLE else View.GONE
            layoutGenres.visibility = if (visibility.showGenres) View.VISIBLE else View.GONE

            // Comments content
            feedbackRecycler.visibility = if (visibility.showComments) View.VISIBLE else View.GONE
        }
    }

    private fun handleUiEvent(event: MovieDetailsViewModel.UiEvent) {
        when (event) {
            is MovieDetailsViewModel.UiEvent.ShowRatingDialog -> {
                showRatingDialog()
            }
        }
        viewModel.clearUiEvent()
    }

    private fun showRatingDialog() {
        try {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rating, null)
            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
            val ratingText = dialogView.findViewById<TextView>(R.id.tvRatingValue)
            val btnApply = dialogView.findViewById<Button>(R.id.btnApply)
            val btnRemove = dialogView.findViewById<Button>(R.id.btnRemove)
            val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)

            ratingBar?.apply {
                numStars = 5
                stepSize = 0.5f
                rating = currentUserRating
            }

            ratingText?.text = String.format("%.1f", currentUserRating)

            ratingBar?.setOnRatingBarChangeListener { _, rating, _ ->
                ratingText?.text = String.format("%.1f", rating)
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            btnApply?.setOnClickListener {
                val newRating = ratingBar?.rating ?: 0f
                if (newRating > 0) {
                    submitRatingToFirebase(newRating)
                } else {
                    Toast.makeText(requireContext(), "Please select a rating", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            btnRemove?.setOnClickListener {
                removeRatingFromFirebase()
                dialog.dismiss()
            }

            btnClose?.setOnClickListener {
                dialog.dismiss()
            }

            if (!isDetached && !requireActivity().isFinishing) {
                dialog.show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error opening rating dialog: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserRating() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            currentUserRating = 0f
            updateUserRatingDisplay(0f)
            return
        }

        if (!isAdded || context == null) {
            return
        }

        try {
            firestore.enableNetwork()
        } catch (e: Exception) {
            android.util.Log.w("MovieRating", "Network enable failed", e)
        }

        firestore.collection("movie_ratings")
            .document("${movieId}_${userId}")
            .get()
            .addOnSuccessListener { document ->
                if (isAdded && context != null) {
                    if (document.exists()) {
                        currentUserRating = document.getDouble("rating")?.toFloat() ?: 0f
                    } else {
                        currentUserRating = 0f
                    }
                    updateUserRatingDisplay(currentUserRating)
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded && context != null) {
                    currentUserRating = 0f
                    updateUserRatingDisplay(0f)

                    if (exception.message?.contains("offline") != true) {
                        Toast.makeText(requireContext(), "Failed to load rating", Toast.LENGTH_SHORT).show()
                    }
                    android.util.Log.w("MovieRating", "Failed to load user rating", exception)
                }
            }
    }

    private fun submitRatingToFirebase(rating: Float) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Please login to rate movies", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isAdded || context == null) {
            return
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Saving rating...")
        progressDialog.setCancelable(true)
        progressDialog.show()

        val ratingData = hashMapOf(
            "userId" to userId,
            "movieId" to movieId,
            "rating" to rating.toDouble(),
            "timestamp" to System.currentTimeMillis()
        )

        try {
            firestore.enableNetwork()
        } catch (e: Exception) {
            android.util.Log.w("MovieRating", "Network enable failed", e)
        }

        val saveTask = firestore.collection("movie_ratings")
            .document("${movieId}_${userId}")
            .set(ratingData)

        saveTask.addOnSuccessListener {
            if (isAdded && context != null) {
                progressDialog.dismiss()
                currentUserRating = rating
                updateUserRatingDisplay(rating)
                Toast.makeText(requireContext(), "Rating saved successfully!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            if (isAdded && context != null) {
                progressDialog.dismiss()
                android.util.Log.e("MovieRating", "Failed to save rating", exception)

                if (exception.message?.contains("offline") == true) {
                    Toast.makeText(requireContext(), "No internet connection. Rating will be saved when online.", Toast.LENGTH_LONG).show()
                    currentUserRating = rating
                    updateUserRatingDisplay(rating)
                } else {
                    Toast.makeText(requireContext(), "Failed to save rating. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (progressDialog.isShowing && isAdded && context != null) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Request timed out. Please check your internet connection.", Toast.LENGTH_LONG).show()
            }
        }, 10000)
    }

    private fun removeRatingFromFirebase() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Please login to remove rating", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isAdded || context == null) {
            return
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Removing rating...")
        progressDialog.setCancelable(true)
        progressDialog.show()

        try {
            firestore.enableNetwork()
        } catch (e: Exception) {
            android.util.Log.w("MovieRating", "Network enable failed", e)
        }

        val deleteTask = firestore.collection("movie_ratings")
            .document("${movieId}_${userId}")
            .delete()

        deleteTask.addOnSuccessListener {
            if (isAdded && context != null) {
                progressDialog.dismiss()
                currentUserRating = 0f
                updateUserRatingDisplay(0f)
                Toast.makeText(requireContext(), "Rating removed successfully!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            if (isAdded && context != null) {
                progressDialog.dismiss()
                android.util.Log.e("MovieRating", "Failed to remove rating", exception)

                if (exception.message?.contains("offline") == true) {
                    Toast.makeText(requireContext(), "No internet connection. Please try again when online.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to remove rating. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (progressDialog.isShowing && isAdded && context != null) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Request timed out. Please check your internet connection.", Toast.LENGTH_LONG).show()
            }
        }, 10000)
    }

    private fun updateUserRatingDisplay(rating: Float) {
        try {
            val textView = binding.btnRate.findViewById<TextView>(R.id.rateNowText)
            textView?.text = if (rating > 0) "Update Rating" else "Rate Now"
            updateRateNowIcon(rating > 0)
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    private fun updateRatingDisplay(rating: Float) {
        // Implementation for updating movie rating display
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}