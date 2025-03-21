package com.seva.tracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [CoordinatesEntity::class,RouteEntity::class],
    version  = 2,
    exportSchema  = false
)

abstract class Database: RoomDatabase(){
    abstract val  dao: CoordDao

}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE route ADD COLUMN lenght TEXT NOT NULL DEFAULT ''")
    }
}