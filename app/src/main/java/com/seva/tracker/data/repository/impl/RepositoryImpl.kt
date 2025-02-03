package com.seva.tracker.data.repository.impl

import android.util.Log
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.Database
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.internetconnection.MyConnectivityManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val databasse: Database,
    private val connectivityManager: MyConnectivityManager
): Repository {

    override val isConnectedFlow = connectivityManager.connectionAsStateFlow

    override suspend fun updateFutureMatches() {
        if (!connectivityManager.isConnected) {
            return
        }
    }
    override suspend fun insertRoute(newRoute: RouteEntity) {
        databasse.dao.insertRoute(newRoute)
    }
    override suspend fun insertCoord(c: CoordinatesEntity) {
        databasse.dao.insertCoord(c)
    }
}