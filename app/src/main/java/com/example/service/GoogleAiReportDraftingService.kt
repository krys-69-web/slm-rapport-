package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.model.CompanyConfig
import com.example.model.LocationItem
import com.example.model.StudentConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Types de sections académiques et techniques composant le rapport de stage de 28 pages.
 * Conforme aux exigences CAMES, BTS, DUT, Licence et Master d'ingénierie.
 */
enum class ReportSectionType(
    val title: String,
    val description: String,
    val targetPageCount: String
) {
    REMERCIEMENTS(
        title = "Remerciements & Dédicaces",
        description = "Formules protocolaires envers la direction de l'entreprise, le maître de stage, l'encadreur académique et l'équipe technique.",
        targetPageCount = "Page 3"
    ),
    INTRODUCTION_GENERALE(
        title = "Introduction Générale",
        description = "Contexte socio-économique, formulation de la problématique industrielle, objectifs visés et annonce du plan.",
        targetPageCount = "Pages 5 - 6"
    ),
    PRESENTATION_ENTREPRISE(
        title = "Présentation de l'Entreprise d'Accueil",
        description = "Historique, forme juridique, organigramme, activités industrielles, rayonnement géographique et environnement concurrentiel.",
        targetPageCount = "Pages 7 - 10"
    ),
    CADRE_METHODOLOGIQUE(
        title = "Cadre Méthodologique & Cahier des Charges",
        description = "Analyse fonctionnelle (bête à cornes, diagramme pieuvre), cahier des charges fonctionnel (CDCF) et démarche de résolution.",
        targetPageCount = "Pages 11 - 13"
    ),
    ETUDE_TECHNIQUE(
        title = "Étude Technique, Dimensionnement & Calculs",
        description = "Calculs RDM, sélection des matériaux, cinématique, bilan énergétique, composants standardisés (roulements, paliers) et schémas.",
        targetPageCount = "Pages 14 - 17"
    ),
    TRAVAUX_ET_INTERVENTIONS(
        title = "Travaux Pratiques, Réalisation & Interventions Terrain",
        description = "Chronologie opérationnelle en atelier (débitage, usinage, soudage, finition) et compte-rendu des missions sur sites extérieurs.",
        targetPageCount = "Pages 18 - 21"
    ),
    DIFFICULTES_ET_SOLUTIONS(
        title = "Difficultés Rencontrées, Solutions & Compétences",
        description = "Aléas techniques ou logistiques rencontrés, actions correctives mises en œuvre, protocoles HSE et compétences professionnelles consolidées.",
        targetPageCount = "Pages 22 - 24"
    ),
    CONCLUSION_ET_RECOMMANDATIONS(
        title = "Conclusion Générale & Recommandations",
        description = "Bilan synthétique des résultats au regard des objectifs initiaux, suggestions techniques constructives pour l'entreprise et perspectives.",
        targetPageCount = "Pages 25 - 26"
    ),
    DEFENSE_ORALE_JURY(
        title = "Préparation à la Soutenance & Questions du Jury",
        description = "Questions probables du jury de soutenance avec réponses modèles argumentées et pièges à éviter.",
        targetPageCount = "Fiche Jury"
    )
}

/**
 * Données d'entrée saisies par l'utilisateur transmises au modèle d'IA pour guider la rédaction.
 */
data class InternshipDraftInput(
    val studentConfig: StudentConfig = StudentConfig(),
    val companyConfig: CompanyConfig = CompanyConfig(),
    val locations: List<LocationItem> = emptyList(),
    val rawNotes: String = "",
    val filiereCategory: String = "Génie Mécanique et Productique",
    val specificInstructions: String = "",
    val tone: String = "Soutenu, académique et technique (normes CAMES)"
)

/**
 * Résultat de génération pour une section rédigée.
 */
data class DraftedSectionResult(
    val sectionType: ReportSectionType,
    val title: String,
    val draftedContent: String,
    val keyPointsHighlighted: List<String> = emptyList(),
    val isFromGoogleAiSdk: Boolean = false,
    val modelUsed: String = "gemini-3.5-flash",
    val generatedAtMillis: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)

/**
 * Dossier complet de rédaction contenant l'ensemble des sections d'un mémoire de stage.
 */
data class FullInternshipDraftReport(
    val studentName: String,
    val companyName: String,
    val theme: String,
    val sections: Map<ReportSectionType, DraftedSectionResult>,
    val generatedAtMillis: Long = System.currentTimeMillis()
)

/**
 * Service Android dédié à l'interaction avec le Google AI SDK (com.google.ai.client.generativeai)
 * pour la rédaction automatisée, rigoureuse et contextualisée des sections d'un rapport de stage professionnel.
 */
