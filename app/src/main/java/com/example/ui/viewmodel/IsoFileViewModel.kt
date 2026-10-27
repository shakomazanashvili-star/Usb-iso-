package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.IsoFile
import com.example.data.local.IsoFileDao
import com.example.data.local.VentoyDatabase
import com.example.data.network.OsMetadataApiClient
import com.example.data.network.model.OsMetadataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing IsoFile records via IsoFileDao,
 * and querying online OS metadata via Retrofit OsMetadataApiClient.
 * Exposes a reactive StateFlow/Flow of stored ISOs and provides CRUD & Auto-populate operations.
 */
class IsoFileViewModel(
    application: Application,
    private val isoFileDao: IsoFileDao = VentoyDatabase.getDatabase(application).isoFileDao(),
    private val osMetadataApiClient: OsMetadataApiClient = OsMetadataApiClient.getInstance()
) : AndroidViewModel(application) {

    // Filter and search state
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Online OS metadata search state
    private val _isSearchingMetadata = MutableStateFlow(false)
    val isSearchingMetadata: StateFlow<Boolean> = _isSearchingMetadata.asStateFlow()

    private val _latestMetadata = MutableStateFlow<OsMetadataResult?>(null)
    val latestMetadata: StateFlow<OsMetadataResult?> = _latestMetadata.asStateFlow()

    // Expose all stored ISO files as a reactive Flow
    val allIsoFiles: Flow<List<IsoFile>> = isoFileDao.getAllIsoFiles()


    // Combined, filtered StateFlow for UI consumption
    val isoFiles: StateFlow<List<IsoFile>> = combine(
        allIsoFiles,
        _selectedCategory,
        _searchQuery
    ) { files, category, query ->
        files.filter { file ->
            val matchesCategory = category.isNullOrBlank() || category == "ALL" || file.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    file.filename.contains(query, ignoreCase = true) ||
                    file.path.contains(query, ignoreCase = true) ||
                    file.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Search for OS metadata via Retrofit API (Wikipedia & OS search engine)
     */
    fun searchOnlineMetadata(
        queryOrFilename: String,
        onResult: ((OsMetadataResult) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isSearchingMetadata.value = true
            try {
                val result = osMetadataApiClient.searchOsMetadata(queryOrFilename).getOrNull()
                _latestMetadata.value = result
                if (result != null) {
                    onResult?.invoke(result)
                }
            } finally {
                _isSearchingMetadata.value = false
            }
        }
    }

    /**
     * Automatically queries online OS metadata for the given ISO file and updates
     * its category and description in the Room database.
     */
    fun autoPopulateMetadataForIso(
        isoFile: IsoFile,
        onCompleted: ((OsMetadataResult) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isSearchingMetadata.value = true
            try {
                val result = osMetadataApiClient.searchOsMetadata(isoFile.filename).getOrNull()
                if (result != null) {
                    _latestMetadata.value = result
                    isoFileDao.updateCategoryAndDescription(
                        id = isoFile.id,
                        category = result.category,
                        description = result.description
                    )
                    onCompleted?.invoke(result)
                }
            } finally {
                _isSearchingMetadata.value = false
            }
        }
    }

    /**
     * Add a new ISO file record to the database with optional description
     */
    fun addIsoFile(
        path: String,
        filename: String,
        category: String,
        checksum: String,
        sizeBytes: Long = 0L,
        isVerified: Boolean = false,
        description: String = ""
    ) {
        viewModelScope.launch {
            val isoFile = IsoFile(
                path = path,
                filename = filename,
                category = category,
                checksum = checksum,
                sizeBytes = sizeBytes,
                isVerified = isVerified,
                description = description,
                createdAt = System.currentTimeMillis()
            )
            isoFileDao.insert(isoFile)
        }
    }

    /**
     * Add an existing IsoFile entity
     */
    fun addIsoFile(isoFile: IsoFile) {
        viewModelScope.launch {
            isoFileDao.insert(isoFile)
        }
    }

    /**
     * Update an ISO file record
     */
    fun updateIsoFile(isoFile: IsoFile) {
        viewModelScope.launch {
            isoFileDao.update(isoFile)
        }
    }

    /**
     * Update checksum and verification status for an ISO record
     */
    fun updateChecksum(id: Long, checksum: String, isVerified: Boolean = true) {
        viewModelScope.launch {
            isoFileDao.updateChecksum(id, checksum, isVerified)
        }
    }

    /**
     * Update category for an ISO record
     */
    fun updateCategory(id: Long, category: String) {
        viewModelScope.launch {
            isoFileDao.updateCategory(id, category)
        }
    }

    /**
     * Update description for an ISO record
     */
    fun updateDescription(id: Long, description: String) {
        viewModelScope.launch {
            isoFileDao.updateDescription(id, description)
        }
    }

    /**
     * Atomically update category and description
     */
    fun updateCategoryAndDescription(id: Long, category: String, description: String) {
        viewModelScope.launch {
            isoFileDao.updateCategoryAndDescription(id, category, description)
        }
    }

    /**
     * Delete an ISO file record
     */
    fun deleteIsoFile(isoFile: IsoFile) {
        viewModelScope.launch {
            isoFileDao.delete(isoFile)
        }
    }

    /**
     * Delete an ISO file by ID
     */
    fun deleteIsoFileById(id: Long) {
        viewModelScope.launch {
            isoFileDao.deleteById(id)
        }
    }

    /**
     * Delete all ISO records
     */
    fun deleteAll() {
        viewModelScope.launch {
            isoFileDao.deleteAll()
        }
    }
}

