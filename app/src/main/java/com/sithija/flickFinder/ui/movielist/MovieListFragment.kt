package com.sithija.flickFinder.ui.movielist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sithija.flickFinder.adapter.MovieListAdapter
import com.sithija.flickFinder.databinding.FragmentMovieListBinding
import com.google.android.material.chip.Chip
import androidx.navigation.fragment.findNavController
import com.sithija.flickFinder.Model.Movie


class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieListViewModel by viewModels()
    private val TAG = "MovieListFragment"

    private lateinit var adapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        Log.d(TAG, "MovieListFragment onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "MovieListFragment onViewCreated")

        setupRecyclerView()
        observeViewModel()
        setupSearch()
    }

    private fun setupRecyclerView() {
        try {
            adapter = MovieListAdapter(emptyList()) { movie ->
                navigateToMovieDetails(movie)
            }
            binding.movieListRecycler.apply {

                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@MovieListFragment.adapter

                setHasFixedSize(true)
            }
            Log.d(TAG, "RecyclerView setup complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView: ${e.message}", e)
        }
    }

    private fun observeViewModel() {
        try {
            viewModel.movies.observe(viewLifecycleOwner) { movies ->
                Log.d(TAG, "Movies received: ${movies.size}")
                adapter.updateData(movies)
            }

            viewModel.genres.observe(viewLifecycleOwner) { genreList ->
                Log.d(TAG, "Genres received: ${genreList.size}")
                setupChips(genreList)
            }


            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

                Log.d(TAG, "Loading state: $isLoading")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error observing ViewModel: ${e.message}", e)
        }
    }

    private fun setupSearch() {
        try {
            binding.searchBar.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.d(TAG, "Search query: $s")
                    viewModel.filterByQuery(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            Log.d(TAG, "Search setup complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up search: ${e.message}", e)
        }
    }

    private fun setupChips(categories: List<String>) {
        try {
            val chipGroup = binding.categoryChipGroup
            chipGroup.removeAllViews()


            val allChip = Chip(requireContext()).apply {
                text = "All"
                isCheckable = true
                isChecked = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.showAllMovies()
                        for (i in 1 until chipGroup.childCount) {
                            (chipGroup.getChildAt(i) as? Chip)?.isChecked = false
                        }
                    }
                }
            }
            chipGroup.addView(allChip)


            for (cat in categories) {
                val chip = Chip(requireContext()).apply {
                    text = cat
                    isCheckable = true
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            viewModel.filterByGenre(cat)
                            allChip.isChecked = false
                            for (i in 1 until chipGroup.childCount) {
                                val otherChip = chipGroup.getChildAt(i) as? Chip
                                if (otherChip != this && otherChip != null) {
                                    otherChip.isChecked = false
                                }
                            }
                        }
                    }
                }
                chipGroup.addView(chip)
            }
            Log.d(TAG, "Chips setup complete: ${categories.size + 1} chips")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up chips: ${e.message}", e)
        }
    }

    private fun navigateToMovieDetails(movie: Movie) {
        try {
            val action = MovieListFragmentDirections.actionMovieListFragmentToMovieDetailsFragment(movie)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to movie details: ${e.message}", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "MovieListFragment onDestroyView")
    }
}