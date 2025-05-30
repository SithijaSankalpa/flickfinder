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


    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _upcomingMovies = MutableLiveData<List<Movie>>()
    val upcomingMovies: LiveData<List<Movie>> = _upcomingMovies


    private val fullPopularMovieList = mutableListOf<Movie>()

    init {
        viewModelScope.launch {

            val popular = repository.getPopularMovies()
            fullPopularMovieList.clear()
            fullPopularMovieList.addAll(popular)
            _popularMovies.value = popular

            _upcomingMovies.value = repository.getUpcomingMovies()
        }
    }


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
