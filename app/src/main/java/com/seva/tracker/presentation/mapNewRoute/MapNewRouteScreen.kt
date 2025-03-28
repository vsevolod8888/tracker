package com.seva.tracker.presentation.mapNewRoute

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.permissions.LocationPermissionHandler
import com.seva.tracker.presentation.MyViewModel
import com.seva.tracker.presentation.mapDraw.calculateRouteLength
import com.seva.tracker.service.CounterService
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapNewRouteScreen(
    viewModel: MyViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var routeLength by remember { mutableStateOf("") }
    var scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(POSITION_KYIV, 10f)
    }
    val lastMarker = remember { mutableStateOf<MarkerState?>(null) }

    var markerLatLngList by remember { mutableStateOf(emptyList<LatLng>()) }
    val kmText = stringResource(R.string.km)
    val metersText = stringResource(R.string.meters)

    val routeId by viewModel.routeId.collectAsState()

    LaunchedEffect(routeId) {
        if (routeId == 0L) {
            viewModel.updateRouteId(System.currentTimeMillis())
        }
    }
    val coordinates by viewModel.coordtListLiveFlow(routeId).collectAsState(initial = emptyList())

    LaunchedEffect(coordinates) {
        markerLatLngList = coordinates.map { LatLng(it.lattitude, it.longittude) }

        if (markerLatLngList.isNotEmpty()) {
            val markerState = MarkerState(position = markerLatLngList.first())
            lastMarker.value = MarkerState(position = markerState.position)
            routeLength = calculateRouteLength(markerLatLngList, kmText, metersText)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(markerLatLngList.last(), 15f),
                durationMs = 200
            )
        }
    }

    LocationPermissionHandler(
        onPermissionResult = { isGranted -> locationPermissionGranted = isGranted },
        onLocationReceived = { latLng ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    )
    DisposableEffect(Unit) {
        val stopReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "STOP_SERVICE_ACTION") {
                    navController.popBackStack()
                }
            }
        }
        val filter = IntentFilter("STOP_SERVICE_ACTION")
        context.registerReceiver(stopReceiver, filter, Context.RECEIVER_EXPORTED)

        onDispose {
            context.unregisterReceiver(stopReceiver)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface

    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
                onMapClick = {}
            ) {
                lastMarker.value?.let { markerState ->
                    Marker(
                        state = markerState,
                        title = "Last Marker"
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
                            stopService(context)
                            navController.popBackStack()
                        }

                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.save),
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
            viewModel.deleteRouteAndRecordNumberTogether(routeId)
            navController.popBackStack()
        }
    }
}

@SuppressLint("SuspiciousIndentation")
fun stopService(context: Context) {
    val intent = Intent(context, CounterService::class.java)
    context.stopService(intent)
}

val POSITION_KYIV =LatLng(50.4501, 30.5234)





