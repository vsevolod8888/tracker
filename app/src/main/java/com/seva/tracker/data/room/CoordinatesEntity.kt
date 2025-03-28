package com.seva.tracker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coord")
data class CoordinatesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "checktime")
    val checkTime: Long,
    @ColumnInfo(name = "recordNumber")
    val recordNumber: Long,
    @ColumnInfo(name = "lattitude")
    val lattitude: Double,
    @ColumnInfo(name = "longittude")
    val longittude: Double
)