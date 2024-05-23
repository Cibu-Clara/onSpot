package com.example.onspot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.green
import com.example.onspot.ui.theme.lightPurple

@Composable
fun ConfirmationBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = lightPurple, shape = RoundedCornerShape(20.dp)),
    ) {
        Column {
            Text(
                modifier = Modifier.padding(20.dp),
                text = "Congrats! Your parking spot has successfully been posted and is waiting to be reserved.",
                fontSize = 15.sp,
                fontFamily = RegularFont
            )
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = green,
                modifier = Modifier
                    .size(70.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}