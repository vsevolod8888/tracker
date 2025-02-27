package com.seva.tracker.presentation.routessmallcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.presentation.bottomnavigation.NavigationItem

@Composable
fun RouteHolder(routeEntity:RouteEntity, navController: NavHostController){
    Row (modifier = Modifier.fillMaxWidth()
        .height(70.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.White).clickable {
        //    navController.navigate("${NavigationItem.MapReady.route}/${routeEntity.id}")
            navController.navigate(
                "${NavigationItem.MapReady.route}/${routeEntity.id}/${routeEntity.recordRouteName}"
            )

        },){
        Text(text = routeEntity.recordRouteName, color = Color.Black)
    }
}