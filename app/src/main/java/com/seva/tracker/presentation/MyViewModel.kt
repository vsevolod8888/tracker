package com.seva.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seva.tracker.data.datastore.SettingsDataStore
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.RouteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val settingsData: SettingsDataStore,
    private val repository: Repository
) : ViewModel() {
    val isNetworkAvailable: StateFlow<Boolean> = repository.isConnectedFlow
    val routeId: StateFlow<Long> = settingsData.routeId
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    suspend fun updateRouteId(newId: Long) {
        settingsData.saveRouteId(newId)
    }

    suspend fun saveRouteName(routeName: String) {
        settingsData.saveRouteNameDataStore(routeName)
    }

    suspend fun clearRouteId() {
        settingsData.saveRouteId(0L)
    }

    suspend fun saveDrawCoord(
        lat: Double,
        lon: Double,
        checkTime: Long,
        recNum: Long
    ) {
        val newcoord = CoordinatesEntity(
            id = 0,
            checkTime = checkTime,
            recordNumber = recNum,
            lattitude = lat,
            longittude = lon
        )
        repository.insertCoord(newcoord)
    }

    suspend fun saveDrawRoute(nameOfDrRoute: String, numbOfRecord: Long, lenght: String) {
        var epochDays = numbOfRecord / 86400000
        val newRoute =
            RouteEntity(
                id = numbOfRecord,
                epochDays = epochDays.toInt(),
                lenght = lenght,
                isDrawing = true,
                checkTime = System.currentTimeMillis(),//1743436572754
                recordRouteName = nameOfDrRoute,
                isClicked = false
            )
        repository.insertRoute(newRoute)
    }

    fun coordtListLiveFlow(routeId: Long): Flow<List<CoordinatesEntity>> {
        return repository.coordtListLiveFlow(routeId)
    }

    suspend fun deleteRouteAndRecordNumberTogether(id: Long) {
        repository.deleteRouteAndRecordNumberTogether(id)
    }

    fun allRoutesFlow(): Flow<List<RouteEntity>> {
        return repository.allRoutesFlow()
    }

    suspend fun routeById(routeId: Long): RouteEntity? {
        return repository.routeById(routeId)
    }

    val isThemeDark: StateFlow<Boolean> = settingsData.isThemeDarkDataStore
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    suspend fun updateTheme(theme: Boolean) {
        settingsData.saveIsThemeDarkDataStore(theme)
    }

    suspend fun deleteAllRoutesAndCoords() {
        repository.deleteAllRoutesAndCoords()
    }

    fun getOnlyIdList(): Flow<List<Long>> {
        return repository.getOnlyIdList()
    }
}