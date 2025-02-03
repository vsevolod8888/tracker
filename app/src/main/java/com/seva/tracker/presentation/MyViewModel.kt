package com.seva.tracker.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seva.tracker.data.datastore.SettingsDataStore
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.RouteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
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

    init {
        viewModelScope.launch {
            isNetworkAvailable.collect() {
                if (it) {

                }
            }
        }
    }
    suspend fun saveDrawCoord(
        lat: Double,
        lon: Double,
        t: Long,
        recNum: Long
    ) {
        val newcoord = CoordinatesEntity(
            id = 0,
            checkTime = t,
            recordNumber = recNum,                                                              //recordRouteName = nameOfRoute,
            Lattitude = lat,
            Longittude = lon
        )
        repository.insertCoord(newcoord)
    }

    suspend fun saveDrawRoute(nameOfDrRoute: String, numbOfRecord: Long) {
        val newRoute =
            RouteEntity(
                id = numbOfRecord!!,
                checkTime = System.currentTimeMillis(),
                recordRouteName = nameOfDrRoute,
                isClicked = false
            )
        repository.insertRoute(newRoute)
    }
}