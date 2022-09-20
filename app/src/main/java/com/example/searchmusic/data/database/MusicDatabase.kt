package com.example.searchmusic.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.database.model.MusicKeys


@Database(
    entities = [MusicEntity::class, MusicKeys::class], version = 1
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun musicKeyDao(): MusicKeysDao
}