package com.seva.tracker.data.repository.impl

import android.util.Log
import androidx.room.withTransaction
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
   override suspend fun lastNumberOfList(): Long? {
        return try {
            databasse.dao.getLastRecordNumber()//
        } catch (e: Exception) {
            Log.d("eee", e.message!!)
            null
        }
    }
   override fun coordtListLiveFlow(routeId: Long): Flow<List<CoordinatesEntity>> {
        return databasse.dao.getListByUnicalRecordNumber(routeId)
    }
    override suspend fun deleteRouteAndRecordNumberTogether(id: Long) {

        databasse.withTransaction {
            databasse.dao.deleteRouteById(id)
            databasse.dao.deleteCoordByRecordNumber(id)
        }
    }
    override fun allRoutesFlow(): Flow<List<RouteEntity>> {
        return databasse.dao.getRoutes()
    }
}