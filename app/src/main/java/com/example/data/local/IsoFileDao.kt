package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for performing CRUD operations on IsoFile entities.
 */
@Dao
interface IsoFileDao {

    // --- CREATE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(isoFile: IsoFile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(isoFiles: List<IsoFile>): List<Long>

    // --- READ ---
    @Query("SELECT * FROM iso_files ORDER BY createdAt DESC")
    fun getAllIsoFiles(): Flow<List<IsoFile>>

    @Query("SELECT * FROM iso_files WHERE id = :id LIMIT 1")
    suspend fun getIsoFileById(id: Long): IsoFile?

    @Query("SELECT * FROM iso_files WHERE path = :path LIMIT 1")
    suspend fun getIsoFileByPath(path: String): IsoFile?

    @Query("SELECT * FROM iso_files WHERE category = :category ORDER BY filename ASC")
    fun getIsoFilesByCategory(category: String): Flow<List<IsoFile>>

    @Query("SELECT * FROM iso_files WHERE isVerified = :isVerified ORDER BY filename ASC")
    fun getIsoFilesByVerification(isVerified: Boolean): Flow<List<IsoFile>>

    @Query("SELECT * FROM iso_files WHERE filename LIKE '%' || :query || '%' OR path LIKE '%' || :query || '%'")
    fun searchIsoFiles(query: String): Flow<List<IsoFile>>

    // --- UPDATE ---
    @Update
    suspend fun update(isoFile: IsoFile): Int

    @Query("UPDATE iso_files SET checksum = :checksum, isVerified = :isVerified WHERE id = :id")
    suspend fun updateChecksum(id: Long, checksum: String, isVerified: Boolean = true): Int

    @Query("UPDATE iso_files SET category = :category WHERE id = :id")
    suspend fun updateCategory(id: Long, category: String): Int

    @Query("UPDATE iso_files SET description = :description WHERE id = :id")
    suspend fun updateDescription(id: Long, description: String): Int

    @Query("UPDATE iso_files SET category = :category, description = :description WHERE id = :id")
    suspend fun updateCategoryAndDescription(id: Long, category: String, description: String): Int


    // --- DELETE ---
    @Delete
    suspend fun delete(isoFile: IsoFile): Int

    @Query("DELETE FROM iso_files WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM iso_files")
    suspend fun deleteAll(): Int
}
