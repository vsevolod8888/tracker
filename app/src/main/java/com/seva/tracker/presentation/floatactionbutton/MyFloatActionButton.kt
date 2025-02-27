package com.seva.tracker.presentation.floatactionbutton

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.seva.tracker.presentation.bottomnavigation.NavigationItem

@Composable
fun MyFloatingActionButton(navController: NavHostController, onClickMyFloatingActionButton: () -> Unit,) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val selectedRoute = currentRoute?.destination?.route

    when (currentRoute?.destination?.route) {
        NavigationItem.RoutesSmallCalendar.route -> {
            FloatingActionButton(onClick = {
                onClickMyFloatingActionButton()
//                if (selectedRoute != null) {
//                    navController.navigate("${NavigationItem.MapDraw.route}/Default") {
//                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Карта")
            }
        }
    }
}