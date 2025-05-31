package com.sithija.flickFinder.Model

data class FeedbackResponse(
    val id: Int,
    val page: Int,
    val results: List<FeedbackItem>
)

data class FeedbackItem(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String?,
    val authorDetails: AuthorDetails?
)

data class AuthorDetails(
    val rating:Double?
)