package com.seva.tracker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey
    var id: Long,
    val epochDays: Int = 0,
    val isDrawing: Boolean,
    @ColumnInfo(name = "lenght")
    val lenght: String = "",
    @ColumnInfo(name = "checktime")
    val checkTime: Long,
    @ColumnInfo(name = "recordRouteName")
    val recordRouteName: String,
    @ColumnInfo(name = "isClicked")
    val isClicked: Boolean
)