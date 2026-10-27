package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.AutoInstallScript
import com.example.data.model.BootMode
import com.example.data.model.DistroType
import com.example.data.model.IsoItem

/**
 * Room database entity representing locally stored ISO file metadata,
 * including storage paths, categorization, and checksums for integrity verification.
 */
@Entity(
    tableName = "iso_items",
    indices = [
        Index(value = ["filePath"]),
        Index(value = ["categoryName"]),
        Index(value = ["verificationStatus"])
    ]
)
data class IsoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "filename")
    val filename: String,

    @ColumnInfo(name = "filePath")
    val filePath: String = "",

    @ColumnInfo(name = "sizeBytes")
    val sizeBytes: Long,

    @ColumnInfo(name = "distroType")
    val distroType: String,

    @ColumnInfo(name = "categoryName")
    val categoryName: String = "LINUX_DESKTOP",

    @ColumnInfo(name = "architecture")
    val architecture: String = "x86_64",

    @ColumnInfo(name = "version")
    val version: String,

    @ColumnInfo(name = "bootMode")
    val bootMode: String,

    // Checksum & Integrity Verification metadata
    @ColumnInfo(name = "sha256Expected")
    val sha256Expected: String = "",

    @ColumnInfo(name = "sha256Calculated")
    val sha256Calculated: String = "",

    @ColumnInfo(name = "md5Checksum")
    val md5Checksum: String = "",

    @ColumnInfo(name = "isVerified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "verificationStatus")
    val verificationStatus: String = "UNVERIFIED",

    @ColumnInfo(name = "verifiedTimestamp")
    val verifiedTimestamp: Long = 0L,

    // Boot & Vault configuration
    @ColumnInfo(name = "isEncrypted")
    val isEncrypted: Boolean = false,

    @ColumnInfo(name = "isPinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "persistenceMb")
    val persistenceMb: Int = 0,

    @ColumnInfo(name = "autoInstall")
    val autoInstall: String = "NONE",

    @ColumnInfo(name = "isDefaultBoot")
    val isDefaultBoot: Boolean = false,

    @ColumnInfo(name = "dateAdded")
    val dateAdded: Long = System.currentTimeMillis()
) {
    fun toDomain(): IsoItem {
        return IsoItem(
            id = id,
            name = name,
            filename = filename,
            filePath = filePath.ifBlank { "/ventoy/$filename" },
            sizeBytes = sizeBytes,
            distroType = runCatching { DistroType.valueOf(distroType) }.getOrDefault(DistroType.CUSTOM),
            version = version,
            bootMode = runCatching { BootMode.valueOf(bootMode) }.getOrDefault(BootMode.UEFI_BIOS),
            sha256Expected = sha256Expected,
            sha256Calculated = sha256Calculated,
            md5Checksum = md5Checksum,
            isVerified = isVerified,
            verificationStatus = verificationStatus,
            verifiedTimestamp = verifiedTimestamp,
            isEncrypted = isEncrypted,
            isPinned = isPinned,
            persistenceMb = persistenceMb,
            autoInstall = runCatching { AutoInstallScript.valueOf(autoInstall) }.getOrDefault(AutoInstallScript.NONE),
            isDefaultBoot = isDefaultBoot,
            dateAdded = dateAdded,
            categoryName = categoryName,
            architecture = architecture
        )
    }

    companion object {
        fun fromDomain(item: IsoItem): IsoEntity {
            return IsoEntity(
                id = item.id,
                name = item.name,
                filename = item.filename,
                filePath = item.filePath.ifBlank { "/ventoy/${item.filename}" },
                sizeBytes = item.sizeBytes,
                distroType = item.distroType.name,
                categoryName = item.categoryName,
                architecture = item.architecture,
                version = item.version,
                bootMode = item.bootMode.name,
                sha256Expected = item.sha256Expected,
                sha256Calculated = item.sha256Calculated,
                md5Checksum = item.md5Checksum,
                isVerified = item.isVerified,
                verificationStatus = if (item.isVerified) "VERIFIED" else item.verificationStatus,
                verifiedTimestamp = item.verifiedTimestamp,
                isEncrypted = item.isEncrypted,
                isPinned = item.isPinned,
                persistenceMb = item.persistenceMb,
                autoInstall = item.autoInstall.name,
                isDefaultBoot = item.isDefaultBoot,
                dateAdded = item.dateAdded
            )
        }
    }
}

@Entity(tableName = "security_logs")
data class SecurityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val eventType: String,
    val description: String,
    val severity: String
)
