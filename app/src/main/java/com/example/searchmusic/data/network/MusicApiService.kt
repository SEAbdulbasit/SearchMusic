package com.example.searchmusic.data.network

import com.example.searchmusic.data.network.response.SearchResults
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("search?entity=musicVideo")
    suspend fun searchForMusic(
        @Query("term") query: String,
        @Query("offset") offSet: Int,
        @Query("limit") limit: Int
    ): SearchResults

}