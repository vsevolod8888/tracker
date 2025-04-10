package com.seva.tracker

import com.seva.tracker.data.datastore.SettingsDataStore
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.presentation.MyViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@ExperimentalCoroutinesApi
class MyViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MyViewModel
    private val settingsData: SettingsDataStore = mockk(relaxed = true)
    private val repository: Repository =
        mockk(relaxed = true)//relaxed = false so you need to write down what the method returns

    @Before
    fun setup() {
        viewModel = MyViewModel(settingsData, repository)
    }

    @Test
    fun `updateRouteId saves new value`() = runTest {
        viewModel.updateRouteId(123L)

        coVerify { settingsData.saveRouteId(123L) }//coVerify - check that the function was called (for suspend)
    } // for usual verify

    @Test
    fun `saveDrawRoute creates route and saves it in DB`() = runTest {
        val routeName = "Test Route"
        val recordNumber = 86400000L
        val length = "10km"

        viewModel.saveDrawRoute(routeName, recordNumber, length)
        coVerify { repository.insertRoute(any()) }
    }

    @Test
    fun `deleteAllRoutesAndCoords deletes all routes and coordinates`() = runTest {
        viewModel.deleteAllRoutesAndCoords()

        coVerify { repository.deleteAllRoutesAndCoords() }
    }
}


@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestRule {

    private val testCoroutineScope = TestCoroutineScope(dispatcher)

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                Dispatchers.setMain(dispatcher)
                try {
                    base.evaluate()
                } finally {
                    Dispatchers.resetMain()
                    dispatcher.cleanupTestCoroutines()
                }
            }
        }
    }

    fun getDispatcher() = dispatcher
    fun getTestScope() = testCoroutineScope
}


