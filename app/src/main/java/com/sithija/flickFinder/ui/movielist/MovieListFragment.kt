package com.sithija.flickFinder.ui.movielist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sithija.flickFinder.adapter.MovieListAdapter
import com.sithija.flickFinder.databinding.FragmentMovieListBinding
import com.google.android.material.chip.Chip

class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieListViewModel by viewModels()

    private lateinit var adapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MovieListAdapter(emptyList())
        binding.movieListRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.movieListRecycler.adapter = adapter

        viewModel.movies.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.genres.observe(viewLifecycleOwner) { genreList ->
            setupChips(genreList)
        }

        setupSearch()
    }

    private fun setupSearch() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterByQuery(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupChips(categories: List<String>) {
        val chipGroup = binding.categoryChipGroup
        chipGroup.removeAllViews()

        for (cat in categories) {
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                setOnClickListener {
                    viewModel.filterByGenre(cat)
                }
            }
            chipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
