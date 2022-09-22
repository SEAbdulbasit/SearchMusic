package com.example.searchmusic.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music")
data class MusicEntity(
    @PrimaryKey val trackId: Long,
    val musicTitle: String,
    val artisName: String,
    val albumName: String,
    val imageUrl: String,
    val previewUrl: String,
    val timeStamp: Long = System.currentTimeMillis()
)
