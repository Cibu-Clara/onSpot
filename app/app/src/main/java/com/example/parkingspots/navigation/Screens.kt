package com.example.parkingspots.navigation

sealed class Screens(val route: String) {
    object OpeningScreen : Screens(route = "OpeningScreen")
    object SignInScreen : Screens(route = "SignInScreen")
    object SignUpScreen : Screens(route = "SignUpScreen")
    object HomeScreen : Screens(route = "HomeScreen")
}