package com.example.searchmusic.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.searchmusic.data.database.model.MusicEntity

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repos: List<MusicEntity>)

    @Query(
        "SELECT * FROM music WHERE musicTitle LIKE :query ORDER BY musicTitle DESC"
    )
    fun searchMusic(query: String): PagingSource<Int, MusicEntity>

    @Query("DELETE FROM keys")
    fun clearMusic()

}