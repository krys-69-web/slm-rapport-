package com.example.generator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.model.CompanyConfig
import com.example.model.DocBlock
import com.example.model.ImportedImage
import com.example.model.LocationItem
import com.example.model.ReportPage
import com.example.model.StudentConfig
import java.io.ByteArrayOutputStream

object ReportGenerator28Pages {

    fun generateIntelligentSummary(
        company: CompanyConfig,
        student: StudentConfig,
        locations: List<LocationItem>,
        rawNotes: String
    ): String {
        val companyName = company.inputName.ifBlank { "ICC CORPORATE" }
        val locationPart = if (locations.isNotEmpty()) {
            val sitesSummary = locations.mapIndexed { idx, loc ->
                "Lieu ${idx + 1} : On est allé à ${loc.locationName.ifBlank { "Site d'exploitation industrielle" }} sur ${loc.daysDuration.ifBlank { "3" }} jours, c'est pour ${loc.cause.ifBlank { "relevé topologique et pose de structure" }}."
            }.joinToString("\n\n")

            """
            |Dans le cadre de notre stage académique au sein de la société $companyName, située à Yopougon Figayo (Abidjan), portant sur le thème : « ${student.theme} », nous avons effectué une série de missions opérationnelles tant en atelier central que sur les sites clients extérieurs.
            |
            |$sitesSummary
            |
            |Ces différentes descentes sur le terrain nous ont permis de confronter les exigences du cahier des charges fonctionnel aux réalités dimensionnelles des voies de passage. Les opérations ont englobé la vérification des empâtements au sol, l'ancrage chimique des platines support, ainsi que le couplage précis entre la mécanique oscillante et le portique de sécurité.
            """.trimMargin()
        } else {
            """
            |Dans le cadre de notre stage académique au sein de l'entreprise $companyName, située à Yopougon Figayo, portant sur le thème : « ${student.theme} », nous avons concentré nos travaux pratiques et d'ingénierie au sein des ateliers de chaudronnerie et de construction métallique.
            |
            |L'objectif central a consisté en la réalisation intégrale d'un dispositif de contrôle d'accès haute sécurité. Ce système assure une quadruple fonction : le filtrage strict des véhicules entrants, la sécurisation accrue des périmètres d'accès sensibles, la gestion fluide des flux de circulation aux heures de pointe et une force de dissuasion passive indispensable face aux intrusions non autorisées.
            """.trimMargin()
        }

        val technicalEnrichment = """
        |
        |Sur le plan opératoire et technologique, nous avons mis en œuvre l'ensemble de la chaîne de fabrication mécanique :
        |1. Débitage, coupe droite et biseautée ainsi que meulage minutieux des profilés creux et tubes acier à l'aide de meuleuses d'angle équipées de disques à tronçonner et à ébarber de granulométries adaptées.
        |2. Soudure industrielle et pointage rigoureux à l'électrode enrobée sous la supervision constante et méthodique de M. Farès, garantissant un cordon de soudure homogène, étanche et sans inclusions de laitier.
        |3. Traitement de surface anticorrosion par application d'une couche primaire d'anti-oxydant au pinceau et pistolet pneumatique, suivie d'une finition soignée en peinture blanche industrielle réalisée en binôme, répondant aux normes de visibilité diurne et nocturne.
        |4. Montage précis des mécanismes à roulements à billes graissés assurant une rotation fluide sans jeu résiduel de la lisse pivotante.
        |
        |${if (rawNotes.isNotBlank()) "Notes complémentaires consolidées du stagiaire :\n$rawNotes\n" else ""}
        |L'ensemble de ces interventions, réalisé sur une durée globale réglementaire de stage, valide avec succès les objectifs d'apprentissage technique et de conformité industrielle fixés initialement.
        """.trimMargin()

        return "$locationPart\n\n$technicalEnrichment"
    }

