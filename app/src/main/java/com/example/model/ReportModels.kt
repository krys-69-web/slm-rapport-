package com.example.model

import android.net.Uri
import java.util.UUID

data class CompanyConfig(
    val inputName: String = "ICC CORPORATE",
    val companyFullTitle: String = "INTER NATIONALE ET CONSTRUCTION (ICC CORPORATE SARL)",
    val companyAddress: String = "Yopougon Figayo, Derrière la Banque - 21 BP 841 Abidjan 21",
    val rccmBank: String = "RCCM CI-ABJ-2015-B-29389 / CC 1558991 U / BNI 008539660000",
    val contactGerant: String = "M. KOUASSI JEAN YVES (Gérant - 07 49 60 91 67)",
    val contactAtelier: String = "M. BROU PRI (Chef d'Atelier - 07 88 47 88 28)",
    val email: String = "info@icc-ci.com",
    val activities: String = "Chaudronnerie, Soudure industrielle, Tuyauterie, Métallurgie",
    val showAddress: Boolean = true,
    val showWorkshopContact: Boolean = true,
    val showActivities: Boolean = true,
    val showRccmBank: Boolean = true
) {
    val isIccDetected: Boolean
        get() = inputName.trim().contains("ICC", ignoreCase = true)
}

data class LocationItem(
    val id: String = UUID.randomUUID().toString(),
    val locationName: String = "",
    val daysDuration: String = "",
    val cause: String = ""
) {
    val formattedDisplay: String
        get() = "On est allé à ${locationName.ifBlank { "[Lieu non spécifié]" }} sur ${daysDuration.ifBlank { "quelques" }} jours, c'est pour ${cause.ifBlank { "interventions techniques et maintenance" }}."
}

data class StudentConfig(
    val studentName: String = "KOUAME KOUASSI JEAN-LUC",
    val schoolName: String = "INP-HB / ESBTP Yamoussoukro",
    val filiereOption: String = "Génie Mécanique et Productique (Chaudronnerie & Métallurgie)",
    val theme: String = "ÉTUDE, CONCEPTION ET FABRICATION D'UNE BARRIÈRE LEVANTE DE SÉCURITÉ INDUSTRIELLE À MÉCANISMES À ROULEMENTS",
    val academicYear: String = "2025-2026",
    val internshipTutor: String = "M. BROU PRI (Chef d'Atelier) & M. KOUASSI JEAN YVES (Gérant)",
    val academicTutor: String = "Dr. KOUASSI N'GUESSAN (Enseignant-Chercheur)"
)

data class ImportedImage(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri,
    val name: String
)

sealed class DocBlock {
    data class Title(val text: String, val level: Int = 1) : DocBlock()
    data class Paragraph(val text: String, val indent: Boolean = true) : DocBlock()
    data class Callout(val title: String, val text: String, val accentColorHex: String = "#007AFF") : DocBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : DocBlock()
    data class KeyValueList(val items: List<Pair<String, String>>) : DocBlock()
    data class Figure(val imageUri: Uri? = null, val caption: String, val fallbackIcon: String = "gear") : DocBlock()
    data class SignOff(val leftTitle: String, val leftName: String, val rightTitle: String, val rightName: String) : DocBlock()
}

data class ReportPage(
    val pageNumber: Int,
    val totalPages: Int = 28,
    val chapterTitle: String,
    val pageTitle: String,
    val blocks: List<DocBlock>
)

data class JuryQuestion(
    val id: String = UUID.randomUUID().toString(),
    val question: String,
    val category: String, // "Méthodologie", "Calculs & RDM", "Choix Technologiques", "Difficultés", "Sécurité & HSE"
    val suggestedAnswer: String,
    val juryExpectation: String,
    val pitfallsToAvoid: String
)

data class ComplianceAuditIssue(
    val title: String,
    val description: String,
    val isCritical: Boolean = false,
    val recommendation: String
)

data class ComplianceAuditResult(
    val score: Int = 95, // sur 100
    val mentionsPresent: List<String> = emptyList(),
    val missingOrWeakPoints: List<ComplianceAuditIssue> = emptyList(),
    val academicStatus: String = "EXCELLENT - CONFORME AUX NORMES ACADÉMIQUES",
    val adviceForDefense: String = "Structure en 28 pages très équilibrée. Soigner l'introduction de 3 minutes devant le jury."
)

data class FiliereTechnicalContent(
    val filiereCategory: String = "Génie Mécanique & Métallurgie",
    val cdcfTitle: String = "Spécifications Techniques et Cahier des Charges",
    val cdcfIntro: String = "L'ouvrage doit satisfaire à un ensemble d'exigences opérationnelles rigoureuses définies par l'analyse fonctionnelle :",
    val cdcfTableRows: List<List<String>> = emptyList(),
    val materialsTitle: String = "Choix et Caractérisation des Matériaux & Éléments d'Œuvre",
    val materialsIntro: String = "Les matériaux et composants ont été sélectionnés pour concilier performance, fiabilité normative et disponibilité locale :",
    val materialsTableRows: List<List<String>> = emptyList(),
    val calculationsTitle: String = "Calculs Dimensionnels, RDM et Modélisation Technique",
    val calculationsBlocks: List<String> = emptyList(),
    val componentsTitle: String = "Sélection des Composants et Architecture Technique",
    val componentsIntro: String = "La sélection des organes fonctionnels garantit la pérennité et le rendement du système :",
    val componentsTableRows: List<List<String>> = emptyList(),
    val realizationStepsTitle: String = "Processus de Réalisation et Montage Pas à Pas",
    val realizationIntro: String = "La mise en œuvre pratique s'est articulée selon quatre étapes technologiques séquentielles :",
    val realizationSteps: List<Pair<String, String>> = emptyList(),
    val finishingTitle: String = "Ajustage, Assemblage et Traitement de Finition",
    val finishingIntro: String = "Une fois les sous-ensembles réalisés, les opérations de finition et de protection ont été menées :",
    val finishingSteps: List<Pair<String, String>> = emptyList(),
    val qualityTitle: String = "Contrôle Qualité, Métrologie et Normes Industrielles",
    val qualityIntro: String = "Avant validation finale, l'ouvrage a fait l'objet d'un protocole complet d'essais et de conformité :",
    val qualityItems: List<Pair<String, String>> = emptyList(),
    val skillsAcquired: List<Pair<String, String>> = emptyList(),
    val challengesAndSolutions: List<Pair<String, String>> = emptyList(),
    val maintenanceRows: List<List<String>> = emptyList(),
    val strategicRecommendations: List<Pair<String, String>> = emptyList()
)