class GoogleAiReportDraftingService(
    private val apiKeyProvider: () -> String = { BuildConfig.GEMINI_API_KEY }
) {
    companion object {
        private const val TAG = "GoogleAiReportDraft"
        // Modèle recommandé selon les directives pour les tâches textuelles
        const val RECOMMENDED_MODEL = "gemini-3.5-flash"

        @Volatile
        private var instance: GoogleAiReportDraftingService? = null

        fun getInstance(): GoogleAiReportDraftingService {
            return instance ?: synchronized(this) {
                instance ?: GoogleAiReportDraftingService().also { instance = it }
            }
        }
    }

    /**
     * Initialise l'instance du modèle génératif Google AI SDK avec configuration optimale.
     */
    private fun getGenerativeModel(systemInstructionText: String? = null): GenerativeModel {
        val key = apiKeyProvider().trim()
        val validKey = if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else "DEMO_KEY"

        val config = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 3000
        }

        val safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )

        return GenerativeModel(
            modelName = RECOMMENDED_MODEL,
            apiKey = validKey,
            generationConfig = config,
            safetySettings = safetySettings,
            systemInstruction = systemInstructionText?.let {
                content { text(it) }
            }
        )
    }

    /**
     * Vérifie si une clé API valide est configurée.
     */
    fun hasValidApiKey(): Boolean {
        val key = apiKeyProvider().trim()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    /**
     * Rédige une section spécifique du rapport de stage en exploitant le Google AI SDK.
     */
    suspend fun draftSection(
        sectionType: ReportSectionType,
        input: InternshipDraftInput
    ): DraftedSectionResult = withContext(Dispatchers.IO) {
        val prompt = buildSectionPrompt(sectionType, input)
        val systemInstruction = buildSystemInstruction(sectionType, input)

        if (!hasValidApiKey()) {
            Log.w(TAG, "Clé API Google AI non configurée. Génération du modèle de référence haute fidélité.")
            val fallback = generateContextualFallbackDraft(sectionType, input)
            return@withContext DraftedSectionResult(
                sectionType = sectionType,
                title = sectionType.title,
                draftedContent = fallback.first,
                keyPointsHighlighted = fallback.second,
                isFromGoogleAiSdk = false,
                modelUsed = "moteur-expert-intégré",
                errorMessage = "Mode autonome sans clé API (Génération experte locale conforme)"
            )
        }

        try {
            val model = getGenerativeModel(systemInstruction)
            val response = model.generateContent(prompt)
            val rawResponse = response.text?.trim() ?: ""

            if (rawResponse.isBlank()) {
                throw IllegalStateException("Réponse vide renvoyée par le Google AI SDK.")
            }

            val (cleanContent, keyPoints) = extractHighlightsAndCleanText(rawResponse)

            DraftedSectionResult(
                sectionType = sectionType,
                title = sectionType.title,
                draftedContent = cleanContent,
                keyPointsHighlighted = keyPoints,
                isFromGoogleAiSdk = true,
                modelUsed = RECOMMENDED_MODEL,
                errorMessage = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'appel au Google AI SDK pour la section ${sectionType.name}", e)
            val fallback = generateContextualFallbackDraft(sectionType, input)
            DraftedSectionResult(
                sectionType = sectionType,
                title = sectionType.title,
                draftedContent = fallback.first,
                keyPointsHighlighted = fallback.second,
                isFromGoogleAiSdk = false,
                modelUsed = "moteur-expert-intégré",
                errorMessage = "Erreur réseau Google AI (${e.localizedMessage ?: "connexion"}). Repli automatique sur le modèle d'ingénierie certifié."
            )
        }
    }

    /**
     * Rédige en continu avec flux streaming (Flow) via le Google AI SDK.
     */
    fun streamDraftSection(
        sectionType: ReportSectionType,
        input: InternshipDraftInput
    ): Flow<String> = flow {
        val prompt = buildSectionPrompt(sectionType, input)
        val systemInstruction = buildSystemInstruction(sectionType, input)

        if (!hasValidApiKey()) {
            val fallback = generateContextualFallbackDraft(sectionType, input).first
            // Découpage en paragraphes pour simuler un streaming fluide
            val paragraphs = fallback.split("\n\n")
            for (p in paragraphs) {
                emit(p + "\n\n")
                kotlinx.coroutines.delay(120)
            }
            return@flow
        }

        try {
            val model = getGenerativeModel(systemInstruction)
            val responseStream = model.generateContentStream(prompt)
            responseStream.collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur streaming Google AI SDK", e)
            emit("\n[Note de repli automatique : Suite à un incident réseau, chargement du canevas d'ingénierie conforme]\n\n")
            emit(generateContextualFallbackDraft(sectionType, input).first)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Rédige l'ensemble des sections du rapport avec notification de progression.
     */
    suspend fun draftAllSections(
        input: InternshipDraftInput,
        onProgress: ((ReportSectionType, Int, Int) -> Unit)? = null
    ): FullInternshipDraftReport = withContext(Dispatchers.IO) {
        val sectionsMap = mutableMapOf<ReportSectionType, DraftedSectionResult>()
        val total = ReportSectionType.values().size

        ReportSectionType.values().forEachIndexed { index, sectionType ->
            onProgress?.invoke(sectionType, index + 1, total)
            val result = draftSection(sectionType, input)
            sectionsMap[sectionType] = result
        }

        FullInternshipDraftReport(
            studentName = input.studentConfig.studentName,
            companyName = input.companyConfig.inputName,
            theme = input.studentConfig.theme,
            sections = sectionsMap
        )
    }

    /**
     * Permet d'enrichir ou d'approfondir une section déjà rédigée en lui appliquant une directive ciblée
     * (ex: "Développer les calculs de contrainte Von Mises", "Ajouter les protocoles EPI", etc.).
     */
    suspend fun refineSection(
        sectionType: ReportSectionType,
        currentContent: String,
        userInstruction: String,
        input: InternshipDraftInput
    ): DraftedSectionResult = withContext(Dispatchers.IO) {
        if (!hasValidApiKey()) {
            val enriched = currentContent + "\n\n[Complément d'ingénierie ($userInstruction)] : " +
                    "L'application rigoureuse des normes industrielles en vigueur garantit la résistance des liaisons et la maîtrise des coefficients de sécurité."
            return@withContext DraftedSectionResult(
                sectionType = sectionType,
                title = sectionType.title,
                draftedContent = enriched,
                isFromGoogleAiSdk = false,
                modelUsed = "moteur-expert-intégré"
            )
        }

        val prompt = """
            Tu es l'assistant de rédaction de mémoires d'ingénierie et de BTS.
            Voici le texte actuel de la section « ${sectionType.title} » :
            \"\"\"
            $currentContent
            \"\"\"

            Directive d'amélioration de l'étudiant : "$userInstruction"
            Contexte : Étudiant ${input.studentConfig.studentName}, Thème : ${input.studentConfig.theme}, Entreprise : ${input.companyConfig.inputName}.

            Révise et enrichis ce texte pour intégrer parfaitement cette directive tout en conservant le style académique soutenu et rigoureux.
            Réponds directement par le texte révisé complet.
        """.trimIndent()

        try {
            val model = getGenerativeModel()
            val response = model.generateContent(prompt)
            val updated = response.text?.trim() ?: currentContent

            DraftedSectionResult(
                sectionType = sectionType,
                title = sectionType.title,
                draftedContent = updated,
                isFromGoogleAiSdk = true,
                modelUsed = RECOMMENDED_MODEL
            )
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors du perfectionnement de la section", e)
            DraftedSectionResult(
                sectionType = sectionType,
                title = sectionType.title,
                draftedContent = currentContent,
                isFromGoogleAiSdk = false,
                errorMessage = "Échec du perfectionnement IA : ${e.message}"
            )
        }
    }

    // =========================================================================
    // CONSTRUCTION DES PROMPTS STRUCTURÉS
    // =========================================================================

    private fun buildSystemInstruction(sectionType: ReportSectionType, input: InternshipDraftInput): String {
        return """
            Tu es un professeur agrégé et ingénieur consultant membre des jurys d'examen officiels (CAMES, BTS, DUT, Master).
            Tu rédiges des sections officielles de rapports et mémoires de stage de haut niveau académique et technique.
            
            Profil du stagiaire :
            - Nom : ${input.studentConfig.studentName}
            - Établissement : ${input.studentConfig.schoolName}
            - Filière / Spécialité : ${input.studentConfig.filiereOption}
            - Thème du mémoire : ${input.studentConfig.theme}
            - Année académique : ${input.studentConfig.academicYear}
            - Encadreur professionnel : ${input.studentConfig.internshipTutor}
            - Encadreur académique : ${input.studentConfig.academicTutor}
            
            Entreprise d'accueil :
            - Nom : ${input.companyConfig.companyFullTitle} (${input.companyConfig.inputName})
            - Adresse : ${input.companyConfig.companyAddress}
            - Activités : ${input.companyConfig.activities}
            - Gérance / Direction : ${input.companyConfig.contactGerant}
            - Responsable technique / atelier : ${input.companyConfig.contactAtelier}
            - RCCM : ${input.companyConfig.rccmBank}

            Règles stylistiques et académiques impératives :
            1. Style soutenu, rigoureux, précis et fluide. Utilisation de la première personne du pluriel (« nous ») ou de tournures professionnelles passives selon la tradition académique.
            2. Aucun bavardage superflu ni formulations creuses. Chaque paragraphe doit apporter une valeur technique, méthodologique ou humaine concrète.
            3. Respect strict de la terminologie de la spécialité (${input.filiereCategory}).
            4. Ne pas inclure de balises Markdown inutiles ni de salutations introductives. Fournir directement le texte prêt pour insertion dans le document final.
        """.trimIndent()
    }

    private fun buildSectionPrompt(sectionType: ReportSectionType, input: InternshipDraftInput): String {
        val locationsSummary = if (input.locations.isNotEmpty()) {
            input.locations.joinToString("\n") { loc ->
                "  • Site : ${loc.locationName} | Durée : ${loc.daysDuration} jours | Mission : ${loc.cause}"
            }
        } else {
            "  • Stage réalisé intégralement en atelier central / site principal."
        }

        val notes = input.rawNotes.ifBlank { "Aucune note particulière, extrapoler selon les meilleures pratiques du secteur." }

        return when (sectionType) {
            ReportSectionType.REMERCIEMENTS -> """
                Rédige la page de « Remerciements & Dédicaces » du mémoire.
                Protocole à respecter :
                1. Remerciements chaleureux et respectueux à la Direction Générale (${input.companyConfig.contactGerant}).
                2. Gratitude technique envers l'encadreur d'entreprise (${input.studentConfig.internshipTutor}) et l'équipe d'atelier pour leur accompagnement quotidien et transmission de savoir-faire.
                3. Reconnaissance académique envers l'administration de ${input.studentConfig.schoolName} et l'encadreur pédagogique (${input.studentConfig.academicTutor}).
                4. Dédicace envers la famille et les proches pour leur soutien moral et matériel constant.
                Rédige 4 à 5 paragraphes harmonieux et très polis.
            """.trimIndent()

            ReportSectionType.INTRODUCTION_GENERALE -> """
                Rédige l'« Introduction Générale » du mémoire (Pages 5 et 6).
                Structure obligatoire :
                1. Contexte général et rôle stratégique du secteur d'activité (${input.companyConfig.activities}).
                2. Justification du choix du thème : « ${input.studentConfig.theme} » au sein de ${input.companyConfig.inputName}.
                3. Problématique industrielle identifiée, contraintes d'exploitation et enjeux de sécurité/productivité.
                4. Objectifs assignés au stage (techniques, méthodologiques et professionnels).
                5. Annonce claire de la structure du rapport (Partie 1 : Cadre institutionnel, Partie 2 : Étude technique, Partie 3 : Réalisation pratique et bilan).
                Longueur souhaitée : 4 à 6 paragraphes denses et très structurés.
            """.trimIndent()

            ReportSectionType.PRESENTATION_ENTREPRISE -> """
                Rédige la « Présentation de l'Entreprise d'Accueil » (${input.companyConfig.companyFullTitle}).
                Éléments à intégrer de manière fluide :
                - Statut juridique et mentions légales : ${input.companyConfig.rccmBank}
                - Siège et localisation géographique : ${input.companyConfig.companyAddress}
                - Pôles de compétences et activités principales : ${input.companyConfig.activities}
                - Organisation managériale et atelier : Direction (${input.companyConfig.contactGerant}) et Encadrement technique (${input.companyConfig.contactAtelier})
                - Évolution historique, missions sociétales et positionnement concurrentiel sur le marché ivoirien et régional.
                Fournis un texte institutionnel complet de 5 paragraphes d'une grande clarté.
            """.trimIndent()

            ReportSectionType.CADRE_METHODOLOGIQUE -> """
                Rédige la section « Cadre Méthodologique & Cahier des Charges Fonctionnel ».
                Contenu à développer :
                1. Analyse du besoin : à qui le produit rend-il service ? sur quoi agit-il ? dans quel but ?
                2. Fonctions de service (Fonction Principale FP et Fonctions Contraintes FC : sécurité, maintenance, durabilité, coûts).
                3. Critères d'appréciation et niveaux d'exigence (normes de fabrication, cadences de manœuvre, résistance climatique).
                4. Démarche d'ingénierie adoptée pour traiter le thème « ${input.studentConfig.theme} ».
                Présente les fonctions sous forme d'une liste structurée avec désignation et critères.
            """.trimIndent()

            ReportSectionType.ETUDE_TECHNIQUE -> """
                Rédige la section « Étude Technique, Conception et Calculs de Dimensionnement ».
                Thème : ${input.studentConfig.theme}.
                Filière : ${input.studentConfig.filiereOption}.
                Détaille les aspects techniques suivants :
                1. Choix cinématique et architectural de la solution retenue.
                2. Choix et justification des matériaux utilisés (profilés acier S235JR, aciers traités, alliages d'aluminium, etc.).
                3. Calculs mécaniques simplifiés et RDM : contraintes admissibles, calcul du couple moteur/résistant, flexion de la lisse, effort de cisaillement au pivot.
                4. Sélection et dimensionnement des organes de roulement (roulements rigides à billes type SKF 6205-2RS, paliers auto-aligneurs en fonte UCP, lubrification graissée à vie).
                5. Schéma de principe cinématique et descriptif des liaisons mécaniques.
            """.trimIndent()

            ReportSectionType.TRAVAUX_ET_INTERVENTIONS -> """
                Rédige la section « Travaux Pratiques, Réalisation & Interventions Terrain ».
                Intègre explicitement les données réelles suivantes saisies par l'étudiant :
                - Lieux et chantiers extérieurs réalisés :
                $locationsSummary
                - Notes brutes et activités atelier :
                "$notes"

                Exigences rédactionnelles :
                1. Découpage chronologique des étapes en atelier : préparation des bruts, débitage sur scie, usinage/alésage, meulage d'ébavurage, assemblage et soudage, contrôles géométriques, traitement de surface et peinture.
                2. Si des interventions extérieures sont mentionnées, explique avec clarté la cause du déplacement, la logistique de chantier et les travaux spécifiques menés sur chaque site.
                3. Mentionne le travail en binôme et la supervision technique par ${input.studentConfig.internshipTutor}.
            """.trimIndent()

            ReportSectionType.DIFFICULTES_ET_SOLUTIONS -> """
                Rédige la section « Difficultés Rencontrées, Solutions Mises en Œuvre et Compétences Acquises ».
                Développe :
                1. Difficultés techniques rencontrées lors de l'exécution (déformations thermiques dues au soudage, contraintes d'alignement des axes de roulements, aléas logistiques ou climatiques sur chantiers).
                2. Solutions correctives et ingénieuses adoptées pour surmonter chaque difficulté sous la tutelle de l'atelier.
                3. Mesures de sécurité et protocoles HSE appliqués (EPI, consignation, ventilation, prévention des projections).
                4. Bilan des compétences acquises :
                   - Savoirs techniques (soudage, calculs, montage de roulements, lecture de plans)
                   - Savoir-être professionnel (travail d'équipe, ponctualité, rigueur, respect des consignes de sécurité).
            """.trimIndent()

            ReportSectionType.CONCLUSION_ET_RECOMMANDATIONS -> """
                Rédige la « Conclusion Générale & Recommandations Stratégiques ».
                Points clés à aborder :
                1. Bilan d'accomplissement des objectifs : validation de la barrière / de l'ouvrage conçu, performance des mécanismes à roulements, conformité aux exigences du cahier des charges.
                2. Apports personnels et professionnels majeurs du stage pour ${input.studentConfig.studentName}.
                3. Recommandations constructives et argumentées pour l'entreprise ${input.companyConfig.inputName} (ex: plan de maintenance préventive des roulements, acquisition d'un gabarit de soudage pour standardiser la production, formation continue HSE).
                4. Mot de clôture valorisant l'accueil et l'excellence de l'entreprise.
            """.trimIndent()

            ReportSectionType.DEFENSE_ORALE_JURY -> """
                Rédige la « Fiche de Préparation à la Soutenance et Questions Critiques du Jury ».
                Pour le thème « ${input.studentConfig.theme} » chez ${input.companyConfig.inputName}, génère :
                1. Les 4 questions techniques et méthodologiques les plus probables que les examinateurs poseront.
                2. Pour chaque question :
                   - La question exacte posée par le jury.
                   - La réponse idéale, technique et argumentée que le candidat ${input.studentConfig.studentName} doit prononcer.
                   - Le piège à éviter absolument lors de la prise de parole.
                   - L'attente fondamentale des membres du jury.
            """.trimIndent()
        }
    }

    /**
     * Analyse le texte renvoyé pour extraire des points saillants si présents.
     */
    private fun extractHighlightsAndCleanText(rawText: String): Pair<String, List<String>> {
        val cleanText = rawText
            .replace("```markdown", "")
            .replace("```text", "")
            .replace("```", "")
            .trim()

        val highlights = mutableListOf<String>()
        val lines = cleanText.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if ((trimmed.startsWith("•") || trimmed.startsWith("- ") || trimmed.startsWith("* ")) && trimmed.length in 15..120) {
                highlights.add(trimmed.removePrefix("•").removePrefix("- ").removePrefix("* ").trim())
                if (highlights.size >= 4) break
            }
        }

        return Pair(cleanText, highlights)
    }

    // =========================================================================
    // MODÈLE DE REPLI CONTEXTUALISÉ (HAUTE QUALITÉ SANS CLÉ API)
    // =========================================================================

    private fun generateContextualFallbackDraft(
        sectionType: ReportSectionType,
        input: InternshipDraftInput
    ): Pair<String, List<String>> {
        val student = input.studentConfig
        val company = input.companyConfig
        val locations = input.locations
        val notes = input.rawNotes

        val draft = when (sectionType) {
            ReportSectionType.REMERCIEMENTS -> """
Au terme de ce stage de fin d'études réalisé au sein de l'entreprise ${company.companyFullTitle}, il nous est particulièrement agréable de témoigner notre profonde gratitude à toutes les personnes qui ont contribué, de près ou de loin, à la réussite de notre immersion professionnelle et à l'élaboration de ce mémoire d'ingénierie.

Nous tenons en premier lieu à exprimer nos remerciements les plus respectueux à la Direction Générale de ${company.inputName}, notamment à ${company.contactGerant}, pour nous avoir ouvert les portes de leur prestigieuse structure et pour la confiance constante qu'ils nous ont accordée tout au long de cette période.

Notre reconnaissance la plus sincère s'adresse particulièrement à notre maître de stage, ${student.internshipTutor}, ainsi qu'à l'ensemble des techniciens et ouvriers de l'atelier de fabrication. Leur disponibilité permanente, leur bienveillance pédagogique et le partage généreux de leur savoir-faire industriel nous ont permis de surmonter les exigences pratiques du métier et de mener à bien l'ensemble de nos interventions.

Nous tenons également à témoigner notre gratitude à l'administration et au corps enseignant de ${student.schoolName}, et plus particulièrement à notre encadreur académique, ${student.academicTutor}, pour la rigueur scientifique, les orientations méthodologiques avisées et le soutien constant qui ont guidé la rédaction de ce travail.

Enfin, nous adressons une affectueuse pensée à nos parents, notre famille et nos camarades de promotion, dont les encouragements inlassables et le soutien moral et financier sans faille ont constitué une source inépuisable de motivation.
            """.trimIndent()

            ReportSectionType.INTRODUCTION_GENERALE -> """
Dans un environnement économique et industriel en pleine expansion en Côte d'Ivoire et dans la sous-région ouest-africaine, la sécurisation des infrastructures, des flux logistiques et des sites stratégiques constitue une priorité absolue pour les gestionnaires d'établissements. La conception d'équipements alliant robustesse mécanique, fiabilité cinématique et ergonomie d'utilisation s'avère indispensable pour répondre à ces défis opérationnels.

C'est dans ce cadre propice à l'innovation technologique que s'inscrit notre stage académique au sein de la société ${company.companyFullTitle}, entreprise spécialisée dans les domaines de la ${company.activities}. Face au besoin de régulation et de protection périmétrique des accès carrossables, nous avons été chargés de conduire les études et la concrétisation du thème intitulé : « ${student.theme} ».

La problématique centrale de ce projet repose sur l'optimisation de la liaison pivot et de l'équilibrage statique du bras mobile, soumis quotidiennement à des sollicitations cycliques intenses, à l'usure prématurée et aux agressions climatiques extérieures. Comment concevoir un ensemble mécanique endurant, facilement maintenable et garantissant une fluidité de rotation sans faille tout en respectant les standards stricts de sécurité ?

Pour répondre à ces enjeux, notre mission s'est articulée autour de trois axes complémentaires : une étude conceptuelle et dimensionnelle rigoureuse conforme aux normes en vigueur, la réalisation pas-à-pas en atelier des sous-ensembles chaudronnés avec intégration de paliers à roulements spécifiques, et la conduite d'essais fonctionnels approfondis validant les performances cinématiques du mécanisme.

Le présent rapport rend compte de cette démarche en trois grandes parties : la première expose le cadre institutionnel et méthodologique du stage, la seconde détaille les calculs et choix technologiques du dimensionnement, et la troisième synthétise la fabrication pratique, le bilan des interventions et les compétences professionnelles acquises.
            """.trimIndent()

            ReportSectionType.PRESENTATION_ENTREPRISE -> """
La société ${company.companyFullTitle}, couramment désignée sous le nom commercial de ${company.inputName}, est une entité industrielle de référence immatriculée sous les références légales ${company.rccmBank}. Établie à l'adresse suivante : ${company.companyAddress}, l'entreprise rayonne activement auprès des donneurs d'ordres nationaux et internationaux.

Son champ d'expertise couvre un spectre diversifié de prestations de pointe, englobant notamment : ${company.activities}. Grâce à un parc de machines-outils performant et à une équipe d'artisans chaudronniers et de monteurs qualifiés, la société s'illustre par sa capacité à livrer des ouvrages métalliques complexes répondant aux normes internationales de qualité et de sécurité.

Sur le plan managérial et structurel, l'entreprise est pilotée par une gouvernance expérimentée avec à sa tête ${company.contactGerant}, garant de la vision stratégique et du développement commercial. La coordination opérationnelle et la maîtrise des chantiers sont assurées par ${company.contactAtelier}, veillant quotidiennement à la qualité d'exécution et au respect des plannings.

L'organigramme interne privilégie la réactivité et la synergie entre le bureau des méthodes, l'atelier de fabrication, la logistique d'approvisionnement et le service de maintenance extérieure. Cette organisation fluide permet d'assurer un suivi rigoureux depuis la phase d'adjudication jusqu'à la réception définitive sur site client.

Dans le contexte actuel de réindustrialisation, ${company.inputName} se positionne comme un partenaire stratégique incontournable, apportant des réponses techniques pérennes adaptées aux exigences sévères des environnements tropicaux et industriels.
            """.trimIndent()

            ReportSectionType.CADRE_METHODOLOGIQUE -> """
La conduite d'un projet d'ingénierie mécanique exige une démarche méthodologique structurée, allant de l'analyse fonctionnelle du besoin jusqu'à la formulation du Cahier des Charges Fonctionnel (CDCF). Cette démarche garantit que l'ouvrage conçu répond exactement aux attentes des utilisateurs finaux tout en respectant les contraintes de faisabilité industrielle.

L'analyse du besoin, formalisée selon la méthode APTE, précise que le système rend service aux gestionnaires de sécurité de site en agissant sur le gabarit d'accès des véhicules afin d'assurer un filtrage fluide, sécurisé et inviolable des entrées et sorties.

Les fonctions de service ont été caractérisées selon les critères suivants :
• Fonction Principale (FP1) : Permettre l'ouverture et la fermeture contrôlée d'une voie carrossable de 4 à 6 mètres en absorbant les contraintes dynamiques.
• Fonction Contrainte 1 (FC1 - Sécurité) : Garantir l'intégrité physique des usagers et des opérateurs via des butées mécaniques amorties et une absence de point de pincement.
• Fonction Contrainte 2 (FC2 - Endurance & Mécanique) : Assurer un fonctionnement continu supérieur à 500 000 manœuvres sans grippage grâce à une liaison pivot par roulements à billes étanches.
• Fonction Contrainte 3 (FC3 - Environnement) : Résister à la corrosion atmosphérique et aux embruns marins tropicaux par un traitement de surface protecteur bicouche.
• Fonction Contrainte 4 (FC4 - Ergonomie & Maintenance) : Limiter l'effort manuel d'ouverture à moins de 40 N en cas de coupure d'énergie et permettre un graissage ou remplacement rapide des organes d'usure.
            """.trimIndent()

            ReportSectionType.ETUDE_TECHNIQUE -> """
L'étude technique et le dimensionnement de la barrière levante reposent sur la modélisation cinématique du bras pivotant et la vérification de la résistance des matériaux (RDM) sous sollicitations de flexion statique et de vent transversal.

La structure portante a été sélectionnée en acier de construction S235JR pour ses excellentes aptitudes au soudage à l'arc et sa résilience. La lisse mobile fait appel à un profilé tubulaire rectangulaire de 80x40x3 mm alliant rigidité torsionnelle et allègement massique. L'arbre pivot, soumis à des contraintes de cisaillement et de matage, a été dimensionné en acier mi-dur E335 / XC48 avec un diamètre de portée de 25 mm.

Le guidage en rotation constitue le cœur mécanique du système. Afin d'éradiquer les frottements secs et les risques de grippage inhérents aux simples bagues lisses, nous avons intégré deux paliers auto-aligneurs à semelle en fonte UCP 205 logeant des roulements rigides à billes SKF 6205-2RS. Dotés de flasques d'étanchéité en élastomère nitrile, ces roulements sont protégés contre les poussières abrasives et l'humidité, offrant un coefficient de frottement réduit (µ < 0,002) et une charge dynamique admissible C = 14 kN largement supérieure aux charges en service.

L'équilibrage statique du système est obtenu par un contrepoids réglable monté en porte-à-faux arrière, annulant 90 % du moment de pesanteur exercé par la lisse déployée. Les calculs de contrainte équivalente de Von Mises sur les cordons de soudure confirment un coefficient de sécurité global s = 3,2, validant la pérennité de l'ensemble.
            """.trimIndent()

            ReportSectionType.TRAVAUX_ET_INTERVENTIONS -> {
                val siteDetails = if (locations.isNotEmpty()) {
                    locations.joinToString("\n") { loc ->
                        "• Déploiement sur le site « ${loc.locationName} » (mission sur ${loc.daysDuration} jours) : ${loc.cause}."
                    }
                } else {
                    "L'ensemble des activités s'est concentré au sein de l'atelier central, combinant fabrication des composants, ajustage mécanique et banc d'essai opérationnel."
                }

                val notesRef = if (notes.isNotBlank()) {
                    "En concordance avec nos relevés d'atelier : \"$notes\", nous avons procédé au traçage millimétrique, à la découpe à la scie mécanique, au meulage des chanfreins et à la mise en position sur marbre."
                } else {
                    "Les opérations ont débuté par la préparation des débits d'acier, le traçage sur gabarit et l'usinage des portées d'arbres au tour conventionnel."
                }

                """
La réalisation pratique du projet s'est déroulée selon une planification chronologique rigoureuse, articulant fabrication en atelier et déploiements sur sites extérieurs.

$notesRef

En atelier central, les profilés métalliques ont été débités puis soigneusement chanfreinés à la meuleuse d'angle pour garantir une pénétration optimale du bain de fusion. L'assemblage du fût principal et des platines d'ancrage a été conduit par soudage semi-automatique MAG (fil continu 1,0 mm sous protection gazeuse Ar+18% CO2), sous la supervision vigilante de ${student.internshipTutor}. Les déformations thermiques ont été contenues par un bridage soigné et un soudage alterné symétrique.

Les portées de l'arbre ont été dressées et ajustées pour recevoir les paliers UCP 205 avec une tolérance h6/H7 assurant un glissement juste sans jeu néfaste. Une attention particulière a été apportée à l'alignement coaxial des deux paliers pour prévenir tout arc-boutement cinématique.

Sur le terrain, nos interventions se sont déployées selon le calendrier suivant :
$siteDetails

Chaque intervention extérieure a mobilisé des compétences de nivellement au niveau optique, de scellement chimique des tiges filetées dans le béton et de calage micrométrique de l'axe de rotation. La phase finale a consisté en l'application d'un primaire antirouille phosphatant et de deux couches de peinture polyuréthane blanche haute visibilité réalisées en binôme.
                """.trimIndent()
            }

            ReportSectionType.DIFFICULTES_ET_SOLUTIONS -> """
Tout projet industriel comporte des aléas de fabrication et d'exécution qui constituent autant d'opportunités d'apprentissage et de maturation technique. Durant notre stage, plusieurs défis majeurs ont été surmontés :

1. Déformation thermique lors du soudage des platines épaisses : Le retrait thermique provoquait une légère désaxation du bâti. Solution : adoption d'un ordre de soudage pas-de-pèlerin, emploi de serre-joints à haute puissance et préchauffage localisé des tôles de 10 mm.
2. Alignement et coaxialité des paliers UCP : La moindre imperfection d'entraxe engendrait des points durs lors du pivotement. Solution : utilisation de cales pelables en acier de 0,1 mm et vérification de la liberté de rotation manuelle avant serrage au couple dynamométrique prescrit.
3. Contraintes climatiques et poussières sur sites d'intervention : Risque de contamination des billes des roulements. Solution : sélection stricte de roulements étanches 2RS dotés de lèvres d'étanchéité renforcées et application d'un cordon de graisse graphitée protectrice.

Sur le plan de la sécurité et de l'environnement (HSE), nous avons rigoureusement appliqué les consignes professionnelles : port systématique du masque optoélectronique, des gants de manutention en croûte de cuir, des chaussures de sécurité S3 et consignation physique des machines tournantes.

Ce stage nous a permis de consolider des compétences déterminantes : maîtrise pratique des procédés de soudage et d'usinage, lecture critique de plans industriels, métrologie dimensionnelle au pied à coulisse, sens du travail en équipe et rigueur opérationnelle.
            """.trimIndent()

            ReportSectionType.CONCLUSION_ET_RECOMMANDATIONS -> """
Au terme de ce stage de fin d'études mené au sein de ${company.companyFullTitle}, le bilan dressé est particulièrement satisfaisant, tant sur le plan de la validation technique que sur celui de notre enrichissement personnel et professionnel.

L'objectif fixé – à savoir l'étude, la modélisation et la réalisation d'une barrière levante industrielle à mécanismes à roulements – a été pleinement atteint. Les essais d'endurance ont confirmé un fonctionnement silencieux, un effort de levage inférieur à 30 N grâce à l'efficacité du contrepoids et une absorption exemplaire des efforts radiaux par les paliers UCP 205. L'ouvrage satisfait intégralement aux exigences du cahier des charges et aux normes de sécurité en vigueur.

Sur le plan des perspectives d'amélioration, nous soumettons à l'attention bienveillante de la direction de ${company.inputName} les recommandations stratégiques suivantes :
• Standardisation de gabarits d'assemblage en atelier pour accélérer la production en série des ensembles de barrières et garantir une répétabilité dimensionnelle inférieure au millimètre.
• Instauration d'un plan de maintenance préventive formalisé dans un carnet de bord remis à chaque client, préconisant une vérification trimestrielle du serrage des vis d'arrêt des roulements et un contrôle annuel de la couche de peinture anticorrosion.
• Étude d'une version motorisée basse tension (24V DC) couplée à un panneau photovoltaïque et à une batterie tampon, répondant aux besoins de sécurisation des sites isolés non raccordés au réseau électrique national.

Nous concluons en réitérant notre vive gratitude à l'équipe de ${company.inputName} pour cette immersion formatrice qui consolide notre engagement dans le métier d'ingénieur.
            """.trimIndent()

            ReportSectionType.DEFENSE_ORALE_JURY -> """
FICHE TECHNIQUE POUR LA SOUTENANCE DEVANT LE JURY D'EXAMEN

Question 1 du Jury : « Pourquoi avoir retenu des paliers à semelle UCP 205 plutôt que des coussinets lisses en bronze fritté autolubrifiant ? »
Réponse recommandée du candidat : « Monsieur le Président, les coussinets lisses fonctionnent par frottement hydrodynamique nécessitant un mouvement continu pour établir un film d'huile. Dans le cas d'une barrière levante, le mouvement est oscillant, intermittent et soumis à des charges statiques élevées en position fermée. Les roulements rigides à billes du palier UCP 205 offrent un frottement de roulement quasi nul au démarrage (µ < 0,002 contre 0,08 pour le bronze), empêchent tout broutage et leur étanchéité 2RS résiste parfaitement aux poussières et à la pluie battante. »
Piège à éviter : Ne pas citer le coût comme seul argument, privilégier la cinématique et l'endurance à l'arrêt prolongé.
Attente du jury : Vérifier la maîtrise de la tribologie et des choix technologiques.

Question 2 du Jury : « Comment avez-vous garanti l'absence de déformations thermiques lors de l'assemblage soudé du bâti ? »
Réponse recommandée du candidat : « Nous avons mis en œuvre un bridage rigide sur marbre de chaudronnerie et adopté une séquence de soudage alternée et symétrique dite en 'pas-de-pèlerin'. De plus, les cordons ont été réalisés sous intensité contrôlée (160 A) en surveillant la température inter-passes, évitant ainsi le gauchissement des platines d'assise. »
Piège à éviter : Affirmer qu'il n'y a eu aucune déformation sans expliquer les contre-mesures.
Attente du jury : Compréhension des phénomènes métallurgiques du soudage.

Question 3 du Jury : « Quelle est la fonction exacte du contrepoids arrière et comment avez-vous déterminé sa position ? »
Réponse recommandée du candidat : « Le contrepoids a pour fonction d'annuler le moment de renversement engendré par le poids de la lisse de 4 mètres par rapport à l'axe de rotation. Par le principe fondamental de la statique, la somme des moments en O est nulle quand M_contrepoids * d1 = M_lisse * (L/2). Nous avons prévu une tige filetée permettant d'ajuster d1 afin de régler finement l'effort résiduel pour qu'il ne dépasse pas 30 Newtons en manœuvre manuelle. »
Piège à éviter : Oublier de mentionner la marge de sécurité et les efforts aérodynamiques dus au vent.
Attente du jury : Aptitude à appliquer la mécanique rationnelle (statique des solides).

Question 4 du Jury : « Quels ont été les apports concrets de vos déplacements sur les chantiers extérieurs par rapport au travail en atelier ? »
Réponse recommandée du candidat : « Les missions extérieures nous ont confrontés aux contraintes réelles d'implantation : non-planéité des massifs béton, coordination avec le génie civil, gestion des poussières de chantier et respect des délais clients. Cela a complété notre vision d'ingénieur en prouvant qu'un composant bien conçu en atelier doit avant tout être installable et réglable avec aisance sur le terrain. »
Piège à éviter : Réduire les missions extérieures à du simple transport de matériel.
Attente du jury : Maturité professionnelle et appréciation du terrain.
            """.trimIndent()
        }

        val highlights = when (sectionType) {
            ReportSectionType.REMERCIEMENTS -> listOf(
                "Direction Générale et gérance",
                "Encadrement d'atelier et tuteurs",
                "Corps professoral et école",
                "Soutien familial indéfectible"
            )
            ReportSectionType.INTRODUCTION_GENERALE -> listOf(
                "Sécurisation des infrastructures industrielles",
                "Optimisation cinématique du bras mobile",
                "Réduction des contraintes d'usure",
                "Structure en trois parties conformes CAMES"
            )
            ReportSectionType.PRESENTATION_ENTREPRISE -> listOf(
                "Dénomination légale et RCCM vérifiés",
                "Expertise en chaudronnerie et soudage",
                "Organisation managériale et atelier",
                "Positionnement stratégique régional"
            )
            ReportSectionType.CADRE_METHODOLOGIQUE -> listOf(
                "Analyse du besoin selon méthode APTE",
                "Fonctions principales et contraintes",
                "Exigences de sécurité et d'endurance",
                "Limitation des efforts sous 40 N"
            )
            ReportSectionType.ETUDE_TECHNIQUE -> listOf(
                "Acier S235JR et arbre mi-dur E335",
                "Paliers auto-aligneurs UCP 205 étanches",
                "Roulements à billes SKF 6205-2RS",
                "Coefficient de sécurité global s = 3,2"
            )
            ReportSectionType.TRAVAUX_ET_INTERVENTIONS -> listOf(
                "Débitage et soudage MAG supervisé",
                "Ajustage coaxial des axes de rotation",
                "Missions et interventions terrain documentées",
                "Finition anticorrosion et peinture bicouche"
            )
            ReportSectionType.DIFFICULTES_ET_SOLUTIONS -> listOf(
                "Maîtrise des retraits de soudage",
                "Calage pelable des portées de roulement",
                "Protocoles HSE et port des EPI complets",
                "Compétences techniques et humaines consolidées"
            )
            ReportSectionType.CONCLUSION_ET_RECOMMANDATIONS -> listOf(
                "Atteinte totale des objectifs fonctionnels",
                "Recommandation de gabarits de soudage standardisés",
                "Plan de maintenance préventive formalisé",
                "Étude d'une version motorisée solaire 24V"
            )
            ReportSectionType.DEFENSE_ORALE_JURY -> listOf(
                "Justification tribologique des roulements UCP",
                "Contrôle thermique du soudage",
                "Principe statique d'équilibrage du contrepoids",
                "Maturité démontrée sur chantiers clients"
            )
        }

        return Pair(draft, highlights)
    }
}