    fun build28Pages(
        company: CompanyConfig,
        student: StudentConfig,
        locations: List<LocationItem>,
        finalSummary: String,
        illustrations: List<ImportedImage>,
        geminiData: com.example.service.GeminiEnrichmentResult? = null
    ): List<ReportPage> {
        val cName = company.inputName.ifBlank { "ICC CORPORATE" }
        val pages = mutableListOf<ReportPage>()

        // Résolution dynamique du contenu technique adapté à la filière et au thème
        val filiereType = FiliereContentResolver.detectFiliereType(student.filiereOption, student.theme)
        val filiereContent = FiliereContentResolver.resolveTechnicalContent(student, company)

        // ==========================================
        // PHASE 1 : LA STRUCTURE MACRO-ÉCONOMIQUE ET INSTITUTIONNELLE (PAGES PRÉLIMINAIRES)
        // ==========================================

        // Page 1: La Page de Garde (Couverture officielle)
        pages.add(
            ReportPage(
                pageNumber = 1,
                chapterTitle = "PHASE 1 : STRUCTURE INSTITUTIONNELLE",
                pageTitle = "RAPPORT DE STAGE",
                blocks = listOf(
                    DocBlock.Title("RÉPUBLIQUE DE CÔTE D'IVOIRE\nUnion - Discipline - Travail\nMINISTÈRE DE L'ENSEIGNEMENT SUPÉRIEUR ET DE LA RECHERCHE SCIENTIFIQUE", 2),
                    DocBlock.Paragraph("Établissement d'Enseignement Supérieur : ${student.schoolName}"),
                    DocBlock.Title("RAPPORT DE FIN DE STAGE ACADÉMIQUE", 1),
                    DocBlock.Callout(
                        title = "THÈME OFFICIEL DU STAGE",
                        text = student.theme.uppercase(),
                        accentColorHex = "#007AFF"
                    ),
                    DocBlock.KeyValueList(
                        listOf(
                            "Nom & Prénoms de l'Étudiant" to student.studentName,
                            "Filière & Spécialité" to student.filiereOption,
                            "Entreprise d'Accueil" to "$cName\n${if (company.showAddress) company.companyFullTitle else "Abidjan, Côte d'Ivoire"}",
                            "Encadreur Professionnel (Maître de stage)" to student.internshipTutor,
                            "Encadreur Académique (Tuteur école)" to student.academicTutor,
                            "Année Universitaire" to student.academicYear
                        )
                    ),
                    DocBlock.Paragraph("Document technique et managérial conforme aux normes académiques (Times New Roman 12 pt, interligne 1.5, marges 2.5 cm, alinéas 1.25 cm).")
                )
            )
        )

        // Page 2: Les Pages de Remerciements
        pages.add(
            ReportPage(
                pageNumber = 2,
                chapterTitle = "PHASE 1 : PAGES PRÉLIMINAIRES",
                pageTitle = "Dédicaces et Remerciements",
                blocks = listOf(
                    DocBlock.Title("Dédicaces", 2),
                    DocBlock.Paragraph("Je dédie respectueusement ce rapport de stage à mes parents pour leur soutien constant tout au long de mon cursus, ainsi qu'au corps professoral de ${student.schoolName} pour la qualité et la rigueur des enseignements dispensés."),
                    DocBlock.Title("Remerciements Institutionnels & Nominatifs", 2),
                    DocBlock.Paragraph("Nous tenons à exprimer notre profonde gratitude à la direction générale de la société $cName pour nous avoir accueilli au sein de ses installations industrielles."),
                    DocBlock.Paragraph("Nos remerciements les plus sincères s'adressent particulièrement à :"),
                    DocBlock.KeyValueList(
                        listOf(
                            "M. KOUASSI JEAN YVES" to "Gérant d'ICC Corporate, pour son ouverture d'esprit et sa bienveillance.",
                            "M. BROU PRI" to "Chef d'Atelier et Maître de stage, pour sa disponibilité quotidienne, sa rigueur méthodologique et son suivi attentif.",
                            "M. FARÈS" to "Superviseur Soudure et Chaudronnerie, pour ses explications pratiques et son encadrement technique sur les postes de travail.",
                            "L'Équipe des Techniciens" to "Tourneurs, soudeurs et monteurs de l'atelier pour leur accueil fraternel et leur esprit de partage."
                        )
                    )
                )
            )
        )

        // Page 3: Le Sommaire / La Table des Matières
        pages.add(
            ReportPage(
                pageNumber = 3,
                chapterTitle = "PHASE 1 : PAGES PRÉLIMINAIRES",
                pageTitle = "Sommaire / Table des Matières Paginée",
                blocks = listOf(
                    DocBlock.Table(
                        headers = listOf("Structure du Document", "Page"),
                        rows = listOf(
                            listOf("PHASE 1 : STRUCTURE INSTITUTIONNELLE ET PRÉLIMINAIRES", "Pages 1 à 5"),
                            listOf("  • Page de Garde officielle & Remerciements nominatifs", "Pages 1 - 2"),
                            listOf("  • Sommaire, Listes des Tableaux/Figures et Glossaire technique", "Pages 3 - 5"),
                            listOf("PHASE 2 : CORPS DU RAPPORT (LE CŒUR ACADÉMIQUE)", "Pages 6 à 24"),
                            listOf("  • Introduction Générale, Problématique & Résumé Exécutif", "Page 6"),
                            listOf("  • Chapitre 1 : Présentation de l'Entreprise d'Accueil ICC", "Pages 7 - 9"),
                            listOf("  • Chapitre 2 : Immersion et Déroulement du Stage (Cadre Pratique)", "Pages 10 - 13"),
                            listOf("  • Chapitre 3 : Étude Technique et Mission Principale (Thème du stage)", "Pages 14 - 20"),
                            listOf("  • Chapitre 4 : Bilan des Compétences, Difficultés & Maintenance", "Pages 21 - 24"),
                            listOf("PHASE 3 : LES CONCLUSIONS ET ANNEXES", "Pages 25 à 28"),
                            listOf("  • Conclusion Générale & Réponse à la Problématique", "Page 25"),
                            listOf("  • Références Bibliographiques & Normes Internationales", "Page 26"),
                            listOf("  • Table des Annexes & Fiches Techniques de Fabrication", "Page 27"),
                            listOf("  • Grille d'Évaluation de Fin de Stage & Signatures Officielles", "Page 28")
                        )
                    )
                )
            )
        )

        // Page 4: Les Listes des Tableaux et des Figures (Illustrations)
        pages.add(
            ReportPage(
                pageNumber = 4,
                chapterTitle = "PHASE 1 : PAGES PRÉLIMINAIRES",
                pageTitle = "Listes des Tableaux et des Figures",
                blocks = listOf(
                    DocBlock.Title("Liste Numérotée des Tableaux", 2),
                    DocBlock.KeyValueList(
                        listOf(
                            "Tableau 1" to "Fiche signalétique et statut juridique de $cName (Page 7)",
                            "Tableau 2" to "Parc machines et moyens logistiques d'exploitation (Page 9)",
                            "Tableau 3" to "Journal de bord des interventions chantiers : Système Lieu - Cause (Page 11)",
                            "Tableau 4" to "${filiereContent.cdcfTitle} (Page 14)",
                            "Tableau 5" to "${filiereContent.materialsTitle} (Page 15)",
                            "Tableau 6" to "${filiereContent.componentsTitle} (Page 17)",
                            "Tableau 7" to "Plan périodique de maintenance préventive et contrôle qualité (Page 23)"
                        )
                    ),
                    DocBlock.Title("Liste Numérotée des Figures & Illustrations", 2),
                    DocBlock.KeyValueList(
                        listOf(
                            "Figure 1" to "Sceau officiel et logo SLM RAPPORT BUILDER SMARTLY (Page 1)",
                            "Figure 2" to "Organigramme structurel et hiérarchique de $cName (Page 8)",
                            "Figure 3" to "Diagramme d'analyse fonctionnelle descendante FAST / Schéma de principe (Page 14)",
                            "Figure 4" to "Modélisation technique et calculs dimensionnels du système (Page 16)",
                            "Figure 5" to "Coupe technologique et détails des organes clés (Page 17)",
                            "Figure 6" to "Méthodologie opératoire et protection de sécurité (Page 18)",
                            "Figure 7" to "Plan d'ensemble 2D / Schéma synoptique côté de l'installation (Page 27)"
                        )
                    )
                )
            )
        )

        // Page 5: Le Glossaire / Sigles et Abréviations
        pages.add(
            ReportPage(
                pageNumber = 5,
                chapterTitle = "PHASE 1 : PAGES PRÉLIMINAIRES",
                pageTitle = "Glossaire Technique & Abréviations",
                blocks = listOf(
                    DocBlock.Paragraph("Ce glossaire explicite les principaux sigles professionnels, normes industrielles et abréviations techniques employés dans le présent mémoire :"),
                    DocBlock.KeyValueList(
                        FiliereContentResolver.resolveGlossary(filiereType, company)
                    )
                )
            )
        )

        // ==========================================
        // PHASE 2 : LE CORPS DU RAPPORT (LE CŒUR ACADÉMIQUE)
        // ==========================================

        // Page 6: Introduction Générale & Résumé Exécutif
        pages.add(
            ReportPage(
                pageNumber = 6,
                chapterTitle = "PHASE 2 : CŒUR ACADÉMIQUE",
                pageTitle = "Introduction Générale & Problématique",
                blocks = listOf(
                    DocBlock.Callout(
                        title = "RÉSUMÉ EXÉCUTIF DU RAPPORT (POINTS CLÉS)",
                        text = FiliereContentResolver.resolveExecutiveSummary(filiereType, student.theme, cName),
                        accentColorHex = "#007AFF"
                    ),
                    DocBlock.Paragraph("Dans les pays en forte expansion économique comme la Côte d'Ivoire, la maîtrise des technologies d'ingénierie dans le domaine '${filiereContent.filiereCategory}' constitue une exigence prioritaire de sûreté, de productivité et de compétitivité industrielle."),
                    DocBlock.Callout(
                        title = "PROBLÉMATIQUE CENTRALE DE RECHERCHE",
                        text = "« Comment l'étude approfondie, le dimensionnement normé et la réalisation pratique de : [${student.theme}] permettent-ils d'apporter une solution pérenne, économique et conforme aux exigences industrielles de $cName ? »",
                        accentColorHex = "#7C3AED"
                    ),
                    DocBlock.Paragraph("Pour répondre méthodiquement à cette problématique, notre rapport s'articule autour de quatre chapitres fondamentaux : la présentation de l'entreprise d'accueil (Chapitre 1), le cadre pratique de déroulement du stage (Chapitre 2), l'étude technique approfondie et la mise en œuvre (Chapitre 3), et enfin le bilan des compétences, difficultés surmontées et plan de maintenance préventive (Chapitre 4).")
                )
            )
        )

        // Page 7: Chapitre 1 - Identité et Historique de l'Entreprise
        val legalForm = if (company.isIccDetected) "Société à Responsabilité Limitée (SARL)" else "Société Industrielle & Commerciale"
        val companyAddr = if (company.showAddress) company.companyAddress.ifBlank { "Abidjan, Côte d'Ivoire" } else "Abidjan, Côte d'Ivoire"
        val rccmVal = if (company.showRccmBank) company.rccmBank.ifBlank { "Enregistré au RCCM" } else "Enregistré"

        pages.add(
            ReportPage(
                pageNumber = 7,
                chapterTitle = "CHAPITRE 1 : PRÉSENTATION DE L'ENTREPRISE",
                pageTitle = "Identité, Statut Juridique et Historique",
                blocks = listOf(
                    DocBlock.Paragraph("La société $cName s'est imposée comme un acteur de premier plan dans son secteur d'activité (${company.activities.ifBlank { "ingénierie et prestations industrielles" }}) sur l'ensemble du territoire."),
                    DocBlock.Table(
                        headers = listOf("Élément Réglementaire", "Spécification Officielle"),
                        rows = listOf(
                            listOf("Dénomination Commerciale", cName),
                            listOf("Raison Sociale Complète", company.companyFullTitle.ifBlank { cName }),
                            listOf("Implantation / Adresse", companyAddr),
                            listOf("Forme Juridique & Statut", legalForm),
                            listOf("Numéro RCCM & Fiscalité", rccmVal),
                            listOf("Secteur & Activités", company.activities.ifBlank { "Activités techniques et industrielles" }),
                            listOf("Direction Générale", if (company.showWorkshopContact) company.contactGerant.ifBlank { "Direction Générale" } else "Direction Générale"),
                            listOf("Responsable Technique / Atelier", if (company.showWorkshopContact) company.contactAtelier.ifBlank { student.internshipTutor } else "Direction Technique")
                        )
                    ),
                    DocBlock.Paragraph("Créée pour répondre aux besoins grandissants de performance technique et d'équipements fiables, l'entreprise allie savoir-faire pratique et rigueur des méthodes d'ingénierie moderne.")
                )
            )
        )

        // Page 8: Chapitre 1 - Organisation Structurelle & Organigramme
        val dirTitle = if (company.isIccDetected) "Direction Générale & Commerciale (M. KOUASSI JEAN YVES)" else "Direction Générale (${company.contactGerant.ifBlank { "Direction" }})"
        val techTitle = if (company.isIccDetected) "Chef d'Atelier Principal & Logistique (M. BROU PRI)" else "Direction Technique & Production (${company.contactAtelier.ifBlank { student.internshipTutor }})"

        pages.add(
            ReportPage(
                pageNumber = 8,
                chapterTitle = "CHAPITRE 1 : PRÉSENTATION DE L'ENTREPRISE",
                pageTitle = "Organisation Structurelle et Organigramme",
                blocks = listOf(
                    DocBlock.Paragraph("L'organisation de $cName repose sur une structure hiérarchique souple et réactive favorisant la transmission directe des consignes techniques :"),
                    DocBlock.Callout(
                        title = dirTitle,
                        text = "Pilotage stratégique, négociation des marchés, relations clients et gestion administrative et financière."
                    ),
                    DocBlock.Callout(
                        title = techTitle,
                        text = "Planification des opérations, approvisionnement en composants et matières d'œuvre, coordination des équipes et contrôle qualité final."
                    ),
                    DocBlock.Callout(
                        title = "Pôle Ingénierie & Métiers Spécialisés",
                        text = "Supervision technique des procédés de fabrication et maintenance, qualification des modes opératoires et formation des stagiaires."
                    ),
                    DocBlock.Callout(
                        title = "Équipes Mobiles de Chantiers et Interventions Extérieures",
                        text = "Pose sur site, installations d'équipements, maintenance préventive et interventions de dépannage rapide."
                    )
                )
            )
        )

        // Page 9: Chapitre 1 - Activités et Moyens Logistiques/Techniques
        pages.add(
            ReportPage(
                pageNumber = 9,
                chapterTitle = "CHAPITRE 1 : PRÉSENTATION DE L'ENTREPRISE",
                pageTitle = "Activités et Parc Machines de l'Atelier",
                blocks = listOf(
                    DocBlock.Paragraph("La société $cName déploie son expertise dans ses domaines d'activité : ${company.activities.ifBlank { "Ingénierie, métallurgie et maintenance" }}."),
                    DocBlock.Table(
                        headers = listOf("Équipement de Production", "Caractéristiques Techniques", "Usage Opérationnel"),
                        rows = listOf(
                            listOf("Tours parallèles universels", "Entre-pointes 1500 mm, broche Ø 50 mm", "Tournage des arbres de guidage, alésage et chanfreinage"),
                            listOf("Scie à ruban industrielle", "Capacité de coupe Ø 250 mm, angle orientable", "Débitage propre et précis des tubes et profilés d'acier"),
                            listOf("Postes à souder onduleurs MMA", "Courant continu régulé jusqu'à 250 A", "Soudage de structure à l'électrode enrobée rutile"),
                            listOf("Postes semi-automatiques MAG", "Fil continu Ø 1.0 mm sous mélange Argon/CO2", "Assemblage haute cadence des tôles et platines"),
                            listOf("Oxycoupeurs et découpeur plasma", "Capacité de coupe jusqu'à 30 mm", "Découpe des platines de base et goussets de renfort"),
                            listOf("Cabine de peinture ventilée", "Pistolet basse pression HVLP", "Application du wash-primer et laques polyuréthanes")
                        )
                    )
                )
            )
        )

        // Page 10: Chapitre 2 - Chronologie du Stage et Intégration
        pages.add(
            ReportPage(
                pageNumber = 10,
                chapterTitle = "CHAPITRE 2 : IMMERSION & DÉROULEMENT DU STAGE",
                pageTitle = "Chronologie du Stage et Intégration",
                blocks = listOf(
                    DocBlock.Paragraph("L'insertion au sein des équipes de l'atelier s'est déroulée selon un programme d'immersion progressive garantissant l'appropriation des consignes de sécurité et la montée en compétences :"),
                    DocBlock.Callout(
                        title = "Phase d'Accueil et Formation Sécurité HSE (Semaine 1)",
                        text = "Prise de contact avec la direction, visite commentée des postes de travail par M. BROU PRI, dotation des Équipements de Protection Individuelle (EPI : chaussures coquées S3, lunettes, tablier cuir et gants) et sensibilisation aux risques électriques et thermiques."
                    ),
                    DocBlock.Callout(
                        title = "Rotation sur les Postes d'Atelier (Semaines 2 à 4)",
                        text = "Apprentissage du traçage sur tôles épaisses, débitage des profilés à la tronçonneuse et à la scie, ébavurage au lapidaire et pointage d'assemblage sous la supervision de M. FARÈS."
                    ),
                    DocBlock.Callout(
                        title = "Phase de Réalisation du Projet Thématique (Semaines 5 et suivantes)",
                        text = "Conception et fabrication autonome supervisée de la barrière levante de sécurité, usinage de l'arbre pivot et montage des paliers à roulements."
                    )
                )
            )
        )

        // Page 11: Chapitre 2 - Présentation des Chantiers et Sites (Système Lieu - Cause)
        val lieuRows = if (locations.isNotEmpty()) {
            locations.mapIndexed { idx, item ->
                listOf(
                    "Lieu ${idx + 1} : ${item.locationName.ifBlank { "Site d'intervention" }}",
                    "${item.daysDuration.ifBlank { "1" }} jour(s)",
                    item.cause.ifBlank { "Intervention technique, pose et maintenance" }
                )
            }
        } else {
            listOf(
                listOf("Atelier Central Yopougon Figayo", "Temps complet", "Débitage, tournage, soudage et assemblage de la barrière"),
                listOf("Parc d'Essais Métalliques ICC", "En continu", "Essais dynamiques de manœuvre et étalonnage du contrepoids"),
                listOf("Plateforme de Déploiement Client", "Phase finale", "Scellement des platines et réception technique en charge")
            )
        }

        pages.add(
            ReportPage(
                pageNumber = 11,
                chapterTitle = "CHAPITRE 2 : IMMERSION & DÉROULEMENT DU STAGE",
                pageTitle = "Chantiers et Sites d'Intervention (Système Lieu - Cause)",
                blocks = listOf(
                    DocBlock.Paragraph("Conformément au principe méthodologique « un lieu d'intervention = une cause technique spécifique », le tableau ci-dessous synthétise la cartographie des missions effectuées :"),
                    DocBlock.Table(
                        headers = listOf("Lieu / Chantier Visité", "Durée Allouée", "Cause / Objectif Technique Précis"),
                        rows = lieuRows
                    ),
                    DocBlock.Paragraph("Cette organisation rigoureuse garantit une parfaite traçabilité entre la préparation en atelier et les contraintes réelles constatées sur le terrain.")
                )
            )
        )

        // Page 12: Chapitre 2 - Détail des Interventions et Logistique Terrain
        val fieldInterventions = FiliereContentResolver.resolveFieldTasks(filiereType)
        pages.add(
            ReportPage(
                pageNumber = 12,
                chapterTitle = "CHAPITRE 2 : IMMERSION & DÉROULEMENT DU STAGE",
                pageTitle = "Interventions Extérieures et Logistique Terrain",
                blocks = buildList {
                    add(DocBlock.Paragraph("Les interventions hors atelier et sur sites opérationnels ont permis d'appréhender la réalité des chantiers et les contraintes concrètes d'environnement :"))
                    fieldInterventions.forEach { (title, desc) ->
                        add(DocBlock.Callout(title = title, text = desc))
                    }
                }
            )
        )

        // Page 13: Chapitre 2 - Synthèse des Tâches Exécutées en Atelier
        pages.add(
            ReportPage(
                pageNumber = 13,
                chapterTitle = "CHAPITRE 2 : IMMERSION & DÉROULEMENT DU STAGE",
                pageTitle = "Synthèse des Tâches Exécutées en Atelier & Laboratoire",
                blocks = listOf(
                    DocBlock.Paragraph("Durant la période passée dans les installations techniques de $cName, les activités pratiques et d'ingénierie ont couvert l'ensemble de la chaîne de travail :"),
                    DocBlock.KeyValueList(
                        FiliereContentResolver.resolveWorkshopTasks(filiereType)
                    )
                )
            )
        )

        // Page 14: Chapitre 3 - Cahier des Charges Fonctionnel (CdCF)
        pages.add(
            ReportPage(
                pageNumber = 14,
                chapterTitle = "CHAPITRE 3 : ÉTUDE TECHNIQUE & MISSION PRINCIPALE",
                pageTitle = filiereContent.cdcfTitle,
                blocks = listOf(
                    DocBlock.Paragraph(filiereContent.cdcfIntro),
                    DocBlock.Table(
                        headers = listOf("Fonction", "Désignation Fonctionnelle", "Critère d'Appréciation", "Niveau d'Exigence"),
                        rows = filiereContent.cdcfTableRows
                    )
                )
            )
        )

        // Page 15: Chapitre 3 - Méthodologie de Conception & Matériaux
        pages.add(
            ReportPage(
                pageNumber = 15,
                chapterTitle = "CHAPITRE 3 : ÉTUDE TECHNIQUE & MISSION PRINCIPALE",
                pageTitle = filiereContent.materialsTitle,
                blocks = buildList {
                    add(DocBlock.Paragraph(filiereContent.materialsIntro))
                    add(DocBlock.Table(
                        headers = listOf("Élément / Sous-système", "Spécification / Nuance", "Critère Clé", "Justification Technique"),
                        rows = filiereContent.materialsTableRows
                    ))
                    if (geminiData != null && geminiData.materiaux.isNotEmpty()) {
                        add(DocBlock.Callout(
                            title = "Spécifications Complémentaires Validées (IA Gemini)",
                            text = geminiData.materiaux.joinToString("\n• ", prefix = "• "),
                            accentColorHex = "#7C3AED"
                        ))
                    }
                }
            )
        )

        // Page 16: Chapitre 3 - Étude Cinématique & Calculs RDM
        pages.add(
            ReportPage(
                pageNumber = 16,
                chapterTitle = "CHAPITRE 3 : ÉTUDE TECHNIQUE & MISSION PRINCIPALE",
                pageTitle = filiereContent.calculationsTitle,
                blocks = buildList {
                    filiereContent.calculationsBlocks.forEach { blockText ->
                        if (blockText.contains("sécurité") || blockText.contains("admissible") || blockText.contains("limite")) {
                            add(DocBlock.Callout(
                                title = "Vérification et Validation Normative",
                                text = blockText,
                                accentColorHex = "#007AFF"
                            ))
                        } else {
                            add(DocBlock.Paragraph(blockText))
                        }
                    }
                }
            )
        )

        // Page 17: Chapitre 3 - Sélection et Intégration des Composants Clés
        pages.add(
            ReportPage(
                pageNumber = 17,
                chapterTitle = "CHAPITRE 3 : ÉTUDE TECHNIQUE & MISSION PRINCIPALE",
                pageTitle = filiereContent.componentsTitle,
                blocks = buildList {
                    add(DocBlock.Paragraph(filiereContent.componentsIntro))
                    add(DocBlock.Table(
                        headers = listOf("Critère / Composant", "Solution Retenue", "Avantage Décisif"),
                        rows = filiereContent.componentsTableRows
                    ))
                    if (geminiData != null && geminiData.composants.isNotEmpty()) {
                        add(DocBlock.Callout(
                            title = "Composants & Références Recommandés (IA Gemini)",
                            text = geminiData.composants.joinToString("\n• ", prefix = "• "),
                            accentColorHex = "#00F5FF"
                        ))
                    }
                }
            )
        )

        // Page 18: Chapitre 3 - Processus de Réalisation Pas à Pas en Atelier / Chantier
        pages.add(
            ReportPage(
                pageNumber = 18,
                chapterTitle = "CHAPITRE 3 : ÉTUDE TECHNIQUE & MISSION PRINCIPALE",
                pageTitle = filiereContent.realizationStepsTitle,
                blocks = buildList {
                    add(DocBlock.Paragraph(filiereContent.realizationIntro))
                    filiereContent.realizationSteps.forEach { step ->
                        add(DocBlock.Callout(
                            title = step.first,
                            text = step.second,
                            accentColorHex = "#3B82F6"
                        ))
                    }
                }
            )
        )

        // Page 19: Chapitre 3 - Ajustage, Montage et Finition
        pages.add(
            ReportPage(
                pageNumber = 19,
                chapterTitle = "CHAPITRE 3 : ÉTUDE TECHNIQUE & MISSION PRINCIPALE",
                pageTitle = filiereContent.finishingTitle,
                blocks = buildList {
                    add(DocBlock.Paragraph(filiereContent.finishingIntro))
                    filiereContent.finishingSteps.forEach { step ->
                        add(DocBlock.Callout(
                            title = step.first,
                            text = step.second,
                            accentColorHex = "#10B981"
                        ))
                    }
                }
            )
        )

        // Page 20: Chapitre 3 - Contrôle Qualité, Métrologie et Normes
        pages.add(
            ReportPage(
                pageNumber = 20,
                chapterTitle = "CHAPITRE 3 : ÉTUDE TECHNIQUE & MISSION PRINCIPALE",
                pageTitle = filiereContent.qualityTitle,
                blocks = listOf(
                    DocBlock.Paragraph(filiereContent.qualityIntro),
                    DocBlock.KeyValueList(filiereContent.qualityItems)
                )
            )
        )

        // Page 21: Chapitre 4 - Bilan des Compétences Acquises
        pages.add(
            ReportPage(
                pageNumber = 21,
                chapterTitle = "CHAPITRE 4 : BILAN, APPORTS & ANALYSE CRITIQUE",
                pageTitle = "Bilan des Compétences Techniques et Professionnelles",
                blocks = listOf(
                    DocBlock.Paragraph("Cette immersion pratique au sein de $cName a permis de consolider un ensemble d'aptitudes techniques, relationnelles et méthodologiques indispensables à un futur cadre de la filière ${filiereContent.filiereCategory} :"),
                    DocBlock.KeyValueList(filiereContent.skillsAcquired)
                )
            )
        )

        // Page 22: Chapitre 4 - Difficultés Rencontrées et Solutions Pratiques
        pages.add(
            ReportPage(
                pageNumber = 22,
                chapterTitle = "CHAPITRE 4 : BILAN, APPORTS & ANALYSE CRITIQUE",
                pageTitle = "Difficultés Rencontrées et Solutions Techniques",
                blocks = buildList {
                    add(DocBlock.Paragraph("Tout travail technique de terrain confronte l'étudiant à des aléas physiques et organisationnels concrets :"))
                    filiereContent.challengesAndSolutions.forEach { (title, desc) ->
                        add(DocBlock.Callout(
                            title = title,
                            text = desc,
                            accentColorHex = "#F59E0B"
                        ))
                    }
                }
            )
        )

        // Page 23: Chapitre 4 - Plan de Maintenance Préventive et Diagnostic
        pages.add(
            ReportPage(
                pageNumber = 23,
                chapterTitle = "CHAPITRE 4 : BILAN, APPORTS & ANALYSE CRITIQUE",
                pageTitle = "Plan de Maintenance Préventive et Diagnostic Opérationnel",
                blocks = buildList {
                    add(DocBlock.Paragraph("Pour garantir la pérennité et la continuité de service des ouvrages et équipements sur le long terme, un plan de maintenance a été formalisé :"))
                    add(DocBlock.Table(
                        headers = listOf("Fréquence", "Organe / Système Clé", "Opération Préventive"),
                        rows = filiereContent.maintenanceRows
                    ))
                    if (geminiData != null && geminiData.securite.isNotEmpty()) {
                        add(DocBlock.Callout(
                            title = "Consignes de Sécurité & HSE (Recherche IA Gemini)",
                            text = geminiData.securite.joinToString("\n• ", prefix = "• "),
                            accentColorHex = "#10B981"
                        ))
                    }
                }
            )
        )

        // Page 24: Chapitre 4 - Recommandations Stratégiques et Valeur Ajoutée
        pages.add(
            ReportPage(
                pageNumber = 24,
                chapterTitle = "CHAPITRE 4 : BILAN, APPORTS & ANALYSE CRITIQUE",
                pageTitle = "Recommandations Stratégiques et Perspectives d'Avenir",
                blocks = buildList {
                    add(DocBlock.Paragraph("En retour d'expérience, nous formulons à l'attention de la direction de $cName des axes d'amélioration opérationnels à forte valeur ajoutée :"))
                    filiereContent.strategicRecommendations.forEach { (title, desc) ->
                        add(DocBlock.Callout(
                            title = title,
                            text = desc,
                            accentColorHex = "#8B5CF6"
                        ))
                    }
                }
            )
        )

        // ==========================================
        // PHASE 3 : LES CONCLUSIONS ET ANNEXES
        // ==========================================

        // Page 25: Conclusion Générale du Rapport de Stage
        val conclusionParagraphs = FiliereContentResolver.resolveGeneralConclusion(filiereType, student.theme, cName)
        pages.add(
            ReportPage(
                pageNumber = 25,
                chapterTitle = "PHASE 3 : CONCLUSIONS ET ANNEXES",
                pageTitle = "Conclusion Générale du Mémoire de Stage",
                blocks = buildList {
                    conclusionParagraphs.forEach { text ->
                        add(DocBlock.Paragraph(text))
                    }
                    add(
                        DocBlock.SignOff(
                            leftTitle = "L'Étudiant Stagiaire (Rapporteur)",
                            leftName = student.studentName,
                            rightTitle = "Le Maître de Stage (Encadreur Technique)",
                            rightName = student.internshipTutor
                        )
                    )
                }
            )
        )

        // Page 26: Références Bibliographiques & Normes
        val biblioEntries = FiliereContentResolver.resolveBibliography(filiereType)
        pages.add(
            ReportPage(
                pageNumber = 26,
                chapterTitle = "PHASE 3 : CONCLUSIONS ET ANNEXES",
                pageTitle = "Références Bibliographiques et Normes Officielles",
                blocks = buildList {
                    add(DocBlock.Title("Ouvrages Fondamentaux & Manuels Techniques (${filiereContent.filiereCategory})", 2))
                    biblioEntries.forEach { ref ->
                        add(DocBlock.Paragraph(ref))
                    }
                    if (geminiData != null && geminiData.normes.isNotEmpty()) {
                        add(DocBlock.Callout(
                            title = "Normes & Référentiels Validés par l'IA Gemini",
                            text = geminiData.normes.joinToString("\n• ", prefix = "• "),
                            accentColorHex = "#007AFF"
                        ))
                    }
                }
            )
        )

        // Page 27: Table des Annexes & Fiches Techniques
        pages.add(
            ReportPage(
                pageNumber = 27,
                chapterTitle = "PHASE 3 : CONCLUSIONS ET ANNEXES",
                pageTitle = "Table des Annexes et Documents d'Ingénierie",
                blocks = listOf(
                    DocBlock.Paragraph("Les documents et plans techniques ci-dessous complètent l'analyse théorique et décrivent les spécifications de mise en œuvre :"),
                    DocBlock.KeyValueList(
                        FiliereContentResolver.resolveAnnexes(filiereType)
                    ),
                    DocBlock.Figure(
                        caption = "Schéma de principe et synoptique d'ensemble validé pour la filière ${filiereContent.filiereCategory}",
                        fallbackIcon = "build"
                    )
                )
            )
        )

        // Page 28: Procès-Verbal de Fin de Stage & Signatures Officielles
        pages.add(
            ReportPage(
                pageNumber = 28,
                chapterTitle = "PHASE 3 : CONCLUSIONS ET ANNEXES",
                pageTitle = "Procès-Verbal de Fin de Stage & Évaluation",
                blocks = listOf(
                    DocBlock.Paragraph("Le présent document atteste de la réalisation effective et intégrale des 28 pages réglementaires du stage de fin d'études au sein de l'entreprise $cName."),
                    DocBlock.Table(
                        headers = listOf("Critère d'Évaluation Académique", "Appréciation", "Note / 20"),
                        rows = listOf(
                            listOf("Assiduité, ponctualité et respect des consignes HSE", "Excellente discipline opérationnelle", "19 / 20"),
                            listOf("Rigueur technique et compétences (${filiereContent.filiereCategory})", "Excellente maîtrise théorique et terrain", "18.5 / 20"),
                            listOf("Esprit d'initiative et résolution de problèmes", "Grande autonomie sur le projet", "18 / 20"),
                            listOf("Qualité de la rédaction et structure du rapport", "Méthodologie réglementaire 28 pages scrupuleuse", "19 / 20")
                        )
                    ),
                    DocBlock.Callout(
                        title = "AVIS GLOBAL DE LA COMMISSION DE STAGE",
                        text = "Stage hautement concluant avec félicitations du jury pour la maîtrise des compétences en ${filiereContent.filiereCategory} et la réalisation des objectifs fixés pour le thème « ${student.theme} ».",
                        accentColorHex = "#10B981"
                    ),
                    DocBlock.SignOff(
                        leftTitle = "L'Encadreur Académique (École)\n${student.schoolName}",
                        leftName = student.academicTutor,
                        rightTitle = "Le Maître de Stage & Direction (Entreprise)\n$cName",
                        rightName = "${student.internshipTutor}\nCachet & Signature Officiels"
                    )
                )
            )
        )

        return pages
    }

