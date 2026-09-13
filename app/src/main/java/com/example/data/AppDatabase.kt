package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de données locale Room pour l'application SLM Rapport Builder.
 * Permet la conservation sécurisée et l'accès hors-ligne aux rédactions IA et synthèses d'ingénierie.
 */
@Database(
    entities = [DraftedSectionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun draftedSectionDao(): DraftedSectionDao

    companion object {
        private const val DATABASE_NAME = "slm_rapport_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
