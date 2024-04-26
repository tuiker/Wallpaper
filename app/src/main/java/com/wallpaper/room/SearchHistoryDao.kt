package com.wallpaper.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history")
    fun getAllSearchHistory(): LiveData<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchTerm(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistoryById(id: Long)
    @Query("DELETE FROM search_history WHERE search_text = :searchTerm")
    suspend fun deleteSearchTerm(searchTerm: String)
}
