package com.seva.tracker.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoordDao {
    @Query("select * from route")
    fun getRoutes(): LiveData<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //перезаписывает если одинак. айдишка
    suspend fun insertRoute(r: RouteEntity)

    @Query("DELETE FROM route")
    suspend fun deleteAllRoutes()

    @Query("DELETE FROM route WHERE id = :id")
    suspend fun deleteRouteById(id: Long)

    @Query("DELETE FROM coord WHERE recordNumber = :recordNumberId")
    suspend fun deleteCoordByRecordNumber(recordNumberId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoordList(coordlist: List<CoordinatesEntity>)

    @Query("SELECT * FROM coord WHERE recordNumber =:routeId")
    suspend fun coordtList(routeId: Int): List<CoordinatesEntity>

    @Query("SELECT * FROM route WHERE id =:routeId")
    suspend fun routeById(routeId: Long): RouteEntity?

    @Query("SELECT * FROM coord WHERE recordNumber =:routeId")
    fun coordtListLiveData(routeId: Int): LiveData<List<CoordinatesEntity>>

    @Insert//(onConflict = OnConflictStrategy.)
    suspend fun insertCoord(c: CoordinatesEntity)

    @Query("DELETE FROM coord")
    suspend fun clear()

    @Query("DELETE FROM route")
    suspend fun clear2()

    @Query("SELECT * FROM coord ORDER BY id DESC LIMIT 1")
    suspend fun lastCoord(): CoordinatesEntity?

    @Query("SELECT * FROM coord ORDER BY id DESC")
    fun allCoords(): LiveData<List<CoordinatesEntity>>

    @Query("SELECT MAX(recordNumber) FROM coord ")
    suspend fun getLastRecordNumber(): Long?

    @Query("SELECT * FROM coord WHERE recordNumber=:recordNumberId ORDER BY checktime ")
    fun getListByUnicalRecordNumber(recordNumberId: Long?): LiveData<List<CoordinatesEntity>>

    @Query("SELECT * FROM coord WHERE recordNumber=:recordNumberId ORDER BY checktime ")
    suspend fun getListByUnicalRecordNumberSuspend(recordNumberId: Long?): List<CoordinatesEntity>

    @Query("SELECT DISTINCT recordNumber FROM coord ORDER BY recordNumber ")
    fun getOnlyRecordNumbersList(): LiveData<List<Int?>>

    @Query("SELECT DISTINCT id FROM route ORDER BY id ")
    suspend fun getOnlyIdList(): List<Long>

    @Query("SELECT COUNT (*) FROM coord")
    suspend fun getCountNumberOfRecords(): Int

    @Query("SELECT COUNT (*) FROM route")
    fun getCountNumberOfRecords2(): Int
}