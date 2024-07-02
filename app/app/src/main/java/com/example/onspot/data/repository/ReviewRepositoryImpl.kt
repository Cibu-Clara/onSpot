package com.example.onspot.data.repository

import com.example.onspot.data.model.ListingDetails
import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.Review
import com.example.onspot.data.model.ReviewDetails
import com.example.onspot.data.model.User
import com.example.onspot.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl : ReviewRepository {
    private val reviewsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("reviews")
    private val usersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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

    override fun addReview(review: Review): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            val timestamp = System.currentTimeMillis()
            reviewsCollection
                .document(review.uuid)
                .set(review.copy(reviewerId = currentUserId!!, timestamp = timestamp))
                .await()
            emit(Resource.Success(null))

            val userRef = usersCollection.document(review.reviewedUserId)
            val userSnapshot = userRef.get().await()
            val user = userSnapshot.toObject(User::class.java) ?: throw IllegalStateException("User not found")

            val newCount = user.ratingCount + 1
            val newRating = ((user.rating * user.ratingCount) + review.rating) / newCount

            userRef.update(mapOf(
                "rating" to newRating,
                "ratingCount" to newCount
            )).await()
        } catch (e: NullPointerException) {
            emit(Resource.Error(e.message ?: "User not logged in"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add review"))
        }
    }

    override fun getReviewDetails(reviewedUserId: String): Flow<Resource<List<ReviewDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val reviewsSnapshot = reviewsCollection
                .whereEqualTo("reviewedUserId", reviewedUserId)
                .get()
                .await()
            val reviews = reviewsSnapshot.documents.mapNotNull { it.toObject(Review::class.java) }

            var users = emptyList<User>()
            val userIds = reviews.map { it.reviewerId }
            if (userIds.isNotEmpty()) {
                val usersSnapshot = usersCollection
                    .whereIn("uuid", userIds)
                    .get()
                    .await()
                users = usersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            }
            val reviewDetailsList = reviews.map { review ->
                val user = users.find { it.uuid == review.reviewerId }
                    ?: throw IllegalStateException("Reviewer not found for review ${review.uuid}")
                ReviewDetails(
                    user = user,
                    review = review
                )
            }
            emit(Resource.Success(reviewDetailsList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch reviews"))
        }
    }
}