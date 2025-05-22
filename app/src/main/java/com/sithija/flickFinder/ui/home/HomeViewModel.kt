package com.sithija.flickFinder.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sithija.flickFinder.Model.Movie
import com.sithija.flickFinder.repository.MovieRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = MovieRepository()

    // LiveData exposed to UI
    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _upcomingMovies = MutableLiveData<List<Movie>>()
    val upcomingMovies: LiveData<List<Movie>> = _upcomingMovies

    // Holds full list for search filtering
    private val fullPopularMovieList = mutableListOf<Movie>()

    init {
        viewModelScope.launch {
            // Fetch and store all data once
            val popular = repository.getPopularMovies()
            fullPopularMovieList.clear()
            fullPopularMovieList.addAll(popular)
            _popularMovies.value = popular

            _upcomingMovies.value = repository.getUpcomingMovies()
        }
    }

    // Search/filter function
    fun searchMovies(query: String) {
        if (query.isEmpty()) {
            _popularMovies.value = fullPopularMovieList
        } else {
            _popularMovies.value = fullPopularMovieList.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
    }
}
