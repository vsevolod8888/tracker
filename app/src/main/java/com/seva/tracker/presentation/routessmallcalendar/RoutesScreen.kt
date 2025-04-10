package com.seva.tracker.presentation.routessmallcalendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seva.tracker.presentation.MyViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.seva.tracker.R
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.presentation.calendar.WeekView
import com.seva.tracker.presentation.calendar.isDateInRange
import com.seva.tracker.presentation.calendar.today
import com.seva.tracker.presentation.bottomnavigation.NavigationItem
import com.seva.tracker.presentation.dialogs.DeleteRouteDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import androidx.compose.ui.res.painterResource
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.presentation.common.NoInternetPicture
import com.seva.tracker.presentation.common.NoRoutesText

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutesScreen(viewModel: MyViewModel, navController: NavHostController) {
    val routes by viewModel.allRoutesFlow().collectAsState(initial = emptyList())
    val isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState()
    var filteredRoutes by remember { mutableStateOf<List<RouteEntity>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var deletedMatch by remember { mutableStateOf<RouteEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDayToEpoch by remember { mutableStateOf<Int?>(null) }
    val today = LocalDate.today()
    var scope = rememberCoroutineScope()
    var selectedDay by remember { mutableStateOf<LocalDate?>(LocalDate.today()) }
    var isScrolling by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.allRoutesFlow().collect { routes ->
                filteredRoutes = if (selectedDayToEpoch == null)
                    routes.filter { it.epochDays == today.toEpochDays() }
                else
                    routes.filter { it.epochDays == selectedDayToEpoch }
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.routes), style = TextStyleLocal.semibold20)
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(NavigationItem.RoutesBigCalendar.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_calendar),
                            contentDescription = stringResource(R.string.settings),
                            modifier = Modifier
                                .width(30.dp)
                                .wrapContentHeight()
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(NavigationItem.Settings.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.settings),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.primary

    ) { padding ->
        if (isNetworkAvailable.value) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                LazyColumn(
                    modifier = Modifier.padding(top = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        WeekView(
                            startDate = LocalDate.today(),
                            minDate = LocalDate.today().minus(1, DateTimeUnit.DAY),
                            maxDate = LocalDate.today().plus(1, DateTimeUnit.DAY),
                            isActive = { it == selectedDay },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(50.dp)
                        ) { state ->

                            CalendarDaySmall(
                                state = state,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxWidth(
                                        fraction = when {
                                            isDateInRange(state.date) -> {
                                                if (state.date == LocalDate.today())
                                                    0.225f
                                                else
                                                    0.1125f
                                            }

                                            else -> {
                                                0.12874956f
                                            }
                                        }
                                    ),
                                isSelected = state.date == selectedDay,
                                onClick = {
                                    if (!isScrolling) {
                                        isScrolling = true
                                        selectedDay = state.date
                                        selectedDayToEpoch = state.date.toEpochDays()
                                        scope.launch {
                                            try {
                                                filteredRoutes =
                                                    routes.filter { it.epochDays == state.date.toEpochDays() }
                                            } catch (_: Exception) {
                                            } finally {
                                                isScrolling = false
                                            }
                                        }
                                    }
                                },
                                isDotVisible = routes.any {
                                    it.epochDays == state.date.toEpochDays().toInt()
                                },
                            )
                        }
                    }
                    if (filteredRoutes.isNotEmpty()) {
                        items(
                            items = filteredRoutes,
                            key = { selectedRoute -> selectedRoute.id }
                        ) { selectedRoute ->
                            val swipeState = rememberSwipeToDismissBoxState(
                                initialValue = SwipeToDismissBoxValue.Settled,
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                        deletedMatch = selectedRoute
                                        showDialog = true
                                        false
                                    } else {
                                        true
                                    }
                                },
                            )

                            SwipeToDismissBox(
                                modifier = Modifier.fillMaxWidth(),
                                state = swipeState,
                                enableDismissFromStartToEnd = true,
                                enableDismissFromEndToStart = true,
                                gesturesEnabled = true,
                                backgroundContent = {
                                    BackgroundHolderWhenDelete()
                                },
                            ) {
                                if (deletedMatch == selectedRoute) {
                                    BackgroundHolderWhenDelete()
                                } else {
                                    RouteHolder(routeEntity = selectedRoute, navController)
                                }
                            }
                        }
                    } else {
                        item {
                            NoRoutesText(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(bottom = 50.dp),
                                if (selectedDay == today) {
                                    stringResource(R.string.therearenoroutesfortoday)
                                } else {
                                    (stringResource(R.string.therearenoroutesonthisdate))
                                }
                            )
                        }

                    }

                }
            }
        } else {
            NoInternetPicture(padding)
        }

        if (showDialog && deletedMatch != null) {
            DeleteRouteDialog(
                title = stringResource(R.string.tittledeleteallert),
                message = stringResource(R.string.areyousurewanttodeleteroute) + " ${deletedMatch?.recordRouteName}?",
                confirmText = stringResource(R.string.delete),
                dismissText = stringResource(R.string.cancel),
                onConfirm = {
                    coroutineScope.launch {
                        deletedMatch?.let {
                            viewModel.deleteRouteAndRecordNumberTogether(it.id)
                        }

                        deletedMatch = null
                    }
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                    deletedMatch = null
                }
            )
        }
    }
}