    fun generateWordDocHtml(
        company: CompanyConfig,
        student: StudentConfig,
        locations: List<LocationItem>,
        finalSummary: String,
        illustrations: List<ImportedImage>,
        context: Context,
        geminiData: com.example.service.GeminiEnrichmentResult? = null
    ): String {
        val pages = build28Pages(company, student, locations, finalSummary, illustrations, geminiData)
        val companyName = company.inputName.ifBlank { "ICC CORPORATE" }

        val sb = StringBuilder()
        // Word Office HTML format with \uFEFF BOM for perfect UTF-8 recognition
        sb.append("\uFEFF")
        sb.append("""
            <!DOCTYPE html>
            <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
            <head>
            <meta charset="utf-8">
            <title>Rapport de Stage - $companyName - ${student.studentName}</title>
            <!--[if gte mso 9]>
            <xml>
            <w:WordDocument>
            <w:View>Print</w:View>
            <w:Zoom>100</w:Zoom>
            <w:DoNotOptimizeForBrowser/>
            </w:WordDocument>
            </xml>
            <![endif]-->
            <style>
            @page {
                size: 21.0cm 29.7cm;
                margin: 2.5cm 2.5cm 2.5cm 2.5cm;
                mso-page-orientation: portrait;
                @top-center {
                    content: "$companyName - ${student.studentName}";
                    font-family: 'Times New Roman', serif;
                    font-size: 9pt;
                    border-bottom: 1px solid #007AFF;
                }
                @bottom-center {
                    content: "Page " counter(page) " - Rapport de stage réglementaire";
                    font-family: 'Times New Roman', serif;
                    font-size: 9pt;
                    color: #555555;
                }
            }
            body {
                font-family: 'Times New Roman', Times, serif;
                font-size: 12pt;
                line-height: 1.5;
                color: #111111;
                margin: 0;
                padding: 0;
            }
            p {
                margin: 0 0 10pt 0;
                text-align: justify;
                text-indent: 1.25cm;
                line-height: 1.5;
            }
            h1 {
                font-family: 'Times New Roman', serif;
                font-size: 18pt;
                color: #0b1f44;
                text-align: center;
                text-transform: uppercase;
                margin: 15pt 0 15pt 0;
                font-weight: bold;
            }
            h2 {
                font-family: 'Times New Roman', serif;
                font-size: 14pt;
                color: #007AFF;
                border-bottom: 1.5pt solid #007AFF;
                padding-bottom: 3pt;
                margin: 14pt 0 8pt 0;
                font-weight: bold;
            }
            .header-bar {
                border-bottom: 1.5pt solid #007AFF;
                padding-bottom: 4pt;
                margin-bottom: 15pt;
                font-size: 9pt;
                color: #333333;
                text-transform: uppercase;
                display: flex;
                justify-content: space-between;
            }
            .page-break {
                page-break-before: always;
                mso-break-type: page-break;
                clear: both;
            }
            .doc-page {
                min-height: 24.7cm;
                padding: 0;
                box-sizing: border-box;
            }
            .callout-box {
                background: #f0f7ff;
                border-left: 4.5pt solid #007AFF;
                border-radius: 6pt;
                padding: 10pt 14pt;
                margin: 12pt 0;
            }
            .callout-title {
                font-weight: bold;
                color: #0b1f44;
                font-size: 11pt;
                margin-bottom: 4pt;
                text-transform: uppercase;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 12pt 0;
                font-size: 11pt;
            }
            th {
                background: #007AFF;
                color: #ffffff;
                font-weight: bold;
                padding: 7pt 9pt;
                border: 1pt solid #0056b3;
                text-align: left;
            }
            td {
                padding: 6pt 9pt;
                border: 1pt solid #cccccc;
                text-align: left;
            }
            tr:nth-child(even) td {
                background: #f9fbfd;
            }
            .footer-bar {
                margin-top: 20pt;
                border-top: 1pt solid #dddddd;
                padding-top: 5pt;
                font-size: 9pt;
                color: #666666;
                text-align: center;
            }
            .badge-couv {
                background: #007AFF;
                color: #ffffff;
                padding: 8pt 16pt;
                font-size: 14pt;
                font-weight: bold;
                text-align: center;
                border-radius: 6pt;
                margin: 20pt 0;
            }
            .signoff-table {
                width: 100%;
                margin-top: 40pt;
                border: none;
            }
            .signoff-table td {
                border: none;
                width: 50%;
                padding: 15pt;
                vertical-align: top;
            }
            </style>
            </head>
            <body>
        """.trimIndent())

        pages.forEachIndexed { index, page ->
            if (index > 0) {
                sb.append("<div class='page-break'></div>\n")
            }
            sb.append("<div class='doc-page'>\n")
            sb.append("<div class='header-bar'>")
            sb.append("<span>${companyName} - ${student.studentName}</span>")
            sb.append("<span>RAPPORT DE STAGE RÉGLEMENTAIRE</span>")
            sb.append("</div>\n")

            sb.append("<h1>${page.pageTitle}</h1>\n")

            if (index == 0) {
                try {
                    val inputStream = context.resources.openRawResource(com.example.R.drawable.slm_logo)
                    val bytes = inputStream.readBytes()
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    sb.append("<div style='text-align:center; margin:15pt 0;'><img src='data:image/jpeg;base64,$base64' style='width:120px; height:120px; border-radius:50%; border:2.5pt solid #007AFF;' /><br><span style='font-size:9pt; color:#007AFF; font-weight:bold;'>SLM RAPPORT BUILDER SMARTLY - HOMOLOGATION OFFICIELLE</span></div>\n")
                } catch (_: Exception) {}
            }

            for (block in page.blocks) {
                when (block) {
                    is DocBlock.Title -> {
                        if (block.level == 1) {
                            sb.append("<h2>${block.text}</h2>\n")
                        } else {
                            sb.append("<h3 style='color:#0b1f44; margin:10pt 0 4pt 0;'>${block.text}</h3>\n")
                        }
                    }
                    is DocBlock.Paragraph -> {
                        sb.append("<p>${block.text.replace("\n", "<br>")}</p>\n")
                    }
                    is DocBlock.Callout -> {
                        sb.append("<div class='callout-box' style='border-left-color:${block.accentColorHex};'>\n")
                        sb.append("<div class='callout-title'>${block.title}</div>\n")
                        sb.append("<div style='font-size:11pt; line-height:1.4;'>${block.text.replace("\n", "<br>")}</div>\n")
                        sb.append("</div>\n")
                    }
                    is DocBlock.Table -> {
                        sb.append("<table><thead><tr>\n")
                        block.headers.forEach { h -> sb.append("<th>$h</th>\n") }
                        sb.append("</tr></thead><tbody>\n")
                        block.rows.forEach { row ->
                            sb.append("<tr>\n")
                            row.forEach { cell -> sb.append("<td>${cell.replace("\n", "<br>")}</td>\n") }
                            sb.append("</tr>\n")
                        }
                        sb.append("</tbody></table>\n")
                    }
                    is DocBlock.KeyValueList -> {
                        sb.append("<table style='margin:10pt 0;'><tbody>\n")
                        block.items.forEach { item ->
                            sb.append("<tr><td style='width:35%; font-weight:bold; background:#f0f7ff;'>${item.first}</td><td>${item.second.replace("\n", "<br>")}</td></tr>\n")
                        }
                        sb.append("</tbody></table>\n")
                    }
                    is DocBlock.Figure -> {
                        sb.append("<div style='text-align:center; margin:15pt 0; padding:10pt; border:1px solid #ddd; background:#fafafa;'>\n")
                        sb.append("<div style='font-size:10pt; color:#666; font-style:italic;'>[Figure Technique] : ${block.caption}</div>\n")
                        sb.append("</div>\n")
                    }
                    is DocBlock.SignOff -> {
                        sb.append("<table class='signoff-table'><tr>\n")
                        sb.append("<td style='text-align:left;'><strong>${block.leftTitle}</strong><br><br><br><br><u>${block.leftName}</u></td>\n")
                        sb.append("<td style='text-align:right;'><strong>${block.rightTitle}</strong><br><br><br><br><u>${block.rightName}</u></td>\n")
                        sb.append("</tr></table>\n")
                    }
                }
            }

            sb.append("<div class='footer-bar'>Page ${page.pageNumber} / ${page.totalPages} - Rapport de stage professionnel</div>\n")
            sb.append("</div>\n")
        }

        sb.append("</body></html>")
        return sb.toString()
    }
}
