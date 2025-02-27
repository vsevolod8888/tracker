package com.seva.tracker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey//(autoGenerate = true)
    var id: Long,//= System.currentTimeMillis()
    var isDrawing: Boolean,
    @ColumnInfo(name = "checktime")
    var checkTime: Long,//= System.currentTimeMillis()
    @ColumnInfo(name = "recordRouteName")
    var recordRouteName: String,
    @ColumnInfo(name = "isClicked")
    var isClicked: Boolean
)