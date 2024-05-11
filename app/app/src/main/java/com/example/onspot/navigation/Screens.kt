package com.example.onspot.navigation

sealed class Screens(val route: String) {
    // authentication screens
    object OpeningScreen : Screens(route = "OpeningScreen")
    object SignInScreen : Screens(route = "SignInScreen")
    object SignUpScreen : Screens(route = "SignUpScreen")
    object ChangePasswordScreen : Screens(route = "ChangePasswordScreen")

    // main screens
    object SearchScreen : Screens(route = "SearchScreen")
    object OfferScreen : Screens(route = "OfferScreen")
    object PostsScreen : Screens(route = "PostsScreen")
    object InboxScreen : Screens(route = "InboxScreen")
    object UserProfileScreen : Screens(route = "UserProfileScreen")

    // secondary screens
    object PersonalDetailsScreen : Screens(route = "PersonalDetailsScreen")
    object AddParkingSpotScreen : Screens(route = "AddParkingSpotScreen")
    object ParkingSpotDetailsScreen : Screens(route = "ParkingSpotDetailsScreen")
}