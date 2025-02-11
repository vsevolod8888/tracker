package com.seva.tracker.presentation.mapNewRoute

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.seva.tracker.presentation.MyViewModel
import com.seva.tracker.presentation.common.LocationPermissionHandler
import com.seva.tracker.presentation.mapDraw.calculateRouteLength
import kotlinx.coroutines.launch

@Composable
fun MapNewRoute(viewModel: MyViewModel, navController: NavHostController) {
    val context = LocalContext.current
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var routeLength by remember { mutableStateOf(0.0) } // Длина маршрута
    var scope = rememberCoroutineScope()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.4501, 30.5234), 10f)
    }
    val markers = remember { mutableStateListOf<MarkerState>() }

    var markerLatLngList by remember { mutableStateOf(emptyList<LatLng>()) }


    val routeId by viewModel.routeId.collectAsState()

    LaunchedEffect(Unit) {
        if (routeId == 0L) {
            viewModel.updateRouteId(System.currentTimeMillis())
        }
    }
    val coordinates by viewModel.coordtListLiveFlow(routeId).collectAsState(initial = emptyList())
    LaunchedEffect(coordinates) {
        markers.clear()

        // Создаём новый список вместо изменения старого
        markerLatLngList = coordinates.map { LatLng(it.Lattitude, it.Longittude) }
        routeLength = calculateRouteLength(markerLatLngList)
        markers.addAll(markerLatLngList.map { MarkerState(position = it) })

        if (markerLatLngList.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(markerLatLngList.last(), 15f),
                durationMs = 200
            )
        }
    }

    LocationPermissionHandler(
        onPermissionResult = { isGranted -> locationPermissionGranted = isGranted },
        onLocationReceived = { latLng ->
            Log.d("zzz"," location : ${latLng.latitude}, ${latLng.longitude})")
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    )


    val onMapClick: (LatLng) -> Unit = { latLng ->
        val markerState = MarkerState(position = latLng)
        markers.add(markerState)

        scope.launch {
            viewModel.saveDrawCoord(
                markerState.position.latitude,
                markerState.position.longitude,
                checkTime = System.currentTimeMillis(),
                recNum = routeId
            )
        }

        // Добавляем новую точку в список сразу
        markerLatLngList = markerLatLngList + latLng

        // Пересчитываем длину маршрута
            // routeLength = calculateRouteLength(markerLatLngList)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Отображаем карту
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
            onMapClick = onMapClick
        ) {


            // Отображаем маркеры
            markers.forEach { markerState ->
                Marker(
                    state = markerState,
                    title = "New Marker"
                )
            }
            if (markerLatLngList.size > 1) {
                Polyline(
                    points = markerLatLngList,
                    color = Color.Blue,
                    width = 5f
                )
            }
        }
        Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween){
            Text(
                text = "Route Length: ${"%.2f".format(routeLength)} meters",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = {
                scope.launch {
                    viewModel.saveDrawRoute(
                        nameOfDrRoute = "Route Length: ${"%.2f".format(routeLength)} m", // Заглушка длины
                        numbOfRecord = routeId
                    )
                    viewModel.clearRouteId() // Очищаем ID после сохранения
                    navController.popBackStack()
                }

            }) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Отображаем длину маршрута

    }
    BackHandler {
        scope.launch {
            viewModel.deleteRouteAndRecordNumberTogether(routeId)
            navController.popBackStack()
        }
    }
}





