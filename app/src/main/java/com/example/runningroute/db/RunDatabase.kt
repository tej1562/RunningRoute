package com.example.runningroute.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertors::class)
abstract class RunDatabase: RoomDatabase() {

    abstract fun getDao(): RunDAO
}