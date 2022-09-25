package com.example.searchmusic.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.searchmusic.presentation.musiclist.MusicUiModel

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


fun MusicEntity.toUiModel() = MusicUiModel(
    trackId = this.trackId,
    artisName = this.artisName,
    musicTitle = this.musicTitle,
    albumName = this.albumName,
    imageUrl = this.imageUrl,
    previewUrl = this.previewUrl
)
