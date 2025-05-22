package com.sithija.flickFinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sithija.flickFinder.databinding.ItemMovieListBinding
import com.sithija.flickFinder.Model.Movie

class MovieListAdapter(
    private var movies: List<Movie>
) : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieListBinding) :
        RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieListBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        with(holder.binding) {
            title.text = movie.title
            releaseDate.text = "Release Date: ${movie.release_date}"
            //overview.text = movie.overview
            rating.text = String.format("%.1f", movie.vote_average)

            Glide.with(poster.context)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .into(poster)
        }
    }

    fun updateData(newList: List<Movie>) {
        movies = newList
        notifyDataSetChanged()
    }
}