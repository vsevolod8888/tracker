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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.seva.tracker.R
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.io.wojciechosak.calendar.view.MyCalendar
import com.seva.tracker.presentation.common.rowbuttons
import com.seva.tracker.presentation.dialogs.ConfirmationDialog
import com.seva.tracker.presentation.routessmallcalendar.BackgroundHolderWhenDelete
import com.seva.tracker.presentation.routessmallcalendar.RouteHolder
import com.seva.tracker.presentation.topbar.ToolBar
import kotlin.math.absoluteValue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutesBigScreen(viewModel: MyViewModel, navController: NavHostController) {
    val routes by viewModel.allRoutesFlow().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var deletedMatch by remember { mutableStateOf<RouteEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isClickedSport by remember { mutableStateOf(1) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
                topBar = { Column {
                    ToolBar(
                        "History",
                        isVisiblePicturesInRight = false,
                        isVisiblePictureInLeft = true,
                        goToCalendar = {},
                        goToSettings = {},
                        onArrowBackClick = { navController.popBackStack() }
                    )
                    // Перемещаем rowbuttons внутрь topBar
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
                } },
//                    bottomBar = {
//                        if (currentRoute?.destination?.route != NavigationItem.MapDraw.route) {
//                            BottomNavigationBar(navController)
//                        }
//                    },
        //    floatingActionButton = {MyFloatingActionButton(navController)}
    ) { padding->

    Column (modifier = Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {


//        Spacer(modifier = Modifier.height(8.dp))
//
//        Box(modifier = Modifier.fillMaxWidth().height(50.dp)) {
//            rowbuttons(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//                    .padding(start = 12.dp, end = 20.dp, bottom = 12.dp)
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(colorResource(id = R.color.purple_200))
//            ) { isClickedSport = it }
//        }
        MyCalendar( onDateSelected = { selectedDate ->
//            selectedDayToEpoch = selectedDate
//            filteredGames = listOfGames.value.filter { it.epochDays == selectedDate }
//            scope.launch {
//                try {
//                    viewModel.getMatchesByDate(selectedDate.toString()).catch {
//                    }.collect {
//                    }
//                } catch (e: Exception){
//
//                }
//                delay(1000)
//
//                filteredGames =
//                    listOfGames.value.filter { it.epochDays == selectedDate }
//            }
        })


        when (isClickedSport.absoluteValue) {
            1 -> {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = routes.filter { !it.isDrawing },
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
                        items = routes.filter { it.isDrawing },
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
        }



    }

    if (showDialog && deletedMatch != null) {
        ConfirmationDialog(
            title = stringResource(R.string.tittledeleteallert),
            message = stringResource(R.string.areyousurewanttodeleteroute)+" ${deletedMatch?.recordRouteName}?",
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                coroutineScope.launch {
                //    Log.d("zzz","deletedMatch : ${deletedMatch!!.recordRouteName}")
                    deletedMatch?.let {
                        viewModel.deleteRouteAndRecordNumberTogether(it.id) }

                    deletedMatch = null
                }
                showDialog = false
            },
            onDismiss = {
                showDialog = false
                deletedMatch = null
            }
        )
    }}
}





