package com.seva.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.seva.tracker.ui.theme.TrackerTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.seva.tracker.presentation.MapScreen
import com.seva.tracker.presentation.bottomnavigation.BottomNavigationBar
import com.seva.tracker.presentation.bottomnavigation.NavigationItem
import com.seva.tracker.presentation.RoutesScreen
import com.seva.tracker.presentation.SettingsScreen
import com.seva.tracker.presentation.floatactionbutton.MyFloatingActionButton
import com.seva.tracker.presentation.topbar.TopBar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackerTheme {
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar(navController) },
                    bottomBar = {
                        if(currentRoute?.destination?.route!=NavigationItem.Map.route){
                            BottomNavigationBar(navController)
                        }
                       },
                    floatingActionButton = {MyFloatingActionButton(navController)}
                ) { innerPadding ->
                    NavigationGraph(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController,
        startDestination = NavigationItem.Routes.route,
        modifier = modifier) {
        composable(NavigationItem.Routes.route) { RoutesScreen() }
        composable(NavigationItem.Settings.route) { SettingsScreen() }
        composable(NavigationItem.Map.route) { MapScreen() }
    }
}



