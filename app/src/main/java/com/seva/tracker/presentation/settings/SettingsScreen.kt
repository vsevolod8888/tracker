package com.seva.tracker.presentation.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.presentation.MyViewModel
import com.seva.tracker.presentation.bottomnavigation.NavigationItem
import com.seva.tracker.presentation.dialogs.DeleteRouteDialog
import com.seva.tracker.utils.makeToastNoInternet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MyViewModel, navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var context = LocalContext.current
    val isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings),style = TextStyleLocal.semibold20)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )

        },
        containerColor = MaterialTheme.colorScheme.primary

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ButtonSwitchTheme(
                stringResource(R.string.themelightdark),
                viewModel,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(52.dp).border(
                    1.dp, MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(50.dp)
                )
            )

            ButtonOnSettingsScreen(
                stringResource(R.string.deleteallroutes),//stringResource(R.string.sound)
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(52.dp).border(
                    1.dp, MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(50.dp)
                ),
                onClick = {
                    showDialog = true
                }
            )

            ButtonOnSettingsScreen(
                stringResource(R.string.notifications),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(52.dp).border(
                    1.dp, MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(50.dp)
                ),
                onClick = {
                    viewModel.goToSettings(context)
                }
            )

            ButtonOnSettingsScreen(
                stringResource(R.string.showallroutes),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(52.dp).border(
                    1.dp, MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(50.dp)
                ),
                onClick = {
                    if (isNetworkAvailable.value){
                        navController.navigate(NavigationItem.MapAll.route) {
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = false //
//                        }
                            launchSingleTop = true
                            //   restoreState = true
                        }
                    }else{
                        makeToastNoInternet(context)
                    }

                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {

            }
        }
        if (showDialog) {
            DeleteRouteDialog(
                title = stringResource(R.string.tittledeletalldialog),
                message = stringResource(R.string.areyousurewanttodeleteallroutes),
                confirmText = stringResource(R.string.delete),
                dismissText = stringResource(R.string.cancel),
                onConfirm = {

                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.deleteAllRoutesAndCoords()
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