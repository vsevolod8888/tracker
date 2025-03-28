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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
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
import com.seva.tracker.presentation.mapNewRoute.POSITION_KYIV
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
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var selectedRouteName by remember { mutableStateOf<String?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(POSITION_KYIV, 10f)
    }
    val allIds by viewModel.getOnlyIdList().collectAsState(initial = emptyList())
    val routeCoordinatesMap = remember { mutableStateMapOf<Long, List<LatLng>>() }

    val routeEntities = remember { mutableStateMapOf<Long, RouteEntity?>() }

    var selectedRouteId by remember { mutableStateOf<Long?>(null) }

    val stringDeleteHelper = stringResource(R.string.clickwindowtodeleteroute)
    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(allIds) {
        for (routeId in allIds) {
            val routeEntity = viewModel.routeById(routeId)
            routeEntities[routeId] = routeEntity

            val coordinates = viewModel.coordtListLiveFlow(routeId).firstOrNull() ?: emptyList()
            routeCoordinatesMap[routeId] = coordinates.map { LatLng(it.lattitude, it.longittude) }
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
                        val customIcon = getBitmapDescriptor(R.drawable.ic_circle_)
                        Marker(
                            state = MarkerState(position = routePoints.first()),
                            title = routeEntity?.recordRouteName?.let { shortenString(it) } + " • ${routeEntity?.lenght}",
                            snippet = "${
                                routeEntity?.epochDays?.let {
                                    formatEpochDays(
                                        it
                                    )
                                }
                            } • $stringDeleteHelper",
                            icon = customIcon,
                            onClick = { marker ->
                                selectedRouteId = if (selectedRouteId == routeId) null else routeId
                                marker.showInfoWindow()
                                true
                            },
                            onInfoWindowClick = { marker ->
                                selectedRouteName = routeEntities[selectedRouteId]?.recordRouteName
                                showDialog = true
                            }
                        )

                        val isDrawing = routeEntity?.isDrawing ?: false

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
            if (showDialog) {
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
                                routeEntities.remove(selectedRouteId)
                                routeCoordinatesMap.remove(selectedRouteId)
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









