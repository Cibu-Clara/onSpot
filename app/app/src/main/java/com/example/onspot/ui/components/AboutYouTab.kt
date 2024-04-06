package com.example.onspot.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.onspot.R
import com.example.onspot.data.model.User
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AboutYouTab(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel,
    showBottomSheet: () -> Unit
) {
    val userDetails by userProfileViewModel.userDetails.collectAsState()
    val changeProfilePictureState = userProfileViewModel.changeProfilePictureState.collectAsState(initial = null)
    val deleteProfilePictureState = userProfileViewModel.deleteProfilePictureState.collectAsState(initial = null)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    when (userDetails) {
        is Resource.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            UserInfo(userDetails.data!!, showBottomSheet)
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching user details", Toast.LENGTH_LONG).show()
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (changeProfilePictureState.value?.isLoading == true || deleteProfilePictureState.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = changeProfilePictureState.value?.isSuccess) {
        scope.launch {
            if (changeProfilePictureState.value?.isSuccess?.isNotEmpty() == true) {
                val success = changeProfilePictureState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = changeProfilePictureState.value?.isError) {
        scope.launch {
            if (changeProfilePictureState.value?.isError?.isNotEmpty() == true) {
                val error = changeProfilePictureState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = deleteProfilePictureState.value?.isSuccess) {
        scope.launch {
            if (deleteProfilePictureState.value?.isSuccess?.isNotEmpty() == true) {
                val success = deleteProfilePictureState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = deleteProfilePictureState.value?.isError) {
        scope.launch {
            if (deleteProfilePictureState.value?.isError?.isNotEmpty() == true) {
                val error = deleteProfilePictureState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun UserInfo(user: User, showBottomSheet: () -> Unit) {
    val signUpDate = user.creationTimestamp.let { timestamp ->
        val date = Date(timestamp)
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoRow(
            firstName = user.firstName,
            signUpDate = signUpDate,
            profilePictureUrl = user.profilePictureUrl,
            showBottomSheet = showBottomSheet
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            repeat(5) {
                Icon(Icons.Filled.Star, contentDescription = "Star", tint = Color.Yellow)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bio: Enthusiastic traveler and adventure seeker. Love sharing journeys with new friends.",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UserInfoRow(
    firstName: String,
    signUpDate: String,
    profilePictureUrl: String,
    showBottomSheet: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = firstName,
                fontWeight = FontWeight.Bold,
                fontFamily = RegularFont,
                fontSize = 27.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "member since $signUpDate",
                fontFamily = RegularFont,
                color = Color.DarkGray
            )
        }
        Box {
            if (profilePictureUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(profilePictureUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(97.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_user_picture),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(97.dp)
                        .background(lightPurple, CircleShape)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            IconButton(
                onClick = { showBottomSheet() },
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomEnd),
                colors = IconButtonDefaults.iconButtonColors(Color.LightGray)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = purple
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageOptionsBottomSheet(
    profilePictureUrl: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onDeletePhoto: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            BottomSheetButton(
                text = "Take Photo",
                icon = Icons.Default.CameraAlt,
                contentDescription = "Take Photo",
                textAlign = TextAlign.Start,
                onClick = onTakePhoto
            )
            BottomSheetButton(
                text = "Choose from Gallery",
                icon = Icons.Default.PhotoLibrary,
                contentDescription = "Choose from Gallery",
                textAlign = TextAlign.Start,
                onClick = onChooseFromGallery
            )
            if (profilePictureUrl.isNotEmpty()) {
                BottomSheetButton(
                    text = "Remove Photo",
                    icon = Icons.Default.Delete,
                    contentDescription = "Remove Photo",
                    onClick = onDeletePhoto,
                    textColor = Color.Red,
                    textAlign = TextAlign.Start,
                    iconColor = Color.Red
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletePhotoBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDeletePhoto: () -> Unit,
    onCancel: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            BottomSheetButton(
                text = "Remove Photo",
                contentDescription = "Remove Photo",
                onClick = onDeletePhoto,
                textColor = Color.Red,
                textAlign = TextAlign.Center,
                iconColor = Color.Red
            )
            BottomSheetButton(
                text = "Cancel",
                contentDescription = "Cancel",
                textAlign = TextAlign.Center,
                onClick = onCancel
            )
        }
    }
}

@Composable
fun BottomSheetButton(
    text: String,
    icon: ImageVector? = null,
    contentDescription: String?,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign,// Default text color
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = text,
                color = textColor,
                modifier = Modifier.weight(1f),
                textAlign = textAlign
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
