package com.seva.tracker.presentation.bottomnavigation

import com.seva.tracker.R

sealed class NavigationItem(val route: String, val icon: Int? = null, val titleResId: Int) {
    data object RoutesSmallCalendar : NavigationItem("routes", R.drawable.ic_notes, R.string.routes)
    data object RoutesBigCalendar :
        NavigationItem("routesbiggalendar", R.drawable.ic_notes, R.string.routes)

    data object Settings : NavigationItem("settings", R.drawable.ic_settings, R.string.settings)
    data object MapDraw : NavigationItem("mapdraw/{routeName}", titleResId = R.string.mapdraw)
    data object MapAll : NavigationItem("map_all", R.drawable.ic_add, R.string.create)
    data object MapReady : NavigationItem("map_ready", titleResId = R.string.mapready)
    data object MapNew : NavigationItem("map_new/{routeName}", titleResId = R.string.mapready)

}