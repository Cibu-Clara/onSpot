package com.example.onspot.data.repository

import com.example.onspot.data.model.Review
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviewsForUser(userId: String): Flow<Resource<List<Review>>>
}