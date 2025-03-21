package com.seva.tracker.presentation.mapReady

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.permissions.LocationPermissionHandler
import com.seva.tracker.presentation.MyViewModel
import com.seva.tracker.presentation.bottomnavigation.NavigationItem
import com.seva.tracker.presentation.mapDraw.calculateRouteLength
import com.seva.tracker.startCounterService
import kotlinx.coroutines.launch

@Composable
fun MapReadyScreen(
    viewModel: MyViewModel,
    navController: NavHostController,
    routeId: Long,
    recordRouteName: String
) {
    var routeEntity by remember { mutableStateOf<RouteEntity?>(null) }
    val context = LocalContext.current
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var routeLength by remember { mutableStateOf("") }
    var scope = rememberCoroutineScope()


    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.4501, 30.5234), 10f)
    }
    val markers = remember { mutableStateListOf<MarkerState>() }
    var markerLatLngList by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val kmText = stringResource(R.string.km)
    val metersText = stringResource(R.string.meters)

    val coordinates by viewModel.coordtListLiveFlow(routeId).collectAsState(initial = emptyList())

    LaunchedEffect(coordinates) {
        coordinates.forEachIndexed { index, coordinatesEntity ->

            Log.d(
                "zzz",
                " coordinates.size : ${index} ${coordinatesEntity.Lattitude} ${coordinatesEntity.Longittude})"
            )
        }

        markers.clear()
        markerLatLngList = coordinates.map { LatLng(it.Lattitude, it.Longittude) }
        routeLength = calculateRouteLength(markerLatLngList, kmText, metersText)
        markers.addAll(markerLatLngList.map { MarkerState(position = it) })

        // Центрируем камеру на первую точку маршрута
        if (markerLatLngList.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(markerLatLngList.last(), 15f),
                durationMs = 200
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.updateRouteId(routeId)
    }

    LocationPermissionHandler(
        onPermissionResult = { isGranted -> locationPermissionGranted = isGranted },
        onLocationReceived = { latLng ->
            Log.d("zzz", " location : ${latLng.latitude}, ${latLng.longitude})")
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    )
    LaunchedEffect(routeId) {
        routeEntity = viewModel.routeById(routeId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface

    ) { padding ->

        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
            ) {
                if (markerLatLngList.size > 1) {
                    Polyline(
                        points = markerLatLngList,
                        color = if (routeEntity?.isDrawing == true) Color.Red else Color.Blue,
                        width = 5f
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = routeLength,
                    style = TextStyleLocal.semibold18,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        scope.launch {
                            when (routeEntity?.isDrawing) {
                                true -> {
                                    viewModel.saveRouteName(recordRouteName)
                                    navController.navigate("${NavigationItem.MapDraw.route}/$recordRouteName") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }

                                else -> {
                                    viewModel.saveRouteName(recordRouteName)
                                    navController.navigate("${NavigationItem.MapNew.route}/$recordRouteName") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    startCounterService(context)
                                }
                            }
                        }
                    },
                    colors = ButtonColors(
                        containerColor =  MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.conti),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
    BackHandler {
        scope.launch {
            viewModel.updateRouteId(0L)
            navController.popBackStack()
        }
    }
}





