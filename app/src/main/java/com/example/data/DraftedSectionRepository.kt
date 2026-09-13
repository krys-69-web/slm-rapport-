package com.example.data

import com.example.service.DraftedSectionResult
import com.example.service.ReportSectionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository abstrayant la persistance locale Room pour les sections rédigées du rapport.
 * Respecte scrupuleusement le pattern Repository préconisé par Android et le guide Room.
 */
class DraftedSectionRepository(
    private val dao: DraftedSectionDao
) {

    /** Flux réactif de toutes les sections enregistrées localement */
    val allDraftedSections: Flow<List<DraftedSectionEntity>> = dao.getAllDraftedSections()

    /** Flux du nombre de sections stockées en base locale */
    val draftedSectionsCount: Flow<Int> = dao.getDraftedSectionsCount()

    /**
     * Observe réactivement une section donnée par son type.
     */
    fun getSectionFlow(sectionType: ReportSectionType): Flow<DraftedSectionResult?> {
        return dao.getSectionByKey(sectionType.name).map { entity ->
            entity?.toDraftedSectionResult()
        }
    }

    /**
     * Récupère de manière ponctuelle une section sauvegardée localement.
     */
    suspend fun getSectionOnce(sectionType: ReportSectionType): DraftedSectionResult? {
        return dao.getSectionByKeyOnce(sectionType.name)?.toDraftedSectionResult()
    }

    /**
     * Enregistre ou met à jour une section rédigée dans la base Room locale.
     */
    suspend fun saveSection(
        result: DraftedSectionResult,
        studentName: String = "",
        companyName: String = "",
        theme: String = ""
    ) {
        val entity = DraftedSectionEntity(
            sectionKey = result.sectionType.name,
            sectionTitle = result.title,
            targetPageCount = result.sectionType.targetPageCount,
            draftedContent = result.draftedContent,
            keyHighlights = result.keyPointsHighlighted.joinToString("|||"),
            isFromGoogleAiSdk = result.isFromGoogleAiSdk,
            modelUsed = result.modelUsed,
            studentName = studentName,
            companyName = companyName,
            theme = theme,
            updatedAtMillis = result.generatedAtMillis
        )
        dao.insertSection(entity)
    }

    /**
     * Enregistre un lot complet de sections rédigées en base locale.
     */
    suspend fun saveAllSections(
        results: Map<ReportSectionType, DraftedSectionResult>,
        studentName: String = "",
        companyName: String = "",
        theme: String = ""
    ) {
        val entities = results.values.map { result ->
            DraftedSectionEntity(
                sectionKey = result.sectionType.name,
                sectionTitle = result.title,
                targetPageCount = result.sectionType.targetPageCount,
                draftedContent = result.draftedContent,
                keyHighlights = result.keyPointsHighlighted.joinToString("|||"),
                isFromGoogleAiSdk = result.isFromGoogleAiSdk,
                modelUsed = result.modelUsed,
                studentName = studentName,
                companyName = companyName,
                theme = theme,
                updatedAtMillis = result.generatedAtMillis
            )
        }
        dao.insertSections(entities)
    }

    /**
     * Supprime une section sauvegardée.
     */
    suspend fun deleteSection(sectionType: ReportSectionType) {
        dao.deleteSectionByKey(sectionType.name)
    }

    /**
     * Efface toutes les rédactions locales.
     */
    suspend fun clearAll() {
        dao.clearAllSections()
    }
}
