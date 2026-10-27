package com.example.data.network.api

import com.example.data.network.model.GoogleSearchResponse
import com.example.data.network.model.WikipediaSearchResponse
import com.example.data.network.model.WikipediaSummary
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for Wikipedia / Wikimedia API.
 * Provides public, rate-friendly search and summary endpoints for operating systems and utilities.
 */
interface WikipediaApiService {

    /**
     * Fetch a concise page summary (title, extract, thumbnail, description)
     * e.g., /api/rest_v1/page/summary/Ubuntu
     */
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getSummary(
        @Path("title") title: String
    ): Response<WikipediaSummary>

    /**
     * Full-text search for matching OS titles and snippet previews
     * e.g., /w/api.php?action=query&list=search&srsearch=Kali+Linux&format=json
     */
    @GET("w/api.php")
    suspend fun search(
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("srsearch") query: String,
        @Query("format") format: String = "json",
        @Query("srlimit") limit: Int = 5
    ): Response<WikipediaSearchResponse>
}

/**
 * Retrofit interface for Google Custom Search JSON API.
 * Used when a Google Search API key and CX (engine ID) are configured.
 */
interface GoogleSearchApiService {

    @GET("customsearch/v1")
    suspend fun search(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("cx") cx: String,
        @Query("num") num: Int = 3
    ): Response<GoogleSearchResponse>
}
