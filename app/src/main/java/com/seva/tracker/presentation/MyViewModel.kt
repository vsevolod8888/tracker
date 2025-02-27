package com.seva.tracker.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seva.tracker.data.datastore.SettingsDataStore
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.RouteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    val settingsData: SettingsDataStore,
    private val repository: Repository,
    @ApplicationContext context: Context
) : ViewModel() {
    var jobCurrentWeek: Job? = null
    private val isNetworkAvailable: StateFlow<Boolean> = repository.isConnectedFlow

    private var numbOfRecord: Long? = null

    init {
        viewModelScope.launch {
            isNetworkAvailable.collect() {
                if (it) {

                }
            }
        }
    }


    init {
//        viewModelScope.launch {
//            val savedRouteId = settingsData.route_id.first()
//            _routeId.value = if (savedRouteId > 0) savedRouteId.toLong() else System.currentTimeMillis()
//        }
    }

    val routeId: StateFlow<Long> = settingsData.route_id
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    suspend fun updateRouteId(newId: Long) {
        Log.d("zzz", "viewmodel updateRouteId routeId $newId")
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
            recordNumber = recNum,                                 //recordRouteName = nameOfRoute,
            Lattitude = lat,
            Longittude = lon
        )
        repository.insertCoord(newcoord)
    }

    suspend fun saveDrawRoute(nameOfDrRoute: String, numbOfRecord: Long) {
        val newRoute =
            RouteEntity(
                id = numbOfRecord,
                isDrawing = true,
                checkTime = System.currentTimeMillis(),
                recordRouteName = nameOfDrRoute,
                isClicked = false
            )
        repository.insertRoute(newRoute)
    }

    suspend fun lastNumberOfList(): Long {
        if (numbOfRecord != null) {
            return numbOfRecord!!
        }
        var lastNumberOfList = repository.lastNumberOfList()
        numbOfRecord = if (lastNumberOfList == null) {
            System.currentTimeMillis()
        } else {
            lastNumberOfList
            //System.currentTimeMillis()
        }
        return numbOfRecord!!
    }

    fun coordtListLiveFlow(routeId: Long): Flow<List<CoordinatesEntity>> {
        return repository.coordtListLiveFlow(routeId)
    }
    suspend fun deleteRouteAndRecordNumberTogether(id: Long){
        repository.deleteRouteAndRecordNumberTogether(id)
    }
    fun allRoutesFlow(): Flow<List<RouteEntity>> {
        return repository.allRoutesFlow()
    }

    suspend fun routeById(routeId: Long): RouteEntity?{
        return repository.routeById(routeId)
    }
}