package com.seva.tracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import com.seva.tracker.data.room.CoordDao
import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.Database
import com.seva.tracker.data.room.MIGRATION_1_2
import com.seva.tracker.data.room.RouteEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class DatabaseTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: Database
    private lateinit var dao: CoordDao

    @JvmField
    @Rule//you can set up test repetitions
    var migrationHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        Database::class.java.getCanonicalName(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            Database::class.java
        ).allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2)
            .build()
        dao = database.dao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertRoute() = runBlocking {
        val route = RouteEntity(
            id = 1,
            lenght = "10 km",
            epochDays = 12345,
            isDrawing = false,
            checkTime = 1743436572754,//System.currentTimeMillis(),
            recordRouteName = "Test Route",
            isClicked = false
        )
        dao.insertRoute(route)
        val retrieved = dao.routeById(1)
        assertNotNull(retrieved) // check that it is not empty
        assertEquals("10 km", retrieved?.lenght)// check fields
        assertEquals("Test Route", retrieved?.recordRouteName)
    }

    @Test
    fun deleteRouteById() = runBlocking {
        val route = RouteEntity(
            id = 2,
            lenght = "5 km",
            epochDays = 12346,
            isDrawing = false,
            checkTime = System.currentTimeMillis(),
            recordRouteName = "Another Route",
            isClicked = false
        )
        dao.insertRoute(route)
        dao.deleteRouteById(2)
        val retrieved = dao.routeById(2)
        assertNull(retrieved)// we check that it is really null
    }

    @Test
    fun insertAndRetrieveCoordinates() = runBlocking {
        val coord = CoordinatesEntity(
            id = 1,
            checkTime = System.currentTimeMillis(),
            recordNumber = 1,
            lattitude = 55.75,
            longittude = 37.61
        )
        dao.insertCoord(coord)

        val retrieved = dao.getListByUnicalRecordNumber(1).first()
        assertEquals(1, retrieved.size)
        assertEquals(55.75, retrieved.first().lattitude, 0.01)// delta - error is allowed
    }

    @Test
    fun deleteAllCoordinates() = runBlocking {
        val coords = listOf(
            CoordinatesEntity(
                id = 2,
                checkTime = System.currentTimeMillis(),
                recordNumber = 1,
                lattitude = 40.71,
                longittude = -74.01
            ),
            CoordinatesEntity(
                id = 3,
                checkTime = System.currentTimeMillis(),
                recordNumber = 1,
                lattitude = 34.05,
                longittude = -118.24
            )
        )
        coords.forEach { dao.insertCoord(it) }
        dao.deleteAllCoords()

        val retrieved = dao.getListByUnicalRecordNumber(1).first()
        assertTrue(retrieved.isEmpty())
    }

    @Test
    fun getOnlyIdList() = runBlocking {
        val routes = listOf(
            RouteEntity(
                id = 10,
                lenght = "8 km",
                epochDays = 12347,
                isDrawing = false,
                checkTime = System.currentTimeMillis(),
                recordRouteName = "Route A",
                isClicked = false
            ),
            RouteEntity(
                id = 20,
                lenght = "12 km",
                epochDays = 12348,
                isDrawing = false,
                checkTime = System.currentTimeMillis(),
                recordRouteName = "Route B",
                isClicked = false
            )
        )
        routes.forEach { dao.insertRoute(it) }
        val ids = dao.getOnlyIdList().first()
        assertEquals(setOf(10L, 20L), ids.toSet())
    }
}

