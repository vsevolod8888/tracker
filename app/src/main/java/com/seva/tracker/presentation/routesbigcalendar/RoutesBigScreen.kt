package com.seva.tracker.presentation.routesbigcalendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.io.wojciechosak.calendar.view.MyCalendar
import com.seva.tracker.io.wojciechosak.calendar.view.today
import com.seva.tracker.presentation.common.rowbuttons
import com.seva.tracker.presentation.dialogs.ConfirmationDialog
import com.seva.tracker.presentation.routessmallcalendar.BackgroundHolderWhenDelete
import com.seva.tracker.presentation.routessmallcalendar.RouteHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutesBigScreen(viewModel: MyViewModel, navController: NavHostController) {
    val routes by viewModel.allRoutesFlow().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var deletedMatch by remember { mutableStateOf<RouteEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isClickedSport by remember { mutableStateOf(1) }
    var filteredRoutes by remember { mutableStateOf<List<RouteEntity>>(emptyList()) }
    var selectedDayToEpoch by remember { mutableStateOf<Int?>(null) }
    val today = LocalDate.today()

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
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.routes), style = TextStyleLocal.regular16)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Фон
                        titleContentColor = MaterialTheme.colorScheme.onSurface, // Цвет текста
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface, // Цвет иконки "назад"
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(colorResource(id = R.color.purple_200))
                        .clip(RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    rowbuttons(Modifier.fillMaxSize()) { isClickedSport = it }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.primary

        ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            MyCalendar(routes, onDateSelected = { selectedDate ->
                selectedDayToEpoch = selectedDate
                filteredRoutes = routes.filter { it.epochDays == selectedDate }

            })


            when (isClickedSport.absoluteValue) {
                1 -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredRoutes.filter { !it.isDrawing },
                            key = { selectedRoute -> selectedRoute.id }
                        ) { selectedRoute ->

                            // Создаем состояние для смахивания
                            val swipeState = rememberSwipeToDismissBoxState(
                                initialValue = SwipeToDismissBoxValue.Settled,
                                confirmValueChange = {
                                    // Логика для обработки смахивания
                                    if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                        // Если элемент был смахнут
                                        deletedMatch = selectedRoute
                                        showDialog = true // Показать диалог
                                        false
                                    } else {
                                        true
                                    }
                                },
                            )

                            // Используем SwipeToDismissBox
                            SwipeToDismissBox(
                                modifier = Modifier.fillMaxWidth(),
                                state = swipeState,
                                enableDismissFromStartToEnd = true,
                                enableDismissFromEndToStart = true,
                                gesturesEnabled = true,
                                backgroundContent = {
                                    // Задний фон для смахивания
                                    BackgroundHolderWhenDelete()
                                },
                            ) {
                                // Показываем Background только для удаляемого маршрута
                                if (deletedMatch == selectedRoute) {
                                    BackgroundHolderWhenDelete()
                                } else {
                                    RouteHolder(routeEntity = selectedRoute, navController)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredRoutes.filter { it.isDrawing },
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
                    }
                }
            }
        }

        if (showDialog && deletedMatch != null) {
            ConfirmationDialog(
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





