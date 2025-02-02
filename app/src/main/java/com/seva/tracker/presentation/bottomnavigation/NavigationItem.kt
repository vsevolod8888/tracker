package com.seva.tracker.presentation.bottomnavigation

import com.seva.tracker.R

sealed class NavigationItem(val route: String, val icon: Int, val titleResId: Int) {
    object Routes : NavigationItem("routes", R.drawable.ic_notes, R.string.routes)
    object Settings : NavigationItem("settings", R.drawable.ic_settings, R.string.settings)
    object Map : NavigationItem("map", R.drawable.ic_settings, R.string.map)

}