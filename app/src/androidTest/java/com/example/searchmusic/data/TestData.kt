package com.example.searchmusic.data

import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.network.response.SearchResults
import com.example.searchmusic.data.network.response.getResult

val listOfMusic = mutableListOf(
    MusicEntity(
        trackId = 1441154571,
        artisName = "Rihanna",
        musicTitle = "Disturbia",
        albumName = "Good Girl Gone Bad: Reloaded",
        imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music126/v4/2b/c0/81/2bc081c8-25f0-ba43-d451-587a54613778/16UMGIM59202.rgb.jpg/100x100bb.jpg",
        previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/a4/16/ce/a416ce10-e861-6e5a-194d-ad4fb2e4cd38/mzaf_7519322396128489319.plus.aac.p.m4a"
    ), MusicEntity(
        trackId = 1441154572,
        artisName = "Rihanna",
        musicTitle = "Disturbia",
        albumName = "Good Girl Gone Bad: Reloaded",
        imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music126/v4/2b/c0/81/2bc081c8-25f0-ba43-d451-587a54613778/16UMGIM59202.rgb.jpg/100x100bb.jpg",
        previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/a4/16/ce/a416ce10-e861-6e5a-194d-ad4fb2e4cd38/mzaf_7519322396128489319.plus.aac.p.m4a"
    ), MusicEntity(
        trackId = 1441154573,
        artisName = "Rihanna",
        musicTitle = "Disturbia",
        albumName = "Good Girl Gone Bad: Reloaded",
        imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music126/v4/2b/c0/81/2bc081c8-25f0-ba43-d451-587a54613778/16UMGIM59202.rgb.jpg/100x100bb.jpg",
        previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/a4/16/ce/a416ce10-e861-6e5a-194d-ad4fb2e4cd38/mzaf_7519322396128489319.plus.aac.p.m4a"
    )
)

fun getSearchResults(): SearchResults {

    val resultList = mutableListOf<com.example.searchmusic.data.network.response.Result>()
    for (i in 1..100) {
        resultList.add(
            getResult(
                trackId = 1441154571,
                artistName = "Rihanna",
                previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/a4/16/ce/a416ce10-e861-6e5a-194d-ad4fb2e4cd38/mzaf_7519322396128489319.plus.aac.p.m4a",
                musicTitle = "Disturbia",
                albumName = "Good Girl Gone Bad: Reloaded",
                artWork = "https://is4-ssl.mzstatic.com/image/thumb/Music126/v4/2b/c0/81/2bc081c8-25f0-ba43-d451-587a54613778/16UMGIM59202.rgb.jpg/100x100bb.jpg",
            )
        )
    }
    return SearchResults(
        resultCount = resultList.size, results = resultList
    )
}

fun getEmptySearchResults(): SearchResults {
    return SearchResults(
        resultCount = 0, results = emptyList()
    )
}