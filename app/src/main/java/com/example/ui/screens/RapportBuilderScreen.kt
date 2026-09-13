package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.LocationItem
import com.example.service.ReportSectionType
import com.example.ui.ReportUiState
import com.example.ui.ReportViewModel
import com.example.ui.components.LieuBlockCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonInputField
import com.example.ui.components.NeonLabel
import com.example.ui.theme.BgDark
import com.example.ui.theme.CardGlassBg
import com.example.ui.theme.DangerRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.ReportExporter
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RapportBuilderScreen(
    viewModel: ReportViewModel,
    uiState: ReportUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Android Zero-Permission Pickers (PickVisualMedia)
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = uri.lastPathSegment ?: "logo_entreprise"
            viewModel.setLogoImage(uri, name)
        }
    }

    val multipleImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val items = uris.map { uri ->
                val name = uri.lastPathSegment ?: "illustration_${System.currentTimeMillis()}"
                Pair(uri, name)
            }
            viewModel.addIllustrations(items)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1B1538),
                        Color(0xFF0F1226),
                        BgDark
                    ),
                    radius = 1600f
                )
            )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // HEADER & IDENTITY
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(16.dp, CircleShape)
                            .border(2.5.dp, Brush.linearGradient(listOf(NeonCyan, NeonViolet)), CircleShape)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.slm_logo),
                            contentDescription = "SLM Neon Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    NeonBadge(text = "V4 FINAL FIX NEON GLASS PREMIUM")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SLM RAPPORT BUILDER SMARTLY",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Générateur de rapport de stage Word 28 pages 100% réglementaire",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        NeonButton(
                            text = "NOUVEAU RAPPORT (CHAMPS VIERGES)",
                            onClick = { viewModel.clearAllForCustomInput() },
                            style = NeonButtonStyle.GHOST_GLASS,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            testTag = "btn_reset_clean"
                        )
                    }
                }
            }

            // 1. DÉTECTION ENTREPRISE ICC OU PERSONNALISÉE
            item {
                NeonGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. ENTREPRISE D'ACCUEIL DU STAGE",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        NeonInputField(
                            value = uiState.company.inputName,
                            onValueChange = { viewModel.updateCompanyName(it) },
                            label = "Nom de l'entreprise (ex: CIE, SODECI, TOTAL, ICC...)",
                            placeholder = "Nom ou sigle de l'entreprise",
                            testTag = "input_company_name"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bouton de Recherche Intelligente Gemini & Google
                        Button(
                            onClick = { viewModel.searchAndAutoFillCompanyInfo() },
                            enabled = !uiState.isSearchingCompany && uiState.company.inputName.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("button_search_company_ai"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.company.isIccDetected) Color(0xFF007AFF) else NeonViolet,
                                contentColor = Color.White,
                                disabledContainerColor = Color(0x33475569),
                                disabledContentColor = Color(0x66FFFFFF)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isSearchingCompany) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recherche Gemini & Google en cours...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recherche Intelligente (Infos Légales, Adresse, Contacts)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Suggestions rapides d'entreprises
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Suggestions rapides à tester :",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val suggestions = listOf("CIE", "SODECI", "TOTAL", "PORT AUTONOME", "SOLIBRA", "SIFCA", "ORANGE", "PETROCI", "ICC CORPORATE")
                            items(suggestions) { comp ->
                                val isSelected = uiState.company.inputName.equals(comp, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0x22FFFFFF),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonCyan else Color(0x33FFFFFF),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.updateCompanyName(comp)
                                            viewModel.searchAndAutoFillCompanyInfo(comp)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = comp,
                                        color = if (isSelected) NeonCyan else TextWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Message de confirmation de recherche
                        AnimatedVisibility(
                            visible = uiState.companySearchMessage.isNotBlank(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                                    .background(
                                        if (uiState.companySearchSuccess) Color(0x2210B981) else Color(0x22EF4444),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (uiState.companySearchSuccess) SuccessGreen.copy(alpha = 0.5f) else Color(0x66EF4444),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (uiState.companySearchSuccess) Icons.Default.CheckCircle else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (uiState.companySearchSuccess) SuccessGreen else Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = uiState.companySearchMessage,
                                        color = if (uiState.companySearchSuccess) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (uiState.company.inputName.isBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "💡 Tapez le nom de n'importe quelle entreprise ou cliquez sur une suggestion ci-dessus pour récupérer automatiquement son adresse, son RCCM et ses contacts via Gemini & Google Search.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }

                        // CAS 1 : ENTREPRISE ICC RECONNUE
                        AnimatedVisibility(
                            visible = uiState.company.isIccDetected,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 14.dp)
                                    .fillMaxWidth()
                                    .background(Color(0x3300F5FF), RoundedCornerShape(14.dp))
                                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ENTREPRISE ICC RECONNUE AUTOMATIQUEMENT",
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.company.companyFullTitle,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.company.rccmBank,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uiState.company.contactGerant} / ${uiState.company.contactAtelier} / ${uiState.company.email}",
                                    color = NeonCyan.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Éléments à inclure dans le rapport officiel :",
                                    color = TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                CheckboxOption(
                                    label = "Adresse complète (Yopougon Figayo)",
                                    checked = uiState.company.showAddress,
                                    onCheckedChange = { viewModel.toggleShowAddress(it) }
                                )
                                CheckboxOption(
                                    label = "Contacts atelier (Atelier BROU PRI & Gérant)",
                                    checked = uiState.company.showWorkshopContact,
                                    onCheckedChange = { viewModel.toggleShowWorkshopContact(it) }
                                )
                                CheckboxOption(
                                    label = "Activités (Chaudronnerie, Soudure, Tuyauterie)",
                                    checked = uiState.company.showActivities,
                                    onCheckedChange = { viewModel.toggleShowActivities(it) }
                                )
                                CheckboxOption(
                                    label = "RCCM & Données Bancaires (BNI)",
                                    checked = uiState.company.showRccmBank,
                                    onCheckedChange = { viewModel.toggleShowRccmBank(it) }
                                )
                            }
                        }

                        // CAS 2 : AUTRE ENTREPRISE PERSONNALISÉE
                        AnimatedVisibility(
                            visible = !uiState.company.isIccDetected && uiState.company.inputName.isNotBlank(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 14.dp)
                                    .fillMaxWidth()
                                    .background(Color(0x337C3AED), RoundedCornerShape(14.dp))
                                    .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonViolet,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ENTREPRISE PERSONNALISÉE : ${uiState.company.inputName.uppercase()}",
                                        color = NeonViolet,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Renseignez les coordonnées de votre entreprise d'accueil pour personnaliser automatiquement les 28 pages :",
                                    color = TextWhite.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                NeonInputField(
                                    value = uiState.company.companyFullTitle,
                                    onValueChange = { viewModel.updateCompanyFullTitle(it) },
                                    label = "Raison Sociale Complète",
                                    placeholder = "Ex: ${uiState.company.inputName.uppercase()} SARL / SA",
                                    testTag = "input_company_full_title"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NeonInputField(
                                    value = uiState.company.companyAddress,
                                    onValueChange = { viewModel.updateCompanyAddress(it) },
                                    label = "Adresse & Implantation",
                                    placeholder = "Ex: Abidjan Plateau / Vridi - BP 123",
                                    testTag = "input_company_address"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NeonInputField(
                                    value = uiState.company.activities,
                                    onValueChange = { viewModel.updateCompanyActivities(it) },
                                    label = "Secteur & Activités",
                                    placeholder = "Ex: Production d'énergie, Réseaux, Maintenance",
                                    testTag = "input_company_activities"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NeonInputField(
                                    value = uiState.company.contactGerant,
                                    onValueChange = { viewModel.updateCompanyContactGerant(it) },
                                    label = "Direction Générale / Contact",
                                    placeholder = "Ex: Direction Générale / M. KOUAME (DG)",
                                    testTag = "input_company_gerant"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NeonInputField(
                                    value = uiState.company.contactAtelier,
                                    onValueChange = { viewModel.updateCompanyContactAtelier(it) },
                                    label = "Direction Technique / Encadrement usine",
                                    placeholder = "Ex: Direction Technique / Chef de centre",
                                    testTag = "input_company_atelier"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NeonInputField(
                                    value = uiState.company.email,
                                    onValueChange = { viewModel.updateCompanyEmail(it) },
                                    label = "Email professionnel de l'entreprise",
                                    placeholder = "Ex: contact@entreprise.ci",
                                    testTag = "input_company_email"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NeonInputField(
                                    value = uiState.company.rccmBank,
                                    onValueChange = { viewModel.updateCompanyRccmBank(it) },
                                    label = "RCCM & Données Juridiques (Optionnel)",
                                    placeholder = "Ex: RCCM CI-ABJ-... / CC ...",
                                    testTag = "input_company_rccm"
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Options d'affichage sur les 28 pages :",
                                    color = TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                CheckboxOption(
                                    label = "Afficher l'adresse de l'entreprise",
                                    checked = uiState.company.showAddress,
                                    onCheckedChange = { viewModel.toggleShowAddress(it) }
                                )
                                CheckboxOption(
                                    label = "Afficher les contacts de l'entreprise",
                                    checked = uiState.company.showWorkshopContact,
                                    onCheckedChange = { viewModel.toggleShowWorkshopContact(it) }
                                )
                                CheckboxOption(
                                    label = "Afficher le détail des activités",
                                    checked = uiState.company.showActivities,
                                    onCheckedChange = { viewModel.toggleShowActivities(it) }
                                )
                                CheckboxOption(
                                    label = "Afficher les données RCCM & Enregistrement",
                                    checked = uiState.company.showRccmBank,
                                    onCheckedChange = { viewModel.toggleShowRccmBank(it) }
                                )
                            }
                        }
                    }
                }
            }

            // 2. LIEUX VISITÉS - SYSTÈME LIEU 1 = CAUSE 1
            item {
                NeonGlassCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = NeonViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "2. LIEUX VISITÉS (LIEU X = CAUSE X)",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            NeonBadge(
                                text = if (uiState.locations.isEmpty()) "0 LIEU" else "${uiState.locations.size} LIEU(X)"
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (uiState.locations.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aucun lieu renseigné. Le rapport adaptera le résumé en Mode Atelier.",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.locations.forEachIndexed { index, loc ->
                                LieuBlockCard(
                                    index = index,
                                    item = loc,
                                    onLocationChange = { viewModel.updateLocation(index, loc.copy(locationName = it)) },
                                    onDurationChange = { viewModel.updateLocation(index, loc.copy(daysDuration = it)) },
                                    onCauseChange = { viewModel.updateLocation(index, loc.copy(cause = it)) },
                                    onDelete = { viewModel.removeLocation(index) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeonButton(
                                text = "+ AJOUTER UN LIEU",
                                onClick = { viewModel.addLocation() },
                                style = NeonButtonStyle.NEON_GLASS,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "btn_add_lieu"
                            )

                            if (uiState.locations.isNotEmpty()) {
                                NeonButton(
                                    text = "JE N'AI PAS DE LIEUX",
                                    onClick = {
                                        viewModel.clearLocations()
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(3)
                                        }
                                    },
                                    style = NeonButtonStyle.GHOST_GLASS,
                                    modifier = Modifier.weight(1f),
                                    testTag = "btn_no_lieux"
                                )
                            }
                        }
                    }
                }
            }

            // 3. RÉSUMÉ INTELLIGENT - DOUBLE MODE
            item {
                NeonGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "3. RÉSUMÉ INTELLIGENT (DOUBLE MODE)",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (uiState.locations.isNotEmpty())
                                "Mode A actif : Synthèse intégrant les lieux et causes d'intervention."
                            else
                                "Mode B actif : Reformulation académique atelier (contrôle d'accès, coupe, soudure sous M. Farès, peinture blanche).",
                            color = NeonCyan.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        NeonInputField(
                            value = uiState.rawSummary,
                            onValueChange = { viewModel.updateRawSummary(it) },
                            label = "Notes brutes de l'étudiant (même en désordre)",
                            placeholder = "Ex: on a fait coupe, soudure sous superviseur M. Fares, meulage, peinture...",
                            singleLine = false,
                            minLines = 2,
                            testTag = "input_raw_summary"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // GEMINI AI SMART SEARCH & ENRICHMENT BUTTON
                        NeonButton(
                            text = if (uiState.isGeminiLoading) "RECHERCHE GEMINI EN COURS..." else "ENRICHIR AVEC GEMINI IA (Normes & Formulations réelles)",
                            onClick = { viewModel.enrichWithGemini() },
                            style = NeonButtonStyle.PRIMARY_NEON,
                            enabled = !uiState.isGeminiLoading,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_enrichir_gemini"
                        )

                        AnimatedVisibility(visible = uiState.isGeminiLoading) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .background(Color(0x2200F5FF), RoundedCornerShape(12.dp))
                                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = uiState.geminiStatusMessage,
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (uiState.geminiData != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x2210B981), RoundedCornerShape(10.dp))
                                    .border(1.dp, SuccessGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Données d'ingénierie réelles intégrées au rapport :", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Normes : " + uiState.geminiData.normes.take(2).joinToString(" | "), color = NeonCyan, fontSize = 10.sp)
                                Text("• Composants : " + uiState.geminiData.composants.take(2).joinToString(" | "), color = TextWhite.copy(alpha = 0.9f), fontSize = 10.sp)
                                Text("• Matériaux : " + uiState.geminiData.materiaux.take(2).joinToString(" | "), color = NeonViolet, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (uiState.locations.isNotEmpty()) {
                                NeonButton(
                                    text = "CHARGER RÉSUMÉ AUTO",
                                    onClick = { viewModel.loadAutoSummary() },
                                    style = NeonButtonStyle.NEON_GLASS,
                                    modifier = Modifier.weight(1f),
                                    testTag = "btn_auto_summary_mode_a"
                                )
                            } else {
                                NeonButton(
                                    text = "REFORMULER PRO ATELIER",
                                    onClick = { viewModel.reformulateInProModeB() },
                                    style = NeonButtonStyle.NEON_GLASS,
                                    modifier = Modifier.weight(1f),
                                    testTag = "btn_auto_summary_mode_b"
                                )
                            }

                            NeonButton(
                                text = "REFORMULER",
                                onClick = { viewModel.loadAutoSummary() },
                                style = NeonButtonStyle.GHOST_GLASS,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_reformulate_pro"
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NeonLabel(text = "RÉSUMÉ FINAL ACADÉMIQUE GÉNÉRÉ")
                            IconButton(
                                onClick = {
                                    ReportExporter.copyToClipboard(context, "Résumé", uiState.finalSummary)
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        NeonInputField(
                            value = uiState.finalSummary,
                            onValueChange = { viewModel.updateFinalSummary(it) },
                            placeholder = "Le résumé académique généré s'affiche ici...",
                            singleLine = false,
                            minLines = 6,
                            testTag = "input_final_summary"
                        )
                    }
                }
            }

            // 4. RÉDACTEUR DE SECTIONS OFFICIELLES (GOOGLE AI SDK)
            item {
                NeonGlassCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "4. RÉDACTION DE SECTIONS (GOOGLE AI SDK)",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                NeonBadge(
                                    text = if (uiState.offlineSavedCount > 0) "Room DB (${uiState.offlineSavedCount}/9)" else "Room DB Actif",
                                    isAccent = false
                                )
                                NeonBadge(
                                    text = "gemini-3.5-flash",
                                    isAccent = true
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Génération contextualisée des 9 sections académiques officielles du mémoire selon vos données réelles (étudiant, entreprise, chantiers, notes d'atelier).",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Horizontally scrollable section chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(ReportSectionType.values()) { section ->
                                val isSelected = section == uiState.selectedDraftSection
                                val isDrafted = uiState.draftedSections.containsKey(section)

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (isSelected) NeonCyan.copy(alpha = 0.25f)
                                            else if (isDrafted) Color(0x2210B981)
                                            else CardGlassBg
                                        )
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) NeonCyan
                                            else if (isDrafted) SuccessGreen.copy(alpha = 0.7f)
                                            else Color(0x33FFFFFF),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable { viewModel.selectDraftSection(section) }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isDrafted) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = section.title.split(" & ").first().take(22),
                                            color = if (isSelected) NeonCyan else TextWhite,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Active section details card
                        val activeSection = uiState.selectedDraftSection
                        val draftedResult = uiState.draftedSections[activeSection]

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x1500F5FF), RoundedCornerShape(12.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeSection.title,
                                        color = TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${activeSection.targetPageCount} • ${activeSection.description}",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        maxLines = 2
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NeonButton(
                                    text = if (uiState.isDraftingSection) "RÉDACTION EN COURS..." else "RÉDIGER CETTE SECTION",
                                    onClick = { viewModel.draftReportSection(activeSection) },
                                    style = NeonButtonStyle.PRIMARY_NEON,
                                    enabled = !uiState.isDraftingSection,
                                    modifier = Modifier.weight(1.3f),
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    testTag = "btn_draft_single_section"
                                )

                                NeonButton(
                                    text = "TOUT RÉDIGER (9)",
                                    onClick = { viewModel.draftAllReportSections() },
                                    style = NeonButtonStyle.NEON_GLASS,
                                    enabled = !uiState.isDraftingSection,
                                    modifier = Modifier.weight(1f),
                                    testTag = "btn_draft_all_sections"
                                )
                            }

                            // Progress indicator if generating
                            AnimatedVisibility(visible = uiState.isDraftingSection) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .background(Color(0x2200F5FF), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = NeonCyan,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = uiState.draftingProgressText,
                                        color = NeonCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Display drafted text if available
                            if (draftedResult != null) {
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (draftedResult.isFromGoogleAiSdk) "Généré via Google AI SDK" else "Moteur expert certifié",
                                            color = if (draftedResult.isFromGoogleAiSdk) NeonCyan else SuccessGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                ReportExporter.copyToClipboard(
                                                    context,
                                                    activeSection.title,
                                                    draftedResult.draftedContent
                                                )
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copier",
                                                tint = NeonCyan,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }

                                if (draftedResult.keyPointsHighlighted.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Points clés : " + draftedResult.keyPointsHighlighted.joinToString(" • "),
                                        color = NeonViolet,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0x33000000), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = draftedResult.draftedContent,
                                        color = TextWhite,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Refinement chips
                                Text(
                                    text = "Perfectionner cette section avec Google AI :",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x228A2BE2))
                                            .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.refineDraftSection(
                                                    activeSection,
                                                    "Intégrer davantage de calculs RDM et formules de dimensionnement mécanique"
                                                )
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("+ Calculs RDM", color = NeonViolet, fontSize = 10.sp)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x2200F5FF))
                                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.refineDraftSection(
                                                    activeSection,
                                                    "Renforcer les protocoles de sécurité HSE, consignation et EPI"
                                                )
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("+ Normes HSE", color = NeonCyan, fontSize = 10.sp)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x2210B981))
                                            .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.applyDraftedSectionToFinalSummary(activeSection)
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Injecter au résumé", color = SuccessGreen, fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Save,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Stocké en base Room locale (accès 100% hors-ligne)",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Text(
                                        text = "Supprimer de la base",
                                        color = DangerRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .clickable { viewModel.deleteOfflineDraft(activeSection) }
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. IMPORT FICHIERS FIX 100% FONCTIONNEL
            item {
                NeonGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5. IMPORT LOGO & ILLUSTRATIONS",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Logo Import Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                NeonLabel(text = "LOGO OFFICIEL (JPG / PNG)")
                                Text(
                                    text = if (uiState.logoImage != null) "OK importé : ${uiState.logoImage.name}" else "Aucun logo sélectionné",
                                    color = if (uiState.logoImage != null) SuccessGreen else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (uiState.logoImage != null) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, NeonCyan, RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = uiState.logoImage.uri,
                                        contentDescription = "Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeLogo() },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(20.dp)
                                            .background(DangerRed, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Supprimer",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            } else {
                                NeonButton(
                                    text = "CHOISIR LOGO",
                                    onClick = {
                                        logoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    style = NeonButtonStyle.NEON_GLASS,
                                    testTag = "btn_pick_logo"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Illustrations Import Row
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    NeonLabel(text = "ILLUSTRATIONS MULTIPLES DU STAGE")
                                    Text(
                                        text = if (uiState.illustrations.isEmpty()) "0 image importée" else "${uiState.illustrations.size} image(s) importée(s)",
                                        color = if (uiState.illustrations.isNotEmpty()) NeonCyan else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                NeonButton(
                                    text = "+ AJOUTER IMAGES",
                                    onClick = {
                                        multipleImagesLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    style = NeonButtonStyle.NEON_GLASS,
                                    testTag = "btn_pick_illustrations"
                                )
                            }

                            if (uiState.illustrations.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    itemsIndexed(uiState.illustrations) { idx, img ->
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.5.dp, NeonCyan, RoundedCornerShape(12.dp))
                                        ) {
                                            AsyncImage(
                                                model = img.uri,
                                                contentDescription = "Illustration ${idx + 1}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            IconButton(
                                                onClick = { viewModel.removeIllustration(idx) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(20.dp)
                                                    .background(DangerRed, CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Supprimer",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. INFOS ÉTUDIANT & THÈME
            item {
                NeonGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "6. IDENTITÉ ÉTUDIANT & THÈME OFFICIEL",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        NeonInputField(
                            value = uiState.student.studentName,
                            onValueChange = { viewModel.updateStudentName(it) },
                            label = "Nom & Prénoms de l'étudiant",
                            placeholder = "Ex: KOUAME KOUASSI JEAN-LUC",
                            testTag = "input_student_name"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeonInputField(
                            value = uiState.student.schoolName,
                            onValueChange = { viewModel.updateSchoolName(it) },
                            label = "École / Établissement",
                            placeholder = "Ex: INP-HB / ESBTP Yamoussoukro",
                            testTag = "input_school_name"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeonInputField(
                            value = uiState.student.filiereOption,
                            onValueChange = { viewModel.updateFiliereOption(it) },
                            label = "Filière / Option d'études",
                            placeholder = "Génie Mécanique et Productique (Chaudronnerie & Métallurgie)",
                            testTag = "input_filiere_option"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeonInputField(
                            value = uiState.student.theme,
                            onValueChange = { viewModel.updateTheme(it) },
                            label = "Thème officiel du rapport (en majuscules)",
                            placeholder = "ÉTUDE, CONCEPTION ET FABRICATION D'UNE BARRIÈRE LEVANTE DE SÉCURITÉ INDUSTRIELLE À MÉCANISMES À ROULEMENTS",
                            singleLine = false,
                            minLines = 2,
                            testTag = "input_theme"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeonInputField(
                            value = uiState.student.internshipTutor,
                            onValueChange = { viewModel.updateInternshipTutor(it) },
                            label = "Encadreur Professionnel (Maître de stage)",
                            placeholder = "M. BROU PRI (Chef d'Atelier) & M. KOUASSI JEAN YVES (Gérant)",
                            testTag = "input_internship_tutor"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeonInputField(
                            value = uiState.student.academicTutor,
                            onValueChange = { viewModel.updateAcademicTutor(it) },
                            label = "Encadreur Académique (Professeur/Tuteur école)",
                            placeholder = "Dr. KOUASSI N'GUESSAN (Enseignant-Chercheur)",
                            testTag = "input_academic_tutor"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeonInputField(
                            value = uiState.student.academicYear,
                            onValueChange = { viewModel.updateAcademicYear(it) },
                            label = "Année Académique / Session",
                            placeholder = "2025-2026",
                            testTag = "input_academic_year"
                        )
                    }
                }
            }
        }

        // 7. BOUTON GÉNÉRER STICKY BOTTOM
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            NeonButton(
                text = "⚡ GÉNÉRER LE RAPPORT (28 PAGES)",
                onClick = { viewModel.generate28PagesReport() },
                style = NeonButtonStyle.PRIMARY_NEON,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 516.dp)
                    .align(Alignment.Center)
                    .height(54.dp),
                testTag = "btn_generate_report"
            )
        }
    }
}

@Composable
private fun CheckboxOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = NeonCyan,
                uncheckedColor = Color.White.copy(alpha = 0.4f),
                checkmarkColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextWhite,
            fontSize = 12.sp
        )
    }
}
