package com.seva.tracker.presentation.routessmallcalendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.seva.tracker.R
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.io.wojciechosak.calendar.view.CalendarDa
import com.seva.tracker.io.wojciechosak.calendar.view.MyCalendar
import com.seva.tracker.io.wojciechosak.calendar.view.MyWeekView
import com.seva.tracker.io.wojciechosak.calendar.view.WeekView
import com.seva.tracker.io.wojciechosak.calendar.view.isDateInRange
import com.seva.tracker.io.wojciechosak.calendar.view.today
import com.seva.tracker.presentation.bottomnavigation.NavigationItem
import com.seva.tracker.presentation.common.rowbuttons
import com.seva.tracker.presentation.dialogs.ConfirmationDialog
import com.seva.tracker.presentation.topbar.ToolBar
import kotlin.math.absoluteValue
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutesScreen(viewModel: MyViewModel, navController: NavHostController) {
    val routes by viewModel.allRoutesFlow().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var deletedMatch by remember { mutableStateOf<RouteEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isClickedSport by remember { mutableStateOf(1) }
    var selectedDayToEpoch by remember { mutableStateOf<Int?>(null) }
    val today = LocalDate.today()
    var scope = rememberCoroutineScope()
    var selectedDay by remember { mutableStateOf<LocalDate?>(LocalDate.today()) }
    var isScrolling by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxSize(),
                topBar = {
                   Column ( modifier = Modifier.fillMaxWidth().padding(top = 0.dp), verticalArrangement = Arrangement.Top) {
                       ToolBar(
                           "Hello",
                           isVisiblePicturesInRight = true,
                           isVisiblePictureInLeft = false,
                           goToCalendar = {
                               navController.navigate(NavigationItem.RoutesBigCalendar.route) {
                                   popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                   launchSingleTop = true
                                   restoreState = true
                               }
                           },
                           goToSettings = {
                               navController.navigate(NavigationItem.Settings.route) {
                                   popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                   launchSingleTop = true
                                   restoreState = true
                               }
                           },
                           onArrowBackClick = {
                               //  onClickBackFromCalendar()
                           })

                       Box(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                           rowbuttons(
                               modifier = Modifier.fillMaxWidth()
                                   .height(50.dp)
                                   .padding(start = 12.dp, end = 20.dp, bottom = 12.dp)
                                   .clip(RoundedCornerShape(20.dp))
                                   .background(colorResource(id = R.color.purple_200))
                           ) { isClickedSport = it }}
                   }

                    },
        containerColor = Color.Blue

    ) {padding->

        Column (modifier = Modifier.padding(padding).padding(horizontal = 20.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {

   //     MyCalendar( onDateSelected = { selectedDate ->
//            selectedDayToEpoch = selectedDate
//
//                filteredGames =
//                    listOfGames.value.filter { it.epochDays == selectedDate }
//            }
  //      })
            WeekView(
                startDate = LocalDate.today(),
                minDate = LocalDate.today().minus(1, DateTimeUnit.DAY),
                maxDate = LocalDate.today().plus(1, DateTimeUnit.DAY),
                isActive = { it == selectedDay },
                modifier = Modifier.fillMaxWidth().height(30.dp)
            ) { state ->

                CalendarDa(
                    state = state,
                    modifier = Modifier.height(30.dp)
                        //.width(if (state.date == LocalDate.today()) 90.dp else 40.dp)
                        .fillMaxWidth(
    //                        fraction =0.1428f
                            fraction = when {
                                isDateInRange(state.date) -> {
                                    if (state.date == LocalDate.today())
                                        0.23f
                                    else
                                        0.12f
                                }

                                else -> {
                                    0.1427f
                                }
                            }
                        ),//0.14285f
                    isSelected = state.date == selectedDay,
                    onClick = {
                        if (!isScrolling) {
                            isScrolling = true
                            selectedDay = state.date
                            selectedDayToEpoch = state.date.toEpochDays()
                            scope.launch {
                                try {
                               //     viewModel.getMatchesByDate(selectedDay.toString()).collect {

                   //                 }
//                                    filteredGames = listOfGames.value.filter { it.epochDays == state.date.toEpochDays() }
//                                        .filter { it.leagueId != 238L }
//                                        .filter { it.leagueId != 649L }
                                } catch (e: Exception) {

                                } finally {
                                    isScrolling = false
                                }
                            }
                        }
                    }
                )

            }


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





