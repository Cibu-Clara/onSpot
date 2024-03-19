package com.example.parkingspots.ui.components

import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.parkingspots.navigation.Screens

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavigationItem("Posts", Icons.Filled.Menu, Icons.Outlined.Menu, false, route = Screens.PostsScreen.route),
        BottomNavigationItem("Offer", Icons.Filled.AddCircleOutline, Icons.Outlined.AddCircleOutline, false, route = Screens.OfferScreen.route),
        BottomNavigationItem("Search", Icons.Filled.Search, Icons.Outlined.Search, false, route = Screens.SearchScreen.route),
        BottomNavigationItem("Inbox", Icons.Filled.Email, Icons.Outlined.Email, false, 16, route = Screens.InboxScreen.route),
        BottomNavigationItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, true, route = Screens.UserProfileScreen.route)
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
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            } else if (item.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}