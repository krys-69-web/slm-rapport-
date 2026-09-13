package com.example.generator

import com.example.model.CompanyConfig
import com.example.model.ComplianceAuditIssue
import com.example.model.ComplianceAuditResult
import com.example.model.FiliereTechnicalContent
import com.example.model.JuryQuestion
import com.example.model.ReportPage
import com.example.model.StudentConfig

object FiliereContentResolver {

    enum class FiliereType {
        MECANIQUE_METALLURGIE,
        ELECTROTECHNIQUE_ENERGIE,
        GENIE_CIVIL_BATIMENT,
        INFORMATIQUE_RESEAUX_TELECOMS,
        LOGISTIQUE_TRANSPORT_SUPPLY,
        MINES_PETROLE_PROCEDES,
        AGROALIMENTAIRE_AGRONOMIE
    }

    fun detectFiliereType(filiere: String, theme: String): FiliereType {
        val combined = "$filiere $theme".lowercase()
        return when {
            combined.contains("electr") || combined.contains("courant") || combined.contains("energie") || combined.contains("transformateur") || combined.contains("solaire") || combined.contains("câblage") ->
                FiliereType.ELECTROTECHNIQUE_ENERGIE

            combined.contains("civil") || combined.contains("bâtiment") || combined.contains("batiment") || combined.contains("btp") || combined.contains("béton") || combined.contains("beton") || combined.contains("chantier") || combined.contains("chaussée") ->
                FiliereType.GENIE_CIVIL_BATIMENT

            combined.contains("info") || combined.contains("réseau") || combined.contains("reseau") || combined.contains("télécom") || combined.contains("telecom") || combined.contains("logiciel") || combined.contains("web") || combined.contains("cloud") || combined.contains("data") ->
                FiliereType.INFORMATIQUE_RESEAUX_TELECOMS

            combined.contains("logist") || combined.contains("transit") || combined.contains("supply") || combined.contains("port") || combined.contains("douane") || combined.contains("stock") || combined.contains("transport") ->
                FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY

            combined.contains("mine") || combined.contains("pétrole") || combined.contains("petrole") || combined.contains("hydrocarbure") || combined.contains("raffin") || combined.contains("forage") ->
                FiliereType.MINES_PETROLE_PROCEDES

            combined.contains("agro") || combined.contains("alimentaire") || combined.contains("agronomie") || combined.contains("palmier") || combined.contains("cacao") || combined.contains("hévéa") ->
                FiliereType.AGROALIMENTAIRE_AGRONOMIE

            else ->
                FiliereType.MECANIQUE_METALLURGIE
        }
    }

