package com.seva.tracker.presentation.bottomnavigation

import com.seva.tracker.R

sealed class NavigationItem(val route: String, val icon: Int?=null, val titleResId: Int) {
    object RoutesSmallCalendar : NavigationItem("routes", R.drawable.ic_notes, R.string.routes)
    object RoutesBigCalendar : NavigationItem("routesbiggalendar", R.drawable.ic_notes, R.string.routes)
    object Settings : NavigationItem("settings", R.drawable.ic_settings, R.string.settings)
    object MapDraw : NavigationItem("mapdraw/{routeName}", titleResId= R.string.mapdraw)
    object Create : NavigationItem("create", R.drawable.ic_add, R.string.create)
    object MapReady : NavigationItem("map_ready", titleResId= R.string.mapready)
    object MapNew : NavigationItem("map_new/{routeName}", titleResId= R.string.mapready)

}