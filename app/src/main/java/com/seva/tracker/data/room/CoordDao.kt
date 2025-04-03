package com.seva.tracker.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordDao {
    @Query("select * from route")
    fun getRoutes(): Flow<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(r: RouteEntity)

    @Query("DELETE FROM route")
    suspend fun deleteAllRoutes()

    @Query("DELETE FROM coord")
    suspend fun deleteAllCoords()

    @Query("DELETE FROM route WHERE id = :id")
    suspend fun deleteRouteById(id: Long)

    @Query("DELETE FROM coord WHERE recordNumber = :recordNumberId")
    suspend fun deleteCoordByRecordNumber(recordNumberId: Long)

    @Query("SELECT * FROM route WHERE id =:routeId")
    suspend fun routeById(routeId: Long): RouteEntity?

    @Insert
    suspend fun insertCoord(c: CoordinatesEntity)

    @Query("SELECT * FROM coord WHERE recordNumber=:recordNumberId ORDER BY checktime ")
    fun getListByUnicalRecordNumber(recordNumberId: Long?): Flow<List<CoordinatesEntity>>

    @Query("SELECT DISTINCT id FROM route ORDER BY id ")
    fun getOnlyIdList(): Flow<List<Long>>
}