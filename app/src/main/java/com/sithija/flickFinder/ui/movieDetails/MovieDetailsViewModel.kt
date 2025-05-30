package com.sithija.flickFinder.ui.movieDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sithija.flickFinder.Model.Feedback
import com.sithija.flickFinder.Model.FeedbackItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.sithija.flickFinder.Model.Movie
import com.sithija.flickFinder.repository.MovieRepository
import java.text.SimpleDateFormat
import java.util.Locale

class MovieDetailsViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _feedbacks = MutableLiveData<List<Feedback>>()
    val feedbacks: LiveData<List<Feedback>> = _feedbacks

    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> = _movieDetails.asStateFlow()

    private val _selectedTab = MutableStateFlow(Tab.ABOUT)
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private val _isRated = MutableStateFlow(false)
    val isRated: StateFlow<Boolean> = _isRated.asStateFlow()

    private val _isDescriptionExpanded = MutableStateFlow(false)
    val isDescriptionExpanded: StateFlow<Boolean> = _isDescriptionExpanded.asStateFlow()

    private val _contentVisibility = MutableStateFlow(ContentVisibility())
    val contentVisibility: StateFlow<ContentVisibility> = _contentVisibility.asStateFlow()

    private val _uiEvents = MutableStateFlow<UiEvent?>(null)
    val uiEvents: StateFlow<UiEvent?> = _uiEvents.asStateFlow()

    init {
        updateContentVisibility(Tab.ABOUT)
    }

    fun loadFeedbacks(movieId: Int) {
        viewModelScope.launch {
            try {
                val feedbackItems: List<FeedbackItem> = repository.getFeedbacks(movieId)

                val mapped = feedbackItems.map { item ->
                    Feedback(
                        id = item.id,
                        username = item.author,
                        comment = item.content,
                        date = extractDate(item),
                        rating = item.authorDetails?.rating ?: 0.0
                    )
                }

                _feedbacks.postValue(mapped)
            } catch (e: Exception) {
                _feedbacks.postValue(emptyList())
            }
        }
    }

    private fun extractDate(item: FeedbackItem): String {
        val rawDate = item.createdAt ?: return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = inputFormat.parse(rawDate)
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    fun setMovie(movie: Movie) {
        _movieDetails.value = movie
        checkBookmarkStatus(movie)
    }

    fun onTabSelected(tab: Tab) {
        _selectedTab.value = tab
        updateContentVisibility(tab)
    }

    fun onBookmarkClicked() {
        viewModelScope.launch {
            val currentMovie = _movieDetails.value ?: return@launch
            val currentState = _isBookmarked.value
            _isBookmarked.value = !currentState
        }
    }

    fun onRateClicked() {
        _uiEvents.value = UiEvent.ShowRatingDialog
    }

    fun onReadMoreClicked() {
        _isDescriptionExpanded.value = !_isDescriptionExpanded.value
    }

    private fun updateContentVisibility(tab: Tab) {
        val visibility = when (tab) {
            Tab.ABOUT -> ContentVisibility(
                showRatings = true,
                showDescription = true,
                showReadMore = true,
                showGenres = true,
                showComments = false
            )
            Tab.COMMENTS -> ContentVisibility(
                showRatings = false,
                showDescription = false,
                showReadMore = false,
                showGenres = false,
                showComments = true
            )
            Tab.RECOMMENDATIONS -> ContentVisibility(
                showRatings = false,
                showDescription = false,
                showReadMore = false,
                showGenres = false,
                showComments = false
            )
        }
        _contentVisibility.value = visibility
    }

    private fun checkBookmarkStatus(movie: Movie) {
        viewModelScope.launch {
            _isBookmarked.value = false
        }
    }

    fun clearUiEvent() {
        _uiEvents.value = null
    }

    enum class Tab {
        ABOUT, COMMENTS, RECOMMENDATIONS
    }

    data class ContentVisibility(
        val showRatings: Boolean = false,
        val showDescription: Boolean = false,
        val showReadMore: Boolean = false,
        val showGenres: Boolean = false,
        val showComments: Boolean = false,
        val showRecommendations: Boolean = false
    )

    sealed class UiEvent {
        object ShowRatingDialog : UiEvent()
    }
}