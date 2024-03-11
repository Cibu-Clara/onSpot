package com.example.parkingspots.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parkingspots.ui.screens.SignInScreen
import com.example.parkingspots.ui.screens.SignUpScreen
import com.example.parkingspots.ui.screens.OpeningScreen
import com.example.parkingspots.ui.screens.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser != null) {
            Screens.HomeScreen.route
        } else {
            Screens.OpeningScreen.route
        }

    ) {
        // Opening Screen
        composable(route = Screens.OpeningScreen.route) { OpeningScreen(navController) }

        // Sign In Screen
        composable(route = Screens.SignInScreen.route) { SignInScreen(navController) }

        // Sign Up Screen
        composable(route = Screens.SignUpScreen.route) { SignUpScreen(navController) }

        // Home Screen
        composable(route = Screens.HomeScreen.route) { HomeScreen(navController) }
    }

}