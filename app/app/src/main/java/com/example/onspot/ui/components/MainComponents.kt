package com.example.onspot.ui.components

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple

@Composable
fun CustomTopBar(
    title: String
) {
    TopAppBar (
        title = {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        backgroundColor = purple
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
        backgroundColor = lightPurple
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
