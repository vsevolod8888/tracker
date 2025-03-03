package com.seva.tracker.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.presentation.MyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MyViewModel, navController: NavHostController) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
//            ToolBar(
//                "Hello",
//                isVisiblePicturesInRight = false,
//                isVisiblePictureInLeft = true,
//                goToCalendar = {
//                },
//                goToSettings = {
//                },
//                onArrowBackClick = {
//                    navController.popBackStack()
//                })
            Column {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings),style = TextStyleLocal.regular16,)
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
            )}

        },
        containerColor = MaterialTheme.colorScheme.primary

    ) { padding ->

        Column(
            modifier = Modifier//.background(MaterialTheme.colorScheme.primary)
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                ButtonSwitchTheme(
                    stringResource(R.string.sound),
                    viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}