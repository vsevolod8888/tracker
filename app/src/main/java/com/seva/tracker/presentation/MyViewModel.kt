package com.seva.tracker.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
    val isNetworkAvailable: StateFlow<Boolean> = repository.isConnectedFlow

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

    val routeId: StateFlow<Long> = settingsData.routeId
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

    suspend fun saveDrawRoute(nameOfDrRoute: String, numbOfRecord: Long, lenght:String) {
        var epochDays  = numbOfRecord/86400000
        val newRoute =
            RouteEntity(
                id = numbOfRecord,
                epochDays = epochDays.toInt(),
                lenght = lenght,
                isDrawing = true,
                checkTime = System.currentTimeMillis(),
                recordRouteName = nameOfDrRoute,
                isClicked = false
            )
        repository.insertRoute(newRoute)
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


    val isThemeDark: StateFlow<Boolean> = settingsData.isThemeDarkDataStore
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    suspend fun updateTheme(theme: Boolean) {
        Log.d("vvv", "viewmodel updateTheme $theme")
        settingsData.saveIsThemeDarkDataStore(theme)
    }
    suspend fun deleteAllRoutesAndCoords(){
        repository.deleteAllRoutesAndCoords()
    }

    fun goToSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }

    fun getOnlyIdList(): Flow<List<Long>>{
        return repository.getOnlyIdList()
    }
}