package com.sithija.flickFinder.repository

import com.sithija.flickFinder.api.RetrofitClient
import com.sithija.flickFinder.Model.Genre
import com.sithija.flickFinder.Model.Movie

class MovieRepository {

    private val apiKey = "f9923821f549f034afb399cd27e37afd"

    suspend fun getGenres(): List<Genre> {
        return RetrofitClient.api.getGenres(apiKey).genres
    }

    suspend fun getPopularMovies(): List<Movie> {
        return RetrofitClient.api.getPopularMovies(apiKey).results
    }

    suspend fun getUpcomingMovies(): List<Movie> {
        return RetrofitClient.api.getUpcomingMovies(apiKey).results }
}