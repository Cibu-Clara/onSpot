package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.Review
import com.example.onspot.data.model.ReviewDetails
import com.example.onspot.data.repository.ReviewRepository
import com.example.onspot.data.repository.ReviewRepositoryImpl
import com.example.onspot.ui.states.AddReviewState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    private val reviewRepository: ReviewRepository = ReviewRepositoryImpl()

    private val _reviews = MutableStateFlow<Resource<List<ReviewDetails>>>(Resource.Loading())
    val reviews: StateFlow<Resource<List<ReviewDetails>>> = _reviews.asStateFlow()

    private val _addReviewState = Channel<AddReviewState>()
    val addReviewState = _addReviewState.receiveAsFlow()

    fun fetchReviewsWithDetails(reviewedUserId: String) = viewModelScope.launch {
        reviewRepository.getReviewDetails(reviewedUserId).collect { resource ->
            _reviews.value = resource
        }
    }

    fun addReview(id: String, reviewedUserId: String, rating: Float, comment: String, reservationId: String) = viewModelScope.launch {
        val review = Review(
            uuid = id,
            reviewerId = "",
            reviewedUserId = reviewedUserId,
            rating = rating,
            comment = comment,
            timestamp = 0L,
            reservationId = reservationId
        )
        reviewRepository.addReview(review).collect { result ->
            when(result) {
                is Resource.Loading -> { _addReviewState.send(AddReviewState(isLoading = true)) }
                is Resource.Success -> { _addReviewState.send(AddReviewState(isSuccess = "Review successfully added")) }
                is Resource.Error -> { _addReviewState.send(AddReviewState(isError = result.message)) }
            }
        }
    }
}