package com.sithija.flickFinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sithija.flickFinder.Model.Movie
import com.sithija.flickFinder.R
import com.sithija.flickFinder.databinding.ItemMovieListBinding
import com.sithija.flickFinder.utils.GenreUtils
import com.google.android.material.chip.Chip

class MovieListAdapter(
    private var movies: List<Movie>,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun updateData(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val binding: ItemMovieListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            with(binding) {

                movieTitle.text = movie.title
                releaseDate.text = "Released: ${movie.release_date}"
                movieRating.text = String.format("%.1f", movie.vote_average)

                val posterUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
                Glide.with(itemView.context)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.placeholder_poster)
                    .into(moviePoster)


                genreChipGroup.removeAllViews()
                val movieGenres = movie.genre_ids.take(3)
                    .mapNotNull { GenreUtils.genreMap[it] }

                movieGenres.forEach { genreName ->
                    val chip = Chip(itemView.context).apply {
                        text = genreName
                        isClickable = false
                        setChipBackgroundColorResource(R.color.chip_background)
                        setTextColor(itemView.context.getColor(R.color.white))
                        textSize = 10f
                    }
                    genreChipGroup.addView(chip)
                }


                root.setOnClickListener {
                    onMovieClick(movie)
                }
            }
        }
    }
}