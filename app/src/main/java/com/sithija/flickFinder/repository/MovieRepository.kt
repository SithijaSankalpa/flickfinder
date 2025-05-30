package com.sithija.flickFinder.repository

import com.sithija.flickFinder.Model.FeedbackItem
import com.sithija.flickFinder.api.RetrofitClient
import com.sithija.flickFinder.Model.Genre
import com.sithija.flickFinder.Model.Movie

class MovieRepository {

    private val apiKey = "9566ebe272c5eb212ea2a55e9b7129b1"

    suspend fun getGenres(): List<Genre> {
        return RetrofitClient.api.getGenres(apiKey).genres
    }

    suspend fun getPopularMovies(): List<Movie> {

        val allMovies = mutableListOf<Movie>()

        try {
            for (page in 1..3) {
                val response = RetrofitClient.api.getPopularMovies(apiKey, page)
                allMovies.addAll(response.results)
            }
        } catch (e: Exception) {

        }
        return allMovies
    }
    suspend fun getUpcomingMovies(): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        try {
            for (page in 1..3) {
                val response = RetrofitClient.api.getUpcomingMovies(apiKey, page)
                allMovies.addAll(response.results)
            }
        } catch (e: Exception) {

        }
        return allMovies
    }

    suspend fun getFeedbacks(movieId: Int): List<FeedbackItem> {
        val response = RetrofitClient.api.getFeedbacks(movieId, apiKey)
        return response.results
        }

}