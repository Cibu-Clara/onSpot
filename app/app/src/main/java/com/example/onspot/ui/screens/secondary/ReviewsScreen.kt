package com.example.onspot.ui.screens.secondary

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.onspot.R
import com.example.onspot.data.model.ReviewDetails
import com.example.onspot.data.model.User
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.FeedbackDialog
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.ReservationViewModel
import com.example.onspot.viewmodel.ReviewViewModel
import com.example.onspot.viewmodel.UserProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.math.floor

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReviewsScreen(
    navController: NavController,
    userId: String,
    reviewViewModel: ReviewViewModel = viewModel(),
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUserId = Firebase.auth.currentUser?.uid
    val reviewsState by reviewViewModel.reviews.collectAsState()
    val userDetails by userProfileViewModel.userDetails.collectAsState()

    LaunchedEffect(userId) {
        reviewViewModel.fetchReviewsWithDetails(userId)
    }

    LaunchedEffect(userId) {
        userProfileViewModel.fetchOtherUserDetails(userId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = if (userId == currentUserId) "Your reviews" else "${userDetails.data?.firstName}'s reviews",
                    onBackClick = { navController.popBackStack() }
                )
            },
            backgroundColor = Color.LightGray
        ) {
            when (reviewsState) {
                is Resource.Loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val reviews = (reviewsState as Resource.Success<List<ReviewDetails>>).data
                    ReviewsList(reviews ?: emptyList(), userDetails.data, reviewViewModel)
                }
                is Resource.Error -> {
                    LaunchedEffect(key1 = true) {
                        Toast.makeText(context, "Error fetching reviews", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewsList(reviews: List<ReviewDetails>, user: User?, reviewViewModel: ReviewViewModel) {
    val averageRating = if (reviews.isNotEmpty()) reviews.map { it.review.rating }.average() else 0.0

    if (reviews.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No reviews available",
                fontSize = 20.sp,
                fontFamily = RegularFont,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    } else {
        LazyColumn(modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)) {
            item {
                AverageRatingAndProfile(averageRating, user)
            }
            reviews.forEach { details ->
                item { ReviewCard(details, reviewViewModel) }
            }
        }
    }
}

@Composable
fun AverageRatingAndProfile(averageRating: Double, user: User?) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 60.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.1f", averageRating),
                fontWeight = FontWeight.Bold,
                fontFamily = RegularFont,
                color = Color.DarkGray,
                fontSize = 40.sp
            )
            Row {
                for (i in 1..5) {
                    val iconId = when {
                        i <= floor(averageRating) -> Icons.Filled.StarRate
                        i - averageRating > 0 && i - averageRating < 1 -> Icons.Filled.StarHalf
                        else -> Icons.Filled.StarOutline
                    }
                    Icon(
                        imageVector = iconId,
                        contentDescription = "Rating star",
                        tint = Color.Yellow,
                        modifier = Modifier.size(27.dp)
                    )
                }
            }
        }
        // User profile picture
        if (user != null && user.profilePictureUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(user.profilePictureUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(97.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_user_picture),
                contentDescription = "Default Profile Picture",
                modifier = Modifier
                    .size(97.dp)
                    .clip(CircleShape)
                    .background(lightPurple, CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ReviewCard(
    details: ReviewDetails,
    reviewViewModel: ReviewViewModel,
    reservationViewModel: ReservationViewModel = viewModel()
) {
    val hasReviewed by reservationViewModel.hasReviewed.collectAsState()
    var showFeedbackDialog by rememberSaveable { mutableStateOf(false) }
    val reviewId by rememberSaveable { mutableStateOf(UUID.randomUUID().toString()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val addReviewState = reviewViewModel.addReviewState.collectAsState(initial = null)

    LaunchedEffect(details.review.reservationId) {
        reservationViewModel.checkAlreadyReviewed(details.review.reservationId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Box(modifier = Modifier.padding(10.dp)) {
                    if (details.user.profilePictureUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(details.user.profilePictureUrl),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(lightPurple, CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user_picture),
                            contentDescription = "Default Profile Picture",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(lightPurple, CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Column(modifier = Modifier.width(300.dp)) {
                Text(
                    text = getTimeAgo(details.review.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = details.user.firstName,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RegularFont,
                    fontSize = 15.sp,
                )
                Row {
                    for (i in 1..5) {
                        val iconId = when {
                            i <= floor(details.review.rating) -> Icons.Filled.StarRate
                            i - details.review.rating > 0 && i - details.review.rating < 1 -> Icons.Filled.StarHalf
                            else -> Icons.Filled.StarOutline
                        }
                        Icon(
                            imageVector = iconId,
                            contentDescription = "Rating star",
                            tint = Color.Yellow,
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
                Text(
                    text = details.review.comment,
                    fontFamily = RegularFont,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = if (hasReviewed == false) 5.dp else 10.dp)
                )
                if (hasReviewed == false) {
                    Text(
                        text = "Leave feedback for ${details.user.firstName}",
                        fontSize = 13.sp,
                        fontFamily = RegularFont,
                        textDecoration = TextDecoration.Underline,
                        color = purple,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showFeedbackDialog = true }
                    )
                }
            }
        }
    }

    if (showFeedbackDialog) {
        FeedbackDialog(
            firstName = details.user.firstName,
            onDismiss = { showFeedbackDialog = false },
            onSend = { rating, message ->
                scope.launch {
                    reviewViewModel.addReview(
                        id = reviewId,
                        reviewedUserId = details.user.uuid,
                        rating = rating,
                        comment = message,
                        reservationId = details.review.reservationId
                    )
                    showFeedbackDialog = false
                }
            }
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (addReviewState.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = addReviewState.value?.isSuccess) {
        scope.launch {
            if (addReviewState.value?.isSuccess?.isNotEmpty() == true) {
                val success = addReviewState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                reservationViewModel.checkAlreadyReviewed(details.review.reservationId)
            }
        }
    }
    LaunchedEffect(key1 = addReviewState.value?.isError) {
        scope.launch {
            if (addReviewState.value?.isError?.isNotEmpty() == true) {
                val error = addReviewState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}

fun getTimeAgo(timestamp: Long): String {
    val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()

    val minutesAgo = ChronoUnit.MINUTES.between(time, now)
    val hoursAgo = ChronoUnit.HOURS.between(time, now)
    val daysAgo = ChronoUnit.DAYS.between(time, now)
    val weeksAgo = daysAgo / 7
    val monthsAgo = daysAgo / 30
    val yearsAgo = daysAgo / 365

    return when {
        minutesAgo < 60 -> "$minutesAgo minutes ago"
        hoursAgo < 24 -> "$hoursAgo hours ago"
        daysAgo < 7 -> "$daysAgo days ago"
        weeksAgo < 5 -> "$weeksAgo weeks ago"
        monthsAgo < 12 -> "$monthsAgo months ago"
        else -> "$yearsAgo years ago"
    }
}