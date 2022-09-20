package com.example.searchmusic.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.searchmusic.data.database.model.MusicKeys

@Dao
interface MusicKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<MusicKeys>)

    @Query("SELECT * FROM keys WHERE id = :musicId")
    fun getMusicKey(musicId: Long): MusicKeys?

    @Query("DELETE FROM keys")
    fun clearKeys()
}