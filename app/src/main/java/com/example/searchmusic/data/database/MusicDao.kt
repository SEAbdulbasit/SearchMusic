package com.example.searchmusic.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.searchmusic.data.database.model.MusicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(musicList: List<MusicEntity>)

    @Query("SELECT * FROM music WHERE musicTitle LIKE :query OR artisName LIKE :query OR albumName LIKE :query ORDER BY timeStamp ASC")
    fun searchMusic(query: String): PagingSource<Int, MusicEntity>

    @Query("DELETE FROM music")
    fun clearMusic()

    @Query("SELECT * FROM music")
    fun getAll(): List<MusicEntity>

    @Query("SELECT * FROM music WHERE trackId == :musicId")
    fun getMusic(musicId: Long): Flow<MusicEntity>

}