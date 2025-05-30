package com.sithija.flickFinder.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val poster_path: String? = null,
    val release_date: String = "",
    val vote_average: Double = 0.0,
    val genre_ids: List<Int> = emptyList(),
    val duration: Int? = null,
    val genres: List<String>? = null
) : Parcelable {


    val rating: Float
        get() = vote_average.toFloat()

    val posterUrl: String
        get() = if (!poster_path.isNullOrEmpty()) {
            "https://image.tmdb.org/t/p/w500$poster_path"
        } else {
            ""
        }


    fun isValid(): Boolean {
        return id > 0 && title.isNotBlank() && overview.isNotBlank()
    }
}