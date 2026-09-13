package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) pour l'accès réactif et persistant aux sections rédigées du rapport.
 */
@Dao
interface DraftedSectionDao {

    @Query("SELECT * FROM drafted_sections ORDER BY updatedAtMillis DESC")
    fun getAllDraftedSections(): Flow<List<DraftedSectionEntity>>

    @Query("SELECT * FROM drafted_sections WHERE sectionKey = :sectionKey LIMIT 1")
    fun getSectionByKey(sectionKey: String): Flow<DraftedSectionEntity?>

    @Query("SELECT * FROM drafted_sections WHERE sectionKey = :sectionKey LIMIT 1")
    suspend fun getSectionByKeyOnce(sectionKey: String): DraftedSectionEntity?

    @Query("SELECT COUNT(*) FROM drafted_sections")
    fun getDraftedSectionsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: DraftedSectionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<DraftedSectionEntity>)

    @Query("DELETE FROM drafted_sections WHERE sectionKey = :sectionKey")
    suspend fun deleteSectionByKey(sectionKey: String)

    @Query("DELETE FROM drafted_sections")
    suspend fun clearAllSections()
}
