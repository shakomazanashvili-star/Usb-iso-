package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        IsoEntity::class,
        IsoFile::class,
        SecurityLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class VentoyDatabase : RoomDatabase() {
    abstract fun isoDao(): IsoDao
    abstract fun isoFileDao(): IsoFileDao
    abstract fun securityLogDao(): SecurityLogDao

    companion object {
        @Volatile
        private var INSTANCE: VentoyDatabase? = null

        fun getDatabase(context: Context): VentoyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VentoyDatabase::class.java,
                    "ventoy_plus.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
