package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ReportUiState
import com.example.ui.ReportViewModel
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonGlassCard
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun JurySimulatorScreen(
    viewModel: ReportViewModel,
    uiState: ReportUiState,
    modifier: Modifier = Modifier
) {
    // Defense timer ticker
    LaunchedEffect(uiState.isDefenseTimerActive) {
        while (uiState.isDefenseTimerActive) {
            delay(1000)
            viewModel.tickDefenseTimer()
        }
    }

    val questions = uiState.juryQuestions
    val currentIndex = uiState.currentJuryIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0))
    val currentQuestion = questions.getOrNull(currentIndex)

    val minutes = uiState.defenseTimerSeconds / 60
    val seconds = uiState.defenseTimerSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val categories = listOf("Toutes", "Choix Technologiques", "Calculs & RDM", "Sécurité & HSE", "Difficultés & Terrain", "Pérennité & Rentabilité")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E1538),
                        Color(0xFF101228),
                        BgDark
                    ),
                    radius = 1600f
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SIMULATEUR DE JURY",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Entraînement aux questions orales de la commission d'examen",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    NeonBadge(text = "ORAL & DÉFENSE", isAccent = true)
                }
            }

            // CHRONOMETRE DE SOUTENANCE
            item {
                NeonGlassCard(modifier = Modifier.testTag("defense_timer_card")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2200F5FF))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CHRONO EXPOSÉ",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = timeFormatted,
                                    color = if (minutes >= 15) Color(0xFFEF4444) else TextWhite,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NeonButton(
                                text = if (uiState.isDefenseTimerActive) "PAUSE" else "DÉMARRER",
                                onClick = { viewModel.toggleDefenseTimer() },
                                style = if (uiState.isDefenseTimerActive) NeonButtonStyle.GHOST_GLASS else NeonButtonStyle.PRIMARY_NEON,
                                icon = {
                                    Icon(
                                        imageVector = if (uiState.isDefenseTimerActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (uiState.isDefenseTimerActive) TextWhite else Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                testTag = "btn_timer_toggle"
                            )

                            NeonButton(
                                text = "",
                                onClick = { viewModel.resetDefenseTimer() },
                                style = NeonButtonStyle.GHOST_GLASS,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Replay,
                                        contentDescription = "Reset",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                testTag = "btn_timer_reset"
                            )
                        }
                    }
                }
            }

            // QUESTION CARD
            item {
                if (currentQuestion != null) {
                    NeonGlassCard(modifier = Modifier.testTag("jury_question_card")) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Question header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                NeonBadge(
                                    text = "QUESTION ${currentIndex + 1} / ${questions.size}",
                                    isAccent = true
                                )
                                Text(
                                    text = currentQuestion.category,
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Question statement
                            Text(
                                text = "« ${currentQuestion.question} »",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action button: Reveal answer
                            NeonButton(
                                text = if (uiState.showJuryAnswer) "MASQUER LA RÉPONSE TYPE" else "AFFICHER LA RÉPONSE RECOMMANDÉE",
                                onClick = { viewModel.toggleShowJuryAnswer() },
                                style = if (uiState.showJuryAnswer) NeonButtonStyle.GHOST_GLASS else NeonButtonStyle.NEON_GLASS,
                                icon = {
                                    Icon(
                                        imageVector = if (uiState.showJuryAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_toggle_answer"
                            )

                            // Suggested Answer Section (toggled)
                            if (uiState.showJuryAnswer) {
                                Spacer(modifier = Modifier.height(14.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0x2210B981), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0x4010B981), RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = NeonGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "RÉPONSE MODÈLE STRUCTURÉE :",
                                                color = NeonGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = currentQuestion.suggestedAnswer,
                                            color = TextWhite,
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // What jury expects
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0x1800F5FF), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0x3000F5FF), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "CRITÈRES D'ÉVALUATION DU JURY :",
                                            color = NeonCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentQuestion.juryExpectation,
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Pitfalls to avoid
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0x18FBBF24), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0x30FBBF24), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.WarningAmber,
                                            contentDescription = null,
                                            tint = Color(0xFFFBBF24),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "PIÈGES À ÉVITER :",
                                                color = Color(0xFFFBBF24),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = currentQuestion.pitfallsToAvoid,
                                                color = TextWhite,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Self assessment rating
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Auto-évaluation :",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Row {
                                    val currentRating = uiState.jurySelfRatings[currentIndex] ?: 0
                                    (1..5).forEach { star ->
                                        Icon(
                                            imageVector = if (star <= currentRating) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Étoile $star",
                                            tint = if (star <= currentRating) Color(0xFFFBBF24) else TextMuted,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable { viewModel.rateJuryQuestion(currentIndex, star) }
                                                .padding(2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Question Navigation buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                NeonButton(
                                    text = "PRÉCÉDENTE",
                                    onClick = { viewModel.setCurrentJuryIndex(currentIndex - 1) },
                                    style = NeonButtonStyle.GHOST_GLASS,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = TextWhite,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    testTag = "btn_prev_question"
                                )

                                NeonButton(
                                    text = "SUIVANTE",
                                    onClick = { viewModel.setCurrentJuryIndex(currentIndex + 1) },
                                    style = NeonButtonStyle.PRIMARY_NEON,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    testTag = "btn_next_question"
                                )
                            }
                        }
                    }
                } else {
                    NeonGlassCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Aucune question disponible pour l'instant.",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            NeonButton(
                                text = "GÉNÉRER LES QUESTIONS DU JURY",
                                onClick = { viewModel.loadJuryQuestions() },
                                style = NeonButtonStyle.PRIMARY_NEON,
                                testTag = "btn_load_questions"
                            )
                        }
                    }
                }
            }

            // GUIDE D'OR POUR LA SOUTENANCE
            item {
                NeonGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONSEILS CLÉS POUR LE JOUR J",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "1. Formule d'ouverture : « Monsieur le Président du Jury, honorables membres du jury, bonjour... »\n" +
                                    "2. Respectez la règle des 10-15 minutes : ne récitez pas les 28 pages mais mettez en avant le problème concret et vos calculs vérifiés.\n" +
                                    "3. En cas de question difficile : ne mentez jamais. Dites avec assurance : « C'est un aspect très pertinent que nous n'avons pas approfondi sur ce chantier, mais dans les perspectives nous pourrions envisager... »\n" +
                                    "4. Gardez votre carnet de stage et schéma technique sous la main pour appuyer vos explications.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
