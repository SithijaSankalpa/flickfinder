package com.sithija.flickFinder.utils

object GenreUtils {
    val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        14 to "Fantasy",
        878 to "Science Fiction",
        80 to "Crime",
        18 to "Drama",
        35 to "Comedy",
        9648 to "Mystery",
        53 to "Thriller",
        37 to "Western")

    fun getGenreNames(genreIds: List<Int>): List<String> {
        return genreIds.mapNotNull { genreMap[it] }
    }

    // Helper function to get genre ID from name
    fun getGenreId(genreName: String): Int? {
        return genreMap.entries.find { it.value.equals(genreName, ignoreCase = true) }?.key
    }
}
