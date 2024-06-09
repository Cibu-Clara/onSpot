package com.example.onspot.data.repository

import com.example.onspot.data.model.Review
import com.example.onspot.utils.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl : ReviewRepository {
    private val reviewsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("reviews")

    override fun getReviewsForUser(userId: String): Flow<Resource<List<Review>>> = flow {
        try {
            emit(Resource.Loading())
            val snapshot = reviewsCollection
                .whereEqualTo("reviewedUserId", userId)
                .get()
                .await()
            val reviews = snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
            emit(Resource.Success(reviews))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch reviews"))
        }
    }
}