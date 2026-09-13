package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DraftedSectionEntity
import com.example.data.DraftedSectionRepository
import com.example.generator.FiliereContentResolver
import com.example.generator.ReportGenerator28Pages
import com.example.model.CompanyConfig
import com.example.model.ComplianceAuditResult
import com.example.model.ImportedImage
import com.example.model.JuryQuestion
import com.example.model.LocationItem
import com.example.model.ReportPage
import com.example.model.StudentConfig
import com.example.service.GeminiEnrichmentResult
import com.example.service.GeminiService
import com.example.service.DraftedSectionResult
import com.example.service.GoogleAiReportDraftingService
import com.example.service.InternshipDraftInput
import com.example.service.ReportSectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    BUILDER,
    PREVIEW,
    AUDIT,
    JURY_SIMULATOR,
    SETTINGS
}

data class ReportUiState(
    val currentScreen: AppScreen = AppScreen.BUILDER,
    val company: CompanyConfig = CompanyConfig(),
    val student: StudentConfig = StudentConfig(),
    val locations: List<LocationItem> = listOf(
        LocationItem(
            locationName = "Chantier Port Autonome d'Abidjan",
            daysDuration = "4",
            cause = "Prise de cotes, assemblage sur site et soudure des platines de fixation"
        ),
        LocationItem(
            locationName = "Site Industriel Yopougon Zone",
            daysDuration = "2",
            cause = "Implantation du portique pivot et pose de la lisse avec mécanismes à roulements"
        )
    ),
    val rawSummary: String = "on a fait coupe meulage soudure peinture barriere levante",
    val finalSummary: String = "",
    val logoImage: ImportedImage? = null,
    val illustrations: List<ImportedImage> = emptyList(),
    val importStatusText: String = "",
    val isGenerated: Boolean = false,
    val generatedPages: List<ReportPage> = emptyList(),
    val currentPreviewPage: Int = 1,
    val isAiProcessing: Boolean = false,
    val geminiData: GeminiEnrichmentResult? = null,
    val isGeminiLoading: Boolean = false,
    val geminiStatusMessage: String = "",
    val isSearchingCompany: Boolean = false,
    val companySearchMessage: String = "",
    val companySearchSuccess: Boolean = false,
    val auditResult: ComplianceAuditResult? = null,
    val isAuditing: Boolean = false,
    val juryQuestions: List<JuryQuestion> = emptyList(),
    val currentJuryIndex: Int = 0,
    val selectedJuryCategory: String = "Toutes",
    val showJuryAnswer: Boolean = false,
    val jurySelfRatings: Map<Int, Int> = emptyMap(),
    val defenseTimerSeconds: Int = 0,
    val isDefenseTimerActive: Boolean = false,
    val academicLevel: String = "BTS Industriel",
    val typographicNorm: String = "Normes CAMES / Ministère (28 Pages)",
    val fontChoice: String = "Times New Roman (Standard)",
    val lineSpacingChoice: String = "1.5 (Standard)",
    val marginsChoice: String = "2.5 cm partout",
    val highDefPdf: Boolean = true,
    val draftedSections: Map<ReportSectionType, DraftedSectionResult> = emptyMap(),
    val selectedDraftSection: ReportSectionType = ReportSectionType.INTRODUCTION_GENERALE,
    val isDraftingSection: Boolean = false,
    val draftingProgressText: String = "",
    val offlineSavedCount: Int = 0
)

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DraftedSectionRepository = DraftedSectionRepository(
        AppDatabase.getInstance(application).draftedSectionDao()
    )

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        // Initial auto-generation of professional summary based on defaults
        loadAutoSummary()
        observeOfflineDraftedSections()
    }

    private fun observeOfflineDraftedSections() {
        viewModelScope.launch {
            repository.allDraftedSections.collect { entities ->
                val sectionsMap = entities.associate { entity ->
                    val result = entity.toDraftedSectionResult()
                    result.sectionType to result
                }
                _uiState.update { current ->
                    val merged = sectionsMap + current.draftedSections
                    current.copy(
                        draftedSections = merged,
                        offlineSavedCount = entities.size
                    )
                }
            }
        }
    }

    fun updateCompanyName(newName: String) {
        _uiState.update { state ->
            val isIcc = newName.trim().contains("ICC", ignoreCase = true)
            val updatedCompany = if (isIcc) {
                state.company.copy(
                    inputName = newName,
                    companyFullTitle = if (state.company.companyFullTitle.isBlank() || !state.company.companyFullTitle.contains("INTER NATIONALE", ignoreCase = true)) {
                        "INTER NATIONALE ET CONSTRUCTION (ICC CORPORATE SARL)"
                    } else state.company.companyFullTitle,
                    companyAddress = if (state.company.companyAddress.isBlank()) {
                        "Yopougon Figayo, Derrière la Banque - 21 BP 841 Abidjan 21"
                    } else state.company.companyAddress,
                    activities = if (state.company.activities.isBlank()) {
                        "Chaudronnerie, Soudure industrielle, Tuyauterie, Métallurgie"
                    } else state.company.activities,
                    contactGerant = if (state.company.contactGerant.isBlank()) {
                        "M. KOUASSI JEAN YVES (Gérant - 07 49 60 91 67)"
                    } else state.company.contactGerant,
                    contactAtelier = if (state.company.contactAtelier.isBlank()) {
                        "M. BROU PRI (Chef d'Atelier - 07 88 47 88 28)"
                    } else state.company.contactAtelier,
                    rccmBank = if (state.company.rccmBank.isBlank()) {
                        "RCCM CI-ABJ-2015-B-29389 / CC 1558991 U / BNI 008539660000"
                    } else state.company.rccmBank
                )
            } else {
                // Entreprise personnalisée : si les champs avaient les valeurs par défaut d'ICC, on les adapte
                val newTitle = if (state.company.companyFullTitle.contains("INTER NATIONALE", ignoreCase = true) || state.company.companyFullTitle.isBlank()) {
                    newName.uppercase()
                } else state.company.companyFullTitle

                val newAddress = if (state.company.companyAddress.contains("Figayo", ignoreCase = true)) {
                    "Abidjan, Côte d'Ivoire"
                } else state.company.companyAddress

                val newActivities = if (state.company.activities.contains("Chaudronnerie, Soudure", ignoreCase = true)) {
                    "Production, maintenance et prestations industrielles"
                } else state.company.activities

                val newGerant = if (state.company.contactGerant.contains("KOUASSI JEAN YVES", ignoreCase = true)) {
                    "Direction Générale"
                } else state.company.contactGerant

                val newAtelier = if (state.company.contactAtelier.contains("BROU PRI", ignoreCase = true)) {
                    "Direction Technique / Exploitation"
                } else state.company.contactAtelier

                val newRccm = if (state.company.rccmBank.contains("CI-ABJ-2015-B-29389", ignoreCase = true)) {
                    "Enregistré au RCCM"
                } else state.company.rccmBank

                state.company.copy(
                    inputName = newName,
                    companyFullTitle = newTitle,
                    companyAddress = newAddress,
                    activities = newActivities,
                    contactGerant = newGerant,
                    contactAtelier = newAtelier,
                    rccmBank = newRccm
                )
            }
            state.copy(company = updatedCompany)
        }
    }

    fun updateCompanyFullTitle(title: String) {
        _uiState.update { it.copy(company = it.company.copy(companyFullTitle = title)) }
    }

    fun updateCompanyAddress(address: String) {
        _uiState.update { it.copy(company = it.company.copy(companyAddress = address)) }
    }

    fun updateCompanyActivities(activities: String) {
        _uiState.update { it.copy(company = it.company.copy(activities = activities)) }
    }

    fun updateCompanyContactGerant(contact: String) {
        _uiState.update { it.copy(company = it.company.copy(contactGerant = contact)) }
    }

    fun updateCompanyContactAtelier(contact: String) {
        _uiState.update { it.copy(company = it.company.copy(contactAtelier = contact)) }
    }

    fun updateCompanyEmail(email: String) {
        _uiState.update { it.copy(company = it.company.copy(email = email)) }
    }

    fun updateCompanyRccmBank(rccm: String) {
        _uiState.update { it.copy(company = it.company.copy(rccmBank = rccm)) }
    }

    fun toggleShowAddress(show: Boolean) {
        _uiState.update { it.copy(company = it.company.copy(showAddress = show)) }
    }

    fun toggleShowWorkshopContact(show: Boolean) {
        _uiState.update { it.copy(company = it.company.copy(showWorkshopContact = show)) }
    }

    fun toggleShowActivities(show: Boolean) {
        _uiState.update { it.copy(company = it.company.copy(showActivities = show)) }
    }

    fun toggleShowRccmBank(show: Boolean) {
        _uiState.update { it.copy(company = it.company.copy(showRccmBank = show)) }
    }

    fun updateStudentName(name: String) {
        _uiState.update { it.copy(student = it.student.copy(studentName = name)) }
    }

    fun updateSchoolName(school: String) {
        _uiState.update { it.copy(student = it.student.copy(schoolName = school)) }
    }

    fun updateTheme(theme: String) {
        _uiState.update { it.copy(student = it.student.copy(theme = theme)) }
    }

    fun updateAcademicYear(year: String) {
        _uiState.update { it.copy(student = it.student.copy(academicYear = year)) }
    }

    fun updateFiliereOption(filiere: String) {
        _uiState.update { it.copy(student = it.student.copy(filiereOption = filiere)) }
    }

    fun updateAcademicTutor(tutor: String) {
        _uiState.update { it.copy(student = it.student.copy(academicTutor = tutor)) }
    }

    fun updateInternshipTutor(tutor: String) {
        _uiState.update { it.copy(student = it.student.copy(internshipTutor = tutor)) }
    }

    fun addLocation() {
        _uiState.update {
            it.copy(locations = it.locations + LocationItem())
        }
    }

    fun updateLocation(index: Int, updated: LocationItem) {
        _uiState.update { state ->
            val list = state.locations.toMutableList()
            if (index in list.indices) {
                list[index] = updated
            }
            state.copy(locations = list)
        }
    }

    fun removeLocation(index: Int) {
        _uiState.update { state ->
            val list = state.locations.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            state.copy(locations = list)
        }
    }

    fun clearLocations() {
        _uiState.update { it.copy(locations = emptyList()) }
        // Re-synthesize summary in Mode B (without locations)
        reformulateInProModeB()
    }

    fun updateRawSummary(raw: String) {
        _uiState.update { it.copy(rawSummary = raw) }
    }

    fun updateFinalSummary(summary: String) {
        _uiState.update { it.copy(finalSummary = summary) }
    }

    fun loadAutoSummary() {
        val state = _uiState.value
        val summary = ReportGenerator28Pages.generateIntelligentSummary(
            company = state.company,
            student = state.student,
            locations = state.locations,
            rawNotes = state.rawSummary
        )
        _uiState.update { it.copy(finalSummary = summary) }
    }

    fun reformulateInProModeB() {
        val state = _uiState.value
        val summary = ReportGenerator28Pages.generateIntelligentSummary(
            company = state.company,
            student = state.student,
            locations = emptyList(),
            rawNotes = state.rawSummary
        )
        _uiState.update { it.copy(finalSummary = summary) }
    }

    fun setLogoImage(uri: Uri, name: String) {
        val img = ImportedImage(uri = uri, name = name)
        _uiState.update {
            it.copy(
                logoImage = img,
                importStatusText = "Logo « $name » importé avec succès"
            )
        }
    }

    fun removeLogo() {
        _uiState.update { it.copy(logoImage = null) }
    }

    fun addIllustrations(items: List<Pair<Uri, String>>) {
        val newImages = items.map { ImportedImage(uri = it.first, name = it.second) }
        _uiState.update {
            val total = it.illustrations + newImages
            it.copy(
                illustrations = total,
                importStatusText = "${total.size} image(s) importée(s)"
            )
        }
    }

    fun removeIllustration(index: Int) {
        _uiState.update { state ->
            val list = state.illustrations.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            state.copy(
                illustrations = list,
                importStatusText = "${list.size} image(s) importée(s)"
            )
        }
    }

    fun enrichWithGemini() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGeminiLoading = true,
                    geminiStatusMessage = "Recherche en cours par l'IA Gemini (normes ISO, aciers, roulements, HSE)..."
                )
            }
            val enrichment = GeminiService.enrichirTechnique(
                theme = state.student.theme,
                company = state.company.inputName
            )
            val formulatedSummary = GeminiService.reformulerEnProIA(
                theme = state.student.theme,
                company = state.company.inputName,
                rawNotes = state.rawSummary,
                locations = state.locations
            )
            _uiState.update {
                val newPages = if (it.isGenerated) {
                    ReportGenerator28Pages.build28Pages(
                        company = it.company,
                        student = it.student,
                        locations = it.locations,
                        finalSummary = formulatedSummary,
                        illustrations = it.illustrations,
                        geminiData = enrichment
                    )
                } else it.generatedPages

                it.copy(
                    geminiData = enrichment,
                    finalSummary = formulatedSummary,
                    isGeminiLoading = false,
                    geminiStatusMessage = "Rapport enrichi avec succès par Gemini IA !",
                    generatedPages = newPages
                )
            }
        }
    }

    fun searchAndAutoFillCompanyInfo(companyNameOverride: String? = null) {
        val targetName = companyNameOverride?.trim() ?: _uiState.value.company.inputName.trim()
        if (targetName.isBlank()) {
            _uiState.update {
                it.copy(
                    companySearchMessage = "Veuillez d'abord saisir le nom d'une entreprise.",
                    companySearchSuccess = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearchingCompany = true,
                    companySearchMessage = "Recherche en direct de « $targetName » via Gemini & Google Search...",
                    companySearchSuccess = false
                )
            }

            val searchResult = GeminiService.searchCompanyInfo(targetName)

            _uiState.update { state ->
                val updatedCompany = state.company.copy(
                    inputName = if (searchResult.companyName.isNotBlank()) searchResult.companyName else targetName,
                    companyFullTitle = searchResult.fullLegalTitle.ifBlank { state.company.companyFullTitle },
                    companyAddress = searchResult.address.ifBlank { state.company.companyAddress },
                    activities = searchResult.activities.ifBlank { state.company.activities },
                    contactGerant = searchResult.directionContact.ifBlank { state.company.contactGerant },
                    contactAtelier = searchResult.technicalContact.ifBlank { state.company.contactAtelier },
                    rccmBank = searchResult.rccm.ifBlank { state.company.rccmBank },
                    email = searchResult.email.ifBlank { state.company.email }
                )

                // Mise à jour de l'encadreur de stage avec le contact technique si vide
                val updatedStudent = if (state.student.internshipTutor.isBlank() && searchResult.technicalContact.isNotBlank()) {
                    state.student.copy(internshipTutor = searchResult.technicalContact)
                } else state.student

                val newPages = if (state.isGenerated) {
                    ReportGenerator28Pages.build28Pages(
                        company = updatedCompany,
                        student = updatedStudent,
                        locations = state.locations,
                        finalSummary = state.finalSummary,
                        illustrations = state.illustrations,
                        geminiData = state.geminiData
                    )
                } else state.generatedPages

                state.copy(
                    company = updatedCompany,
                    student = updatedStudent,
                    isSearchingCompany = false,
                    companySearchMessage = searchResult.message,
                    companySearchSuccess = searchResult.success,
                    generatedPages = newPages
                )
            }
        }
    }

    fun clearAllForCustomInput() {
        _uiState.update {
            it.copy(
                company = CompanyConfig(
                    inputName = "",
                    companyFullTitle = "",
                    companyAddress = "",
                    rccmBank = "",
                    contactGerant = "",
                    contactAtelier = "",
                    email = "",
                    activities = ""
                ),
                student = StudentConfig(
                    studentName = "",
                    schoolName = "",
                    theme = "",
                    academicYear = "2025-2026",
                    internshipTutor = ""
                ),
                locations = emptyList(),
                rawSummary = "",
                finalSummary = "",
                geminiData = null,
                geminiStatusMessage = "Champs réinitialisés. Vous pouvez saisir vos données et cliquer sur « Enrichir avec Gemini »."
            )
        }
    }

    fun generate28PagesReport() {
        val state = _uiState.value
        val pages = ReportGenerator28Pages.build28Pages(
            company = state.company,
            student = state.student,
            locations = state.locations,
            finalSummary = state.finalSummary,
            illustrations = state.illustrations,
            geminiData = state.geminiData
        )
        _uiState.update {
            it.copy(
                generatedPages = pages,
                isGenerated = true,
                currentPreviewPage = 1,
                currentScreen = AppScreen.PREVIEW
            )
        }
    }

    fun setPreviewPage(page: Int) {
        if (page in 1..28) {
            _uiState.update { it.copy(currentPreviewPage = page) }
        }
    }

    fun returnToEditor() {
        _uiState.update { it.copy(isGenerated = false, currentScreen = AppScreen.BUILDER) }
    }

    fun setScreen(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
        if (screen == AppScreen.PREVIEW && (!_uiState.value.isGenerated || _uiState.value.generatedPages.isEmpty())) {
            generate28PagesReport()
        }
        if (screen == AppScreen.AUDIT && _uiState.value.auditResult == null) {
            runComplianceAudit()
        }
        if (screen == AppScreen.JURY_SIMULATOR && _uiState.value.juryQuestions.isEmpty()) {
            loadJuryQuestions()
        }
    }

    fun runComplianceAudit() {
        val state = _uiState.value
        val pages = if (state.isGenerated && state.generatedPages.isNotEmpty()) {
            state.generatedPages
        } else {
            ReportGenerator28Pages.build28Pages(
                company = state.company,
                student = state.student,
                locations = state.locations,
                finalSummary = state.finalSummary,
                illustrations = state.illustrations,
                geminiData = state.geminiData
            )
        }
        val result = FiliereContentResolver.performComplianceAudit(
            student = state.student,
            company = state.company,
            pages = pages,
            rawNotes = state.rawSummary
        )
        _uiState.update {
            it.copy(
                auditResult = result,
                generatedPages = pages,
                isGenerated = true
            )
        }
    }

    fun autoFixCompliance() {
        val state = _uiState.value
        val updatedCompany = state.company.copy(
            rccmBank = if (state.company.rccmBank.isBlank()) "RCCM CI-ABJ-2015-B-29389 / CC 1558991 U" else state.company.rccmBank,
            inputName = if (state.company.inputName.isBlank()) "ICC CORPORATE" else state.company.inputName,
            companyFullTitle = if (state.company.companyFullTitle.isBlank()) "INTER NATIONALE ET CONSTRUCTION (ICC CORPORATE SARL)" else state.company.companyFullTitle
        )
        val updatedStudent = state.student.copy(
            internshipTutor = if (state.student.internshipTutor.isBlank()) "M. BROU PRI (Chef d'Atelier) & M. KOUASSI JEAN YVES (Gérant)" else state.student.internshipTutor,
            academicTutor = if (state.student.academicTutor.isBlank()) "Dr. KOUASSI N'GUESSAN (Enseignant-Chercheur)" else state.student.academicTutor,
            theme = if (state.student.theme.length < 15) "ÉTUDE, CONCEPTION ET FABRICATION D'UNE BARRIÈRE LEVANTE DE SÉCURITÉ INDUSTRIELLE" else state.student.theme
        )
        val newPages = ReportGenerator28Pages.build28Pages(
            company = updatedCompany,
            student = updatedStudent,
            locations = state.locations,
            finalSummary = state.finalSummary,
            illustrations = state.illustrations,
            geminiData = state.geminiData
        )
        val newAudit = FiliereContentResolver.performComplianceAudit(
            student = updatedStudent,
            company = updatedCompany,
            pages = newPages,
            rawNotes = state.rawSummary
        )
        _uiState.update {
            it.copy(
                company = updatedCompany,
                student = updatedStudent,
                generatedPages = newPages,
                isGenerated = true,
                auditResult = newAudit
            )
        }
    }

    fun loadJuryQuestions() {
        val state = _uiState.value
        val type = FiliereContentResolver.detectFiliereType(state.student.filiereOption, state.student.theme)
        val questions = FiliereContentResolver.generateJuryQuestions(
            type = type,
            theme = state.student.theme,
            companyName = state.company.inputName
        )
        _uiState.update {
            it.copy(
                juryQuestions = questions,
                currentJuryIndex = 0,
                showJuryAnswer = false
            )
        }
    }

    fun toggleShowJuryAnswer() {
        _uiState.update { it.copy(showJuryAnswer = !it.showJuryAnswer) }
    }

    fun setCurrentJuryIndex(index: Int) {
        val state = _uiState.value
        if (index in state.juryQuestions.indices) {
            _uiState.update { it.copy(currentJuryIndex = index, showJuryAnswer = false) }
        }
    }

    fun rateJuryQuestion(index: Int, rating: Int) {
        _uiState.update {
            val map = it.jurySelfRatings.toMutableMap()
            map[index] = rating
            it.copy(jurySelfRatings = map)
        }
    }

    fun setJuryCategory(category: String) {
        _uiState.update { it.copy(selectedJuryCategory = category) }
    }

    fun tickDefenseTimer() {
        _uiState.update {
            if (it.isDefenseTimerActive) {
                it.copy(defenseTimerSeconds = it.defenseTimerSeconds + 1)
            } else it
        }
    }

    fun toggleDefenseTimer() {
        _uiState.update { it.copy(isDefenseTimerActive = !it.isDefenseTimerActive) }
    }

    fun resetDefenseTimer() {
        _uiState.update { it.copy(defenseTimerSeconds = 0, isDefenseTimerActive = false) }
    }

    fun updateSettings(
        academicLevel: String? = null,
        norm: String? = null,
        font: String? = null,
        lineSpacing: String? = null,
        margins: String? = null,
        highDefPdf: Boolean? = null
    ) {
        _uiState.update {
            it.copy(
                academicLevel = academicLevel ?: it.academicLevel,
                typographicNorm = norm ?: it.typographicNorm,
                fontChoice = font ?: it.fontChoice,
                lineSpacingChoice = lineSpacing ?: it.lineSpacingChoice,
                marginsChoice = margins ?: it.marginsChoice,
                highDefPdf = highDefPdf ?: it.highDefPdf
            )
        }
    }

    fun getWordDocHtml(context: Context): String {
        val state = _uiState.value
        return ReportGenerator28Pages.generateWordDocHtml(
            company = state.company,
            student = state.student,
            locations = state.locations,
            finalSummary = state.finalSummary,
            illustrations = state.illustrations,
            context = context,
            geminiData = state.geminiData
        )
    }

    // =========================================================================
    // GOOGLE AI SDK - RÉDACTEUR DE SECTIONS DE RAPPORT DE STAGE
    // =========================================================================

    fun selectDraftSection(sectionType: ReportSectionType) {
        _uiState.update { it.copy(selectedDraftSection = sectionType) }
    }

    fun draftReportSection(sectionType: ReportSectionType) {
        val state = _uiState.value
        val input = InternshipDraftInput(
            studentConfig = state.student,
            companyConfig = state.company,
            locations = state.locations,
            rawNotes = state.rawSummary,
            filiereCategory = state.student.filiereOption
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDraftingSection = true,
                    draftingProgressText = "Rédaction de « ${sectionType.title} » via Google AI SDK..."
                )
            }

            val result = GoogleAiReportDraftingService.getInstance().draftSection(sectionType, input)

            // Sauvegarde automatique dans Room pour l'accès hors-ligne
            repository.saveSection(
                result = result,
                studentName = state.student.studentName,
                companyName = state.company.inputName,
                theme = state.student.theme
            )

            _uiState.update { current ->
                val updatedMap = current.draftedSections.toMutableMap().apply {
                    put(sectionType, result)
                }
                current.copy(
                    draftedSections = updatedMap,
                    selectedDraftSection = sectionType,
                    isDraftingSection = false,
                    draftingProgressText = "Section « ${sectionType.title} » rédigée et sauvegardée hors-ligne (Room) !"
                )
            }
        }
    }

    fun draftAllReportSections() {
        val state = _uiState.value
        val input = InternshipDraftInput(
            studentConfig = state.student,
            companyConfig = state.company,
            locations = state.locations,
            rawNotes = state.rawSummary,
            filiereCategory = state.student.filiereOption
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDraftingSection = true,
                    draftingProgressText = "Démarrage de la rédaction intégrale du mémoire..."
                )
            }

            val fullReport = GoogleAiReportDraftingService.getInstance().draftAllSections(input) { section, current, total ->
                _uiState.update {
                    it.copy(draftingProgressText = "Rédaction [$current/$total] : ${section.title}...")
                }
            }

            // Persistance de toutes les sections dans la base locale Room
            repository.saveAllSections(
                results = fullReport.sections,
                studentName = state.student.studentName,
                companyName = state.company.inputName,
                theme = state.student.theme
            )

            _uiState.update {
                it.copy(
                    draftedSections = fullReport.sections,
                    isDraftingSection = false,
                    draftingProgressText = "L'ensemble des 9 sections du mémoire a été rédigé et persisté hors-ligne (Room) !"
                )
            }
        }
    }

    fun refineDraftSection(sectionType: ReportSectionType, userInstruction: String) {
        val state = _uiState.value
        val currentSection = state.draftedSections[sectionType] ?: return
        val input = InternshipDraftInput(
            studentConfig = state.student,
            companyConfig = state.company,
            locations = state.locations,
            rawNotes = state.rawSummary,
            filiereCategory = state.student.filiereOption
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDraftingSection = true,
                    draftingProgressText = "Perfectionnement de « ${sectionType.title} » ($userInstruction)..."
                )
            }

            val refined = GoogleAiReportDraftingService.getInstance().refineSection(
                sectionType = sectionType,
                currentContent = currentSection.draftedContent,
                userInstruction = userInstruction,
                input = input
            )

            // Sauvegarde de la révision dans Room
            repository.saveSection(
                result = refined,
                studentName = state.student.studentName,
                companyName = state.company.inputName,
                theme = state.student.theme
            )

            _uiState.update { current ->
                val updatedMap = current.draftedSections.toMutableMap().apply {
                    put(sectionType, refined)
                }
                current.copy(
                    draftedSections = updatedMap,
                    isDraftingSection = false,
                    draftingProgressText = "Section « ${sectionType.title} » mise à jour et persistée hors-ligne !"
                )
            }
        }
    }

    fun deleteOfflineDraft(sectionType: ReportSectionType) {
        viewModelScope.launch {
            repository.deleteSection(sectionType)
            _uiState.update { current ->
                val updated = current.draftedSections.toMutableMap().apply {
                    remove(sectionType)
                }
                current.copy(
                    draftedSections = updated,
                    draftingProgressText = "Section « ${sectionType.title} » supprimée de la base locale."
                )
            }
        }
    }

    fun clearAllOfflineDrafts() {
        viewModelScope.launch {
            repository.clearAll()
            _uiState.update { current ->
                current.copy(
                    draftedSections = emptyMap(),
                    draftingProgressText = "Base locale Room réinitialisée."
                )
            }
        }
    }

    fun applyDraftedSectionToFinalSummary(sectionType: ReportSectionType) {
        val section = _uiState.value.draftedSections[sectionType] ?: return
        _uiState.update { state ->
            val updatedSummary = section.draftedContent
            val newPages = if (state.isGenerated) {
                ReportGenerator28Pages.build28Pages(
                    company = state.company,
                    student = state.student,
                    locations = state.locations,
                    finalSummary = updatedSummary,
                    illustrations = state.illustrations,
                    geminiData = state.geminiData
                )
            } else state.generatedPages

            state.copy(
                finalSummary = updatedSummary,
                generatedPages = newPages,
                draftingProgressText = "Section « ${sectionType.title} » injectée comme résumé principal du rapport !"
            )
        }
    }
}
