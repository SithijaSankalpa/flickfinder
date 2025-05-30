package com.sithija.flickFinder.ui.movielist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sithija.flickFinder.Model.Movie
import com.sithija.flickFinder.repository.MovieRepository
import com.sithija.flickFinder.utils.GenreUtils
import kotlinx.coroutines.launch


class MovieListViewModel : ViewModel() {

    private val repository = MovieRepository()
    private val TAG = "MovieListViewModel"

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _genres = MutableLiveData<List<String>>()
    val genres: LiveData<List<String>> = _genres

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val allMovies = mutableListOf<Movie>()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "Loading movies...")

                val fetched = repository.getPopularMovies()
                allMovies.clear()
                allMovies.addAll(fetched)

                _movies.value = fetched
                updateGenreFilters(fetched)

                Log.d(TAG, "Movies loaded: ${fetched.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading movies: ${e.message}", e)
                _movies.value = emptyList()
                _genres.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateGenreFilters(movies: List<Movie>) {
        try {
            val allGenreIds = movies.flatMap { it.genre_ids }.distinct()
            val genreNames = allGenreIds.mapNotNull { GenreUtils.genreMap[it] }.distinct().sorted()
            _genres.value = genreNames
            Log.d(TAG, "Genre filters updated: $genreNames")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating genre filters: ${e.message}", e)
            _genres.value = emptyList()
        }
    }

    fun filterByQuery(query: String) {
        try {
            val result = if (query.isEmpty()) {
                allMovies
            } else {
                allMovies.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.overview.contains(query, ignoreCase = true)
                }
            }
            _movies.value = result
            Log.d(TAG, "Filter by query '$query': ${result.size} results")
        } catch (e: Exception) {
            Log.e(TAG, "Error filtering by query: ${e.message}", e)
        }
    }

    fun filterByGenre(genreName: String) {
        try {
            val genreId = GenreUtils.genreMap.entries.find {
                it.value.equals(genreName, ignoreCase = true)
            }?.key

            if (genreId != null) {
                val filtered = allMovies.filter { it.genre_ids.contains(genreId) }
                _movies.value = filtered
                Log.d(TAG, "Filter by genre '$genreName': ${filtered.size} results")
            } else {
                Log.w(TAG, "Genre '$genreName' not found in genre map")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error filtering by genre: ${e.message}", e)
        }
    }

    fun showAllMovies() {
        try {
            _movies.value = allMovies
            Log.d(TAG, "Showing all movies: ${allMovies.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing all movies: ${e.message}", e)
        }
    }

    fun refreshMovies() {
        loadMovies()
    }
}