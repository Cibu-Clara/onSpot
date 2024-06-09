package com.example.onspot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onspot.ui.screens.auth.ChangePasswordScreen
import com.example.onspot.ui.screens.auth.SignInScreen
import com.example.onspot.ui.screens.auth.SignUpScreen
import com.example.onspot.ui.screens.auth.OpeningScreen
import com.example.onspot.ui.screens.main.OfferScreen
import com.example.onspot.ui.screens.main.ReservationsScreen
import com.example.onspot.ui.screens.secondary.PersonalDetailsScreen
import com.example.onspot.ui.screens.main.UserProfileScreen
import com.example.onspot.ui.screens.main.SearchScreen
import com.example.onspot.ui.screens.secondary.AddParkingSpotScreen
import com.example.onspot.ui.screens.secondary.AddVehicleScreen
import com.example.onspot.ui.screens.secondary.ParkingSpotDetailsScreen
import com.example.onspot.ui.screens.secondary.ReviewsScreen
import com.example.onspot.ui.screens.secondary.VehicleDetailsScreen
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    placesClient: PlacesClient
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
        composable(route = Screens.SearchScreen.route) { SearchScreen(navController, placesClient) }

        // Offer Screen
        composable(route = Screens.OfferScreen.route) { OfferScreen(navController, placesClient)}

        // Reservations Screen
        composable(route = Screens.ReservationsScreen.route) { ReservationsScreen(navController) }

        // User Profile Screen
        composable(route = Screens.UserProfileScreen.route) { UserProfileScreen(navController) }

        // Change Password Screen
        composable(route = Screens.ChangePasswordScreen.route) { ChangePasswordScreen(navController) }

        // Personal Details Screen
        composable(route = Screens.PersonalDetailsScreen.route) { PersonalDetailsScreen(navController) }

        // Add Parking Spot Screen
        composable(route = Screens.AddParkingSpotScreen.route) { AddParkingSpotScreen(navController) }

        // Parking Spot Details Screen
        composable(route = Screens.ParkingSpotDetailsScreen.route) { backStackEntry ->
            val parkingSpotId = backStackEntry.arguments?.getString("parkingSpotId") ?: ""
            ParkingSpotDetailsScreen(navController, parkingSpotId)
        }

        // Add Vehicle Screen
        composable(route = Screens.AddVehicleScreen.route) { AddVehicleScreen(navController) }

        // Vehicle Details Screen
        composable(route = Screens.VehicleDetailsScreen.route) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailsScreen(navController, vehicleId)
        }

        // Reviews Screen
        composable(route = Screens.ReviewsScreen.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ReviewsScreen(navController, userId)
        }
    }
}