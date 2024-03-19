package com.example.parkingspots.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parkingspots.ui.screens.auth.SignInScreen
import com.example.parkingspots.ui.screens.auth.SignUpScreen
import com.example.parkingspots.ui.screens.auth.OpeningScreen
import com.example.parkingspots.ui.screens.main.InboxScreen
import com.example.parkingspots.ui.screens.main.OfferScreen
import com.example.parkingspots.ui.screens.main.PostsScreen
import com.example.parkingspots.ui.screens.main.UserProfileScreen
import com.example.parkingspots.ui.screens.main.SearchScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser != null) {
            Screens.SearchScreen.route
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

        // Search Screen
        composable(route = Screens.SearchScreen.route) { SearchScreen(navController) }

        // Offer Screen
        composable(route = Screens.OfferScreen.route) { OfferScreen(navController)}

        // Posts Screen
        composable(route = Screens.PostsScreen.route) { PostsScreen(navController) }

        // Inbox Screen
        composable(route = Screens.InboxScreen.route) { InboxScreen(navController) }

        // Profile Screen
        composable(route = Screens.UserProfileScreen.route) { UserProfileScreen(navController) }
    }

}