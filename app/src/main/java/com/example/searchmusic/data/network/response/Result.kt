package com.example.searchmusic.data.network.response

data class Result(
    val amgArtistId: Int,
    val artistId: Int,
    val trackId: Long,
    val artistName: String,
    val artistViewUrl: String?,
    val artworkUrl100: String,
    val artworkUrl60: String?,
    val collectionCensoredName: String?,
    val collectionExplicitness: String?,
    val collectionId: Int?,
    val collectionName: String,
    val collectionPrice: Double?,
    val collectionViewUrl: String?,
    val copyright: String?,
    val country: String?,
    val currency: String?,
    val description: String?,
    val previewUrl: String,
    val primaryGenreName: String,
    val releaseDate: String?,
    val trackCount: Int?,
    val wrapperType: String?
)