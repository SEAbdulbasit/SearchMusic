package com.example.searchmusic.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keys")
data class MusicKeys(
    @PrimaryKey val id: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
