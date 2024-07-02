package com.example.onspot.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

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
        },
        colors = TopAppBarColors(purple, purple, Color.White, Color.White, Color.White)
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
                text = { androidx.compose.material.Text(text = title, fontFamily = RegularFont) },
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

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilter: (LocalDate?, LocalTime?, LocalDate?, LocalTime?) -> Unit
) {
    var startDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var isStartDateEmpty by rememberSaveable { mutableStateOf(true) }
    var startTime by rememberSaveable { mutableStateOf(LocalTime.NOON) }
    var isStartTimeEmpty by rememberSaveable { mutableStateOf(true) }
    var endDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var isEndDateEmpty by rememberSaveable { mutableStateOf(true) }
    var endTime by rememberSaveable { mutableStateOf(LocalTime.NOON) }
    var isEndTimeEmpty by rememberSaveable { mutableStateOf(true) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val isButtonEnabled = !isStartDateEmpty && !isEndDateEmpty && !isStartTimeEmpty && !isEndTimeEmpty

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Set filter",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = "When do you need the parking spot?",
                    fontFamily = RegularFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "•", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "From:",
                        fontFamily = RegularFont,
                        fontSize = 15.sp,
                    )
                }
                Row {
                    DatePicker(
                        label = "Date",
                        onDateSelected = {
                            startDate = it
                            isStartDateEmpty = false
                        },
                        greaterThan = LocalDate.now().minusDays(1),
                        modifier = Modifier.weight(0.5f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TimePicker(
                        label = "Time",
                        enabled = !isStartDateEmpty,
                        onTimeSelected = {
                            startTime = it
                            isStartTimeEmpty = false
                        },
                        greaterThan = if (startDate == LocalDate.now()) LocalTime.now().truncatedTo(
                            ChronoUnit.MINUTES) else LocalTime.MIN,
                        modifier = Modifier.weight(0.5f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "•", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "To:",
                        fontFamily = RegularFont,
                        fontSize = 15.sp,
                    )
                }
                Row {
                    DatePicker(
                        label = "Date",
                        enabled = !isStartDateEmpty,
                        onDateSelected = {
                            endDate = it
                            isEndDateEmpty = false
                        },
                        greaterThan = startDate.minusDays(1),
                        modifier = Modifier.weight(0.5f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TimePicker(
                        label = "Time",
                        enabled = !isStartTimeEmpty && !isEndDateEmpty,
                        onTimeSelected = {
                            endTime = it
                            isEndTimeEmpty = false
                        },
                        greaterThan = if(startDate == endDate) startTime.plusHours(1) else LocalTime.MIN,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startDateTime = LocalDateTime.of(startDate, startTime)
                    val endDateTime = LocalDateTime.of(endDate, endTime)
                    if (startDateTime.isBefore(endDateTime)) {
                        onApplyFilter(startDate, startTime, endDate, endTime)
                        } else { showDialog = true }
                    },
                enabled = isButtonEnabled
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("Cancel")
            }
        }
    )
    if (showDialog) {
        CustomAlertDialog(
            title = "Error",
            text = "Start date and time must be before end date and time.",
            onConfirm = { showDialog = false },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun FeedbackDialog(
    firstName: String,
    onDismiss: () -> Unit,
    onSend: (Float, String) -> Unit
) {
    var rating by rememberSaveable { mutableStateOf(0) }
    var comment by rememberSaveable { mutableStateOf("") }
    val isButtonEnabled =  (rating in 1..5) && comment.isNotEmpty()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Leave feedback for $firstName",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    (1..5).forEach { index ->
                        Icon(
                            imageVector = if (index <= rating) Icons.Filled.StarRate else Icons.Filled.StarBorder,
                            contentDescription = "Rating",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = index },
                            tint = if (index <= rating) Color.Yellow else Color.Gray
                        )
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Review message") },
                    placeholder = { Text("Write your review here...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    textStyle = TextStyle(fontSize = 16.sp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(rating.toFloat(), comment)} ,
                enabled = isButtonEnabled
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("Cancel")
            }
        }
    )
}