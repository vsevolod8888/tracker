package com.seva.tracker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey//(autoGenerate = true)
    var id: Long,//= System.currentTimeMillis()

    val epochDays: Int = 0,
    var isDrawing: Boolean,
    @ColumnInfo(name = "lenght")
    var lenght: String = "",
    @ColumnInfo(name = "checktime")
    var checkTime: Long,//= System.currentTimeMillis()
    @ColumnInfo(name = "recordRouteName")
    var recordRouteName: String,
    @ColumnInfo(name = "isClicked")
    var isClicked: Boolean
)