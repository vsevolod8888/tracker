package com.seva.tracker.presentation.floatactionbutton

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.seva.tracker.R
import com.seva.tracker.presentation.bottomnavigation.NavigationItem

@Composable
fun MyFloatingActionButton(navController: NavHostController, onClickMyFloatingActionButton: () -> Unit,) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val selectedRoute = currentRoute?.destination?.route

    when (currentRoute?.destination?.route) {
        NavigationItem.RoutesSmallCalendar.route -> {
            FloatingActionButton(modifier = Modifier.size(60.dp), onClick = {
                onClickMyFloatingActionButton()
            },
                containerColor = MaterialTheme.colorScheme.primaryContainer,//.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.addroute),modifier = Modifier.size(40.dp))
            }
        }
    }
}