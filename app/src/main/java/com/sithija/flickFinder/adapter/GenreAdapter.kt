package com.sithija.flickFinder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sithija.flickFinder.R

class GenreAdapter(private var genres: List<String>) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(genres[position])
    }

    override fun getItemCount(): Int = genres.size

    fun updateGenres(newGenres: List<String>) {
        genres = newGenres
        notifyDataSetChanged()
    }

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val genreText: TextView = itemView.findViewById(R.id.txtGenre)

        fun bind(genre: String) {
            genreText.text = genre
        }
    }
}