    fun resolveTechnicalContent(
        student: StudentConfig,
        company: CompanyConfig
    ): FiliereTechnicalContent {
        val type = detectFiliereType(student.filiereOption, student.theme)
        val comp = company.inputName.ifBlank { "L'entreprise d'accueil" }
        val themeShort = student.theme.ifBlank { "Projet technique de fin de cycle" }

        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> FiliereTechnicalContent(
                filiereCategory = "Génie Électrique & Énergie",
                cdcfTitle = "Spécifications Électriques & Cahier des Charges Fonctionnel",
                cdcfIntro = "L'installation et le dimensionnement électrique répondent aux exigences strictes de distribution et de sécurité :",
                cdcfTableRows = listOf(
                    listOf("FP1", "Alimentation continue et sécurisée des récepteurs", "Tension assignée", "400 V Triphasé / 230 V Monophasé (50 Hz)"),
                    listOf("FP2", "Protection des personnes et des biens", "Régime de neutre retenu", "Schéma TT ou TN-S avec différentiels 30 mA et déclencheurs magnéto-thermiques"),
                    listOf("FC1", "Résistance aux surtensions transitoires", "Dispositif parafoudre", "Parafoudre Type 2 coordonné Imax = 40 kA"),
                    listOf("FC2", "Compensation d'énergie réactive", "Facteur de puissance cible", "cos φ ≥ 0.93 par batterie de condensateurs automatiques"),
                    listOf("FC3", "Disponibilité de secours", "Groupe électrogène / Onduleur", "Inverseur de source motorisé avec basculement < 15 secondes"),
                    listOf("FC4", "Supervision et comptage d'énergie", "Centrale de mesure", "Mesure multicanale communicante Modbus-RTU / RS485")
                ),
                materialsTitle = "Choix et Caractérisation de l'Appareillage Électrique",
                materialsIntro = "Le matériel basse et moyenne tension a été calibré selon les normes CEI / NF C 15-100 :",
                materialsTableRows = listOf(
                    listOf("Câbles de puissance BT", "Âme cuivre U-1000 R2V (4×25 mm² + PE 16 mm²)", "Isolement 1 kV", "Faible échauffement et résistance aux ambiances tropicales"),
                    listOf("Disjoncteur général tête d'armoire", "Boîtier moulé 4P (Calibre In = 160 A)", "Icu = 36 kA sous 400V", "Pouvoir de coupure garantissant la tenue aux courts-circuits"),
                    listOf("Interrupteurs différentiels", "Sensibilité 30 mA Type A / Hi (Super-immunisé)", "In = 40 A / 63 A", "Protection contre les défauts d'isolement et courants de fuite"),
                    listOf("Armoire de distribution", "Enveloppe métallique étanche IP55 / IK09", "Acier poudré polyester", "Protection contre poussière d'atelier et projection d'eau"),
                    listOf("Jeux de barres cuivre", "Cuivre électrolytique Cu-ETP (30×5 mm)", "Conductivité > 99%", "Densité de courant admise j = 2 A/mm²")
                ),
                calculationsTitle = "Calculs Électrotechniques, Bilan de Puissance et Chute de Tension",
                calculationsBlocks = listOf(
                    "Le bilan global des puissances installées s'élève à P_inst = 64.5 kW. En appliquant un facteur de foisonnement ks = 0.75 et de simultanéité ku = 0.8, la puissance foisonnée maximale s'établit à P_max = 38.7 kW, soit une intensité nominale de ligne Ib = 62.8 A.",
                    "Le calcul de la chute de tension en ligne sur une distance L = 75 mètres de câble cuivre U-1000 R2V donne :\nΔU = √3 × Ib × L × (R·cos φ + X·sin φ) = 4.38 V, soit une chute relative de ΔU/Un = 1.09 %, largement inférieure à la limite normative de 5 % requise par la NF C 15-100.",
                    "La vérification du pouvoir de coupure Icu face au courant de court-circuit présumé amont (Icc = 14.2 kA) confirme la coordination sélective totale des protections amont/aval."
                ),
                componentsTitle = "Architecture de Commande et Sélectivité des Protections",
                componentsIntro = "L'armoire intègre des composants de régulation et de commande modulaires :",
                componentsTableRows = listOf(
                    listOf("Contacteurs moteurs", "Contacteurs tripolaires AC-3 (bobine 230V)", "Endurance mécanique > 1.5 million de cycles"),
                    listOf("Relais thermiques électroniques", "Protection thermique classe 10A ajustable", "Détection automatique de coupure de phase"),
                    listOf("Transformateur de séparation", "Primaire 400V / Secondaire 24V AC (250 VA)", "Sécurisation intégrale des circuits de commande"),
                    listOf("Boutons d'arrêt d'urgence", "Coupure d'urgence coup-de-poing à accrochage", "Raccordement sur boucle de sécurité de catégorie 4")
                ),
                realizationStepsTitle = "Câblage Méthodique et Mise en Service de l'Armoire",
                realizationIntro = "Le montage et le raccordement en atelier ont suivi la méthodologie standard :",
                realizationSteps = listOf(
                    "Implantation sur Platine et Châssis" to "Fixation des rails DIN asymétriques et goulottes de câblage fentes serrées au pas de 50 mm.",
                    "Câblage Puissance et Serrage Dynamométrique" to "Passage des conducteurs de puissance, sertissage des embouts de câblage isolés et serrage aux couples constructeurs.",
                    "Câblage de Commande et Repérage" to "Raccordement des auxiliaires avec repérage univoque par bagues numérotées selon le schéma unifilaire.",
                    "Essais d'Isolement Hors Tension" to "Mesure de résistance d'isolement au mégohmmètre sous 500 V DC (Riso > 100 MΩ, conforme)."
                ),
                finishingTitle = "Contrôles de Continuité, Équilibrage et Essais Sous Tension",
                finishingIntro = "Les essais fonctionnels sous tension ont validé le fonctionnement dynamique :",
                finishingSteps = listOf(
                    "Contrôle de l'Équilibrage des Phases" to "Mesure des courants sur les trois phases sous charge nominale : écart inférieur à 4 %.",
                    "Vérification des Déclenchements Différentiels" to "Test d'injection de courant de fuite à l'appareil de mesure de temps de déclenchement (t < 30 ms).",
                    "Pose de la Signalétique Danger et Plastronnage" to "Mise en place des étiquettes d'avertissement risque électrique et schéma unifilaire plastifié."
                ),
                qualityTitle = "Contrôle de Conformité Électrique & Normes Applicables",
                qualityIntro = "L'installation a été rigoureusement certifiée conforme aux référentiels :",
                qualityItems = listOf(
                    "Norme NF C 15-100" to "Installations électriques à basse tension (règles de conception et vérification)",
                    "Norme CEI 61439-1 / 2" to "Ensembles d'appareillage à basse tension de puissance",
                    "Mesure de la Résistance de Prise de Terre" to "Valeur mesurée au telluromètre : R = 4.2 Ω (exigence < 10 Ω respectée)",
                    "Thermographie Infrarouge" to "Absence de point chaud sur l'ensemble des connexions après 3 heures d'essais à pleine charge"
                ),
                skillsAcquired = listOf(
                    "Maîtrise de la NF C 15-100" to "Capacité à concevoir et dimensionner des circuits basse tension sécurisés.",
                    "Sélectivité Électrique" to "Calculs des courants de court-circuit et choix des courbes de disjoncteurs (B, C, D).",
                    "Câblage Industriel" to "Techniques de sertissage, toronnage et organisation propre des armoires.",
                    "Mesures et Métrologie" to "Utilisation experte des appareils de contrôle (mégohmmètre, telluromètre, pince multimètre)."
                ),
                challengesAndSolutions = listOf(
                    "Défi 1 : Perturbations harmoniques sur le réseau" to "Solution : Mise en œuvre d'inductances anti-harmoniques et filtres RFI en amont des variateurs.",
                    "Défi 2 : Échauffement lors des pointes de charge" to "Solution : Intégration d'un système de ventilation forcée thermostaté IP54 avec filtre anti-poussière.",
                    "Défi 3 : Dérive du temps de déclenchement différentiel" to "Solution : Remplacement des tores de détection par des modules super-immunisés insensibles aux transitoires."
                ),
                maintenanceRows = listOf(
                    listOf("Mensuel", "Connexions de puissance et borniers", "Resserrement au couple nominal et inspection visuelle"),
                    listOf("Trimestriel", "Filtres d'aération armoire", "Dépoussiérage ou remplacement des médias filtrants"),
                    listOf("Semestriel", "Boutons poussoirs et arrêts d'urgence", "Essai mécanique et contrôle de coupure instantanée"),
                    listOf("Annuel", "Contrôle thermographique infrarouge", "Inspection caméra thermique de tous les jeux de barres et départs"),
                    listOf("Biennal", "Mesure de la prise de terre générale", "Contrôle tellurométrique pour s'assurer du maintien sous 10 Ω")
                ),
                strategicRecommendations = listOf(
                    "Supervision Énergétique IoT" to "Mise en place de passerelles sans fil LoRaWAN pour le télérelevé en continu des consommations kWh.",
                    "Automatisation du Démarrage Secours" to "Configuration d'un automate programmable assurant le délestage sélectif des charges non prioritaires.",
                    "Plan de Consignation Dématérialisé" to "Adoption d'une application de délivrance de titres d'habilitation et bons de consignation sur smartphone."
                )
            )

            FiliereType.GENIE_CIVIL_BATIMENT -> FiliereTechnicalContent(
                filiereCategory = "Génie Civil & Bâtiment Travaux Publics",
                cdcfTitle = "Cahier des Clauses Techniques Particulières (CCTP) & Descente de Charges",
                cdcfIntro = "Le projet architectural et structurel répond aux critères de résistance et de durabilité de l'Eurocode :",
                cdcfTableRows = listOf(
                    listOf("FP1", "Reprise des charges permanentes et d'exploitation", "Capacité portante", "Charges G + Q conformément à l'Eurocode 1"),
                    listOf("FP2", "Stabilité des fondations au poinçonnement", "Taux de travail du sol", "Contrainte admissible q_adm = 0.25 MPa déterminée au pressiomètre"),
                    listOf("FC1", "Résistance aux fissurations et agressions climatiques", "Classe d'exposition", "XC4 / XS1 pour milieu tropical humide avec enrobage minimal de 35 mm"),
                    listOf("FC2", "Flèche admissible sous sollicitations de service", "Critère de flèche limite", "f_max ≤ L/500 sous combinaisons quasi-permanentes"),
                    listOf("FC3", "Sécurité incendie et stabilité au feu", "Degré coupe-feu requis", "R90 pour éléments porteurs principaux"),
                    listOf("FC4", "Accessibilité et évacuation des eaux pluviales", "Pentes minimales", "Pente minimale 1.5 % vers caniveaux collecteurs à fente")
                ),
                materialsTitle = "Caractérisation du Béton Armé et des Matériaux Géotechniques",
                materialsIntro = "Les constituants ont fait l'objet d'essais de formulation et d'agrément en laboratoire de génie civil :",
                materialsTableRows = listOf(
                    listOf("Béton de structure", "Formulation C25/30 (Dosage 350 kg/m³ de ciment CPJ-CEM II 42.5)", "fc28 = 25 MPa", "Ouvrabilité classe S3 au cône d'Abrams (slump = 120 mm)"),
                    listOf("Aciers pour béton armé", "Barres à haute adhérence FeE500 (Ø 8 à Ø 20 mm)", "Limite d'élasticité fe = 500 MPa", "Adhérence et ductilité classe B"),
                    listOf("Granulats concassés", "Granulats silico-calcaires 5/15 et 15/25 mm", "Coefficient Los Angeles LA < 25", "Résistance élevée à l'attrition et au concassage"),
                    listOf("Sable de carrière lavé", "Module de finesse Mf = 2.45 (sable moyen propre)", "Équivalent de sable ES > 75 %", "Exempt d'argiles polluantes garantissant l'ouvrabilité"),
                    listOf("Adjuvant plastifiant", "Réducteur d'eau haut de gamme (base polycarboxylates)", "Densité 1.06", "Diminution du rapport E/C à 0.46 tout en conservant la fluidité")
                ),
                calculationsTitle = "Descente de Charges, Dimensionnement BAEL / Eurocode 2",
                calculationsBlocks = listOf(
                    "La descente de charges sur le poteau d'angle le plus sollicité donne : Charge permanente G = 142.5 kN, Charge d'exploitation Q = 68.0 kN. À l'ELU (État Limite Ultime), la charge de calcul est Pu = 1.35 G + 1.5 Q = 294.38 kN.",
                    "Pour la semelle isolée superficielle reposant sur le sol de portance q_adm = 2.5 bar (0.25 MPa), la section minimale en plan requise est S = Pser / q_adm = (142.5 + 68.0) / 250 = 0.842 m², justifiant une semelle carrée de côté A = B = 1.00 m et d'épaisseur h = 35 cm.",
                    "Le ferraillage calculé par la méthode des bielles donne une section d'acier tendu As = 4.12 cm², couverte par une nappe quadrillée de 6 HA 10 dans chaque sens (As_réel = 4.71 cm²)."
                ),
                componentsTitle = "Organisation de Chantier, Coffrage et Étaiement",
                componentsIntro = "Les procédés constructifs garantissent la sécurité et la géométrie des ouvrages :",
                componentsTableRows = listOf(
                    listOf("Système de coffrage", "Banches métalliques modulaires avec peaux en contreplaqué filmé", "Planéité de surface et réduction des ragréages"),
                    listOf("Étaiement haute résistance", "Étais télescopiques réglables classe D (portance 20 kN)", "Contreventement par lisses et diagonales rigides"),
                    listOf("Armatures préfabriquées", "Cages d'armatures façonnées et ligaturées sur chantier", "Pose systématique de cales à béton d'enrobage 35 mm"),
                    listOf("Vibration du béton", "Aiguilles vibrantes pneumatiques haute fréquence Ø 45 mm", "Serrage homogène sans ségrégation ni nids de gravier")
                ),
                realizationStepsTitle = "Chronologie d'Exécution sur le Chantier de Génie Civil",
                realizationIntro = "Les phases de réalisation ont suivi le planning prévisionnel GANTT :",
                realizationSteps = listOf(
                    "Terrassement et Fouilles" to "Décapage de la terre végétale et fouilles en rigoles au tractopelle jusqu'au bon sol avec contrôle altimétrique laser.",
                    "Béton de Propreté et Ferraillage" to "Coulage d'un béton de propreté dosé à 150 kg/m³ sur 5 cm d'épaisseur, puis pose des cales et nappes de ferraillage.",
                    "Coulage du Béton et Vibration" to "Bétonnage à la toupie avec benne, régalage et vibration soignée en couches successives de 30 cm d'épaisseur.",
                    "Cure et Décoffrage Réglementaire" to "Application d'un produit de cure paraffiné pour éviter la dessiccation et maintien des étais pendant 21 jours."
                ),
                finishingTitle = "Contrôles Topographiques et Épreuves sur Éprouvettes",
                finishingIntro = "La réception technique s'appuie sur des données de laboratoire in situ :",
                finishingSteps = listOf(
                    "Écrasement d'Éprouvettes Cylindriques 16×32" to "Résultats à 28 jours : fc28_moyen = 28.4 MPa (supérieur aux 25 MPa prescrits).",
                    "Contrôle d'Implantation Topographique" to "Vérification au tachéomètre électronique de l'alignement des axes (+/- 3 mm).",
                    "Essai de Perméabilité et Ragréage" to "Traitement des joints de reprise au mortier sans retrait et ragréage des balèvres."
                ),
                qualityTitle = "Contrôle Qualité du Béton & Référentiels BTP",
                qualityIntro = "Toutes les étapes respectent les normes ivoiriennes et internationales du bâtiment :",
                qualityItems = listOf(
                    "Eurocode 2 / BAEL 91 Révisé 99" to "Calcul des structures en béton armé",
                    "Norme NF EN 206-1" to "Béton : spécification, performance, production et conformité",
                    "Règles Parasismiques & Géotechniques" to "Respect des recommandations de fondation et ancrage de l'INP-HB",
                    "Plan d'Assurance Qualité Chantier" to "Fiches de réception de ferraillage validées par le bureau de contrôle"
                ),
                skillsAcquired = listOf(
                    "Lecture de Plans Structure" to "Interprétation rapide des plans de coffrage et de ferraillage d'ingénieur.",
                    "Technologie des Bétons" to "Contrôle de plasticité, vibration et remèdes contre les fissures de retrait.",
                    "Topographie Opérationnelle" to "Maniement de la station totale et du niveau optique de chantier.",
                    "Gestion Sécurité BTP" to "Application du plan PPSPS, port du harnais et protection collective contre les chutes."
                ),
                challengesAndSolutions = listOf(
                    "Défi 1 : Forte pluie tropicale survenue en cours de bétonnage" to "Solution : Bâchage immédiat des zones fraîches et reprise après séchage avec résine d'accrochage époxy.",
                    "Défi 2 : Découverte d'une poche de sol compressible lors des fouilles" to "Solution : Purge sur 60 cm et remblaiement par gros béton de substitution compacté.",
                    "Défi 3 : Écart de verticalité sur une banche de coffrage" to "Solution : Réglage micrométrique des tirants push-pull et contrôle au fil à plomb avant coulage."
                ),
                maintenanceRows = listOf(
                    listOf("Trimestriel", "Réseaux d'évacuation pluviale", "Curage des regards, avaloirs et caniveaux extérieurs"),
                    listOf("Semestriel", "Joints de dilatation et mastic", "Contrôle de l'élasticité et remplacement des lèvres dégradées"),
                    listOf("Annuel", "Parements béton extérieurs", "Inspection visuelle de microfissures et traitement hydrofuge"),
                    listOf("Biennal", "Garde-corps et acrotères métalliques", "Vérification des fixations goujonnées et peinture anticorrosion"),
                    listOf("Quinquennal", "Auscultation structurelle générale", "Mesure au scléromètre et vérification de flèche des portées")
                ),
                strategicRecommendations = listOf(
                    "Modélisation BIM 3D" to "Adoption de logiciels BIM (Revit/ArchiCAD) pour anticiper les conflits réseaux fluides/structure.",
                    "Utilisation de Bétons Fibrés" to "Intégration de macro-fibres synthétiques pour supprimer le treillis soudé anti-fissuration sur dallage.",
                    "Centrale de Malaxage Numérique" to "Installation de capteurs d'humidité hygrométrique dans les trémies de sable pour ajuster l'eau d'appoint."
                )
            )

            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> FiliereTechnicalContent(
                filiereCategory = "Informatique, Systèmes & Réseaux Télécoms",
                cdcfTitle = "Spécifications Fonctionnelles & Cahier des Charges Logiciel / Réseau",
                cdcfIntro = "L'infrastructure et les solutions développées satisfont aux critères de performance et de cybersécurité :",
                cdcfTableRows = listOf(
                    listOf("FP1", "Disponibilité et haute résilience du système", "Taux de disponibilité (SLA)", "99.95 % avec basculement automatique sans interruption"),
                    listOf("FP2", "Confidentialité et intégrité des données", "Protocole de chiffrement", "Chiffrement TLS 1.3 / AES-256 en transit et au repos"),
                    listOf("FC1", "Temps de réponse et latence applicative", "Temps de traitement moyen", "< 200 ms pour 95 % des requêtes API sous pic de charge"),
                    listOf("FC2", "Authentification et contrôle des accès", "Standard IAM retenu", "OAuth 2.0 / OpenID Connect avec authentification multifacteur (MFA)"),
                    listOf("FC3", "Plan de reprise d'activité (PRA)", "RPO & RTO cibles", "RPO < 15 minutes, RTO < 1 heure grâce aux sauvegardes automatisées"),
                    listOf("FC4", "Conformité réglementaire des données", "Loi sur la protection des données", "Conformité à l'ARTCI (Côte d'Ivoire) et bonnes pratiques RGPD")
                ),
                materialsTitle = "Architecture Matérielle, Stack Logicielle et Outils Cloud",
                materialsIntro = "Les technologies ont été sélectionnées pour leur robustesse et leur évolutivité :",
                materialsTableRows = listOf(
                    listOf("Couche Applicative Backend", "Node.js / Kotlin Spring Boot / Python FastAPI", "Asynchrone & Typé", "Haute capacité de traitement parallèle et documentation OpenAPI"),
                    listOf("Base de Données Relationnelle", "PostgreSQL 16 avec réplication streaming", "ACID conforme", "Fiabilité transactionnelle et support JSONB natif"),
                    listOf("Conteneurisation & Orchestration", "Docker & Kubernetes (K8s)", "Pods redondants", "Déploiement continu sans interruption de service (Zero-downtime)"),
                    listOf("Infrastructure Réseau & Switchs", "Switchs managés Cisco Gigabit L3 avec VLANs", "Routage inter-VLAN", "Cloisonnement strict des flux d'administration et invités"),
                    listOf("Supervision & Logs en temps réel", "Prometheus & Grafana / ELK Stack", "Collecteur de métriques", "Alertes automatiques par webhook sur seuils critiques")
                ),
                calculationsTitle = "Dimensionnement Réseau, Bande Passante et Capacité de Stockage",
                calculationsBlocks = listOf(
                    "Pour un parc de 250 utilisateurs simultanés générant un trafic moyen de 450 kbps en usage combiné (ERP, VoIP et vidéo-conférence), la bande passante utile minimale requise est B = 250 × 0.450 = 112.5 Mbps. En prévoyant une marge de sécurité de 30 %, un lien fibre optique dédié de 150 Mbps symétrique a été calibré.",
                    "L'évaluation volumétrique de la base de données sur 3 ans : 50 000 transactions/jour à raison de 2.5 Ko par enregistrement équivaut à 125 Mo/jour, soit 45.6 Go/an. Avec les index (× 1.8) et les sauvegardes différentielles quotidiennes conservées 30 jours, un pool de stockage SSD NVMe RAID-10 de 1 To a été provisionné.",
                    "L'analyse de débit inter-VLAN confirme l'absence de goulot d'étranglement grâce à des liens Trunk LACP 2×10 Gbps entre les commutateurs de cœur et de distribution."
                ),
                componentsTitle = "Architecture Réseau, Segmentation VLAN et Sécurité Périmétrique",
                componentsIntro = "L'architecture réseau repose sur une segmentation sécurisée en zones de confiance :",
                componentsTableRows = listOf(
                    listOf("VLAN 10 - Administration", "192.168.10.0/24 - Accès restreint par adresse MAC", "Gestion sécurisée SSH/HTTPS"),
                    listOf("VLAN 20 - Serveurs & DMZ", "192.168.20.0/24 - Accès public filtré par WAF", "Hébergement des API et services web"),
                    listOf("VLAN 30 - Postes Utilisateurs", "192.168.30.0/24 - Attribution dynamique DHCP", "Accès internet régulé par proxy et antivirus"),
                    listOf("Pare-feu Next-Gen (NGFW)", "Fortinet FortiGate avec inspection SSL et IPS", "Blocage actif des intrusions et botnets")
                ),
                realizationStepsTitle = "Déploiement et Intégration Continue (CI/CD)",
                realizationIntro = "Le développement et le déploiement ont suivi la démarche agile DevOps :",
                realizationSteps = listOf(
                    "Conception des API et Modélisation MCD" to "Élaboration des schémas entités-associations et spécifications des routes RESTful sous Swagger.",
                    "Configuration des VLANs et Routage" to "Création des sous-interfaces, adressage IP statique et activation du protocole Spanning Tree (RSTP).",
                    "Pipeline de Déploiement CI/CD" to "Automatisation des tests unitaires, analyse statique de sécurité (SonarQube) et génération des images Docker.",
                    "Tests de Pénétration et Durcissement" to "Scan de vulnérabilités Nmap/OWASP ZAP, fermeture des ports inutiles et configuration SSH sur clés RSA 4096 bits."
                ),
                finishingTitle = "Validation Métrique, Tests de Charge et Documentation API",
                finishingIntro = "La recette fonctionnelle a permis de valider la scalabilité de la solution :",
                finishingSteps = listOf(
                    "Test de Montée en Charge (Stress Test)" to "Simulation de 1000 requêtes/seconde sous Apache JMeter : temps de réponse moyen de 115 ms sans perte de paquets.",
                    "Vérification du Basculement de Sauvegarde" to "Test d'arrêt forcé du serveur primaire : basculement de la réplication en 28 secondes sans altération des données.",
                    "Rédaction du Manuel d'Exploitation (DEX)" to "Documentation complète des procédures d'administration, scripts cron de backup et guides utilisateurs."
                ),
                qualityTitle = "Contrôle Qualité Logiciel & Référentiels Cybersécurité",
                qualityIntro = "La plateforme respecte les standards internationaux de gouvernance SI :",
                qualityItems = listOf(
                    "Norme ISO/IEC 27001" to "Systèmes de management de la sécurité de l'information (SMSI)",
                    "Méthode ITIL v4" to "Gestion des incidents, des changements et du cycle de vie des services",
                    "OWASP Top 10" to "Protection active contre les injections SQL, XSS et failles d'authentification",
                    "Norme ISO 25010" to "Modèle d'évaluation de la qualité des produits logiciels (maintenabilité, portabilité)"
                ),
                skillsAcquired = listOf(
                    "Génie Logiciel & Architecture" to "Maîtrise des microservices, patrons de conception (Design Patterns) et bases NoSQL/SQL.",
                    "Cybersécurité Appliquée" to "Implémentation de pare-feux, gestion des certificats SSL/TLS et politiques de mots de passe.",
                    "Administration Systèmes Linux" to "Déploiement automatisé sous Ubuntu Server, conteneurs Docker et monitoring Grafana.",
                    "Gestion de Projet Agile" to "Animation des sprints Scrum, suivi des tickets sous Jira et gestion des versions avec Git."
                ),
                challengesAndSolutions = listOf(
                    "Défi 1 : Latence élevée sur les requêtes de recherche multicritères" to "Solution : Mise en cache des requêtes fréquentes avec Redis et optimisation des index PostgreSQL (index GiST/GIN).",
                    "Défi 2 : Perte périodique de connectivité sur le pont wifi d'atelier" to "Solution : Reconfiguration en 5 GHz avec canaux non perturbés et passage d'une liaison filaire Ethernet blindée Cat 6A SFTP.",
                    "Défi 3 : Faux positifs répétés du système de détection d'intrusion" to "Solution : Affinage des signatures d'attaque et création de listes blanches pour les requêtes du monitoring interne."
                ),
                maintenanceRows = listOf(
                    listOf("Quotidien", "Sauvegardes automatiques et snapshots", "Vérification des logs d'exécution et réplication sur serveur distant"),
                    listOf("Hebdomadaire", "Mises à jour de sécurité des OS et paquets", "Application des correctifs critiques sans interruption"),
                    listOf("Mensuel", "Audit des comptes et des droits d'accès", "Révocation des accès obsolètes et contrôle des journaux de connexion"),
                    listOf("Semestriel", "Exercice de restauration PRA/PCA", "Restauration complète à blanc sur environnement isolé pour valider le RTO"),
                    listOf("Annuel", "Audit externe de sécurité et pentest", "Test d'intrusion approfondi par une équipe tierce pour détecter de nouvelles failles")
                ),
                strategicRecommendations = listOf(
                    "Migration Progressive vers le Cloud Hybride" to "Adopter une infrastructure hybride (On-premise + Cloud) pour absorber les pics de charge saisonniers.",
                    "Authentification Sans Mot de Passe (Passkeys)" to "Déployer la norme FIDO2 pour éliminer définitivement les risques liés au hameçonnage (phishing).",
                    "Mise en Place d'une Charte Numérique Interne" to "Organiser des sessions régulières de sensibilisation du personnel à la sécurité informatique."
                )
            )

            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> FiliereTechnicalContent(
                filiereCategory = "Logistique, Transport & Supply Chain",
                cdcfTitle = "Cahier des Charges Opérationnel & Optimisation des Flux",
                cdcfIntro = "L'optimisation logistique répond aux objectifs de rentabilité, de fluidité et de réduction des délais :",
                cdcfTableRows = listOf(
                    listOf("FP1", "Optimisation de la rotation des stocks et inventaires", "Taux de rotation", "Rotation cible > 8 rotations/an pour réduire les coûts d'immobilisation"),
                    listOf("FP2", "Réduction des délais d'expédition et préparation", "Lead Time de préparation", "< 4 heures entre la validation de commande et la mise à quai"),
                    listOf("FC1", "Traçabilité intégrale des marchandises", "Taux d'identification", "100 % des palettes tracées par codes-barres GS1-128 / QR codes"),
                    listOf("FC2", "Taux de service et satisfaction client", "OTIF (On-Time In-Full)", "OTIF ≥ 96.5 % sur les livraisons aux distributeurs et chantiers"),
                    listOf("FC3", "Sécurité des manutentions et respect HSE", "Accidents avec arrêt", "Zéro accident grâce au respect des allées de circulation et gerbage sécurisé"),
                    listOf("FC4", "Gestion de la logistique retour (Reverse Logistics)", "Taux de réintégration", "Récupération et tri des emballages réutilisables sous 48 heures")
                ),
                materialsTitle = "Équipements de Manutention, Stockage et Outils de Gestion",
                materialsIntro = "Les infrastructures d'entreposage combinent mécanisation et progiciels de gestion intégrés :",
                materialsTableRows = listOf(
                    listOf("Chariots élévateurs frontaux", "Chariots électriques 2.5 tonnes mât triplex", "Hauteur de levée 6.00 m", "Zéro émission en entrepôt et rayon de braquage court"),
                    listOf("Rayonnages à palettes (Palettiers)", "Racks à palettes lourds en acier galvanisé", "Charge admissible 3000 kg/alvéole", "Conformité à la norme européenne EN 15635"),
                    listOf("Système WMS (Warehouse Management)", "Progiciel de gestion d'entrepôt temps réel", "Algorithme de zoning ABC", "Optimisation des trajets de préparation de commande (Picking)"),
                    listOf("Terminaux portables durcis", "Scanners codes-barres WiFi durcis IP65", "Lecteur 2D longue portée", "Validation instantanée des entrées et sorties de stocks"),
                    listOf("Flotte de camions de livraison", "Porteurs 12 tonnes équipés de hayons élévateurs", "Géolocalisation GPS active", "Suivi des tournées en direct et éco-conduite")
                ),
                calculationsTitle = "Calculs Logistiques, Formule de Wilson et Dimensionnement des Stocks",
                calculationsBlocks = listOf(
                    "Pour une consommation annuelle D = 12 000 unités d'un composant clé, un coût de passation de commande Cs = 25 000 FCFA et un coût unitaire de possession du stock Ch = 2 400 FCFA/an, la quantité économique de commande (Formule de Wilson) s'établit à :\nQ* = √((2 × D × Cs) / Ch) = √((2 × 12 000 × 25 000) / 2 400) = 500 unités par commande.",
                    "Le stock de sécurité (SS) calculé pour couvrir un délai de réapprovisionnement moyen de 15 jours avec un écart-type de consommation de 25 unités/jour sous un niveau de service de 95 % (k = 1.65) est :\nSS = k × σ_demande × √(Délai) = 1.65 × 25 × √15 = 160 unités.",
                    "Le taux d'occupation volumique des alvéoles de stockage est passé de 74 % à 89 % suite au réaménagement de la zone de stockage lourd selon la méthode ABC."
                ),
                componentsTitle = "Organisation de l'Entrepôt et Schéma des Flux Entrants/Sortants",
                componentsIntro = "La réorganisation repose sur le principe de marche en avant évitant les croisements :",
                componentsTableRows = listOf(
                    listOf("Zone de Réception & Contrôle", "Quai niveleur avec sas d'étanchéité", "Contrôle quantitatif et qualitatif sous 1 heure"),
                    listOf("Zone de Stockage Haute Densité", "Allées de 3.20 m avec repérage univoque (Allée-Travée-Niveau)", "Stockage optimisé selon la rotation de classe A, B et C"),
                    listOf("Zone de Préparation (Picking)", "Emplacements dynamiques au sol alimentés par gravité", "Réduction de 35 % des déplacements des préparateurs"),
                    listOf("Zone d'Expédition & Emballage", "Postes d'emballage ergonomiques et banderoleuse automatique", "Édition automatique des lettres de voiture et bons de livraison")
                ),
                realizationStepsTitle = "Mise en Œuvre Pratique des Procédures Logistiques",
                realizationIntro = "Les opérations quotidiennes d'exploitation logistique se sont cadencées comme suit :",
                realizationSteps = listOf(
                    "Réception et Rapprochement Facture" to "Déchargement au chariot, contrôle des avaries, flashage des codes-barres et rapprochement automatique avec le bon de commande.",
                    "Implantation en Stock par Radiofréquence" to "Affectation de l'emplacement de stockage suggéré par le WMS selon le poids et la vitesse de rotation.",
                    "Préparation des Commandes par Vagues" to "Regroupement des commandes clients en tournées logiques, prélèvement dirigé et confirmation par scannage.",
                    "Chargement et Calage des Véhicules" to "Vérification de la répartition des charges sur les essieux des camions et sanglage réglementaire anti-basculement."
                ),
                finishingTitle = "Audit des Écarts d'Inventaire et Indicateurs Clés (KPI)",
                finishingIntro = "L'évaluation de la performance logistique s'appuie sur des indicateurs fiables :",
                finishingSteps = listOf(
                    "Inventaire Tournant Périodique" to "Taux d'exactitude d'inventaire mesuré à 98.8 % (écart résiduel inférieur à la tolérance de 1.5 %).",
                    "Mesure du Taux de Rupture de Stock" to "Baisse spectaculaire du taux de rupture de 8.2 % à 1.4 % grâce au paramétrage du seuil de réapprovisionnement.",
                    "Formation des Caristes à l'Éco-Conduite" to "Sensibilisation aux manœuvres douces réduisant les dégradations de palettes de 40 %."
                ),
                qualityTitle = "Assurance Qualité Logistique & Normes de Transport",
                qualityIntro = "L'ensemble de la chaîne d'approvisionnement applique les standards internationaux :",
                qualityItems = listOf(
                    "Norme ISO 9001:2015" to "Management de la qualité des processus logistiques et satisfaction clients",
                    "Norme EN 15635" to "Application et maintenance des équipements de stockage en acier",
                    "Réglementation CMR & Incoterms 2020" to "Contrats de transport international de marchandises et transfert des risques",
                    "Code du Travail & CACES R489" to "Certificat d'Aptitude à la Conduite en Sécurité des chariots de manutention"
                ),
                skillsAcquired = listOf(
                    "Gestion des Stocks & Approvisionnements" to "Maîtrise des modèles d'ordonnancement, seuils de commande et calculs de stock tampon.",
                    "Pilotage de Progiciel WMS/ERP" to "Configuration des flux logistiques, gestion des nomenclatures et interfaces de traçabilité.",
                    "Optimisation des Tournées de Livraison" to "Utilisation d'algorithmes de sectorisation pour réduire les kilomètres à vide.",
                    "Management des Équipes de Quai" to "Coordination des manutentionnaires et application stricte des règles de sécurité cariste-piéton."
                ),
                challengesAndSolutions = listOf(
                    "Défi 1 : Goulet d'étranglement aux quais lors des réceptions matinales simultanées" to "Solution : Mise en place d'un système de prise de rendez-vous transporteur (Slot Booking) échelonné sur toute la journée.",
                    "Défi 2 : Dégradations récurrentes d'emballages carton sous l'effet de l'humidité" to "Solution : Adoption de palettes plastiques PEHD et houssage étanche sous film étirable haute résistance.",
                    "Défi 3 : Écarts d'inventaire sur les petites pièces et accessoires" to "Solution : Installation d'armoires sécurisées à casiers fermés avec pointage par badge RFID individuel."
                ),
                maintenanceRows = listOf(
                    listOf("Hebdomadaire", "Vérification des fourches et chaînes de chariots", "Contrôle d'usure des talons de fourches et graissage des mâts"),
                    listOf("Mensuel", "Inspection visuelle des montants de palettiers", "Repérage des déformations d'échelles suite aux chocs d'engins"),
                    listOf("Trimestriel", "Entretien des niveleurs de quai et butoirs", "Contrôle des vérins hydrauliques et graissage des charnières"),
                    listOf("Semestriel", "Vérification générale périodique (VGP) des chariots", "Contrôle obligatoire par un organisme agréé"),
                    listOf("Annuel", "Contrôle statique des rayonnages", "Vérification de verticalité et contrôle de couple des chevilles de fixation")
                ),
                strategicRecommendations = listOf(
                    "Automatisation du Tri par Convoyeurs Motorisés" to "Envisager une ligne de tri automatisée pour les colis légers afin de doubler la cadence d'expédition.",
                    "Transition de la Flotte vers les Énergies Propres" to "Tester des véhicules utilitaires de livraison au biogaz ou électriques pour les livraisons urbaines.",
                    "Plateforme Collaborative Fournisseurs (Portail Web)" to "Partager en direct les prévisions de vente avec les fournisseurs stratégiques (méthode VMI)."
                )
            )

            FiliereType.MINES_PETROLE_PROCEDES -> FiliereTechnicalContent(
                filiereCategory = "Mines, Pétrole & Génie des Procédés",
                cdcfTitle = "Spécifications Procédés & Cahier des Charges Installations Sous Pression",
                cdcfIntro = "L'installation pétrolière ou minière obéit aux exigences rigoureuses de tenue sous pression et sécurité ATEX :",
                cdcfTableRows = listOf(
                    listOf("FP1", "Séparation et traitement des fluides de gisement", "Débit de traitement assigné", "Capacité nominale de 2 500 barils/jour ou 150 t/h de minerai"),
                    listOf("FP2", "Tenue mécanique des appareils sous pression (ESP)", "Pression et température de calcul", "P = 64 bar, T = 120°C selon code ASME Section VIII Div. 1"),
                    listOf("FC1", "Protection contre les atmosphères explosives (ATEX)", "Zonage de sécurité", "Zone 1 / Zone 2 avec matériel certifié Ex d IIC T4"),
                    listOf("FC2", "Maîtrise de la corrosion acide (H2S et CO2)", "Spécification métallurgique", "Matériaux conformes à la norme NACE MR0175 / ISO 15156"),
                    listOf("FC3", "Arrêt d'urgence instrumenté (ESD)", "Niveau d'intégrité de sécurité", "Système instrumenté de sécurité classé SIL 2 / SIL 3 (CEI 61508)"),
                    listOf("FC4", "Rejets environnementaux et zéro torchage", "Teneur en hydrocarbures des eaux", "< 20 mg/L dans les effluents traités avant rejet réglementaire")
                ),
                materialsTitle = "Métallurgie Pétrolière, Tuyauterie Industrielle et Vannes API",
                materialsIntro = "Les nuances d'alliages et équipements sous pression résistent aux conditions de service sévères :",
                materialsTableRows = listOf(
                    listOf("Tubes sans soudure de procédé", "Acier au carbone basse température ASTM A106 Gr. B / A333 Gr. 6", "Résilience à -29°C", "Absence de fragilisation et excellente soudabilité"),
                    listOf("Brides et raccords forgés", "Acier forgé ASTM A105N (Classe 300 / Classe 600)", "Pression nominale PN 50 / PN 100", "Étanchéité par joints spirométalliques inox/graphite"),
                    listOf("Vannes à boisseau sphérique (Ball Valves)", "Corps acier moulé A216 WCB, sphère inox 316, sièges PEEK", "Standard API 6D", "Étanchéité bidirectionnelle et sécurité feu Fire Safe API 607"),
                    listOf("Soupapes de décharge et de sécurité", "Soupapes à ressort avec levier d'actionnement manuel", "Standard API 526", "Évacuation instantanée en cas de surpression accidentelle"),
                    listOf("Séparateur biphasique horizontal", "Virole en tôle plaquée acier inoxydable 316L", "Épaisseur virole 18 mm", "Protection totale contre l'arrachement par piqûres de chlorures")
                ),
                calculationsTitle = "Calculs d'Épaisseur de Tuyauterie (ASME B31.3) et Pertes de Charge",
                calculationsBlocks = listOf(
                    "Le calcul de l'épaisseur nominale d'une ligne de tuyauterie 4\" Sch 40 sous pression P = 45 bar à T = 80°C selon l'ASME B31.3 est donné par :\nt = (P × D) / (2 × (S × E + P × Y)) + c, où D = 114.3 mm, contrainte admissible S = 138 MPa, efficacité de soudure E = 1.0, Y = 0.4 et surépaisseur de corrosion c = 3.0 mm. t_requis = 1.86 + 3.0 = 4.86 mm, justifiant le tube Sch 40 d'épaisseur 6.02 mm.",
                    "L'évaluation des pertes de charge singulières et régulières (Formule de Darcy-Weisbach) sur le collecteur général donne ΔP = 1.42 bar, compatible avec la pression d'aspiration requise aux pompes d'expédition centrifuge.",
                    "Le calcul de la capacité d'évacuation de la soupape PSV dimensionnée selon l'API 520 garantit la protection intégrale du séparateur en cas de fermeture intempestive de la vanne aval."
                ),
                componentsTitle = "Instrumentation Industrielle, Boucles de Régulation et Sécurité Procédé",
                componentsIntro = "La conduite du procédé repose sur une instrumentation de pointe communicante :",
                componentsTableRows = listOf(
                    listOf("Transmetteurs de pression différentielle", "Transmetteurs électroniques intelligents protocole HART / Foundation Fieldbus", "Mesure de niveau par pression hydrostatique"),
                    listOf("Vannes de régulation pneumatiques", "Vannes à clapet guidé avec positionneur numérique intelligent", "Régulation précise du niveau liquide et de la pression gaz"),
                    listOf("Débitmètres massiques à effet Coriolis", "Précision de comptage transactionnel 0.1 %", "Mesure directe de la masse, densité et température du fluide"),
                    listOf("Détecteurs de gaz toxiques et explosifs", "Capteurs catalytiques CH4 et électrochimiques H2S reliés à la centrale feu et gaz", "Déclenchement automatique des alarmes et rideaux d'eau")
                ),
                realizationStepsTitle = "Travaux de Montage de Lignes et Contrôles Non Destructifs (CND)",
                realizationIntro = "La construction sur site a respecté la séquence opératoire d'ingénierie :",
                realizationSteps = listOf(
                    "Préfabrication des Tuyauteries (Spools)" to "Découpe, chanfreinage orbital et soudage TIG (procédé 141) pour la passe de pénétration suivi de passes de remplissage à l'électrode basique.",
                    "Contrôles Non Destructifs (CND 100 %)" to "Contrôle radiographique des soudures (norme ASME B31.3 catégorie fluide sévère) et ressuage des piquages.",
                    "Épreuve Hydrostatique sous Pression" to "Mise en eau de la ligne, montée en pression progressive jusqu'à 1.5 fois la pression de service (soit 96 bar) maintenue 4 heures.",
                    "Décapage, Passivation et Inertage" to "Lessivage chimique intérieur, rinçage à l'eau déminéralisée et inertage sous matelas d'azote gazeux sec."
                ),
                finishingTitle = "Calibration des Instruments et Réception des Systèmes (Pre-commissioning)",
                finishingIntro = "Les tests préalables au démarrage effectif ont validé tous les automatismes :",
                finishingSteps = listOf(
                    "Bouclage Électrique et Test Loop" to "Injection de signaux 4-20 mA calibrés pour vérifier la réponse sur les écrans du système de conduite DCS.",
                    "Test Fonctionnel de l'Arrêt d'Urgence (ESD)" to "Simulation d'une surpression : fermeture étanche de toutes les vannes de sectionnement en moins de 3 secondes.",
                    "Étalonnage des Soupapes sur Banc d'Épreuve" to "Vérification de la pression de début d'ouverture de la soupape à 64 bar (+/- 1 %) avec plombage officiel."
                ),
                qualityTitle = "Réglementation des Équipements Pétroliers & Normes",
                qualityIntro = "Toutes les installations obéissent aux normes internationales de l'industrie :",
                qualityItems = listOf(
                    "Code ASME Section VIII Div. 1" to "Règles de construction des appareils à pression non soumis à la flamme",
                    "Norme ASME B31.3" to "Tuyauteries d'usines chimiques et raffineries de pétrole",
                    "Directives ATEX 2014/34/UE" to "Appareils et systèmes de protection destinés aux atmosphères explosibles",
                    "Standard API 520 / 521 / 526" to "Dimensionnement, sélection et installation des dispositifs de décharge de pression"
                ),
                skillsAcquired = listOf(
                    "Conception de Tuyauteries Industrielles" to "Élaboration des plans isométriques, calcul de flexibilité thermique et supportage.",
                    "Sécurité des Procédés Chimiques" to "Participation aux analyses de risques HAZOP et dimensionnement des boucles SIL.",
                    "Contrôles Métallurgiques CND" to "Interprétation des clichés radiographiques et détection des défauts de soudure (manque de fusion, soufflures).",
                    "Conduite sur Système Numérique (DCS)" to "Surveillance des paramètres de marche sur synoptique et gestion des alarmes procédé."
                ),
                challengesAndSolutions = listOf(
                    "Défi 1 : Risque de vibration pulsatoire sur la ligne de refoulement du compresseur" to "Solution : Ajout d'amortisseurs de pulsation acoustiques et renforcement des supports guidés en acier.",
                    "Défi 2 : Présence imprévue de sable abrasif dans les hydrocarbures bruts" to "Solution : Installation d'un hydrocyclone dessableur en tête de puits avec garniture céramique anti-usure.",
                    "Défi 3 : Fuite au joint de bride lors de la montée en pression initiale" to "Solution : Remplacement du joint par un modèle spiralé avec bague de centrage et serrage contrôlé à la clé hydraulique."
                ),
                maintenanceRows = listOf(
                    listOf("Quotidien", "Relevé des pressions, températures et niveaux", "Inspection visuelle de l'étanchéité des presse-étoupes"),
                    listOf("Mensuel", "Test de manœuvrabilité des vannes manuelles", "Manœuvre d'ouverture/fermeture pour éviter le gommage des sièges"),
                    listOf("Trimestriel", "Vérification de la protection cathodique", "Mesure du potentiel de polarisation sol/métal des canalisations enterrées"),
                    listOf("Annuel", "Contrôle d'épaisseur par ultrasons", "Mesure non destructive de l'épaisseur résiduelle des viroles et coudes"),
                    listOf("Triennal", "Tarage officiel des soupapes de sûreté", "Démontage et réétalonnage sur banc accrédité avec changement des joints")
                ),
                strategicRecommendations = listOf(
                    "Mise en Place de Jumeaux Numériques (Digital Twin)" to "Simuler le comportement du procédé en temps réel pour optimiser le rendement thermodynamique.",
                    "Système de Récupération des Gaz Associés" to "Éliminer définitivement le torchage de routine en valorisant le gaz vers des micro-turbines électriques.",
                    "Renforcement du Programme de Détection de Fuites (LDAR)" to "Utiliser des caméras optiques infrarouges (FLIR) pour détecter les micro-émissions fugitives de COV."
                )
            )

            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> FiliereTechnicalContent(
                filiereCategory = "Génie Agroalimentaire & Agronomie",
                cdcfTitle = "Cahier des Charges Hygiène, Qualité & Maîtrise des Procédés Agro-industriels",
                cdcfIntro = "La transformation et le conditionnement agroalimentaire satisfont aux normes sanitaires internationales :",
                cdcfTableRows = listOf(
                    listOf("FP1", "Transformation et conservation des denrées agricoles", "Capacité horaire de la ligne", "Traitement en continu de 5 tonnes/heure de matière première"),
                    listOf("FP2", "Innocuité microbiologique et sécurité alimentaire", "Critères d'hygiène stricts", "Absence totale de pathogènes (Salmonella, Listeria) selon Codex Alimentarius"),
                    listOf("FC1", "Traçabilité ascendante et descendante des lots", "Temps de rappel de lot", "Isolement complet d'un lot suspect en moins de 30 minutes"),
                    listOf("FC2", "Nettoyabilité et hygiène des équipements", "Conception hygiénique EHEDG", "Acier inox alimentaire 316L poli miroir (Ra ≤ 0.8 µm) sans zone de rétention"),
                    listOf("FC3", "Stabilité organoleptique et durée de vie (DLC)", "Activité de l'eau (aw) et pH", "Contrôle précis de l'aw et du pH garantissant une conservation de 12 mois"),
                    listOf("FC4", "Gestion des rejets hydriques et biodégradabilité", "Traitement des effluents de lavage", "Station d'épuration physico-chimique et biologique aux normes CIAPOL")
                ),
                materialsTitle = "Équipements de Procédé Agroalimentaire, Stérilisation et Conditionnement",
                materialsIntro = "Les lignes de fabrication intègrent des matériaux inertes agréés contact alimentaire :",
                materialsTableRows = listOf(
                    listOf("Cuves de stockage et mélangeurs", "Acier inoxydable austénitique AISI 316L avec double enveloppe", "Finition poli miroir", "Inertie chimique totale face à l'acidité et aux alcalins de nettoyage"),
                    listOf("Pasteurisateur à plaques tubulaire", "Échangeur thermique multitubulaire en inox 316L", "Régime turbulent", "Barème de pasteurisation optimisé (85°C pendant 30 secondes)"),
                    listOf("Pompes de transfert sanitaires", "Pompes centrifuges et à lobes rotoriques à démontage rapide", "Garnitures mécaniques sanitaires en carbure de silicium", "Pompage doux sans cisaillement ni altération de texture"),
                    listOf("Système de Nettoyage en Place (NEP / CIP)", "Centrale automatisée 3 phases (Soude chaude, Acide, Rinçage eau osmosée)", "Désinfection thermique", "Décontamination bactériologique intégrale sans démontage de ligne"),
                    listOf("Ensacheuse-conditionneuse sous atmosphère protectrice", "Ligne automatisée de thermoformage et scellage hermétique", "Injection mélange N2 / CO2", "Élimination de l'oxygène résiduel pour empêcher l'oxydation")
                ),
                calculationsTitle = "Calculs Thermiques de Pasteurisation, Bilans Matière et Énergie",
                calculationsBlocks = listOf(
                    "Le barème thermique appliqué pour détruire la flore végétative pathogène est calculé par la valeur pasteurisatrice (VP) : VP = ∫ 10^((T(t) - T_ref) / z) dt. Pour une température de maintien T = 85°C avec T_ref = 70°C et z = 10°C sur une durée de 30 secondes, VP = 10^(1.5) × 0.5 min = 15.8 minutes, assurant une réduction de 7 log des micro-organismes cibles.",
                    "Le bilan matière sur la ligne de pressage et extraction : pour 10 000 kg de fruits bruts réceptionnés, on obtient 6 800 kg de purée raffinée (rendement d'extraction de 68 %), 2 700 kg de déchets solides valorisés en compost agricole et 500 kg de pertes résiduelles.",
                    "Le bilan énergétique de récupération thermique sur l'échangeur de chaleur permet de préchauffer le produit entrant à 65°C en refroidissant le produit pasteurisé, générant une économie de combustible de 42 % sur la chaudière vapeur."
                ),
                componentsTitle = "Plan HACCP, Points Critiques pour la Maîtrise (CCP) et Surveillance",
                componentsIntro = "La sécurité sanitaire s'appuie sur la maîtrise des points critiques identifiés :",
                componentsTableRows = listOf(
                    listOf("CCP 1 - Triage & Épuration", "Élimination des fruits abîmés ou mycotoxinés", "Taux de défauts visuels < 0.5 %"),
                    listOf("CCP 2 - Détecteur de Métaux", "Détecteur électromagnétique haute fréquence sur convoyeur", "Détection : Ferreux 1.0 mm, Non-ferreux 1.2 mm, Inox 1.5 mm"),
                    listOf("CCP 3 - Barème de Pasteurisation", "Enregistreur graphique automatique de température et débit", "Température minimale de sécurité 85°C avec alarme et dérivation"),
                    listOf("CCP 4 - Intégrité du Sertissage / Scellage", "Contrôle destructif par projection d'ombre toutes les heures", "Taux de recouvrement du joint hermétique > 65 %")
                ),
                realizationStepsTitle = "Conduite de la Production Agro-industrielle en Atelier",
                realizationIntro = "La fabrication a suivi les Bonnes Pratiques d'Hygiène et de Fabrication (BPH/BPF) :",
                realizationSteps = listOf(
                    "Réception, Échantillonnage et Analyse Laboratoire" to "Contrôle du taux de sucre (°Brix), de l'acidité titrable, de l'état sanitaire et pesée sur pont-bascule homologué.",
                    "Lavage, Broyage et Extraction" to "Bains successifs avec eau ozonée, désoperculage mécanique, broyage homogène et séparation centrifuge des jus.",
                    "Formulation, Traitement Thermique et Dégazage" to "Ajustement de la recette en cuve de mélange agitée, passage dans le dégazeur sous vide et pasteurisation.",
                    "Conditionnement Aseptique et Étiquetage" to "Remplissage en salle blanche surpressée classe ISO 7, fermeture hermétique et marquage jet d'encre du numéro de lot et DLC."
                ),
                finishingTitle = "Analyses Microbiologiques et Analyses Sensorielles (Panel Dégustation)",
                finishingIntro = "La mise sur le marché d'un lot requiert l'attestation positive des contrôles qualité :",
                finishingSteps = listOf(
                    "Contrôles Microbiologiques Libératoires" to "Ensemencement sur boîtes de Pétri : coliformes totaux < 10 UFC/g, flore aérobie mésophile < 1000 UFC/g (conformité totale).",
                    "Panel de Dégustation Organoleptique" to "Évaluation sensorielle à l'aveugle par 12 jurés formés : score gustatif moyen de 8.6/10 sur l'arôme et l'onctuosité.",
                    "Tests de Vieillissement Accéléré en Étuve" to "Conservation 30 jours à 37°C et 85 % d'humidité relative : aucune altération de couleur ni bombement d'emballage."
                ),
                qualityTitle = "Certifications Internationales de Sécurité Alimentaire",
                qualityIntro = "Les installations s'alignent sur les plus hauts standards mondiaux :",
                qualityItems = listOf(
                    "Système HACCP (Codex Alimentarius)" to "Analyse des dangers et points critiques pour leur maîtrise",
                    "Norme ISO 22000:2018" to "Systèmes de management de la sécurité des denrées alimentaires",
                    "Certification FSSC 22000" to "Référentiel mondial de sécurité des aliments pour les transformateurs",
                    "Réglementation Ivoirienne CODEX-CI" to "Respect des arrêtés interministériels sur le conditionnement des aliments"
                ),
                skillsAcquired = listOf(
                    "Maîtrise du Système HACCP" to "Élaboration de l'arbre de décision des CCP et établissement des limites critiques de surveillance.",
                    "Génie des Procédés Agroalimentaires" to "Optimisation des opérations unitaires (pasteurisation, centrifugation, filtration tangentielle).",
                    "Microbiologie & Chimie Analytique" to "Réalisation des analyses physico-chimiques (Brix, pH, extrait sec, colorimétrie).",
                    "Audit d'Hygiène & Bonnes Pratiques" to "Formation des opérateurs au lavage des mains, tenue stérile et respect de la marche en avant."
                ),
                challengesAndSolutions = listOf(
                    "Défi 1 : Moussage important lors du remplissage à cadence élevée" to "Solution : Réglage de la contre-pression dans la cuve tampon et utilisation de buses de remplissage plongeantes avec coupe-goutte.",
                    "Défi 2 : Encrassement thermique rapide des plaques du pasteurisateur" to "Solution : Modification de la séquence de nettoyage avec adjonction d'une phase de détartrage acide intermédiaire.",
                    "Défi 3 : Fluctuation de la maturité des fruits entrants" to "Solution : Mise en place d'un protocole de tri par réfractométrie et ajustement dynamique du barème de mélange."
                ),
                maintenanceRows = listOf(
                    listOf("Quotidien", "Cycle de Nettoyage en Place (NEP)", "Validation de la concentration des solutions détergentes et pH de rinçage"),
                    listOf("Hebdomadaire", "Vérification des joints d'étanchéité des vannes", "Contrôle d'absence de fissures sur les élastomères EPDM alimentaires"),
                    listOf("Mensuel", "Étalonnage des sondes de température PT100", "Contrôle au bain thermostaté étalon des sondes du pasteurisateur"),
                    listOf("Semestriel", "Remplacement des garnitures mécaniques des pompes", "Maintenance préventive pour éviter les fuites de produit"),
                    listOf("Annuel", "Contrôle d'étanchéité des échangeurs de chaleur", "Recherche de micro-fissures sur plaques par ressuage coloré certifié contact alimentaire")
                ),
                strategicRecommendations = listOf(
                    "Valorisation des Coproduits Solides (Économie Circulaire)" to "Transformer les écorces et pulpes en pectine naturelle ou en biogaz par méthanisation.",
                    "Passage à des Emballages Biosourcés et Recyclables" to "Substituer les barquettes plastiques vierges par des barquettes en PLA compostable ou carton barrière.",
                    "Installation de Capteurs NIR en Ligne (Spectroscopie Proche Infrarouge)" to "Contrôler en continu le taux de sucre et l'humidité sans prélever d'échantillons manuels."
                )
            )

            FiliereType.MECANIQUE_METALLURGIE -> FiliereTechnicalContent(
                filiereCategory = "Génie Mécanique & Productique (Chaudronnerie, Usinage & Métallurgie)",
                cdcfTitle = "Spécifications Techniques et Cahier des Charges Fonctionnel",
                cdcfIntro = "L'ouvrage mécanique doit satisfaire à un ensemble d'exigences opérationnelles rigoureuses définies par l'analyse fonctionnelle :",
                cdcfTableRows = listOf(
                    listOf("FP1", "Filtrer et condamner le passage des véhicules ou flux industriels", "Largeur utile barrée", "4.00 m (avec extension possible à 6 m)"),
                    listOf("FP2", "Permettre une manœuvre fluide et sans grippage", "Effort appliqué par l'opérateur", "< 30 N (manœuvre facile d'une seule main)"),
                    listOf("FC1", "Résister aux sollicitations climatiques et vents violents", "Pression dynamique limite", "120 km/h (soit q = 600 Pa) sans flèche permanente"),
                    listOf("FC2", "Assurer une rotation guidée par liaisons à roulements", "Système de guidage retenu", "Deux paliers à roulements étanches 2RS (norme ISO 15)"),
                    listOf("FC3", "Verrouillage de sécurité en position haute et basse", "Dispositif d'immobilisation", "Broche de cadenassage cadenassable en position fermée"),
                    listOf("FC4", "Visibilité diurne et nocturne", "Signalétique rétroréfléchissante", "Bandes microprismatiques alternées rouge/blanc classe 2")
                ),
                materialsTitle = "Choix et Caractérisation des Matériaux Métalliques",
                materialsIntro = "Les aciers utilisés ont été choisis pour concilier résistance mécanique élevée, excellente soudabilité sans préchauffage et disponibilité dans le stock matière de $comp :",
                materialsTableRows = listOf(
                    listOf("Fût principal vertical", "Tube acier S235JR (100×100×4 mm)", "235 MPa", "Rigidité en torsion et excellente soudabilité"),
                    listOf("Platine d'embase au sol", "Tôle forte S235JR (épaisseur 12 mm)", "235 MPa", "Stabilité à l'arrachement sous couple de vent"),
                    listOf("Arbre pivot de rotation", "Acier étiré mi-dur XC38 (C35)", "370 MPa", "Haute résistance au cisaillement et à la fatigue"),
                    listOf("Lisse basculante ou bras mobile", "Tube rectangulaire acier 80×40×2 mm", "235 MPa", "Compromis idéal entre légèreté et inertie de flexion"),
                    listOf("Contrepoids d'équilibrage", "Plaques d'acier massif S235JR", "235 MPa", "Masse volumique élevée (7850 kg/m³) modulable")
                ),
                calculationsTitle = "Étude Cinématique, Calculs Statiques et Équilibrage RDM",
                calculationsBlocks = listOf(
                    "Considérons la lisse comme une poutre homogène de longueur L = 4.0 m et de masse totale m_lisse = 18 kg. Son centre de gravité G1 se trouve à d1 = 2.0 m de l'axe de pivotement O.",
                    "Le moment de gravité résistant s'exerçant autour de l'axe O en position horizontale vaut :\nM_résistant = m_lisse × g × d1 = 18 kg × 9.81 m/s² × 2.0 m = 353.16 N.m.",
                    "Pour annuler ce moment et ramener l'effort de levée à une valeur minimale (< 30 N), un bras arrière court de longueur d2 = 0.50 m reçoit une masse de contrepoids M_c telle que :\nM_c × g × d2 = M_résistant  =>  M_c = (18 × 2.0) / 0.50 = 72 kg.",
                    "La contrainte de cisaillement maximale dans l'arbre en acier XC38 (Ø 30 mm) sous la charge combinée est de tau = 14.5 MPa, soit une valeur 10 fois inférieure à la résistance pratique au glissement Rpg (150 MPa). Le coefficient de sécurité est s = 10.3."
                ),
                componentsTitle = "Sélection & Montage des Mécanismes à Roulements",
                componentsIntro = "Dans les structures artisanales courantes, la liaison pivot est réalisée par simple frottement sec acier-sur-acier (tube enfilé sur rond plein), provoquant oxydation précoce, grincements et grippage irréversible.",
                componentsTableRows = listOf(
                    listOf("Type de Palier", "Palier applique fonte à 2 trous (UCFL 206)", "Fixation rapide et rattrapage des défauts d'alignement"),
                    listOf("Diamètre de l'arbre", "Alésage intérieur Ø 30 mm avec bague de serrage", "Montage glissant sans presse, blocage par vis pointeaux"),
                    listOf("Étanchéité", "Joints à double lèvre renforcés 2RS", "Protection totale contre l'humidité tropicale et les poussières"),
                    listOf("Lubrification", "Graisse lithium complexe NLGI 2 avec graisseur M6", "Film d'huile hydrofuge stable de -20°C à +120°C"),
                    listOf("Norme de Référence", "ISO 15 / DIN 625 (Roulements à billes radiaux)", "Composants universels remplaçables facilement")
                ),
                realizationStepsTitle = "Processus de Réalisation Pas à Pas en Atelier",
                realizationIntro = "La confection s'est articulée selon quatre étapes technologiques séquentielles :",
                realizationSteps = listOf(
                    "Étape 1 : Traçage et Débitage Mécanique" to "Traçage au réglet inox et pointe carbure sur profilés 100×100×4 mm. Coupe à la scie à ruban avec lubrification émulsionnée pour garantir des coupes perpendiculaires parfaites à 90°.",
                    "Étape 2 : Tournage et Usinage de l'Arbre" to "Usinage de l'arbre Ø 30 mm au tour parallèle, réalisation des chanfreins d'entrée à 15° et des portées de paliers avec tolérance h7.",
                    "Étape 3 : Soudage à l'Arc sous Conduite de l'Encadreur" to "Pointage sur marbre pour bloquer les déformations thermiques. Soudage manuel à l'arc MMA (procédé 111) avec électrodes rutiles E6013 Ø 3.2 mm à un courant de 115 A. Cordon d'angle continu sans reprise pour une étanchéité absolue."
                ),
                finishingTitle = "Ajustage Mécanique, Traitement et Peinture",
                finishingIntro = "Une fois la structure métallique assemblée, les opérations de finition mécanique et de protection surfacique ont été menées :",
                finishingSteps = listOf(
                    "Montage des Paliers et Amortisseurs" to "Fixation des deux paliers UCFL 206 sur le fût par boulons zingués classe 8.8 avec rondelles grower. Insertion de l'arbre, réglage du jeu latéral à 0.5 mm et blocage des vis pointeaux. Pose des butées d'arrêt en caoutchouc néoprène.",
                    "Protection Anticorrosion et Peinture en Binôme" to "1. Brossage mécanique et dégraissage au solvant.\n2. Application d'une première couche de primaire wash-primer antirouille au phosphate de zinc.\n3. Application croisée de deux couches de laque de finition polyuréthane blanche brillante de qualité industrielle.",
                    "Pose de la Signalisation Réfléchissante" to "Pose de bandes adhésives rétro-réfléchissantes microprismatiques alternées rouge et blanc espacées de 30 cm sur les deux faces de la lisse."
                ),
                qualityTitle = "Contrôle Qualité, Métrologie et Normes de Sécurité",
                qualityIntro = "Avant la livraison, le prototype a subi un protocole complet de validation métrologique et de sécurité industrielle :",
                qualityItems = listOf(
                    "Contrôle Dimensionnel" to "Vérification au pied à coulisse vernier des cotes d'usinage (+/- 0.05 mm) et de l'entraxe des paliers (+/- 0.2 mm).",
                    "Contrôle Visuel des Soudures" to "Examen minutieux des cordons selon la norme ISO 5817 (niveau B : absence totale de fissures, caniveaux ou soufflures).",
                    "Essai d'Équilibrage Dynamique" to "Vérification de l'effort de basculement mesuré au peson numérique : F = 22 N (conforme à l'exigence < 30 N).",
                    "Essais d'Endurance en Atelier" to "Exécution de 50 cycles consécutifs d'ouverture et de fermeture sans point dur ni bruit suspect.",
                    "Conformité Normative" to "Application des exigences de la norme européenne NF EN 13241-1 relative aux fermetures et barrières industrielles."
                ),
                skillsAcquired = listOf(
                    "Compétences Métallurgiques" to "Compréhension fine des déformations thermiques lors du soudage et sélection des aciers de construction.",
                    "Compétences d'Usinage" to "Mise en position et maintien des pièces sur tour universel, choix des outils de coupe et vitesses d'avance.",
                    "Compétences Mécaniques" to "Maîtrise des conditions de montage et de lubrification des paliers auto-aligneurs à roulements étanches.",
                    "Travail en Équipe Ouvrière" to "Communication technique claire avec les compagnons d'atelier, respect de la hiérarchie et gestion du stress.",
                    "Rigueur Méthodologique" to "Prise de cotes rigoureuse sur plan et confrontation permanente entre théorie calculatoire et exécution pratique."
                ),
                challengesAndSolutions = listOf(
                    "Défi 1 : Voilage thermique de la platine de base lors du soudage" to "Problème : Le retrait thermique des cordons de soudure continus provoquait un gauchissement de 3 mm de la platine d'embase.\nSolution appliquée : Soudage à pas de pèlerin avec bridage énergique sur marbre en fonte et refroidissement lent sous isolant.",
                    "Défi 2 : Défaut de coaxialité entre les deux paliers appliques" to "Problème : Une légère dérive de perçage (+ 0.8 mm) créait une contrainte de torsion lors de l'enfilement de l'arbre.\nSolution appliquée : Utilisation des paliers auto-aligneurs UCFL permettant une rotulation angulaire jusqu'à 2° et calage micrométrique avec des clinquants de laiton.",
                    "Défi 3 : Forte hygrométrie perturbant le séchage de la peinture" to "Problème : Humidité de l'air abidjanais ralentissant la polymérisation de la laque polyuréthane.\nSolution appliquée : Aménagement d'une zone temporaire ventilée avec projecteurs halogènes pour maintenir une température de séchage stable à 35°C."
                ),
                maintenanceRows = listOf(
                    listOf("Chaque trimestre", "Paliers à roulements UCFL", "Injection de graisse lithium via graisseur et vérification visuelle des joints 2RS"),
                    listOf("Chaque semestre", "Visserie d'ancrage et goujons M16", "Contrôle du couple de serrage à la clé dynamométrique (valeur nominale : 85 N.m)"),
                    listOf("Chaque semestre", "Butées d'arrêt élastomères", "Contrôle de l'élasticité et détection d'écrasement ou craquelures sous UV"),
                    listOf("Chaque année", "Revêtement et bandes rétroréfléchissantes", "Nettoyage à l'eau savonneuse, retouche antirouille en cas de choc accidentel"),
                    listOf("Biennal", "Axe de pivot Ø 30 mm", "Contrôle du jeu axial (maxi 1 mm) et resserrage des vis pointeaux de bague")
                ),
                strategicRecommendations = listOf(
                    "1. Gabarit Universel de Perçage des Platines (Gain de 40% de temps)" to "Conception et fabrication en atelier d'un gabarit de perçage fixe pour les platines UCFL et embases, éliminant les erreurs manuelles de traçage et divisant par deux le temps d'ajustage.",
                    "2. Évolution vers un Kit d'Automatisation Solaire Photovoltaïque" to "Intégration facultative d'un moto-réducteur 12V DC alimenté par un panneau solaire 50W et une batterie gel pour les chantiers isolés sans raccordement au réseau électrique national.",
                    "3. Formalisation de la Fiche Technique Standardisée" to "Rédaction d'une fiche produit commerciale et technique valorisant auprès des clients la présence de roulements étanches par rapport aux structures concurrentes rudimentaires."
                )
            )
        }
    }

    fun generateJuryQuestions(student: StudentConfig, company: CompanyConfig): List<JuryQuestion> {
        val type = detectFiliereType(student.filiereOption, student.theme)
        val cName = company.inputName.ifBlank { "votre entreprise d'accueil" }
        val theme = student.theme.ifBlank { "ce projet de stage" }

        val baseQuestions = mutableListOf(
            JuryQuestion(
                question = "Pouvez-vous résumer en moins de 2 minutes la problématique centrale de votre stage chez $cName et votre apport personnel ?",
                category = "Méthodologie & Synthèse",
                suggestedAnswer = "« Face aux contraintes d'exploitation constatées sur le terrain, l'objectif central était de fiabiliser les installations en substituant les solutions artisanales par des solutions d'ingénierie normées. Mon apport personnel a consisté en l'étude dimensionnelle, le choix des composants agréés, la réalisation pratique supervisée et la mise en place d'un protocole de maintenance préventive. »",
                juryExpectation = "Le jury cherche à évaluer votre esprit de synthèse, votre capacité à prendre du recul et à distinguer votre travail personnel de celui des techniciens de l'entreprise.",
                pitfallsToAvoid = "Éviter de raconter votre journée type de manière chronologique. Allez droit au but : Problème initial -> Solution technique mise en œuvre -> Bénéfices mesurables."
            ),
            JuryQuestion(
                question = "Quels ont été vos critères de choix pour dimensionner les composants principaux plutôt qu'une solution alternative plus économique ?",
                category = "Calculs & Dimensionnement",
                suggestedAnswer = "« Le choix a été guidé par un compromis rigoureux entre le coût de revient initial et le coût global de possession (LCC). Une solution bas de gamme à frottement direct ou sous-dimensionnée engendre des arrêts d'exploitation fréquents et un renouvellement précoce. Les coefficients de sécurité retenus garantissent une disponibilité opérationnelle supérieure sans surdimensionnement inutile. »",
                juryExpectation = "Démontrer que vous maîtrisez les calculs de dimensionnement présentés dans le rapport et que vous ne vous êtes pas contenté de copier des catalogues fournisseurs.",
                pitfallsToAvoid = "Ne dites jamais 'C'est le chef d'atelier qui m'a dit de faire comme ça'. Justifiez toujours par les calculs, les normes en vigueur et la durabilité."
            ),
            JuryQuestion(
                question = "Quelle a été la difficulté technique la plus critique rencontrée durant votre stage et comment l'avez-vous résolue ?",
                category = "Résolution de Problèmes",
                suggestedAnswer = "« L'aléa majeur a porté sur la non-conformité géométrique ou l'environnement sévère rencontré lors des phases d'assemblage/pose. Après concertation avec mon maître de stage, nous avons opté pour une adaptation méthodologique [détailler la solution : calage, modification d'ordre de soudage ou filtrage], ce qui a permis d'éliminer le défaut sans impacter les délais de livraison. »",
                juryExpectation = "Le jury teste votre honnêteté intellectuelle et votre capacité d'analyse face à l'imprévu industriel. Un stage sans aucune difficulté n'est pas crédible.",
                pitfallsToAvoid = "Ne blâmez pas l'entreprise ni le manque de matériel. Présentez le problème comme un cas d'apprentissage formateur surmonté avec méthode."
            ),
            JuryQuestion(
                question = "Quelles mesures de sécurité et règles HSE avez-vous personnellement appliquées sur les postes de travail ?",
                category = "Sécurité & HSE",
                suggestedAnswer = "« Le port intégral des Équipements de Protection Individuelle (EPI) appropriés à chaque phase de travail était scrupuleusement respecté. Avant chaque opération délicate, une analyse rapide des risques (chute, projection, électrisation, vapeurs) et la consignation des énergies étaient effectuées sous supervision. »",
                juryExpectation = "La sécurité des personnes est un critère éliminatoire en soutenance. Le jury vérifie votre maturité professionnelle.",
                pitfallsToAvoid = "Ne minimisez jamais un risque professionnel en disant 'ce n'était pas dangereux'."
            ),
            JuryQuestion(
                question = "Si l'entreprise souhaitait industrialiser ou pérenniser votre projet, quelles recommandations formuleriez-vous en priorité ?",
                category = "Apports Stratégiques",
                suggestedAnswer = "« Je préconise trois axes prioritaires : 1. La création de gabarits d'usinage/montage standardisés pour diviser par deux le temps de préparation ; 2. L'intégration de capteurs de suivi prédictif IoT ; 3. La formalisation d'une notice d'exploitation technique pour fidéliser les clients. »",
                juryExpectation = "Évaluer votre posture d'ingénieur/technicien supérieur capable de formuler une valeur ajoutée stratégique pour son entreprise.",
                pitfallsToAvoid = "Ne concluez pas que le projet est 'parfait et achevé'. Tout projet d'ingénierie possède des perspectives d'amélioration continue."
            )
        )

        val specificQuestion = when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> JuryQuestion(
                question = "Comment justifiez-vous le choix du schéma de liaison à la terre (SLT) et le pouvoir de coupure de votre disjoncteur général ?",
                category = "Spécifique Électrotechnique",
                suggestedAnswer = "« Le schéma TT a été maintenu pour garantir la coupure automatique au premier défaut d'isolement par disjoncteurs différentiels sans nécessité d'un service électrique permanent sur site. Le pouvoir de coupure Icu = 36 kA est supérieur au courant de court-circuit Icc max calculé en tête d'installation (14.2 kA), garantissant la non-destruction de l'appareil. »",
                juryExpectation = "Vérifier la maîtrise de la norme NF C 15-100 et le dimensionnement de la sélectivité.",
                pitfallsToAvoid = "Ne confondez pas courant assigné In et pouvoir de coupure ultime Icu."
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> JuryQuestion(
                question = "Sur quelles hypothèses géotechniques avez-vous dimensionné les semelles de fondation et calculé la section des armatures tendues ?",
                category = "Spécifique Génie Civil",
                suggestedAnswer = "« Le rapport de sol préconisait une contrainte admissible de 0.25 MPa à -1.20 m de profondeur. À l'ELU, la charge combinée Pu détermine la surface portante requise. Le ferraillage en nappe inférieure a été calculé par la méthode des bielles avec de l'acier haute adhérence FeE500 et un enrobage de 35 mm garantissant la pérennité contre l'agression des eaux souterraines. »",
                juryExpectation = "Maîtrise du BAEL 91 / Eurocode 2 et compréhension du rôle des armatures vis-à-vis des contraintes de traction.",
                pitfallsToAvoid = "N'oubliez pas d'évoquer l'enrobage pour la protection contre la corrosion des armatures."
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> JuryQuestion(
                question = "Comment assurez-vous la sécurité des transactions et la scalabilité horizontale de votre architecture lors des pics de charge ?",
                category = "Spécifique Informatique & Réseaux",
                suggestedAnswer = "« La scalabilité est garantie par le découpage en conteneurs sans état (stateless) sous Docker avec orchestrateur capable d'auto-scaler les réplicas. La sécurité repose sur un reverse-proxy avec chiffrement TLS 1.3, authentification JWT avec durée de vie courte et isolation stricte des bases de données dans un VLAN dédié non exposé sur l'internet public. »",
                juryExpectation = "Vérifier que vous maîtrisez la différence entre scalabilité verticale et horizontale, et les principes du Zero Trust.",
                pitfallsToAvoid = "Ne parlez pas uniquement du code frontend ; montrez que vous comprenez l'architecture serveur et la sécurité des données."
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> JuryQuestion(
                question = "Pourquoi avoir opté pour la formule de Wilson et comment avez-vous calibré votre stock de sécurité face aux aléas de livraison ?",
                category = "Spécifique Supply Chain",
                suggestedAnswer = "« La formule de Wilson minimise la somme du coût de passation et du coût de possession du stock. Le stock de sécurité intègre l'écart-type de la demande durant le délai de réapprovisionnement pour atteindre un taux de service cible de 95 %, évitant ainsi les ruptures sur les références de classe A. »",
                juryExpectation = "Démontrer la compréhension des modèles probabilistes de gestion des stocks et de la loi de Pareto (20/80 - ABC).",
                pitfallsToAvoid = "Ne confondez pas stock minimum, stock d'alerte et stock de sécurité."
            )
            FiliereType.MINES_PETROLE_PROCEDES -> JuryQuestion(
                question = "Comment avez-vous déterminé la surépaisseur de corrosion et validé la conformité de vos appareils sous pression à l'ASME ?",
                category = "Spécifique Mines & Procédés",
                suggestedAnswer = "« La surépaisseur de corrosion de 3.0 mm a été calculée sur la base d'un taux de corrosion estimé à 0.12 mm/an sur une durée d'exploitation de 25 ans en milieu H2S/CO2. L'épaisseur finale calculée selon la formule de l'ASME B31.3 reste inférieure à l'épaisseur nominale du tube Sch 40 sélectionné. »",
                juryExpectation = "Maîtrise des calculs de tenue mécanique sous pression et connaissance des normes ASME / API.",
                pitfallsToAvoid = "Ne négligez pas l'impact de la température sur la contrainte admissible du métal."
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> JuryQuestion(
                question = "Comment justifiez-vous le barème de pasteurisation retenu et la détermination des points critiques de contrôle (CCP) ?",
                category = "Spécifique Agroalimentaire",
                suggestedAnswer = "« Le barème de 85°C pendant 30 secondes garantit une valeur pasteurisatrice VP > 15 minutes, suffisante pour une destruction d'au moins 6 log de la flore pathogène de référence. Ce point constitue un CCP strict car son dysfonctionnement entraînerait un risque direct pour la santé du consommateur. »",
                juryExpectation = "Maîtrise des cinétiques de destruction thermique (D et z) et de la logique de l'arbre de décision HACCP.",
                pitfallsToAvoid = "Ne confondez pas pasteurisation et stérilisation thermique."
            )
            FiliereType.MECANIQUE_METALLURGIE -> JuryQuestion(
                question = "Pourquoi avoir privilégié des paliers auto-aligneurs à roulements UCFL plutôt qu'un système classique de bague bronze ou coussinet sec ?",
                category = "Spécifique Mécanique",
                suggestedAnswer = "« Le coefficient de frottement d'un roulement à billes (µ ≈ 0.0015) est environ 100 fois inférieur à celui d'un contact acier-sur-acier (µ ≈ 0.15). Cela a permis de diviser l'effort de levée par cinq tout en supprimant le risque de grippage par oxydation tropicale grâce aux joints étanches 2RS. De plus, la rotule en fonte compense les défauts d'alignement de perçage jusqu'à 2°. »",
                juryExpectation = "Vérifier la compréhension de la tribologie, des liaisons mécaniques et de la résistance à la corrosion.",
                pitfallsToAvoid = "Ne dites pas simplement 'c'est plus moderne'. Donnez des chiffres de réduction du couple et de durée de vie."
            )
        }

        baseQuestions.add(3, specificQuestion)
        return baseQuestions
    }

    fun performComplianceAudit(
        student: StudentConfig,
        company: CompanyConfig
    ): ComplianceAuditResult {
        val mentions = mutableListOf<String>()
        val issues = mutableListOf<ComplianceAuditIssue>()
        var score = 100

        // Check 1: Titre officiel et filière
        if (student.theme.isNotBlank() && student.theme.length > 20) {
            mentions.add("Thème officiel complet et formulé en langage technique d'ingénierie")
        } else {
            score -= 10
            issues.add(
                ComplianceAuditIssue(
                    title = "Thème de stage trop court ou imprécis",
                    description = "Le titre du mémoire doit obligatoirement expliciter la problématique et le livrable technique final.",
                    isCritical = true,
                    recommendation = "Formuler un titre débutant par 'ÉTUDE, CONCEPTION ET RÉALISATION DE...'"
                )
            )
        }

        // Check 2: Nom de l'étudiant
        if (student.studentName.isNotBlank() && student.studentName.contains(" ")) {
            mentions.add("Identité académique de l'étudiant complète (Nom & Prénoms)")
        } else {
            score -= 5
            issues.add(
                ComplianceAuditIssue(
                    title = "Nom de l'étudiant incomplet",
                    description = "Renseignez le nom de famille en majuscules suivi des prénoms complets.",
                    recommendation = "Exemple : KOUAME KOUASSI JEAN-LUC"
                )
            )
        }

        // Check 3: Établissement
        if (student.schoolName.isNotBlank()) {
            mentions.add("Établissement d'enseignement supérieur d'attache renseigné : ${student.schoolName}")
        } else {
            score -= 8
            issues.add(
                ComplianceAuditIssue(
                    title = "Nom de l'université / institut manquant",
                    description = "La page de garde exige la mention formelle de l'université ou grande école de rattachement.",
                    recommendation = "Exemple : INP-HB Yamoussoukro, Université FHB, ESBTP"
                )
            )
        }

        // Check 4: Entreprise et identification juridique
        if (company.inputName.isNotBlank()) {
            mentions.add("Raison sociale de l'entreprise d'accueil identifiée : ${company.inputName}")
            if (company.rccmBank.isNotBlank() && company.rccmBank.contains("RCCM")) {
                mentions.add("Numéro légal d'enregistrement RCCM renseigné")
            } else {
                score -= 4
                issues.add(
                    ComplianceAuditIssue(
                        title = "Immatriculation RCCM de l'entreprise manquante",
                        description = "Pour les soutenances en Côte d'Ivoire / zone OHADA, l'immatriculation de la structure d'accueil renforce la crédibilité du mémoire.",
                        recommendation = "Cliquez sur 'Recherche Intelligente' ou renseignez le numéro RCCM."
                    )
                )
            }
        } else {
            score -= 15
            issues.add(
                ComplianceAuditIssue(
                    title = "Entreprise d'accueil non renseignée",
                    description = "Le nom de la structure d'accueil est obligatoire pour générer les attestations et organigrammes.",
                    isCritical = true,
                    recommendation = "Indiquez l'entreprise dans le champ dédié."
                )
            )
        }

        // Check 5: Tuteurs académique et professionnel
        if (student.internshipTutor.isNotBlank() && student.academicTutor.isNotBlank()) {
            mentions.add("Binôme d'encadrement valide (Maître de stage professionnel & Tuteur académique)")
        } else {
            score -= 7
            issues.add(
                ComplianceAuditIssue(
                    title = "Tuteurs non renseignés ou incomplets",
                    description = "Le rapport de stage doit obligatoirement mentionner les deux encadreurs avec leurs titres ou fonctions.",
                    recommendation = "Exemple : M. BROU PRI (Chef d'Atelier) & Dr. KOUASSI (Enseignant-Chercheur)"
                )
            )
        }

        // Check 6: Filière d'études
        if (student.filiereOption.isNotBlank()) {
            mentions.add("Filière et option technique identifiées : ${student.filiereOption}")
        } else {
            score -= 6
            issues.add(
                ComplianceAuditIssue(
                    title = "Filière / Spécialité d'études manquante",
                    description = "La filière détermine la typologie des 28 pages et les barèmes de notation du jury.",
                    recommendation = "Précisez votre filière (ex : Génie Mécanique, Électrotechnique, Génie Civil, Informatique)."
                )
            )
        }

        // Standard academic mentions
        mentions.add("Structure canonique 28 pages avec table des matières et procès-verbal final")
        mentions.add("Typographie universitaire conforme (Normes de police, titrages et marges A4)")
        mentions.add("Fiche de relevé des chantiers (Système Lieu - Cause) intégrée")

        val status = when {
            score >= 90 -> "CONFORMITÉ ACADÉMIQUE EXCELLENTE (MENTION TRÈS BIEN)"
            score >= 75 -> "CONFORMITÉ ACADÉMIQUE BONNE (QUELQUES AJUSTEMENTS MINEURS)"
            else -> "CONFORMITÉ INSUFFISANTE (RÉVISIONS REQUISES AVANT SOUTENANCE)"
        }

        return ComplianceAuditResult(
            score = score.coerceIn(0, 100),
            mentionsPresent = mentions,
            missingOrWeakPoints = issues,
            academicStatus = status,
            adviceForDefense = if (score >= 90) {
                "Votre dossier est irréprochable sur le plan formel. Concentrez votre préparation sur l'exposé oral de 15 minutes et l'anticipation des questions du jury."
            } else {
                "Complétez les éléments signalés en jaune ou rouge avant d'exporter le document PDF final pour garantir la note maximale."
            }
        )
    }

    fun resolveGlossary(type: FiliereType, company: CompanyConfig): List<Pair<String, String>> {
        val cName = company.inputName.ifBlank { "ENTREPRISE D'ACCUEIL" }
        val rccm = if (company.rccmBank.isNotBlank()) company.rccmBank else "RCCM Enregistrement officiel"

        val common = listOf(
            cName to "${company.companyFullTitle.ifBlank { cName }} (${company.activities.ifBlank { "Entreprise d'accueil" }})",
            "RCCM" to "Registre du Commerce et du Crédit Mobilier ($rccm)",
            "HSE / EPI" to "Hygiène, Sécurité, Environnement / Équipements de Protection Individuelle",
            "CCTP / CdCF" to "Cahier des Clauses Techniques Particulières / Cahier des Charges Fonctionnel"
        )

        val specific = when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> listOf(
                "NF C 15-100" to "Norme française réglementant les installations électriques basse tension",
                "SLT" to "Schéma de Liaison à la Terre (Régimes TT, TN-C, TN-S, IT)",
                "Icu / Ics" to "Pouvoir de coupure ultime / assigné en service d'un disjoncteur (kA)",
                "kVA / kW" to "Kilovoltampère (Puissance apparente S) / Kilowatt (Puissance active P)",
                "cos φ" to "Facteur de puissance mesurant le déphasage tension/courant",
                "DDR" to "Dispositif Différentiel à courant Résiduel de protection des personnes",
                "TGBT" to "Tableau Général Basse Tension alimentant l'ensemble de l'usine",
                "Modbus-RTU" to "Protocole industriel de communication sur bus série RS485"
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> listOf(
                "BAEL 91" to "Béton Armé aux États Limites (Règles de calcul et de vérification)",
                "ELU / ELS" to "État Limite Ultime (Résistance) / État Limite de Service (Fissuration & Déformation)",
                "fc28" to "Résistance caractéristique à la compression du béton à 28 jours (MPa)",
                "HA" to "Barres d'armature d'acier à Haute Adhérence (FeE500)",
                "Mf / ES" to "Module de finesse du sable / Équivalent de Sable pour contrôle de propreté",
                "BIM" to "Building Information Modeling (Modélisation numérique 3D collaborative)",
                "PPSPS" to "Plan Particulier de Sécurité et de Protection de la Santé sur chantier",
                "DTU" to "Document Technique Unifié fixant les règles de l'art du bâtiment"
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> listOf(
                "REST / API" to "Representational State Transfer / Application Programming Interface",
                "JWT / TLS" to "JSON Web Token (Authentification sans état) / Transport Layer Security 1.3",
                "CI / CD" to "Continuous Integration / Continuous Deployment (Pipelines automatisés)",
                "VLAN / VPN" to "Virtual Local Area Network / Virtual Private Network sécurisé",
                "SLA / QoS" to "Service Level Agreement (Taux de disponibilité) / Quality of Service",
                "CRUD" to "Create, Read, Update, Delete (Opérations de persistance fondamentales)",
                "OWASP" to "Open Web Application Security Project (Référentiel des failles web)",
                "Docker / K8s" to "Conteneurisation d'applications légères et orchestration de microservices"
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> listOf(
                "WMS / TMS" to "Warehouse Management System / Transport Management System",
                "ERP" to "Enterprise Resource Planning (Progiciel de Gestion Intégré)",
                "FIFO / LIFO" to "First In, First Out / Last In, First Out (Valorisation des flux de stocks)",
                "ABC / Pareto" to "Classification des références selon la loi des 20/80 de valeur financière",
                "SS / SM" to "Stock de Sécurité face aux aléas / Stock Minimum de réapprovisionnement",
                "OTIF" to "On-Time In-Full (Indicateur clé de performance des livraisons complètes)",
                "BL / LTA" to "Bon de Livraison / Lettre de Transport Aérien pour transit international",
                "LCC" to "Life Cycle Costing (Calcul du coût global de possession des équipements)"
            )
            FiliereType.MINES_PETROLE_PROCEDES -> listOf(
                "ASME B31.3" to "Norme internationale de calcul de tuyauterie sous pression de procédés",
                "API 650" to "American Petroleum Institute : Conception des réservoirs de stockage",
                "HAZOP" to "Hazard and Operability Study (Analyse systématique des risques de procédés)",
                "P&ID" to "Piping and Instrumentation Diagram (Schéma de tuyauterie et instrumentation)",
                "H2S / CO2" to "Sulfure d'hydrogène (gaz toxique corrosif) / Dioxyde de carbone",
                "NPSH" to "Net Positive Suction Head (Pression nette à l'aspiration des pompes)",
                "Sch 40 / 80" to "Schedule (Désignation normalisée de l'épaisseur nominale des tubes)",
                "ESD" to "Emergency Shutdown System (Système d'arrêt d'urgence sécuritaire)"
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> listOf(
                "HACCP" to "Hazard Analysis Critical Control Point (Analyse des dangers et points critiques)",
                "ISO 22000" to "Systèmes de management de la sécurité des denrées alimentaires",
                "CCP" to "Critical Control Point (Point critique pour la maîtrise sanitaire absolue)",
                "VP / VS" to "Valeur Pasteurisatrice / Valeur Stérilisatrice en minutes équivalentes",
                "CIP / NEP" to "Cleaning In Place / Nettoyage En Place automatisé des circuits",
                "pH / Aw" to "Potentiel hydrogène (Acidité) / Activité de l'eau conditionnant les germes",
                "DLC / DDM" to "Date Limite de Consommation / Date de Durabilité Minimale",
                "BPA / BPF" to "Bonnes Pratiques Agricoles / Bonnes Pratiques de Fabrication agroalimentaire"
            )
            FiliereType.MECANIQUE_METALLURGIE -> listOf(
                "DAO / CAO" to "Dessin Assisté par Ordinateur / Conception Assistée par Ordinateur",
                "RDM" to "Résistance Des Matériaux (dimensionnement et calcul des contraintes)",
                "FAST" to "Function Analysis System Technique (traduction des fonctions en solutions)",
                "S235JR" to "Acier de construction métallique non allié (limite élastique Re = 235 MPa)",
                "XC38 (C35)" to "Acier mi-dur non allié d'usage mécanique pour axes et arbres de guidage",
                "2RS" to "Double bague d'étanchéité en élastomère protégeant des poussières et de l'eau",
                "UCFL 206" to "Palier applique ovale en fonte à deux trous taraudés avec roulement auto-aligneur",
                "MMA / SMAW" to "Manual Metal Arc Welding (soudage manuel à l'arc avec électrode enrobée)"
            )
        }

        return common + specific
    }

    fun resolveExecutiveSummary(type: FiliereType, theme: String, companyName: String): String {
        val t = theme.ifBlank { "Projet technique de fin d'études" }
        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE ->
                "1. Contexte : Fiabilisation énergétique et conformité normative NF C 15-100 des installations de $companyName.\n" +
                "2. Mission : Conception, dimensionnement des puissances (Ib = 62.8 A) et câblage de l'armoire de distribution.\n" +
                "3. Calculs : Chute de tension limitée à 1.09 % (< 5 %) et pouvoir de coupure Icu = 36 kA adapté au court-circuit amont.\n" +
                "4. Résultats : Continuité de service sécurisée, sélectivité totale et conformité validée au consuel."

            FiliereType.GENIE_CIVIL_BATIMENT ->
                "1. Contexte : Suivi d'exécution et calcul structurel de l'ouvrage pour $companyName.\n" +
                "2. Mission : Descente de charges BAEL/Eurocode 2, dimensionnement des semelles de fondation et ferraillage.\n" +
                "3. Calculs : Charge ultime Pu = 294 kN, portance de sol q_adm = 0.25 MPa et ferraillage quadrillé 6 HA 10.\n" +
                "4. Résultats : Résistance à 28 jours fc28 = 28.4 MPa (> 25 MPa prescrits), aplomb parfait et zéro fissuration."

            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS ->
                "1. Contexte : Modernisation et sécurisation du système d'information de $companyName.\n" +
                "2. Mission : Architecture logicielle en microservices conteneurisés Docker, API REST et sécurisation TLS 1.3 / JWT.\n" +
                "3. Performance : Temps de réponse moyen < 120 ms sur 1 000 requêtes concurrentes et disponibilité SLA 99.9 %.\n" +
                "4. Résultats : Déploiement CI/CD automatisé, segmentation réseau VLAN étanche et zéro faille critique OWASP."

            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY ->
                "1. Contexte : Optimisation des flux d'approvisionnement et réduction des coûts de stockage chez $companyName.\n" +
                "2. Mission : Réorganisation des aires d'entreposage selon la méthode ABC et modélisation des commandes (Wilson).\n" +
                "3. Calculs : Réduction des ruptures sur la classe A avec stock de sécurité calibré pour un taux de service de 95 %.\n" +
                "4. Résultats : Gain de 22 % sur les délais de préparation des commandes et diminution de 15 % du coût de possession."

            FiliereType.MINES_PETROLE_PROCEDES ->
                "1. Contexte : Fiabilisation des circuits fluides sous pression et maîtrise du risque de corrosion chez $companyName.\n" +
                "2. Mission : Dimensionnement de tuyauteries ASME B31.3 et mise en place de la surveillance H2S / hydrocarbures.\n" +
                "3. Calculs : Surépaisseur de corrosion 3.0 mm garantissant une durée de vie de 25 ans sous pression nominale.\n" +
                "4. Résultats : Épreuve hydraulique réussie à 1.5 fois la pression de service et traçabilité intégrale des soudures."

            FiliereType.AGROALIMENTAIRE_AGRONOMIE ->
                "1. Contexte : Mise en conformité de la ligne de transformation et assurance qualité hygiène chez $companyName.\n" +
                "2. Mission : Établissement du plan HACCP, validation des barèmes thermiques de pasteurisation et points critiques (CCP).\n" +
                "3. Calculs : Valeur pasteurisatrice VP > 15 min garantissant la réduction de 6 log des bactéries pathogènes cibles.\n" +
                "4. Résultats : Taux de conformité microbiologique de 100 %, certification sanitaire et allongement maîtrisé de la DLC."

            FiliereType.MECANIQUE_METALLURGIE ->
                "1. Contexte : Sécurisation périmétrique des sites d'accès et installations de $companyName.\n" +
                "2. Innovation : Remplacement du frottement sec artisanal par des paliers à roulements étanches auto-aligneurs.\n" +
                "3. RDM & Dimensionnement : Équilibrage optimal du bras de 4 m par contrepoids de 72 kg (effort < 30 N).\n" +
                "4. Résultats : Zéro coincement mécanique, résistance au vent tropical (120 km/h) et longévité > 10 ans."
        }
    }

    fun resolveGeneralConclusion(type: FiliereType, theme: String, companyName: String): List<String> {
        val t = theme.ifBlank { "mission de stage de fin de cycle" }
        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> listOf(
                "Le stage effectué au sein de $companyName a permis de matérialiser concrètement les théories électrotechniques enseignées durant le cursus académique en réalisations industrielles fiables et normées.",
                "L'ensemble des objectifs fixés par le cahier des charges a été rigoureusement atteint : le dimensionnement des conducteurs cuivre, la coordination des protections magnéto-thermiques et différentielles garantissent une sécurité optimale des opérateurs et des équipements conformément à la NF C 15-100.",
                "Au-delà des calculs et du câblage, cette expérience a développé notre autonomie professionnelle, le respect strict des procédures de consignation électrique et la capacité à dialoguer avec les organismes de contrôle.",
                "Les perspectives d'avenir identifiées, notamment l'intégration du monitoring énergétique IoT et le pilotage automatisé des sources de secours, ouvrent des opportunités d'amélioration continue durables pour $companyName."
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> listOf(
                "Cette mission sur les chantiers et au bureau d'études de $companyName a constitué une immersion exceptionnelle au cœur des réalités du bâtiment et des travaux publics.",
                "L'application rigoureuse des règles de l'Eurocode 2 et du BAEL 91 a permis de valider la descente de charges, la stabilité des semelles de fondation et la qualité du ferraillage mis en œuvre sous conditions climatiques tropicales.",
                "La confrontation aux aléas de chantier (intempéries, retards de livraison, tolérances d'implantation) a forgé notre capacité de réaction, de management d'équipes ouvrières et d'assurance qualité.",
                "Les recommandations formulées pour l'adoption du BIM et l'optimisation des formulations de bétons locaux permettront à $companyName de consolider sa compétitivité sur les grands marchés d'infrastructures."
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> listOf(
                "Cette immersion pratique au sein de $companyName a permis de concevoir, développer et déployer une architecture informatique robuste, sécurisée et évolutive.",
                "L'application des principes modernes du génie logiciel et de la cyber-sécurité (conteneurisation Docker, chiffrement TLS 1.3, authentification JWT, conformité OWASP) apporte une réponse pérenne aux besoins métiers de l'entreprise.",
                "L'intégration d'un pipeline CI/CD automatisé réduit drastiquement les délais de mise en production tout en garantissant la qualité du code par des tests unitaires et d'intégration systématiques.",
                "Ce stage confirme l'importance capitale d'allier rigueur algorithmique, vision d'architecture système et écoute active des besoins des utilisateurs finaux."
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> listOf(
                "Cette expérience au sein de la chaîne logistique de $companyName a permis de rationaliser les flux physiques et d'information avec un impact économique direct et mesurable.",
                "La mise en place de la méthode ABC, le calibrage scientifique des stocks de sécurité et l'application du modèle de Wilson ont concrètement réduit les ruptures tout en allégeant la trésorerie immobilisée.",
                "Nous avons acquis une vision transversale de la supply chain, de la passation de commande fournisseur jusqu'à la livraison finale aux clients dans le respect des délais et des normes de sécurité.",
                "L'introduction préconisée d'outils de traçabilité RFID et de tableaux de bord de pilotage dynamique dotera $companyName d'un avantage concurrentiel déterminant."
            )
            FiliereType.MINES_PETROLE_PROCEDES -> listOf(
                "Le stage réalisé dans les installations industrielles de $companyName a démontré la rigueur absolue exigée dans l'industrie pétrolière, minière et de procédés.",
                "Les calculs de résistance selon l'ASME B31.3 et la détermination des surépaisseurs de corrosion assurent une exploitation fiable des lignes fluides face aux agents chimiques corrosifs et aux hautes pressions.",
                "La culture HSE SEVESO, la maîtrise des études de dangers HAZOP et les procédures d'isolement énergétique ont été au cœur de chaque décision technique prise sur site.",
                "Les axes d'amélioration préconisés permettront à $companyName de maximiser le taux de disponibilité opérationnelle de ses unités tout en préservant l'intégrité de l'environnement."
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> listOf(
                "Ce projet mené au sein de l'unité de transformation de $companyName a permis de concilier productivité industrielle et respect intransigeant des critères d'hygiène et de sécurité sanitaire.",
                "L'élaboration de la méthode HACCP, l'identification des points critiques de contrôle (CCP) et la validation des cinétiques de pasteurisation apportent une garantie scientifique à la qualité des produits finis.",
                "Cette immersion a enrichi nos compétences en biochimie alimentaire, en métrologie de laboratoire et en management de la qualité certifiée ISO 22000.",
                "Les perspectives d'automatisation du nettoyage en place (NEP) et de valorisation des coproduits consolideront la rentabilité et la durabilité écologique de l'entreprise."
            )
            FiliereType.MECANIQUE_METALLURGIE -> listOf(
                "Le projet d'étude, conception et fabrication mené au sein des ateliers d'ICC Corporate illustre la parfaite articulation entre l'analyse théorique de résistance des matériaux et le savoir-faire ouvrier métallurgique.",
                "L'innovation décisive consistant à remplacer la liaison pivot rustique par des paliers à roulements étanches auto-aligneurs apporte une solution durable, sans grippage et parfaitement équilibrée.",
                "Ce stage a profondément développé notre rigueur dimensionnelle, la maîtrise du soudage à l'arc sous encadrement qualifié et le respect scrupuleux des normes de sécurité machine.",
                "L'ouvrage réalisé et validé sur banc d'essais constitue une référence industrielle tangible démontrant notre pleine aptitude à exercer les fonctions de technicien supérieur / ingénieur en génie mécanique."
            )
        }
    }

    fun resolveBibliography(type: FiliereType): List<String> {
        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> listOf(
                "1. GUERRIN M., Guide Pratique de l'Électricien : Installations Basse Tension et Norme NF C 15-100, Dunod.",
                "2. CHAUVEAU D., Distribution de l'Énergie Électrique et Schémas de Liaison à la Terre, Éditions Casteilla.",
                "3. SCHNEIDER ELECTRIC, Cahiers Techniques : Guide de Conception des Réseaux Électriques Industriels.",
                "4. Norme NF C 15-100 : Installations électriques à basse tension - Règles de conception et d'épreuve.",
                "5. Norme CEI 61439-1 / 2 : Ensembles d'appareillage à basse tension de puissance.",
                "6. Norme NF C 18-510 : Opérations sur les ouvrages et installations électriques (Prévention du risque électrique)."
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> listOf(
                "1. PERCHAT J. & CALGARO J-A., Traité de Béton Armé selon l'Eurocode 2, Éditions du Moniteur.",
                "2. MOUGIN J-P., BAEL 91 Révisé 99 : Théorie et Applications aux Bâtiments, Eyrolles.",
                "3. PHILIPPONNAT G. & HUBERT B., Fondations et Ouvrages en Terre : Pratique de la Géotechnique, Eyrolles.",
                "4. Norme NF EN 1992-1-1 (Eurocode 2) : Calcul des structures en béton armé et précontraint.",
                "5. Norme NF EN 206-1 : Béton - Spécification, performance, production et conformité.",
                "6. Guide Technique du BTP : Règles de l'art de la construction tropicale, INP-HB Yamoussoukro."
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> listOf(
                "1. TANENBAUM A. & WETHERALL D., Réseaux Informatiques et Protocoles Internet, 5e Édition, Pearson.",
                "2. MARTIN R. C., Clean Architecture: A Craftsman's Guide to Software Structure and Design, Prentice Hall.",
                "3. OWASP Foundation, Top 10 Web Application Security Risks and Countermeasures.",
                "4. Norme ISO/IEC 27001 : Systèmes de management de la sécurité de l'information (SMSI).",
                "5. RFC 7519 : JSON Web Token (JWT) Architecture and Security Best Practices, IETF.",
                "6. Docker Community & Kubernetes Documentation : Production Grade Container Orchestration."
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> listOf(
                "1. CHRISTOPHER M., Logistics & Supply Chain Management: Creating Value-Adding Networks, Pearson.",
                "2. ROUX M. & LIU T., Gestion des Entrepôts et Optimisation des Stocks : Méthodes et Pratiques, Eyrolles.",
                "3. PIMOR P. & VALENTIN F., Logistique : Production, Distribution, Soutien, Dunod.",
                "4. Norme ISO 9001 : Systèmes de management de la qualité - Exigences dans la logistique.",
                "5. Référentiel SCOR (Supply Chain Operations Reference model), Association for Supply Chain Management.",
                "6. Guide Douanier & Logistique Portuaire de Côte d'Ivoire : Procédures du Guichet Unique."
            )
            FiliereType.MINES_PETROLE_PROCEDES -> listOf(
                "1. MOHITPOUR M. & GOLSHAN H., Pipeline Design and Construction: A Practical Approach, ASME Press.",
                "2. COHEN P., Le Génie Chimique et la Mécanique des Fluides Industriels, Dunod.",
                "3. API Recommended Practice 571 : Damage Mechanisms Affecting Fixed Equipment in the Refining Industry.",
                "4. Norme ASME B31.3 : Process Piping Code (Design, Fabrication, and Inspection).",
                "5. Norme API 650 : Welded Tanks for Oil Storage - Design and Safety Standards.",
                "6. Directive SEVESO III & Guide de Gestion des Risques Majeurs en Milieu Industriel."
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> listOf(
                "1. JEANTET R. & CROGUENNEC T., Génie des Procédés Alimentaires : Des Bases Théoriques aux Applications, Lavoisier.",
                "2. MORTIMORE S. & WALLACE C., HACCP: A Practical Approach to Food Safety Management, Springer.",
                "3. MULTON J-L., Dictionnaire des Sciences et Techniques Alimentaires, Éditions Tec & Doc.",
                "4. Norme ISO 22000 : Systèmes de management de la sécurité des denrées alimentaires.",
                "5. Codex Alimentarius : Principes généraux d'hygiène alimentaire et de pasteurisation, FAO / OMS.",
                "6. Réglementation Nationale Sanitaire : Normes de commercialisation des denrées en zone UEMOA."
            )
            FiliereType.MECANIQUE_METALLURGIE -> listOf(
                "1. CHEVALIER A., Guide du Dessinateur Industriel (GDI), Éditions Hachette Technique.",
                "2. BARLIER C. & POULAIN R., Mémotech Génie Mécanique et Productique, Éditions Delagrave.",
                "3. AGATI P. & MATTERA N., Mécanique Appliquée : Résistance des Matériaux et Cinématique, Dunod.",
                "4. Norme ISO 15 : Roulements radiaux - Dimensions d'encombrement et tolérances générales.",
                "5. Norme NF EN 10025 : Produits laminés à chaud en aciers de construction non alliés (S235JR).",
                "6. Norme NF EN 13241-1 : Portes et barrières industrielles, commerciales et de garage."
            )
        }
    }

    fun resolveAnnexes(type: FiliereType): List<Pair<String, String>> {
        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> listOf(
                "Annexe A" to "Schéma électrique unifilaire de l'armoire de distribution générale (Format A3 replié)",
                "Annexe B" to "Note de calcul de dimensionnement des câbles et calculs des courants de court-circuit Icc",
                "Annexe C" to "Fiche de relevé tellurométrique de la prise de terre et procès-verbal d'isolement sous 500V",
                "Annexe D" to "Nomenclature exhaustive de l'appareillage modulaire et coordonnées fournisseurs agréés",
                "Annexe E" to "Consignes de sécurité électrique, habilitation et procédure de consignation en 5 étapes"
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> listOf(
                "Annexe A" to "Plan d'implantation général et de coffrage des fondations au 1/50e",
                "Annexe B" to "Plan de ferraillage détaillé des semelles isolées et coupes sur armatures en nappe",
                "Annexe C" to "Procès-verbaux d'essais d'écrasement des éprouvettes béton 16×32 à 7, 14 et 28 jours",
                "Annexe D" to "Rapport d'essais au pressiomètre et coupe lithologique du sol de fondation",
                "Annexe E" to "Planning d'exécution GANTT des travaux de gros œuvre et fiches de contrôle de banches"
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> listOf(
                "Annexe A" to "Diagramme d'architecture système générale, découpage microservices et flux réseaux",
                "Annexe B" to "Schéma relationnel de la base de données (MCD / MLD) et dictionnaires des tables",
                "Annexe C" to "Documentation OpenAPI / Swagger des endpoints d'API REST et contrats d'échange",
                "Annexe D" to "Fichier de configuration Docker Compose et manifestes de déploiement sécurisé",
                "Annexe E" to "Rapport d'audit de sécurité automatisé et couverture des tests unitaires (> 85%)"
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> listOf(
                "Annexe A" to "Plan de masse et zonage de l'entrepôt selon la classification ABC des références",
                "Annexe B" to "Feuille de calcul du modèle de Wilson et dimensionnement des stocks de sécurité",
                "Annexe C" to "Procédure standardisée de réception physique et d'entrée en stock dans le WMS",
                "Annexe D" to "Modèle type de Bon de Livraison (BL), Lettre de Voiture et tracking transport",
                "Annexe E" to "Tableau de bord des Indicateurs Clés de Performance (KPI) logistiques mensuels"
            )
            FiliereType.MINES_PETROLE_PROCEDES -> listOf(
                "Annexe A" to "Schéma P&ID (Piping and Instrumentation Diagram) complet de la section traitée",
                "Annexe B" to "Note de calcul de tenue en pression et contraintes de tuyauterie selon ASME B31.3",
                "Annexe C" to "Procès-verbal de l'épreuve hydraulique sous pression et contrôle par ressuage",
                "Annexe D" to "Fiche d'analyse des risques de procédés (Grille HAZOP) et barrières de sécurité ESD",
                "Annexe E" to "Fiches de Données de Sécurité (FDS) des fluides et consignes d'intervention ATEX"
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> listOf(
                "Annexe A" to "Diagramme de fabrication séquentiel de la ligne et points critiques de contrôle (CCP)",
                "Annexe B" to "Enregistrements thermiques des barèmes de pasteurisation et calculs de la VP",
                "Annexe C" to "Bulletins d'analyses microbiologiques et physico-chimiques des lots témoins",
                "Annexe D" to "Plan de nettoyage et désinfection (NEP) et fiches techniques des détergents agréés",
                "Annexe E" to "Grille d'audit interne selon la norme ISO 22000 et fiches de non-conformités"
            )
            FiliereType.MECANIQUE_METALLURGIE -> listOf(
                "Annexe A" to "Plan d'ensemble 2D côté de la barrière levante et fût pivot (Format normalisé A4 avec cartouche)",
                "Annexe B" to "Plan de détail de l'arbre en acier XC38 et des portées de roulements tolérancées h7",
                "Annexe C" to "Schéma de scellement chimique sur dalle béton et platine d'embase avec 4 goujons M16",
                "Annexe D" to "Fiche de relevé dimensionnel et procès-verbal de réception technique en atelier",
                "Annexe E" to "Fiche de Données de Sécurité (FDS) du primaire anticorrosion et de la laque polyuréthane"
            )
        }
    }

    fun resolveFieldTasks(type: FiliereType): List<Pair<String, String>> {
        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> listOf(
                "Reconnaissance et Cheminement des Câbles" to "Repérage des goulottes, chemins de câbles en tôle perforée et vérification des rayons de courbure autorisés.",
                "Raccordement et Équipotentialité" to "Pose du conducteur principal de protection PE, serrage aux bornes de terre et contrôle d'équipotentialité des masses.",
                "Essais en Charge et Mesures Dynamiques" to "Mise sous tension échelonnée des départs, contrôle de rotation des phases et vérification des échauffements."
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> listOf(
                "Implantation Topographique" to "Contrôle de la planéité et implantation des axes de coffrage au niveau optique de précision.",
                "Réception des Fouilles et Armatures" to "Vérification des profondeurs de fouilles, de la propreté du fond et du bon calage des armatures à 35 mm.",
                "Suivi du Bétonnage et Vibration" to "Prélèvement d'éprouvettes cylindriques, contrôle du cône d'Abrams et surveillance de la vibration continue."
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> listOf(
                "Brassage Réseau et Recette Baie" to "Raccordement des liaisons RJ45 catégorie 6A sur panneau de brassage et testeur de réflectométrie.",
                "Déploiement Serveur et Configuration" to "Installation du système Linux, configuration de l'adresse IP statique, des VLANs et du pare-feu.",
                "Tests de Charge et Recette Applicative" to "Simulation d'utilisateurs simultanés, vérification de la tolérance aux pannes et sauvegarde automatique."
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> listOf(
                "Audit des Emplacements et Réimplantation" to "Reconfiguration des allées de circulation et affectation des casiers de stockage selon les cadences ABC.",
                "Contrôle Réception et Émargement" to "Pointage contradictoire des bons de livraison, contrôle quantitatif et qualitatif des avaries de transport.",
                "Inventaire Tournant et Rapprochement" to "Comptage physique des références sensibles et rapprochement immédiat avec le stock théorique WMS."
            )
            FiliereType.MINES_PETROLE_PROCEDES -> listOf(
                "Inspection Visuelle et Épaisseurs Métal" to "Mesure d'épaisseurs par ultrasons sur les zones coudées sensibles à l'érosion/corrosion.",
                "Épreuve de Pression et Inertage" to "Montage des brides borgnes d'épreuve, mise sous pression d'eau et purge à l'azote avant mise en service.",
                "Vérification des Vannes de Sécurité" to "Étalonnage des soupapes de décharge sur banc d'épreuve et contrôle des verrouillages de sécurité."
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> listOf(
                "Audit Sanitaire des Lignes de Production" to "Inspection visuelle et prélèvements de surface par écouvillonnage avant démarrage de poste.",
                "Étalonnage des Capteurs Critiques" to "Vérification de la justesse des sondes thermométriques et des pH-mètres avec des solutions tampons certifiées.",
                "Contrôle de Traçabilité des Matières" to "Vérification des fiches d'agrément des lots de matières premières et des numéros de traçabilité interne."
            )
            FiliereType.MECANIQUE_METALLURGIE -> listOf(
                "Reconnaissance Préalable et Nivellement" to "Contrôle de la planéité de la dalle d'accueil béton au niveau à bulle et repérage des axes de passage.",
                "Ancrages Chimiques et Mécaniques" to "Perçage de trous Ø 18 mm, soufflage de la poussière, injection de résine bi-composant et tiges filetées M16.",
                "Ajustage Final et Calage de Précision" to "Positionnement de l'embase sur cales biaises métalliques, serrage au couple et coulage d'un mortier sans retrait."
            )
        }
    }

    fun resolveWorkshopTasks(type: FiliereType): List<Pair<String, String>> {
        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> listOf(
                "Implantation sur Châssis" to "Fixation des rails DIN, montage des caniveaux et agencement modulaire aéré.",
                "Câblage de Puissance" to "Sertissage des embouts de câblage sur conducteurs cuivre et serrage au couple prescrit.",
                "Câblage de Commande" to "Raccordement des boutons, contacteurs et repérage systématique par bagues numérotées.",
                "Essais Diélectriques" to "Mesure de résistance d'isolement au mégohmmètre sous 500 V DC (R > 100 MΩ).",
                "Finition et Repérage" to "Pose des plastrons, des étiquettes de danger électrique et du schéma plastifié en porte."
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> listOf(
                "Lecture de Plans et Calepinage" to "Analyse détaillée des plans d'armatures et calepinage des panneaux de coffrage.",
                "Façonnage des Aciers" to "Découpe à la cisaille mécanique et cintrage des barres HA pour former cadres et étriers.",
                "Assemblage des Cages" to "Ligaturage manuel au fil de fer recuit et pose rigoureuse des cales d'enrobage.",
                "Entretien du Matériel" to "Nettoyage et huilage des peaux de coffrage métalliques avec agent de démoulage.",
                "Préparation des Mortiers" to "Dosage volumétrique précis des liants et adjuvants pour reprises et ragréages."
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> listOf(
                "Développement Modulaire" to "Écriture du code métier, séparation en contrôleurs, services et repositories conformes aux standards.",
                "Tests Unitaires et Couverture" to "Rédaction des cas de test automatisés pour valider chaque fonction et cas limite.",
                "Conteneurisation Docker" to "Écriture des Dockerfiles optimisés multi-stage et réduction de la taille des images finales.",
                "Sécurisation des Accès" to "Implémentation du hachage de mots de passe (bcrypt) et validation stricte des entrées utilisateur.",
                "Documentation Technique" to "Génération automatique de la documentation d'API et guides de déploiement pour l'équipe."
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> listOf(
                "Analyse des Données de Vente" to "Extraction des historiques de commande et traitement statistique pour mise à jour ABC.",
                "Optimisation du Picking" to "Définition des chemins de collecte optimisés pour réduire les kilomètres parcourus par les préparateurs.",
                "Paramétrage du WMS" to "Configuration des seuils de réapprovisionnement et alertes automatiques de rupture.",
                "Gestion des Retours et Litiges" to "Traitement des anomalies de livraison, formalisation des réserves légales et réclamations transport.",
                "Animation Sécurité Entrepôt" to "Sensibilisation aux gestes et postures ergonomiques et contrôle des EPI caristes."
            )
            FiliereType.MINES_PETROLE_PROCEDES -> listOf(
                "Vérification des Cotes de Tuyauterie" to "Contrôle des isométriques et alignement des brides selon les tolérances ASME.",
                "Préparation des Soudures" to "Chanfreinage des extrémités des tubes et contrôle visuel de l'accostage.",
                "Surveillance des Paramètres" to "Relevé des pressions, débits et températures sur les bancs d'essai de laboratoire.",
                "Traitement de Protection" to "Application des revêtements époxy anticorrosion sur les parties enterrées ou exposées.",
                "Rédaction des Permis de Feu" to "Vérification de l'absence de gaz inflammable avant toute opération à chaud."
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> listOf(
                "Préparation des Échantillons" to "Prélèvement aseptique et préparation des dilutions décimales sous hotte à flux laminaire.",
                "Analyses Physico-Chimiques" to "Mesures d'acidité titrable, de brix réfractométrique et de densité des matières premières.",
                "Suivi des Process Thermiques" to "Surveillance en temps réel des courbes de température/temps sur enregistreur numérique.",
                "Contrôle du Conditionnement" to "Vérification de l'étanchéité des soudures de sachets et de l'intégrité des opercules.",
                "Tenue des Registres Sanitaires" to "Archivage des fiches de lots et validation des déblocages de produits finis."
            )
            FiliereType.MECANIQUE_METALLURGIE -> listOf(
                "Découpe & Débitage" to "Tronçonnage à sec de profilés creux 100×100×4 mm et 80×40×2 mm dans le respect des tolérances (+/- 1 mm).",
                "Usinage au Tour" to "Dressage des faces, chariotage au diamètre Ø 30h7 pour les portées de roulements et rainurage de clavette.",
                "Perçage & Taraudage" to "Perçage à la perceuse à colonne sur table inclinable et taraudage manuel M10/M12 pour fixation des amortisseurs.",
                "Meulage & Chanfreinage" to "Ébavurage systématique des arrêtes tranchantes au disque abrasif et préparation des chanfreins de soudure à 30°.",
                "Traitement de Surface" to "Dégraissage aux solvants, application au pinceau du primaire anticorrosion au phosphate de zinc et peinture blanche."
            )
        }
    }

    fun generateJuryQuestions(
        type: FiliereType,
        theme: String,
        companyName: String
    ): List<JuryQuestion> {
        val cName = companyName.ifBlank { "l'entreprise d'accueil" }
        return when (type) {
            FiliereType.ELECTROTECHNIQUE_ENERGIE -> listOf(
                JuryQuestion(
                    question = "Pourquoi avoir choisi un régime de neutre TT plutôt qu'un schéma TN-S ou IT pour cette installation ?",
                    category = "Choix Technologiques",
                    suggestedAnswer = "Le schéma TT a été retenu car l'installation est alimentée directement par le réseau public basse tension. Il garantit une coupure automatique instantanée au premier défaut d'isolement grâce aux dispositifs différentiels résiduels (DDR 30 mA et 300 mA), ce qui est le choix le plus sécurisant pour des locaux sans service d'entretien électrique permanent qualifié.",
                    juryExpectation = "Le jury évalue votre compréhension de la norme NF C 15-100, des conditions de déclenchement et de la sécurité des personnes.",
                    pitfallsToAvoid = "Ne confondez pas le rôle du disjoncteur différentiel (défaut d'isolement) et du disjoncteur magnéto-thermique (surintensité et court-circuit)."
                ),
                JuryQuestion(
                    question = "Comment avez-vous calculé et justifié la section du câble principal U-1000 R2V ?",
                    category = "Calculs & Dimensionnement",
                    suggestedAnswer = "La section a été déterminée par un double critère : d'abord la contrainte thermique avec le courant d'emploi Ib (calculé avec le facteur de simultanéité et de foisonnement), puis la vérification de la chute de tension ΔU qui devait rester strictement inférieure à 5 % sur la longueur totale de 75 mètres.",
                    juryExpectation = "Justification scientifique avec formule ΔU = √3 × Ib × L × (R·cos φ + X·sin φ).",
                    pitfallsToAvoid = "Ne donnez pas une section au hasard sans mentionner la distance, la température ambiante et le mode de pose."
                ),
                JuryQuestion(
                    question = "Quelles précautions HSE avez-vous appliquées lors des interventions sur armoire sous tension ?",
                    category = "Sécurité & HSE",
                    suggestedAnswer = "Aucune intervention n'a été réalisée sous tension sans justification impérative. La procédure de consignation en 5 étapes a été scrupuleusement respectée : séparation, condamnation, VAT (Vérification d'Absence de Tension) avec appareil normalisé, mise à la terre/court-circuit et balisage de la zone, avec port des EPI (gants isolants 1000V, écran facial et chaussures de sécurité).",
                    juryExpectation = "Rigueur absolue sur les 5 étapes de la consignation NF C 18-510.",
                    pitfallsToAvoid = "Ne jamais dire que vous avez testé avec un simple tournevis testeur ou sans gants homologués."
                ),
                JuryQuestion(
                    question = "Quelle a été la principale difficulté rencontrée lors du câblage et comment l'avez-vous résolue ?",
                    category = "Difficultés & Terrain",
                    suggestedAnswer = "La principale difficulté a été la présence d'harmoniques générées par les variateurs de fréquence, créant des échauffements sur le conducteur de neutre. Nous avons résolu le problème par l'adjonction d'inductances de ligne anti-harmoniques et le surdimensionnement de la section du neutre.",
                    juryExpectation = "Capacité d'analyse et sens pratique de résolution des aléas industriels.",
                    pitfallsToAvoid = "Évitez de dire que tout s'est passé sans aucun problème, le jury sait que le terrain comporte des imprévus."
                ),
                JuryQuestion(
                    question = "Quel est l'impact économique et énergétique de votre solution pour $cName ?",
                    category = "Pérennité & Rentabilité",
                    suggestedAnswer = "L'amélioration du facteur de puissance cos φ de 0.82 à 0.94 via la batterie de condensateurs élimine les pénalités pour consommation d'énergie réactive sur la facture CIE, garantissant un retour sur investissement en moins de 14 mois.",
                    juryExpectation = "Sens économique de l'ingénieur/technicien.",
                    pitfallsToAvoid = "Ne pas négliger la rentabilité financière face au coût du matériel."
                )
            )
            FiliereType.GENIE_CIVIL_BATIMENT -> listOf(
                JuryQuestion(
                    question = "Comment justifiez-vous le choix d'une fondation superficielle par rapport à des pieux ?",
                    category = "Calculs & Géotechnique",
                    suggestedAnswer = "Le rapport de sol géotechnique a révélé une contrainte admissible q_adm = 0.25 MPa dès 1.20 m de profondeur. La descente de charge à l'ELU ne générant qu'une pression de contact de 0.21 MPa, une semelle superficielle isolée était techniquement satisfaisante et nettement plus économique qu'une solution de fondations profondes.",
                    juryExpectation = "Maîtrise de la formule S = Nser / q_adm et lecture d'un sondage pressiométrique.",
                    pitfallsToAvoid = "Ne pas confondre charges permanentes G et charges variables d'exploitation Q."
                ),
                JuryQuestion(
                    question = "Quelle importance accordez-vous à la cure du béton en climat tropical chaud comme à Abidjan ?",
                    category = "Choix Technologiques",
                    suggestedAnswer = "En milieu tropical humide et chaud, l'évaporation précoce de l'eau de gâchage provoque une dessiccation rapide et des fissures de retrait plastique. L'application d'un produit de cure paraffiné immédiatement après le talochage a permis de préserver l'hydratation du ciment et d'atteindre la résistance nominale de 28 MPa à 28 jours.",
                    juryExpectation = "Connaissance de la cinétique de prise du ciment et de la norme NF EN 206-1.",
                    pitfallsToAvoid = "Penser que l'arrosage superficiel à l'eau suffit toujours en plein soleil."
                ),
                JuryQuestion(
                    question = "Quels sont les contrôles qualité effectués avant d'autoriser le coulage du béton ?",
                    category = "Sécurité & Contrôle",
                    suggestedAnswer = "La réception du ferraillage a vérifié : la conformité des diamètres et espacements des barres HA, la propreté du fond de coffrage, le calage d'enrobage (35 mm minimum) et l'étanchéité des banches pour éviter les fuites de laitance. Sur le béton frais, l'affaissement au cône d'Abrams a été mesuré systématiquement.",
                    juryExpectation = "Rigueur procédurale de chantier et assurance qualité.",
                    pitfallsToAvoid = "Oublier l'enrobage qui est la cause n°1 de corrosion des armatures."
                ),
                JuryQuestion(
                    question = "Si une fissure de 0.3 mm apparaissait sur la poutre après décoffrage, quelle serait votre démarche d'expertise ?",
                    category = "Difficultés & Terrain",
                    suggestedAnswer = "J'analyserais d'abord son orientation : fissure verticale en travée (traction/flexion) ou oblique près des appuis (effort tranchant). Ensuite, je vérifierais si la fissure est active ou stabilisée avec des jauges Saugnac, avant de préconiser si nécessaire une injection de résine époxy sous pression après accord du bureau de contrôle.",
                    juryExpectation = "Démarche d'ingénierie pathologique des structures.",
                    pitfallsToAvoid = "Ne proposez pas un simple plâtrage cosmétique masquant le problème structurel."
                )
            )
            FiliereType.INFORMATIQUE_RESEAUX_TELECOMS -> listOf(
                JuryQuestion(
                    question = "Pourquoi avoir opté pour une architecture REST / modulaire plutôt qu'un monolithe classique ?",
                    category = "Architecture Logicielle",
                    suggestedAnswer = "L'architecture modulaire et les APIs REST permettent de découpler strictement le front-end du back-end, facilitant la scalabilité horizontale, la maintenance par composants indépendants et l'intégration future avec des applications mobiles sans réécrire la logique métier.",
                    juryExpectation = "Compréhension des principes de scalabilité, SoC (Separation of Concerns) et maintenabilité.",
                    pitfallsToAvoid = "Ne pas savoir expliquer comment transitent les requêtes HTTP (GET, POST, PUT, DELETE, codes statut)."
                ),
                JuryQuestion(
                    question = "Comment garantissez-vous la sécurité des données sensibles et des accès utilisateurs ?",
                    category = "Sécurité & Cyber",
                    suggestedAnswer = "La sécurité repose sur l'authentification par tokens JWT avec expiration courte et refresh tokens sécurisés HttpOnly, le hachage des mots de passe avec sel via Bcrypt, la protection CSRF/CORS et la validation systématique des entrées pour prévenir les injections SQL et failles XSS.",
                    juryExpectation = "Maîtrise des standards OWASP Top 10.",
                    pitfallsToAvoid = "Ne jamais mentionner le stockage de mots de passe en clair ou en simple MD5."
                ),
                JuryQuestion(
                    question = "Quels ont été vos indicateurs de performance (KPI) pour valider le système ?",
                    category = "Métrologie & Tests",
                    suggestedAnswer = "Nous avons mesuré le temps de réponse moyen des requêtes (inférieur à 200 ms au 95e percentile sous 100 requêtes concurrentes), le taux de disponibilité supérieur à 99.8 % et la couverture de code par les tests unitaires supérieure à 80 %.",
                    juryExpectation = "Mesures chiffrées concrètes et reproductibles.",
                    pitfallsToAvoid = "Dire simplement que 'le logiciel fonctionne rapidement' sans métrique."
                ),
                JuryQuestion(
                    question = "En cas de crash de la base de données, quel est votre plan de reprise d'activité (PRA) ?",
                    category = "Exploitation & Fiabilité",
                    suggestedAnswer = "Un dump automatique quotidien est envoyé vers un stockage distant chiffré, doublé d'une réplication synchrone en lecture. Le RPO (Recovery Point Objective) est fixé à moins d'une heure et le RTO (Recovery Time Objective) de restauration à moins de 30 minutes.",
                    juryExpectation = "Notions indispensables de RPO et RTO en production.",
                    pitfallsToAvoid = "Croire qu'une sauvegarde locale sur le même disque suffit."
                )
            )
            FiliereType.LOGISTIQUE_TRANSPORT_SUPPLY -> listOf(
                JuryQuestion(
                    question = "Comment la méthode ABC appliquée aux stocks a-t-elle amélioré les flux chez $cName ?",
                    category = "Gestion des Flux",
                    suggestedAnswer = "La classification 20/80 a permis de regrouper les 20 % de références représentant 80 % des rotations (classe A) au plus près des quais de déchargement et d'expédition. Cela a réduit les distances de picking de 35 % et les temps de préparation de commande.",
                    juryExpectation = "Maîtrise de la loi de Pareto et du dimensionnement des zones d'entrepôt.",
                    pitfallsToAvoid = "Ne pas savoir faire la différence entre rotation en valeur et rotation en volume."
                ),
                JuryQuestion(
                    question = "Quelle formule avez-vous employée pour calculer le stock de sécurité ?",
                    category = "Calculs & Modèles",
                    suggestedAnswer = "Le stock de sécurité a été calculé par SS = k × σ_d × √L, où k représente le facteur de service correspondant à 98 % de taux de disponibilité, σ_d l'écart-type de la demande quotidienne et L le délai de réapprovisionnement des fournisseurs en jours.",
                    juryExpectation = "Justification statistique pour éviter rupture et surstockage.",
                    pitfallsToAvoid = "Donner une valeur arbitraire sans lier le stock au délai fournisseur."
                ),
                JuryQuestion(
                    question = "Comment gérez-vous les avaries et litiges à la réception des conteneurs au port d'Abidjan ?",
                    category = "Droit & Douanes",
                    suggestedAnswer = "Dès le constat au dépotage, des réserves précises et motivées sont portées sur le bon de livraison et la lettre de voiture CMR, contresignées par le transporteur. Une déclaration d'avarie est notifiée sous 3 jours ouvrés par exploit d'huissier ou lettre recommandée pour préserver les recours d'assurance.",
                    juryExpectation = "Rigueur sur les délais légaux de l'acte uniforme OHADA sur le transport.",
                    pitfallsToAvoid = "Écrire une mention vague comme 'sous réserve de déballage' qui n'a aucune valeur juridique."
                )
            )
            FiliereType.MINES_PETROLE_PROCEDES -> listOf(
                JuryQuestion(
                    question = "Quels sont les mécanismes de dégradation les plus critiques sur les tuyauteries d'hydrocarbures ?",
                    category = "Matériaux & Procédés",
                    suggestedAnswer = "Les deux mécanismes majeurs sont la corrosion sous tension en présence de sulfure d'hydrogène (H2S sour service) et l'érosion-corrosion dans les coudes à vitesse d'écoulement élevée. C'est pourquoi nous avons sélectionné un acier revêtu avec surépaisseur de corrosion de 3 mm et suivi ultrasonore.",
                    juryExpectation = "Connaissance des normes NACE MR0175 et ASME B31.3.",
                    pitfallsToAvoid = "Confondre pression de service et pression de calcul d'épreuve hydrostatique."
                ),
                JuryQuestion(
                    question = "Comment s'organise la procédure de permis de feu dans une zone ATEX ?",
                    category = "Sécurité & HSE",
                    suggestedAnswer = "Toute opération générant des étincelles en zone ATEX 1 ou 2 exige un permis de travail à chaud cosigné par le chef de site. Il impose un inertage préalable des conduites, une mesure d'explosimétrie (LIE < 10 %), la présence permanente d'un surveillant incendie avec extincteur CO2/poudre et un contrôle 2h après travaux.",
                    juryExpectation = "Maîtrise stricte des consignes de sécurité pyrotechnique et pétrolière.",
                    pitfallsToAvoid = "Omettre le contrôle post-intervention qui évite les départs de feu tardifs."
                )
            )
            FiliereType.AGROALIMENTAIRE_AGRONOMIE -> listOf(
                JuryQuestion(
                    question = "Comment appliquez-vous les 7 principes de la méthode HACCP sur votre chaîne ?",
                    category = "Sécurité Sanitaire",
                    suggestedAnswer = "Nous avons identifié les dangers microbiologiques, chimiques et physiques, déterminé les Points Critiques pour la Maîtrise (CCP tels que le barème de pasteurisation), établi les limites critiques (température ≥ 85°C pendant 30s), mis en place la surveillance continue et les actions correctives immédiates en cas de dérive.",
                    juryExpectation = "Connaissance irréprochable de la norme ISO 22000 et du Codex Alimentarius.",
                    pitfallsToAvoid = "Confondre un PRPo (Programme Prérequis Opérationnel) et un CCP."
                ),
                JuryQuestion(
                    question = "Comment assurez-vous la traçabilité ascendante et descendante d'un lot ?",
                    category = "Traçabilité & Qualité",
                    suggestedAnswer = "Chaque unité de conditionnement reçoit un numéro de lot unique encodé en Datamatrix liant le produit fini aux fiches de pesée des ingrédients, aux fournisseurs d'emballage et aux clients livrés. Un test de simulation de rappel de lot permet d'isoler 100 % des produits en moins de 2 heures.",
                    juryExpectation = "Maîtrise des exigences de rappel et retrait de produit.",
                    pitfallsToAvoid = "Négliger la traçabilité des emballages primaires en contact alimentaire."
                )
            )
            FiliereType.MECANIQUE_METALLURGIE -> listOf(
                JuryQuestion(
                    question = "Pourquoi avoir privilégié des paliers à roulements à billes UCFL 206 plutôt qu'un guidage par bagues lisses en bronze ?",
                    category = "Choix Technologiques",
                    suggestedAnswer = "Les paliers auto-aligneurs étanches UCFL 206 avec joints 2RS réduisent le coefficient de frottement de 0.15 (frottement sec ou bronze graissé) à moins de 0.002. Cela permet d'actionner la lisse de 4 mètres avec un effort musculaire minime inférieur à 30 N, tout en protégeant les organes des poussières abrasives et de l'humidité tropicale d'Abidjan.",
                    juryExpectation = "Comparaison technique et énergétique chiffrée entre frottement de glissement et de roulement.",
                    pitfallsToAvoid = "Ne pas savoir expliquer ce que signifie le suffixe 2RS (double bague d'étanchéité caoutchouc)."
                ),
                JuryQuestion(
                    question = "Détaillez la démarche de calcul ayant abouti au contrepoids de 72 kg.",
                    category = "Calculs & RDM",
                    suggestedAnswer = "Nous avons appliqué le Principe Fondamental de la Statique en sommant les moments par rapport à l'axe de rotation. Le moment créé par la lisse (poids 18 kg à son centre de gravité situé à 2.0 m, soit 360 N.m) et les accessoires a été équilibré par la masse du contrepoids située à un bras de levier de 0.50 m : M_cp = 360 / 0.50 = 720 N, soit environ 72 kg de masselottes en acier.",
                    juryExpectation = "Démonstration du PFS (somme des moments = 0) et rigueur des unités SI (N.m).",
                    pitfallsToAvoid = "Oublier de prendre en compte le poids propre des platines ou confondre masse (kg) et force (N)."
                ),
                JuryQuestion(
                    question = "Quels sont les risques lors du soudage des profilés minces et comment avez-vous évité les déformations angulaires ?",
                    category = "Procédés de Soudage",
                    suggestedAnswer = "Sur les profilés creux de faible épaisseur (2 à 4 mm), l'apport thermique excessif entraîne des retraits et un gauchissement géométrique. Nous avons mis en place un bridage mécanique rigide sur marbre, effectué un pointage symétrique opposé et adopté une séquence de soudage à pas de pèlerin avec une électrode de Ø 2.5 mm sous une intensité maîtrisée de 85-90 A.",
                    juryExpectation = "Maîtrise de la métallurgie du soudage MMA et des contraintes résiduelles.",
                    pitfallsToAvoid = "Dire qu'on a soudé en continu d'un seul côté sans précaution de bridage."
                ),
                JuryQuestion(
                    question = "Quel traitement anticorrosion avez-vous préconisé pour garantir une longévité de 10 ans ?",
                    category = "Matériaux & Finition",
                    suggestedAnswer = "Après un meulage soigné et un dégraissage aux solvants organiques, nous avons appliqué un primaire riche en phosphate de zinc (épaisseur 60 µm sec), suivi de deux couches de laque polyuréthane industrielle blanche et rouge (épaisseur totale 120 µm sec). Pour les environnements marins comme le Port d'Abidjan, une galvanisation à chaud au trempé à 450°C reste l'optimum technique recommandé.",
                    juryExpectation = "Connaissance des classes de corrosivité ISO 12944 (C3/C4 industriel et marin).",
                    pitfallsToAvoid = "Parler de 'peinture classique' sans préciser la nature chimique (polyuréthane, époxy, alkyle) ni l'épaisseur."
                ),
                JuryQuestion(
                    question = "Quelles compétences clés avez-vous développées au sein de $cName sous la supervision de vos maîtres de stage ?",
                    category = "Bilan Personnel & Professionnel",
                    suggestedAnswer = "Au-delà de la rigueur du calcul RDM appris en classe, j'ai acquis le réflexe de l'usinage tolérancé (ajustement h7 sur l'arbre en acier XC38), la dextérité du soudage sans inclusion de laitier sous les conseils de M. Farès et M. Brou Pri, ainsi que la gestion de chantier avec M. Kouassi Jean Yves. Cela a transformé mon savoir théorique en véritable savoir-faire d'ingénieur de terrain.",
                    juryExpectation = "Maturité professionnelle, humilité et reconnaissance des encadreurs.",
                    pitfallsToAvoid = "Adopter une posture prétentieuse ou dénigrer l'expérience des techniciens d'atelier."
                )
            )
        }
    }

    fun performComplianceAudit(
        student: StudentConfig,
        company: CompanyConfig,
        pages: List<ReportPage>,
        rawNotes: String
    ): ComplianceAuditResult {
        val mentions = mutableListOf<String>()
        val issues = mutableListOf<ComplianceAuditIssue>()
        var score = 100

        // 1. Contrôle du nombre de pages
        if (pages.size == 28) {
            mentions.add("Volume académique : Strictement conforme aux 28 pages réglementaires.")
        } else {
            score -= 15
            issues.add(
                ComplianceAuditIssue(
                    title = "Nombre de pages non conforme",
                    description = "Le rapport compte actuellement ${pages.size} pages au lieu des 28 pages exigées par le règlement officiel de soutenance.",
                    isCritical = true,
                    recommendation = "Utilisez le générateur 28 pages pour structurer le mémoire selon le canevas type."
                )
            )
        }

        // 2. Contrôle de l'entreprise
        if (company.inputName.isNotBlank() && company.rccmBank.isNotBlank()) {
            mentions.add("Fiche signalétique entreprise : Raison sociale et identifiants légaux (RCCM/Banque) complets.")
        } else {
            score -= 10
            issues.add(
                ComplianceAuditIssue(
                    title = "Identifiants juridiques d'entreprise incomplets",
                    description = "Le nom ou le numéro d'enregistrement RCCM de l'entreprise d'accueil est manquant ou incomplet.",
                    isCritical = false,
                    recommendation = "Complétez la raison sociale et le RCCM dans l'onglet Éditeur ou effectuez une recherche automatique."
                )
            )
        }

        // 3. Contrôle de l'étudiant et du thème
        if (student.studentName.isNotBlank() && student.theme.length >= 15) {
            mentions.add("Page de garde : Identité de l'étudiant et libellé du thème suffisamment précis et explicite.")
        } else {
            score -= 10
            issues.add(
                ComplianceAuditIssue(
                    title = "Thème de stage trop court ou imprécis",
                    description = "L'intitulé du thème de stage doit définir clairement l'ouvrage étudié et la finalité technique.",
                    isCritical = false,
                    recommendation = "Formulez un thème académique complet (ex: « ÉTUDE, CONCEPTION ET RÉALISATION DE... »)."
                )
            )
        }

        // 4. Contrôle des encadreurs
        if (student.internshipTutor.isNotBlank() && student.academicTutor.isNotBlank()) {
            mentions.add("Encadrement binôme : Tuteur professionnel et tuteur académique dûment renseignés.")
        } else {
            score -= 8
            issues.add(
                ComplianceAuditIssue(
                    title = "Coordonnées des tuteurs incomplètes",
                    description = "Le maître de stage professionnel ou l'enseignant académique n'a pas été renseigné.",
                    isCritical = false,
                    recommendation = "Mentionnez les noms et fonctions précises des deux encadreurs."
                )
            )
        }

        // 5. Journal des interventions
        if (pages.any { it.pageNumber == 11 }) {
            mentions.add("Immersion terrain : Journal de bord structuré selon la méthode 'Lieu - Durée - Cause d'intervention'.")
        }

        // 6. Procès-verbal et signatures
        if (pages.any { it.pageNumber == 28 }) {
            mentions.add("Validation officielle : Page 28 intégrant la grille de notation sur 20 et les encarts de signatures/cachets.")
        }

        val status = when {
            score >= 90 -> "CONFORME - EXCELLENT POUR SOUTENANCE OFFICIELLE"
            score >= 75 -> "CONFORME AVEC RÉSERVES MINEURES"
            else -> "NON CONFORME - CORRECTIONS OBLIGATOIRES AVANT DÉPÔT"
        }

        val advice = when {
            score >= 90 -> "Votre rapport répond à 100 % aux normes académiques et industrielles. Concentrez-vous sur votre posture orale : 10 minutes d'exposé chronométré sans lire vos diapos, puis répondez avec calme et assurance aux questions du jury."
            score >= 75 -> "Quelques détails administratifs ou techniques méritent d'être complétés avant l'impression définitive. Utilisez l'auto-correction pour atteindre le score maximal."
            else -> "Des éléments obligatoires (mentions légales, volume ou encadrement) manquent au dossier. Veuillez corriger ces points pour éviter tout ajournement par la commission."
        }

        return ComplianceAuditResult(
            score = score.coerceIn(0, 100),
            mentionsPresent = mentions,
            missingOrWeakPoints = issues,
            academicStatus = status,
            adviceForDefense = advice
        )
    }
}

