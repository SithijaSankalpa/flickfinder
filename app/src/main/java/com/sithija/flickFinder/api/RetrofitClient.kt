package com.sithija.flickFinder.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val api: TMDBApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApiService::class.java)
    }
}