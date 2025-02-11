package com.seva.tracker.data.repository

import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.RouteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Repository {
    val isConnectedFlow: StateFlow<Boolean>
    suspend fun updateFutureMatches()
    suspend fun insertCoord(c: CoordinatesEntity)
    suspend fun insertRoute(newRoute: RouteEntity)
    suspend fun lastNumberOfList(): Long?
    fun coordtListLiveFlow(routeId: Long): Flow<List<CoordinatesEntity>>
    suspend fun deleteRouteAndRecordNumberTogether(id: Long)
    fun allRoutesFlow(): Flow<List<RouteEntity>>
}