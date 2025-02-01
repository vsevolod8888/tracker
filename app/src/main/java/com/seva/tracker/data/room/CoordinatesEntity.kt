package com.seva.tracker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coord")
data class CoordinatesEntity(                        //WeatherDatabase
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "checktime")
    var checkTime: Long,
    @ColumnInfo(name = "recordNumber")
    var recordNumber: Long,                        // было Int
    @ColumnInfo(name = "lattitude")
    var Lattitude: Double,
    @ColumnInfo(name = "longittude")
    var Longittude: Double
)