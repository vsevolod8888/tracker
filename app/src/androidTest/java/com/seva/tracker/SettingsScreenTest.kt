package com.seva.tracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.seva.tracker.presentation.settings.SettingsScreen
import com.seva.tracker.presentation.MyViewModel
import com.seva.tracker.presentation.bottomnavigation.NavigationItem
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import com.seva.tracker.presentation.mapAllRoutes.MapAllRoutesScreen
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule() //for all compose tests

    private lateinit var navController: TestNavHostController
    private lateinit var viewModel: MyViewModel
    val isNetworkAvailableFlow = MutableStateFlow(true)
    val isThemeDarkFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        viewModel = mockk {
            every { isNetworkAvailable } returns isNetworkAvailableFlow
            every { isThemeDark } returns isThemeDarkFlow
        }
    }

        @Test
    fun testNavigation() {
            composeTestRule.setContent {
                NavHost(
                    navController = navController,
                    startDestination = NavigationItem.Settings.route
                ) {
                    composable(NavigationItem.Settings.route) {
                        SettingsScreen(viewModel = viewModel, navController = navController)
                    }
                    composable(NavigationItem.MapAll.route) {
                        MapAllRoutesScreen(viewModel = viewModel, navController = navController)
                    }
                }
            } // screen is displayed

            composeTestRule.onNodeWithText("Show all routes").assertIsDisplayed().performClick() // action

            assertThat(navController.currentDestination?.route).isEqualTo(NavigationItem.MapAll.route)// what do i expect to see
    }
}


