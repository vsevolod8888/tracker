package com.seva.tracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.seva.tracker.ui.theme.TrackerTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.seva.tracker.permissions.LocationPermissionHandler
import com.seva.tracker.permissions.NotificationPermissionHandler
import com.seva.tracker.presentation.mapDraw.MapDrawScreen
import com.seva.tracker.presentation.MyViewModel
import com.seva.tracker.presentation.bottomnavigation.NavigationItem
import com.seva.tracker.presentation.routessmallcalendar.RoutesScreen
import com.seva.tracker.presentation.settings.SettingsScreen
import com.seva.tracker.presentation.dialogs.NewRouteDialog
import com.seva.tracker.presentation.floatactionbutton.MyFloatingActionButton
import com.seva.tracker.presentation.mapAllRoutes.MapAllRoutesScreen
import com.seva.tracker.presentation.mapNewRoute.MapNewRouteScreen
import com.seva.tracker.presentation.mapReady.MapReadyScreen
import com.seva.tracker.presentation.routesbigcalendar.RoutesBigScreen
import com.seva.tracker.service.CounterService
import com.seva.tracker.utils.makeToastNoInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.provider.Settings
import androidx.core.app.ActivityCompat
import java.util.UUID
import android.net.Uri
import com.seva.tracker.utils.openLocationSettings
import com.seva.tracker.utils.toastLocation
import com.seva.tracker.utils.toastNeedNotifications


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            val isDarkTheme by viewModel.isThemeDark.collectAsState()
            lifecycleScope.launch {
                viewModel.isThemeDark.collect { isDarkTheme ->
                    enableEdgeToEdge(
                        statusBarStyle = if (isDarkTheme) {
                            SystemBarStyle.dark(Color.TRANSPARENT)
                        } else {
                            SystemBarStyle.light(
                                Color.TRANSPARENT, darkScrim = Color.TRANSPARENT
                            )
                        }
                    )
                }
            }
            TrackerTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                var showDialog by remember { mutableStateOf(false) }
                val isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState()
                Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
                    MyFloatingActionButton(navController,
                        onClickMyFloatingActionButton = {
                            if (isNetworkAvailable.value)
                                showDialog = true
                            else
                                makeToastNoInternet(baseContext)
                        })
                }) {
                    NavigationGraph(navController,
                        Modifier,
                        viewModel,
                        showDialog = showDialog,
                        onShowDialogChange = { showDialog = it })
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: MyViewModel,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit
) {
    var myRouteName by remember { mutableStateOf("") }
    val context = LocalContext.current
    var hasNotificationPermission by remember { mutableStateOf(false) }
    var locationPermission by remember { mutableStateOf(false) }
    var backgroundPermission by remember { mutableStateOf(false) }

    var requestNotification by remember { mutableStateOf(false) }
    var requestLocation by remember { mutableStateOf(false) }
    var requestBackgroundLocation by remember { mutableStateOf(false) }
    var pendingAction: (() -> Unit)? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    var backgroundRequestTrigger by remember { mutableStateOf(UUID.randomUUID()) }
    val backgroundRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        backgroundPermission = isGranted
        requestBackgroundLocation = false

        if (isGranted) {
            pendingAction?.invoke()
            pendingAction = null
        } else {
            backgroundRequestTrigger = UUID.randomUUID()
            backgroundPermission = false
        }
    }
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        backgroundPermission = isEnabled
        requestBackgroundLocation = false

        if (isEnabled) {
            pendingAction?.invoke()
            pendingAction = null
        }
    }

    LaunchedEffect(requestBackgroundLocation, backgroundRequestTrigger) {
        if (!requestBackgroundLocation ) return@LaunchedEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                backgroundPermission = true
                requestBackgroundLocation = false
                pendingAction?.invoke()
                pendingAction = null
            } else {
                val activity = context as? Activity
                if (activity != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    openAppSettings(context)
                    requestBackgroundLocation = false
                } else {
                    backgroundRequestLauncher.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }
        } else {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                locationSettingsLauncher.launch(intent)
            } else {
                backgroundPermission = true
                requestBackgroundLocation = false
                pendingAction?.invoke()
                pendingAction = null
            }
        }
    }

    if (showDialog) {
        NewRouteDialog(
            title = stringResource(R.string.routes),
            message = stringResource(R.string.enteraroutenameandselectanaction),
            routeName = myRouteName,
            onRouteNameChange = { myRouteName = it },
            onNewRoute = {
                pendingAction = {
                    scope.launch {
                        viewModel.saveRouteName(myRouteName)
                        myRouteName = ""
                        navController.navigate("${NavigationItem.MapNew.route}/$myRouteName") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        delay(START_DELAY)
                        startCounterService(context)
                    }
                }
                when {
                    !hasNotificationPermission -> requestNotification = true
                    !locationPermission -> requestLocation = true
                    !backgroundPermission -> requestBackgroundLocation = true
                    else -> {
                        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        if (isGpsEnabled) {
                            pendingAction?.invoke()
                            pendingAction = null
                        } else {
                            toastLocation(context)
                            openLocationSettings(context)
                        }
                    }
                }
                onShowDialogChange(false)
            },
            onDrawRoute = {
                pendingAction = {
                    scope.launch {
                        navController.navigate("${NavigationItem.MapDraw.route}/$myRouteName") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        myRouteName = ""
                    }
                }
                when {
                    !locationPermission -> requestLocation = true
                    !backgroundPermission -> requestBackgroundLocation = true
                    else -> {
                        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        if (isGpsEnabled) {
                            pendingAction?.invoke()
                            pendingAction = null
                        } else {
                            toastLocation(context) // Показываем, что локация отключена
                            openLocationSettings(context) // Открываем настройки локации
                        }
                    }
                }
                onShowDialogChange(false)
            },
            onDismiss = {
                onShowDialogChange(false)
                navController.popBackStack()
            }
        )
    }
    if (requestNotification) {
        NotificationPermissionHandler(
            onPermissionResult = { isGranted ->
                Log.d("vvv", "295 isGrantedNotification $isGranted")
                hasNotificationPermission = isGranted
                requestNotification = false
                if (isGranted && !locationPermission) {
                    requestLocation = true
                } else if (!isGranted) {
                    toastNeedNotifications(context)
                    openAppSettings(context)
                }
            }
        )
    }


    if (requestLocation) {
        LocationPermissionHandler(
            onPermissionResult = { isGranted ->
                locationPermission = isGranted
                requestLocation = false
                if (isGranted && !backgroundPermission) {
                    requestBackgroundLocation = true
                }else if (!isGranted) {
                    toastLocation(context)
                    openAppSettings(context)
                }
            },
            onLocationReceived = { /* обработка данных о локации */ }
        )
    }

    NavHost(
        navController = navController,
        startDestination = NavigationItem.RoutesSmallCalendar.route,
        modifier = modifier
    ) {
        composable(NavigationItem.RoutesSmallCalendar.route) {
            RoutesScreen(viewModel, navController)
        }
        composable(NavigationItem.Settings.route) {
            SettingsScreen(viewModel, navController)
        }
        composable("${NavigationItem.MapDraw.route}/{routeName}") { backStackEntry ->
            val routeName = backStackEntry.arguments?.getString("routeName")
            MapDrawScreen(viewModel, navController, routeName)
        }
        composable("${NavigationItem.MapNew.route}/{routeName}") { backStackEntry ->
            val routeName = backStackEntry.arguments?.getString("routeName")
            MapNewRouteScreen(viewModel, navController)
        }
        composable(NavigationItem.MapAll.route) {
            MapAllRoutesScreen(viewModel, navController)
        }
        composable(NavigationItem.RoutesBigCalendar.route) {
            RoutesBigScreen(viewModel, navController)
        }
        composable("${NavigationItem.MapReady.route}/{routeId}/{recordRouteName}") { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId")?.toLongOrNull() ?: return@composable
            val recordRouteName = backStackEntry.arguments?.getString("recordRouteName") ?: ""
            MapReadyScreen(viewModel, navController, routeId, recordRouteName)
        }
    }
}

fun startCounterService(context: Context) {
    val intent = Intent(context, CounterService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}
const val START_DELAY = 50L

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

fun checkAndHandleGpsStatus(
    context: Context,
    pendingAction: (() -> Unit)?,
    toastLocation: () -> Unit,
    openLocationSettings: () -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    if (isGpsEnabled) {
        pendingAction?.invoke()
    } else {
        toastLocation()
        openLocationSettings()
    }
}





