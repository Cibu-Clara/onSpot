package com.example.onspot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageOptionsBottomSheet(
    pictureUrl: String,
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
            if (pictureUrl.isNotEmpty()) {
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
    textAlign: TextAlign,
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