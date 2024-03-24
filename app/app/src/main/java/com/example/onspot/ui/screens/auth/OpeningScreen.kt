package com.example.onspot.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple

@Composable
fun OpeningScreen(
    navController: NavController
) {
    Surface(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column (
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = { navController.navigate(Screens.SignInScreen.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 25.dp, end = 25.dp, bottom = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = purple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(7.dp)
                )
            }
            Button(
                onClick = { navController.navigate(Screens.SignUpScreen.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp, bottom = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = lightPurple,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "Create account",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(7.dp)
                )
            }
        }
    }

}