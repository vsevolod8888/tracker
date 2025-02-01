package com.seva.tracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [CoordinatesEntity::class,RouteEntity::class],
    version  = 1,
    exportSchema  = false
)

abstract class Database: RoomDatabase(){
    abstract val  dao: CoordDao

}