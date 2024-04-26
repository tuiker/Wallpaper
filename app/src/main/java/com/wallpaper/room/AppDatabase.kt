package com.wallpaper.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SearchHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}
