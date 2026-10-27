package com.example.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Normalized result containing auto-populated OS metadata,
 * category, and description for ISO entries.
 */
data class OsMetadataResult(
    val title: String,
    val category: String,
    val description: String,
    val summary: String = description,
    val officialWebsite: String? = null,
    val thumbnailUrl: String? = null,
    val recommendedBootMode: String = "UEFI + Legacy BIOS",
    val source: String = "Wikipedia / Online OS Catalog"
)

// --- Wikipedia / Wikimedia REST Summary Models ---

@JsonClass(generateAdapter = true)
data class WikipediaSummary(
    @Json(name = "title") val title: String? = null,
    @Json(name = "displaytitle") val displayTitle: String? = null,
    @Json(name = "extract") val extract: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "thumbnail") val thumbnail: WikipediaThumbnail? = null,
    @Json(name = "content_urls") val contentUrls: WikipediaContentUrls? = null
)

@JsonClass(generateAdapter = true)
data class WikipediaThumbnail(
    @Json(name = "source") val source: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)

@JsonClass(generateAdapter = true)
data class WikipediaContentUrls(
    @Json(name = "desktop") val desktop: WikipediaUrlPage? = null
)

@JsonClass(generateAdapter = true)
data class WikipediaUrlPage(
    @Json(name = "page") val page: String? = null
)

// --- Wikipedia Action API Search Models ---

@JsonClass(generateAdapter = true)
data class WikipediaSearchResponse(
    @Json(name = "query") val query: WikipediaSearchQuery? = null
)

@JsonClass(generateAdapter = true)
data class WikipediaSearchQuery(
    @Json(name = "search") val search: List<WikipediaSearchItem>? = null
)

@JsonClass(generateAdapter = true)
data class WikipediaSearchItem(
    @Json(name = "title") val title: String? = null,
    @Json(name = "snippet") val snippet: String? = null,
    @Json(name = "pageid") val pageid: Long? = null
)

// --- Google Custom Search API Models ---

@JsonClass(generateAdapter = true)
data class GoogleSearchResponse(
    @Json(name = "items") val items: List<GoogleSearchItem>? = null
)

@JsonClass(generateAdapter = true)
data class GoogleSearchItem(
    @Json(name = "title") val title: String? = null,
    @Json(name = "snippet") val snippet: String? = null,
    @Json(name = "link") val link: String? = null
)
