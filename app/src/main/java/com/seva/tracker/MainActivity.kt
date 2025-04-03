package com.seva.tracker

import android.annotation.SuppressLint
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
import com.seva.tracker.permissions.BackGroundLocationPermissionHandler
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
    var scope = rememberCoroutineScope()

    if (showDialog) {
        NewRouteDialog(title = stringResource(R.string.routes),
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
                        pendingAction?.invoke()
                        pendingAction = null
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
                        pendingAction?.invoke()
                        pendingAction = null
                    }
                }
                onShowDialogChange(false)
            },
            onDismiss = {
                onShowDialogChange(false)
                navController.popBackStack()
            })
    }

    if (requestNotification) {
        NotificationPermissionHandler { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                requestNotification = false
                if (!locationPermission) {
                    requestLocation = true
                }
            }
        }
    }

    if (requestLocation) {
        LocationPermissionHandler(onPermissionResult = { isGranted ->
            locationPermission = isGranted
            if (isGranted) {
                requestLocation = false
                if (!backgroundPermission) {
                    requestBackgroundLocation = true
                }
            }
        }, onLocationReceived = { location ->

        })
    }

    if (requestBackgroundLocation) {
        BackGroundLocationPermissionHandler { isGranted ->
            backgroundPermission = isGranted
            Log.d("vvv", "isGranted BackGroundLocationPermissionHandler $isGranted")

            if (isGranted) {
                requestBackgroundLocation = false
                pendingAction?.invoke()
                pendingAction = null
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavigationItem.RoutesSmallCalendar.route,
        modifier = modifier
    ) {
        composable(NavigationItem.RoutesSmallCalendar.route) {
            RoutesScreen(
                viewModel, navController
            )
        }
        composable(NavigationItem.Settings.route) { SettingsScreen(viewModel, navController) }
        composable("${NavigationItem.MapDraw.route}/{routeName}") { backStackEntry ->
            val routeName = backStackEntry.arguments?.getString("routeName")
            MapDrawScreen(viewModel, navController, routeName)
        }
        composable("${NavigationItem.MapNew.route}/{routeName}") { backStackEntry ->
            val routeName = backStackEntry.arguments?.getString("routeName")
            MapNewRouteScreen(viewModel, navController)
        }
        composable(NavigationItem.MapAll.route) { backStackEntry ->
            MapAllRoutesScreen(viewModel, navController)
        }
        composable(NavigationItem.RoutesBigCalendar.route) {
            RoutesBigScreen(
                viewModel, navController
            )
        }
        composable("${NavigationItem.MapReady.route}/{routeId}/{recordRouteName}") { backStackEntry ->
            val routeId =
                backStackEntry.arguments?.getString("routeId")?.toLongOrNull() ?: return@composable
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

