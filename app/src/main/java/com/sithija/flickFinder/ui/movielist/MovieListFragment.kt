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
import androidx.recyclerview.widget.GridLayoutManager
import com.sithija.flickFinder.adapter.MovieListAdapter
import com.sithija.flickFinder.databinding.FragmentMovieListBinding
import com.google.android.material.chip.Chip

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
            adapter = MovieListAdapter(emptyList())
            binding.movieListRecycler.apply {
                layoutManager = GridLayoutManager(requireContext(), 1) // 2 columns for better movie display
                adapter = this@MovieListFragment.adapter
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

            // Add "All" chip
            val allChip = Chip(requireContext()).apply {
                text = "All"
                isCheckable = true
                isChecked = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.showAllMovies()
                        // Uncheck other chips
                        for (i in 1 until chipGroup.childCount) {
                            (chipGroup.getChildAt(i) as? Chip)?.isChecked = false
                        }
                    }
                }
            }
            chipGroup.addView(allChip)

            // Add genre chips
            for (cat in categories) {
                val chip = Chip(requireContext()).apply {
                    text = cat
                    isCheckable = true
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            viewModel.filterByGenre(cat)
                            // Uncheck "All" chip and other genre chips
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "MovieListFragment onDestroyView")
    }
}