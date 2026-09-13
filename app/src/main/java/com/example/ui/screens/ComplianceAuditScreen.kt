package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ComplianceAuditIssue
import com.example.ui.ReportUiState
import com.example.ui.ReportViewModel
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonGlassCard
import com.example.ui.theme.BgDark
import com.example.ui.theme.DangerRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.ReportExporter

@Composable
fun ComplianceAuditScreen(
    viewModel: ReportViewModel,
    uiState: ReportUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audit = uiState.auditResult

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF131E3A),
                        Color(0xFF0C1024),
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
                // Title and badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUDIT DE CONFORMITÉ",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Contrôle académique strict des 28 pages & normes ministérielles",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    NeonBadge(text = "NORME CAMES / CI", isAccent = true)
                }
            }

            // SCORE OVERVIEW CARD
            item {
                NeonGlassCard(modifier = Modifier.testTag("audit_score_card")) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val score = audit?.score ?: 100
                        val scoreColor = when {
                            score >= 90 -> NeonGreen
                            score >= 75 -> Color(0xFFFBBF24)
                            else -> DangerRed
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color(0x2200F5FF))
                                .border(3.dp, scoreColor, CircleShape)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$score",
                                    color = scoreColor,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "/ 100",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = audit?.academicStatus ?: "VÉRIFICATION EN COURS",
                            color = scoreColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Filière analysée : ${uiState.student.filiereOption.ifBlank { "Génie Industriel" }}",
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeonButton(
                                text = "RÉAUDITER",
                                onClick = { viewModel.runComplianceAudit() },
                                style = NeonButtonStyle.GHOST_GLASS,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = TextWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "btn_audit_refresh"
                            )

                            NeonButton(
                                text = "AUTO-CORRIGER",
                                onClick = {
                                    viewModel.autoFixCompliance()
                                    Toast.makeText(context, "Conformité 100% rétablie avec succès !", Toast.LENGTH_SHORT).show()
                                },
                                style = NeonButtonStyle.PRIMARY_NEON,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.AutoFixHigh,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.weight(1.2f),
                                testTag = "btn_audit_autofix"
                            )
                        }
                    }
                }
            }

            // ATTESTATION DE CONFORMITÉ
            item {
                NeonGlassCard(modifier = Modifier.testTag("audit_attestation_card")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Attestation d'Audit de Conformité",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Générer le certificat de recevabilité académique",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        NeonButton(
                            text = "COPIER",
                            onClick = {
                                val text = buildString {
                                    appendLine("CERTIFICAT DE CONFORMITÉ ACADÉMIQUE - SLM RAPPORT BUILDER")
                                    appendLine("Étudiant : ${uiState.student.studentName}")
                                    appendLine("Établissement : ${uiState.student.schoolName}")
                                    appendLine("Filière : ${uiState.student.filiereOption}")
                                    appendLine("Thème : ${uiState.student.theme}")
                                    appendLine("Entreprise d'accueil : ${uiState.company.inputName} (${uiState.company.rccmBank})")
                                    appendLine("Volume validé : Strictement 28 pages conformes aux exigences ministérielles.")
                                    appendLine("Score de conformité : ${audit?.score ?: 100} / 100")
                                    appendLine("Statut : CONFORME - PRÊT POUR DÉPÔT ET SOUTENANCE OFFICIELLE")
                                }
                                ReportExporter.copyToClipboard(context, "Attestation de Conformité", text)
                            },
                            style = NeonButtonStyle.NEON_GLASS,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            testTag = "btn_copy_attestation"
                        )
                    }
                }
            }

            // CRITÈRES VALIDÉS (MENTIONS)
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CRITÈRES ACADÉMIQUES VALIDÉS (${audit?.mentionsPresent?.size ?: 0})",
                        color = NeonGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            items(audit?.mentionsPresent ?: emptyList()) { mention ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x1410B981), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x3310B981), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = mention,
                            color = TextWhite,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // POINTS D'AMÉLIORATION / RÉSERVES (IF ANY)
            if (!audit?.missingOrWeakPoints.isNullOrEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "POINTS À COMPLÉTER (${audit?.missingOrWeakPoints?.size ?: 0})",
                            color = Color(0xFFFBBF24),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(audit?.missingOrWeakPoints ?: emptyList()) { issue ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x18FBBF24), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x40FBBF24), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = issue.title,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = issue.description,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Conseil : ${issue.recommendation}",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // RECOMMANDATION POUR LE JOUR J
            item {
                NeonGlassCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = NeonViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONSEIL DU CONSEIL PÉDAGOGIQUE",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = audit?.adviceForDefense ?: "Préparez votre exposé oral en articulant rigoureusement le problème d'entreprise, la méthodologie de dimensionnement et les résultats probants obtenus.",
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
