package com.example.onspot.data.repository

import com.example.onspot.data.model.Review
import com.example.onspot.data.model.ReviewDetails
import com.example.onspot.data.model.Vehicle
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviewsForUser(userId: String): Flow<Resource<List<Review>>>
    fun addReview(review: Review): Flow<Resource<Void?>>
    fun getReviewDetails(reviewedUserId: String): Flow<Resource<List<ReviewDetails>>>
}