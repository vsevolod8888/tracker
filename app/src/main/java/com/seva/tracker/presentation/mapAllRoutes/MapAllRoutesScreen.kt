package com.seva.tracker.presentation.mapAllRoutes

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.seva.tracker.R
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.permissions.LocationPermissionHandler
import com.seva.tracker.presentation.MyViewModel
import com.seva.tracker.presentation.dialogs.DeleteRouteDialog

import com.seva.tracker.utils.formatEpochDays
import com.seva.tracker.utils.getBitmapDescriptor
import com.seva.tracker.utils.shortenString
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MapAllRoutesScreen(
    viewModel: MyViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var selectedRouteName by remember { mutableStateOf<String?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.4501, 30.5234), 10f)
    }

    val allIds by viewModel.getOnlyIdList().collectAsState(initial = emptyList())
    val routeCoordinatesMap = remember { mutableStateMapOf<Long, List<LatLng>>() }

    val routeEntities = remember { mutableStateMapOf<Long, RouteEntity?>() }

    var selectedRouteId by remember { mutableStateOf<Long?>(null) }

    val stringDeleteHelper = stringResource(R.string.clickwindowtodeleteroute)
        //  val markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker)
    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(allIds) {
        for (routeId in allIds) {
            val routeEntity = viewModel.routeById(routeId)
            routeEntities[routeId] = routeEntity

            val coordinates = viewModel.coordtListLiveFlow(routeId).firstOrNull() ?: emptyList()
            routeCoordinatesMap[routeId] = coordinates.map { LatLng(it.Lattitude, it.Longittude) }
        }
    }
    LocationPermissionHandler(
        onPermissionResult = { isGranted -> locationPermissionGranted = isGranted },
        onLocationReceived = { latLng ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    )
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
                onMapLoaded = { isMapLoaded = true },
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
                onMapClick = {}
            ) {

                routeCoordinatesMap.forEach { (routeId, routePoints) ->
                    if (routePoints.isNotEmpty()) {
                        val routeEntity = routeEntities[routeId]
                     //   if (isMapLoaded) {
                           // val markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker)
                            val customIcon = getBitmapDescriptor(R.drawable.ic_circle_)
                        Marker(
                            state = MarkerState(position = routePoints.first()),
                            title = routeEntity?.recordRouteName?.let { shortenString(it) }+" • ${routeEntity?.lenght}",
                            snippet = "${
                                routeEntity?.epochDays?.let {
                                    formatEpochDays(
                                        it
                                    )
                                }
                            } • $stringDeleteHelper" ,
                            icon = customIcon,
                            onClick = { marker ->
                                selectedRouteId = if (selectedRouteId == routeId) null else routeId
                                marker.showInfoWindow()
                                true
                            },
                            onInfoWindowClick = { marker ->
                                Log.d("Map", "Клик по InfoWindow у маркера: ${marker.title}")
                                selectedRouteName = routeEntities[selectedRouteId]?.recordRouteName
                                showDialog = true

                            }
                        )
                 //   }


                        val isDrawing = routeEntity?.isDrawing ?: false
                        Log.d("vvv", "isDrawing $isDrawing")

                        val polylineWidth = if (selectedRouteId == routeId)
                            20f
                        else 5f

                        if (routePoints.size > 1) {
                            Polyline(
                                points = routePoints,
                                color = if (isDrawing) Color.Red else Color.Blue,
                                width = polylineWidth
                            )
                        }
                    }
                }

            }
            if (showDialog ) {
                DeleteRouteDialog(
                    title = stringResource(R.string.tittledeleteallert),
                    message = stringResource(R.string.areyousurewanttodeleteroute) + " ${selectedRouteName}?",
                    confirmText = stringResource(R.string.delete),
                    dismissText = stringResource(R.string.cancel),
                    onConfirm = {
                        scope.launch {
                            selectedRouteId?.let {
                                viewModel.deleteRouteAndRecordNumberTogether(
                                    it
                                )
                                // Удаляем маршрут из локальных списков
                                routeEntities.remove(selectedRouteId)
                                routeCoordinatesMap.remove(selectedRouteId)

                                // Обнуляем выбранный маршрут
                                selectedRouteId = null
                            }
                        }
                        showDialog = false
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }
    }

    BackHandler {
        scope.launch {
            navController.popBackStack()
        }
    }
}









