package com.example.parkingspots.navigation

sealed class Screens(val route: String) {
    // authentication screens
    object OpeningScreen : Screens(route = "OpeningScreen")
    object SignInScreen : Screens(route = "SignInScreen")
    object SignUpScreen : Screens(route = "SignUpScreen")

    // main screens
    object SearchScreen : Screens(route = "SearchScreen")
    object OfferScreen : Screens(route = "OfferScreen")
    object PostsScreen : Screens(route = "PostsScreen")
    object InboxScreen : Screens(route = "InboxScreen")
    object ProfileScreen : Screens(route = "ProfileScreen")
}