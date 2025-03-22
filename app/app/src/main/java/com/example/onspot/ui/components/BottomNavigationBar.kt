package com.example.onspot.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavigationItem("Offer", Icons.Filled.AddCircleOutline, Icons.Outlined.AddCircleOutline, route = Screens.OfferScreen.route),
        BottomNavigationItem("Search", Icons.Filled.Search, Icons.Outlined.Search, route = Screens.SearchScreen.route),
        BottomNavigationItem("Reservations", Icons.Filled.Menu, Icons.Outlined.Menu, route = Screens.ReservationsScreen.route),
        BottomNavigationItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, route = Screens.UserProfileScreen.route),
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    onItemSelected(index)
                    navController.navigate(item.route)
                },
                alwaysShowLabel = false,
                label = {
                    Text(text = item.title)
                },
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) {
                            item.selectedIcon
                        } else item.unselectedIcon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}