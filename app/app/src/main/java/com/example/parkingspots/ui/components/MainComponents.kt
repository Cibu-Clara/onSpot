package com.example.parkingspots.ui.components

import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.parkingspots.ui.theme.lightPurple
import com.example.parkingspots.ui.theme.purple

@Composable
fun CustomTopBar(
    title: String
) {
    TopAppBar (
        title = { Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp) },
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