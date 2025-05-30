package com.sithija.flickFinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sithija.flickFinder.databinding.ItemMovieBinding
import com.sithija.flickFinder.Model.Movie
import com.sithija.flickFinder.R

class MovieAdapter(
    private var movies: List<Movie>,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && position < movies.size) {
                    onMovieClick(movies[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]


        holder.binding.movieTitle.text = movie.title


        val imageUrl = when {
            !movie.poster_path.isNullOrEmpty() -> "https://image.tmdb.org/t/p/w500${movie.poster_path}"
            movie.posterUrl.isNotEmpty() -> movie.posterUrl
            else -> null
        }


        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.binding.moviePoster)
    }

    override fun getItemCount(): Int = movies.size


    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }


    fun addMovies(newMovies: List<Movie>) {
        val startPosition = movies.size
        movies = movies + newMovies
        notifyItemRangeInserted(startPosition, newMovies.size)
    }


    fun clearMovies() {
        val size = movies.size
        movies = emptyList()
        notifyItemRangeRemoved(0, size)
    }

    fun getMovieAt(position: Int): Movie? {
        return if (position in 0 until movies.size) movies[position] else null
    }
}