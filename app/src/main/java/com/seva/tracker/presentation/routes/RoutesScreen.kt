package com.seva.tracker.presentation.routes

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seva.tracker.presentation.MyViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.resolveDefaults
import com.seva.tracker.R
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.presentation.common.ConfirmationDialog

@Composable
fun RoutesScreen(viewModel: MyViewModel, navController: NavHostController) {
    val routes by viewModel.allRoutesFlow().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var deletedMatch by remember { mutableStateOf<RouteEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = routes,
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
                    enableDismissFromStartToEnd = true,  // Включаем возможность смахивания с начала в конец
                    enableDismissFromEndToStart = true,  // Включаем возможность смахивания с конца в начало
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
    }
}





