package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.service.DraftedSectionResult
import com.example.service.ReportSectionType

/**
 * Entité Room représentant une section de rapport de stage rédigée et persistée localement
 * sur l'appareil pour un accès complet hors-ligne.
 */
@Entity(
    tableName = "drafted_sections",
    indices = [Index(value = ["sectionKey"], unique = true)]
)
data class DraftedSectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Clé unique de la section (ex: REMERCIEMENTS, INTRODUCTION_GENERALE, etc.) */
    val sectionKey: String,

    /** Titre officiel de la section */
    val sectionTitle: String,

    /** Pagination cible selon la norme 28 pages (ex: "Pages 5 - 6") */
    val targetPageCount: String,

    /** Contenu textuel complet de la section rédigée */
    val draftedContent: String,

    /** Points saillants et mots-clés séparés par "|||" */
    val keyHighlights: String = "",

    /** Vrai si généré par l'API Google AI SDK, faux si généré par le moteur expert hors-ligne */
    val isFromGoogleAiSdk: Boolean = false,

    /** Nom du modèle utilisé (ex: gemini-3.5-flash ou moteur-expert-intégré) */
    val modelUsed: String = "gemini-3.5-flash",

    /** Contexte étudiant associé */
    val studentName: String = "",

    /** Contexte entreprise d'accueil associée */
    val companyName: String = "",

    /** Thème officiel associé */
    val theme: String = "",

    /** Timestamp de la dernière mise à jour ou génération */
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    /**
     * Convertit l'entité Room en objet métier DraftedSectionResult utilisé par l'application.
     */
    fun toDraftedSectionResult(): DraftedSectionResult {
        val type = try {
            ReportSectionType.valueOf(sectionKey)
        } catch (_: Exception) {
            ReportSectionType.INTRODUCTION_GENERALE
        }

        val highlightsList = if (keyHighlights.isNotBlank()) {
            keyHighlights.split("|||").filter { it.isNotBlank() }
        } else {
            emptyList()
        }

        return DraftedSectionResult(
            sectionType = type,
            title = sectionTitle,
            draftedContent = draftedContent,
            keyPointsHighlighted = highlightsList,
            isFromGoogleAiSdk = isFromGoogleAiSdk,
            modelUsed = modelUsed,
            generatedAtMillis = updatedAtMillis
        )
    }
}
