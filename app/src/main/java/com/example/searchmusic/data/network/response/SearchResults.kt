package com.example.searchmusic.data.network.response

data class SearchResults(
    val resultCount: Int?,
    val results: List<Result>?
)