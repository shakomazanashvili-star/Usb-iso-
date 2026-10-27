package com.example.data.network

import android.util.Log
import com.example.data.network.api.GoogleSearchApiService
import com.example.data.network.api.WikipediaApiService
import com.example.data.network.model.OsMetadataResult
import com.example.data.network.model.WikipediaSummary
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Modern Retrofit API client service for searching OS and utility metadata
 * via Wikipedia REST/Action APIs and Google Search to auto-populate categories
 * and descriptions for ISO images.
 */
class OsMetadataApiClient(
    private val customUserAgent: String = "VentoyPlus-Android/1.0 (https://github.com/ventoyplus)"
) {

    companion object {
        private const val TAG = "OsMetadataApiClient"
        private const val WIKIPEDIA_BASE_URL = "https://en.wikipedia.org/"
        private const val GOOGLE_SEARCH_BASE_URL = "https://www.googleapis.com/"

        @Volatile
        private var INSTANCE: OsMetadataApiClient? = null

        fun getInstance(): OsMetadataApiClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: OsMetadataApiClient().also { INSTANCE = it }
            }
        }
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val headerInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", customUserAgent)
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val wikipediaApi: WikipediaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(WIKIPEDIA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WikipediaApiService::class.java)
    }

    private val googleSearchApi: GoogleSearchApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GOOGLE_SEARCH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GoogleSearchApiService::class.java)
    }

    /**
     * Search for OS metadata using an ISO filename or OS name query.
     * Auto-populates title, category, description, and thumbnail.
     */
    suspend fun searchOsMetadata(queryOrFilename: String): Result<OsMetadataResult> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanQuery = cleanIsoFilenameToQuery(queryOrFilename)
            Log.d(TAG, "Searching metadata for '$queryOrFilename' using query '$cleanQuery'")

            // 1. Try direct Wikipedia summary with canonical title
            val directTitle = getDirectCanonicalTitle(cleanQuery)
            if (directTitle != null) {
                val directSummary = fetchWikipediaSummary(directTitle)
                if (directSummary != null && !directSummary.extract.isNullOrBlank()) {
                    return@runCatching buildMetadataResult(directSummary, cleanQuery)
                }
            }

            // 2. Perform Wikipedia Action search to find matching articles
            val searchResponse = runCatching { wikipediaApi.search(query = "$cleanQuery operating system") }.getOrNull()
            val firstSearchHit = searchResponse?.body()?.query?.search?.firstOrNull()?.title

            val targetArticle = firstSearchHit ?: cleanQuery

            // 3. Fetch summary for the matched article
            val summary = fetchWikipediaSummary(targetArticle)
            if (summary != null && !summary.extract.isNullOrBlank()) {
                return@runCatching buildMetadataResult(summary, cleanQuery)
            }

            // 4. Fallback heuristics if network or article is sparse
            buildFallbackResult(cleanQuery, queryOrFilename)
        }
    }

    /**
     * Optional search via Google Custom Search API if API key and Search Engine ID (CX) are provided
     */
    suspend fun searchViaGoogleCustomSearch(
        query: String,
        apiKey: String,
        searchEngineId: String
    ): Result<OsMetadataResult> = withContext(Dispatchers.IO) {
        runCatching {
            val response = googleSearchApi.search(
                query = "$query operating system official iso",
                apiKey = apiKey,
                cx = searchEngineId
            )
            if (!response.isSuccessful) {
                throw IllegalStateException("Google Search failed: ${response.code()} ${response.message()}")
            }

            val item = response.body()?.items?.firstOrNull()
                ?: throw NoSuchElementException("No Google Search results found for $query")

            val title = item.title ?: query
            val snippet = item.snippet ?: "Official operating system distribution."
            val category = inferCategory(title, snippet)

            OsMetadataResult(
                title = title,
                category = category,
                description = snippet,
                summary = snippet,
                officialWebsite = item.link,
                source = "Google Custom Search"
            )
        }
    }

    private suspend fun fetchWikipediaSummary(title: String): WikipediaSummary? {
        return runCatching {
            val encodedTitle = title.trim().replace(" ", "_")
            val response = wikipediaApi.getSummary(encodedTitle)
            if (response.isSuccessful) response.body() else null
        }.getOrNull()
    }

    private fun buildMetadataResult(summary: WikipediaSummary, originalQuery: String): OsMetadataResult {
        val title = summary.title ?: originalQuery
        val description = summary.extract ?: summary.description ?: "Bootable operating system distribution image."
        val category = inferCategory(title, description)
        val website = summary.contentUrls?.desktop?.page

        return OsMetadataResult(
            title = title,
            category = category,
            description = description,
            summary = description,
            officialWebsite = website,
            thumbnailUrl = summary.thumbnail?.source,
            recommendedBootMode = if (title.contains("Windows 11", ignoreCase = true)) "UEFI Only (SecureBoot & TPM 2.0)" else "UEFI + Legacy BIOS",
            source = "Wikipedia REST API"
        )
    }

    /**
     * Fallback generator when offline or for custom internal images
     */
    private fun buildFallbackResult(cleanQuery: String, originalFilename: String): OsMetadataResult {
        val category = inferCategory(cleanQuery, originalFilename)
        val description = when (category) {
            "Microsoft Windows" -> "Microsoft Windows installation and deployment environment with UEFI SecureBoot compatibility."
            "Security & Forensics" -> "Specialized security distribution with bundled penetration testing and digital forensics toolkits."
            "Recovery & Diagnostics" -> "Standalone emergency recovery, partition imaging, and hardware diagnostics environment."
            "Virtualization & Hypervisors" -> "Enterprise bare-metal hypervisor platform for containers and virtual machines."
            "Server & Storage" -> "Robust server operating system optimized for network services, NAS, and container workloads."
            "Linux Desktop" -> "Full-featured Linux operating system providing an intuitive graphical environment and productivity suite."
            else -> "Bootable operating system or diagnostic image for Ventoy multi-boot deployment."
        }

        return OsMetadataResult(
            title = cleanQuery,
            category = category,
            description = description,
            summary = description,
            source = "Ventoy+ OS Heuristic Engine"
        )
    }

    /**
     * Extracts a clean, searchable operating system name from raw ISO filenames
     */
    fun cleanIsoFilenameToQuery(filename: String): String {
        val lower = filename.lowercase()
        return when {
            lower.contains("win11") || lower.contains("windows11") || lower.contains("windows 11") -> "Windows 11"
            lower.contains("win10") || lower.contains("windows10") || lower.contains("windows 10") -> "Windows 10"
            lower.contains("windows") -> "Microsoft Windows"
            lower.contains("kali") -> "Kali Linux"
            lower.contains("parrot") -> "Parrot Security"
            lower.contains("tails") -> "Tails (operating system)"
            lower.contains("arch") -> "Arch Linux"
            lower.contains("ubuntu-server") || lower.contains("live-server") -> "Ubuntu Server"
            lower.contains("ubuntu") -> "Ubuntu"
            lower.contains("debian") -> "Debian"
            lower.contains("fedora") -> "Fedora Linux"
            lower.contains("mint") -> "Linux Mint"
            lower.contains("pop-os") || lower.contains("pop_os") -> "Pop!_OS"
            lower.contains("manjaro") -> "Manjaro"
            lower.contains("clonezilla") -> "Clonezilla"
            lower.contains("systemrescue") || lower.contains("sysresccd") -> "SystemRescue"
            lower.contains("gparted") -> "GParted"
            lower.contains("proxmox") -> "Proxmox Virtual Environment"
            lower.contains("truenas") -> "TrueNAS"
            lower.contains("freenas") -> "FreeNAS"
            lower.contains("alpine") -> "Alpine Linux"
            lower.contains("rocky") -> "Rocky Linux"
            lower.contains("alma") -> "AlmaLinux"
            lower.contains("opensuse") -> "openSUSE"
            lower.contains("nixos") -> "NixOS"
            lower.contains("endeavouros") -> "EndeavourOS"
            lower.contains("freebsd") -> "FreeBSD"
            lower.contains("memtest") -> "MemTest86"
            else -> {
                // Strip extension and common artifact tokens
                filename.substringBeforeLast(".")
                    .replace(Regex("[-_]"), " ")
                    .replace(Regex("(?i)(live|desktop|server|amd64|x86_64|x64|i386|arm64|dvd|minimal|netinst|iso|img)"), "")
                    .replace(Regex("\\s+"), " ")
                    .trim()
                    .ifBlank { "Operating System" }
            }
        }
    }

    /**
     * Maps known OS names directly to verified Wikipedia article titles
     */
    private fun getDirectCanonicalTitle(cleanQuery: String): String? {
        return when (cleanQuery) {
            "Windows 11" -> "Windows 11"
            "Windows 10" -> "Windows 10"
            "Microsoft Windows" -> "Microsoft Windows"
            "Ubuntu" -> "Ubuntu"
            "Ubuntu Server" -> "Ubuntu Server"
            "Debian" -> "Debian"
            "Arch Linux" -> "Arch Linux"
            "Kali Linux" -> "Kali Linux"
            "Fedora Linux" -> "Fedora Linux"
            "Linux Mint" -> "Linux Mint"
            "Tails (operating system)" -> "Tails (operating system)"
            "Clonezilla" -> "Clonezilla"
            "SystemRescue" -> "SystemRescue"
            "GParted" -> "GParted"
            "Proxmox Virtual Environment" -> "Proxmox Virtual Environment"
            "TrueNAS" -> "TrueNAS"
            "Alpine Linux" -> "Alpine Linux"
            "openSUSE" -> "openSUSE"
            "FreeBSD" -> "FreeBSD"
            else -> null
        }
    }

    /**
     * Intelligent categorization based on title, description, and keywords
     */
    fun inferCategory(title: String, description: String): String {
        val text = "$title $description".lowercase()
        return when {
            text.contains("security") || text.contains("penetration testing") ||
                    text.contains("forensics") || text.contains("kali") ||
                    text.contains("parrot") || text.contains("tails") ||
                    text.contains("vulnerability") || text.contains("cybersecurity") -> "Security & Forensics"

            text.contains("backup") || text.contains("clone") || text.contains("clonezilla") ||
                    text.contains("rescue") || text.contains("partition") ||
                    text.contains("diagnostic") || text.contains("gparted") ||
                    text.contains("memtest") || text.contains("disaster recovery") -> "Recovery & Diagnostics"

            text.contains("hypervisor") || text.contains("virtualization") ||
                    text.contains("proxmox") || text.contains("esxi") ||
                    text.contains("kvm") || text.contains("xen") ||
                    text.contains("virtual machine") -> "Virtualization & Hypervisors"

            text.contains("windows") || text.contains("microsoft") -> "Microsoft Windows"

            text.contains("server") || text.contains("nas") || text.contains("truenas") ||
                    text.contains("freenas") || text.contains("unraid") ||
                    text.contains("cloud-init") -> "Server & Storage"

            text.contains("utility") || text.contains("tool") || text.contains("firmware") ||
                    text.contains("benchmark") -> "Utility & Tools"

            text.contains("linux") || text.contains("desktop") || text.contains("distribution") ||
                    text.contains("workstation") || text.contains("bsd") -> "Linux Desktop"

            else -> "Operating System"
        }
    }
}
