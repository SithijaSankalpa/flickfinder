package com.sithija.flickFinder.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sithija.flickFinder.adapter.MovieAdapter
import com.sithija.flickFinder.databinding.FragmentHomeBinding

import androidx.navigation.fragment.findNavController
import com.sithija.flickFinder.Model.Movie

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var popularMovieAdapter: MovieAdapter
    private lateinit var upcomingMovieAdapter: MovieAdapter

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        observeViewModel()
        setupSearchBar()

        return binding.root
    }

    private fun setupRecyclerViews() {

        popularMovieAdapter = MovieAdapter(emptyList()) { movie ->
            navigateToMovieDetails(movie)
        }
        binding.popularRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularMovieAdapter
        }


        upcomingMovieAdapter = MovieAdapter(emptyList()) { movie ->
            navigateToMovieDetails(movie)
        }
        binding.upcomingRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingMovieAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            Log.d(TAG, "Popular movies received: ${movies?.size ?: 0}")
            movies?.let {
                popularMovieAdapter.updateMovies(it)
            }
        }

        viewModel.upcomingMovies.observe(viewLifecycleOwner) { movies ->
            Log.d(TAG, "Upcoming movies received: ${movies?.size ?: 0}")
            movies?.let {
                upcomingMovieAdapter.updateMovies(it)
            }
        }

    }

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                val searchQuery = query?.toString()?.trim() ?: ""
                if (searchQuery.length >= 2) {
                    viewModel.searchMovies(searchQuery)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun navigateToMovieDetails(movie: Movie) {
        try {
            Log.d(TAG, "Navigating to movie details for: ${movie.title}")
            Log.d(TAG, "Movie data: $movie")


            if (movie.title.isBlank()) {
                Toast.makeText(requireContext(), "Invalid movie data", Toast.LENGTH_SHORT).show()
                return
            }

            val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(movie)
            findNavController().navigate(action)

        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to movie details", e)
            Toast.makeText(
                requireContext(),
                "Unable to open movie details: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}