package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.Review
import com.example.onspot.data.repository.ReviewRepository
import com.example.onspot.data.repository.ReviewRepositoryImpl
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    private val reviewRepository: ReviewRepository = ReviewRepositoryImpl()

    private val _reviews = MutableStateFlow<Resource<List<Review>>>(Resource.Loading())
    val reviews: StateFlow<Resource<List<Review>>> = _reviews.asStateFlow()

    fun fetchReviewsForUser(userId: String) = viewModelScope.launch {
        reviewRepository.getReviewsForUser(userId).collect { resource ->
            _reviews.value = resource
        }
    }
}