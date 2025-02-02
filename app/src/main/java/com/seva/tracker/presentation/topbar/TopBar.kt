package com.seva.tracker.presentation.topbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.seva.tracker.R
import com.seva.tracker.presentation.bottomnavigation.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val title = when (currentRoute?.destination?.route) {
        NavigationItem.Routes.route -> stringResource(R.string.routes)
        NavigationItem.Settings.route -> stringResource(R.string.settings)
        else -> stringResource(R.string.app_name) // Заголовок по умолчанию
    }

    TopAppBar(

        title = { Text(title) },
        colors = TopAppBarColors(
            containerColor = colorResource(R.color.black),
            scrolledContainerColor = colorResource(R.color.black),
            navigationIconContentColor = colorResource(R.color.black),
            titleContentColor = colorResource(R.color.white),
            actionIconContentColor = colorResource(R.color.black)
        )
    )
}