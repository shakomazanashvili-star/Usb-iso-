package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing an ISO file with metadata for paths,
 * categories, and integrity verification checksums.
 */
@Entity(
    tableName = "iso_files",
    indices = [
        Index(value = ["path"], unique = true),
        Index(value = ["category"])
    ]
)
data class IsoFile(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "path")
    val path: String,

    @ColumnInfo(name = "filename")
    val filename: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "checksum")
    val checksum: String,

    @ColumnInfo(name = "sizeBytes")
    val sizeBytes: Long = 0L,

    @ColumnInfo(name = "isVerified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "description", defaultValue = "''")
    val description: String = "",

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis()
)

