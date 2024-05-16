package com.example.onspot.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    onIconClick: (() -> Unit)? = null
) {
    TopAppBar (
        title = {
            Text(
                text = title,
                fontFamily = RegularFont,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (icon != null && onIconClick != null) {
                IconButton(onClick = onIconClick) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@Composable
fun CustomTabView(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (selectedTabIndex: Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = lightPurple
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = { androidx.compose.material.Text(title) },
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
fun CustomAlertDialog(
    title: String,
    text: String,
    confirmButtonText: String = "OK",
    dismissButtonText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        },
        text = {
            Text(
                text = text,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Gray
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            dismissButtonText?.let {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(it)
                }
            }
        }
    )
}

@Composable
fun IconWithText(
    text: String,
    isVerified: Boolean,
    isAddIcon: Boolean = true,
    onAddAction: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(bottom = 15.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .clickable(enabled = isAddIcon, onClick = onAddAction)
    ) {
        Icon(
            imageVector = if (isVerified) Icons.Filled.CheckCircle
                            else if (isAddIcon) Icons.Default.AddCircleOutline
                            else Icons.Outlined.WarningAmber,
            contentDescription = if (isVerified) "Verified" else "Add",
            tint = if(isVerified || isAddIcon) purple else Color.Red,
            modifier = Modifier
                .padding(end = 8.dp)
        )
        Text(
            text = text,
            color = if (isVerified) Color.DarkGray else purple
        )
    }
}

@Composable
fun PDFFilePicker(
    isButtonEnabled: Boolean,
    onFilePicked: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onFilePicked(uri) }

    Button(
        enabled = isButtonEnabled,
        modifier = modifier,
        onClick = { pdfPickerLauncher.launch("application/pdf") }
    ) {
        Text("Upload PDF document")
    }
}

@Composable
fun PDFEditPicker(
    isButtonEnabled: Boolean,
    onFilePicked: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onFilePicked(uri) }

    IconButton(
        enabled = isButtonEnabled,
        modifier = modifier,
        onClick = { pdfPickerLauncher.launch("application/pdf") }
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit PDF"
        )
    }
}
