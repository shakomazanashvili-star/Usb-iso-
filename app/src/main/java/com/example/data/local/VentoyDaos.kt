package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing locally stored ISO file metadata,
 * file paths, OS categories, and checksum verification records.
 */
@Dao
interface IsoDao {

    @Query("SELECT * FROM iso_items ORDER BY isPinned DESC, dateAdded DESC")
    fun getAllIsos(): Flow<List<IsoEntity>>

    @Query("SELECT * FROM iso_items WHERE id = :id LIMIT 1")
    suspend fun getIsoById(id: String): IsoEntity?

    @Query("SELECT * FROM iso_items WHERE filePath = :filePath LIMIT 1")
    suspend fun getIsoByPath(filePath: String): IsoEntity?

    @Query("SELECT * FROM iso_items WHERE categoryName = :category ORDER BY name ASC")
    fun getIsosByCategory(category: String): Flow<List<IsoEntity>>

    @Query("SELECT * FROM iso_items WHERE isVerified = :isVerified ORDER BY name ASC")
    fun getIsosByVerification(isVerified: Boolean): Flow<List<IsoEntity>>

    @Query("SELECT * FROM iso_items WHERE verificationStatus = :status ORDER BY name ASC")
    fun getIsosByStatus(status: String): Flow<List<IsoEntity>>

    @Query("SELECT * FROM iso_items WHERE name LIKE '%' || :query || '%' OR filename LIKE '%' || :query || '%' OR filePath LIKE '%' || :query || '%'")
    fun searchIsos(query: String): Flow<List<IsoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIso(iso: IsoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIsos(isos: List<IsoEntity>)

    @Update
    suspend fun updateIso(iso: IsoEntity)

    @Query("""
        UPDATE iso_items 
        SET sha256Calculated = :calculatedSha256,
            md5Checksum = :calculatedMd5,
            isVerified = :isVerified,
            verificationStatus = :status,
            verifiedTimestamp = :verifiedTimestamp
        WHERE id = :id
    """)
    suspend fun updateChecksumAndVerification(
        id: String,
        calculatedSha256: String,
        calculatedMd5: String,
        isVerified: Boolean,
        status: String,
        verifiedTimestamp: Long
    )

    @Query("UPDATE iso_items SET categoryName = :categoryName WHERE id = :id")
    suspend fun updateCategory(id: String, categoryName: String)

    @Delete
    suspend fun deleteIso(iso: IsoEntity)

    @Query("DELETE FROM iso_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE iso_items SET isDefaultBoot = 0")
    suspend fun clearDefaultBoot()

    @Query("UPDATE iso_items SET isDefaultBoot = 1 WHERE id = :id")
    suspend fun setDefaultBoot(id: String)

    @Query("DELETE FROM iso_items")
    suspend fun clearAll()
}

@Dao
interface SecurityLogDao {
    @Query("SELECT * FROM security_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<SecurityLogEntity>>

    @Insert
    suspend fun insertLog(log: SecurityLogEntity)

    @Query("DELETE FROM security_logs")
    suspend fun clearLogs()
}
