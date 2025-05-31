package com.sithija.flickFinder.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.sithija.flickFinder.R
import com.sithija.flickFinder.Model.Feedback

class FeedbackAdapter(private var feedbackList: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    inner class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.user_name)
        val feedbackDate: TextView = view.findViewById(R.id.feedback_date)
        val feedbackRating: RatingBar = view.findViewById(R.id.feedback_rating)
        val userComment: TextView = view.findViewById(R.id.user_comment)
        val commentScroll: NestedScrollView = view.findViewById(R.id.comment_scroll)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.userName.text = feedback.username
        holder.userComment.text = feedback.comment
        holder.feedbackDate.text = feedback.date
        holder.feedbackRating.rating = feedback.rating?.toFloat() ?: 0f

        holder.commentScroll.isNestedScrollingEnabled = true
        holder.commentScroll.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    override fun getItemCount(): Int = feedbackList.size

    fun updateData(newFeedbackList: List<Feedback>) {
        feedbackList = newFeedbackList
        notifyDataSetChanged()
    }
}