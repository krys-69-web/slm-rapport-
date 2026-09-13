package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DocBlock
import com.example.model.ReportPage
import com.example.ui.ReportUiState
import com.example.ui.ReportViewModel
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonGlassCard
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.ReportExporter
import kotlinx.coroutines.launch

@Composable
fun ReportPreviewScreen(
    viewModel: ReportViewModel,
    uiState: ReportUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val pages = uiState.generatedPages
    val currentPageIndex = uiState.currentPreviewPage.coerceIn(1, 28) - 1
    val activePage: ReportPage? = pages.getOrNull(currentPageIndex)

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
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP ACTION BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCC0B0E1E))
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeonButton(
                        text = "RETOUR",
                        onClick = { viewModel.returnToEditor() },
                        style = NeonButtonStyle.GHOST_GLASS,
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = TextWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        testTag = "btn_back_editor"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NeonButton(
                            text = "WORD .DOC",
                            onClick = {
                                val docHtml = viewModel.getWordDocHtml(context)
                                ReportExporter.exportAndShareWord(
                                    context,
                                    docHtml,
                                    uiState.company.inputName
                                )
                            },
                            style = NeonButtonStyle.PRIMARY_NEON,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Export Word",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            testTag = "btn_export_word"
                        )

                        if (activity != null) {
                            NeonButton(
                                text = "PDF / PRINT",
                                onClick = {
                                    val docHtml = viewModel.getWordDocHtml(context)
                                    ReportExporter.printOrSavePdf(activity, docHtml)
                                },
                                style = NeonButtonStyle.NEON_GLASS,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = "Imprimer PDF",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                testTag = "btn_export_pdf"
                            )
                        }
                    }
                }
            }

            // QUICK PAGE JUMPER & STATUS BADGE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x40000000))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MÉMOIRE RÉGLEMENTAIRE (28 PAGES CONFORMES)",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "PAGE ${uiState.currentPreviewPage} SUR 28",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable Quick Jump Row 1 to 28
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 1..28) {
                        val isSelected = i == uiState.currentPreviewPage
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) NeonCyan else Color(0x22FFFFFF)
                                )
                                .clickable {
                                    viewModel.setPreviewPage(i)
                                    scope.launch { listState.animateScrollToItem(0) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$i",
                                color = if (isSelected) Color.Black else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // MAIN PAGE DISPLAY CONTAINER (A4 WORD SHEET SIMULATION)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 90.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    if (activePage != null) {
                        A4PageSheet(page = activePage, companyName = uiState.company.inputName, studentName = uiState.student.studentName)
                    } else {
                        Text("Aucune page générée", color = TextWhite)
                    }
                }
            }
        }

        // BOTTOM NAVIGATOR PREVIOUS / NEXT
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xE60B0E1E))
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 516.dp)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonButton(
                    text = "PAGE PRÉCÉDENTE",
                    onClick = {
                        if (uiState.currentPreviewPage > 1) {
                            viewModel.setPreviewPage(uiState.currentPreviewPage - 1)
                            scope.launch { listState.animateScrollToItem(0) }
                        }
                    },
                    style = NeonButtonStyle.GHOST_GLASS,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = "Précédent",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_prev_page"
                )

                Spacer(modifier = Modifier.width(12.dp))

                NeonButton(
                    text = "PAGE SUIVANTE",
                    onClick = {
                        if (uiState.currentPreviewPage < 28) {
                            viewModel.setPreviewPage(uiState.currentPreviewPage + 1)
                            scope.launch { listState.animateScrollToItem(0) }
                        }
                    },
                    style = NeonButtonStyle.PRIMARY_NEON,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Suivant",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_next_page"
                )
            }
        }
    }
}

@Composable
private fun A4PageSheet(
    page: ReportPage,
    companyName: String,
    studentName: String
) {
    val sheetShape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .shadow(16.dp, sheetShape, spotColor = Color(0x4400F5FF))
            .background(Color(0xFFFCFDFE), sheetShape)
            .border(1.dp, Color(0xFFDDE3EA), sheetShape)
            .padding(22.dp)
    ) {
        Column {
            // Header Bar (Times New Roman 9pt, border-bottom 1px solid #007AFF)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${companyName.ifBlank { "ICC CORPORATE" }} - $studentName".uppercase(),
                    color = Color(0xFF555555),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "RAPPORT DE STAGE RÉGLEMENTAIRE",
                    color = Color(0xFF007AFF),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
                thickness = 1.dp,
                color = Color(0xFF007AFF)
            )

            // Chapter & Page Title
            Text(
                text = page.chapterTitle,
                color = Color(0xFF007AFF),
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = page.pageTitle,
                color = Color(0xFF0B1F44),
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp, bottom = 14.dp),
                thickness = 0.5.dp,
                color = Color(0xFFE2E8F0)
            )

            if (page.pageNumber == 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.slm_logo),
                            contentDescription = "Sceau Officiel SLM",
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF007AFF), CircleShape)
                        )
                        Text(
                            text = "SLM RAPPORT BUILDER SMARTLY - SCEAU OFFICIEL",
                            color = Color(0xFF007AFF),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Blocks content
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (block in page.blocks) {
                    when (block) {
                        is DocBlock.Title -> {
                            Text(
                                text = block.text,
                                color = Color(0xFF0B1F44),
                                fontSize = if (block.level == 1) 15.sp else 13.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                textAlign = if (block.level == 1) TextAlign.Center else TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is DocBlock.Paragraph -> {
                            Text(
                                text = "        " + block.text,
                                color = Color(0xFF1A202C),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Serif,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Justify
                            )
                        }

                        is DocBlock.Callout -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF0F7FF), RoundedCornerShape(8.dp))
                                    .border(
                                        width = 4.dp,
                                        color = Color(0xFF007AFF),
                                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = block.title.uppercase(),
                                        color = Color(0xFF0B1F44),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = block.text,
                                        color = Color(0xFF2D3748),
                                        fontSize = 11.5.sp,
                                        fontFamily = FontFamily.Serif,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        is DocBlock.Table -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                    .clip(RoundedCornerShape(6.dp))
                            ) {
                                // Table Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF007AFF))
                                        .padding(horizontal = 8.dp, vertical = 7.dp)
                                ) {
                                    block.headers.forEachIndexed { i, h ->
                                        Text(
                                            text = h,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(if (i == 0) 1.2f else 1f)
                                        )
                                    }
                                }

                                // Table Rows
                                block.rows.forEachIndexed { rIdx, row ->
                                    val bg = if (rIdx % 2 == 0) Color(0xFFFFFFFF) else Color(0xFFF8FAFC)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(bg)
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        row.forEachIndexed { cIdx, cell ->
                                            Text(
                                                text = cell,
                                                color = Color(0xFF334155),
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Serif,
                                                modifier = Modifier.weight(if (cIdx == 0) 1.2f else 1f)
                                            )
                                        }
                                    }
                                    if (rIdx < block.rows.size - 1) {
                                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE2E8F0))
                                    }
                                }
                            }
                        }

                        is DocBlock.KeyValueList -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                    .clip(RoundedCornerShape(6.dp))
                            ) {
                                block.items.forEachIndexed { idx, item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (idx % 2 == 0) Color(0xFFF0F7FF) else Color.White)
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = item.first,
                                            color = Color(0xFF0B1F44),
                                            fontSize = 10.5.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(0.4f)
                                        )
                                        Text(
                                            text = item.second,
                                            color = Color(0xFF1E293B),
                                            fontSize = 10.5.sp,
                                            fontFamily = FontFamily.Serif,
                                            modifier = Modifier.weight(0.6f)
                                        )
                                    }
                                    if (idx < block.items.size - 1) {
                                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE2E8F0))
                                    }
                                }
                            }
                        }

                        is DocBlock.Figure -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[Figure Technique] : ${block.caption}",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        is DocBlock.SignOff -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 28.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = block.leftTitle,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0B1F44)
                                    )
                                    Spacer(modifier = Modifier.height(36.dp))
                                    Text(
                                        text = block.leftName,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = block.rightTitle,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0B1F44)
                                    )
                                    Spacer(modifier = Modifier.height(36.dp))
                                    Text(
                                        text = block.rightName,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Bar (Times New Roman 9pt)
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 6.dp),
                thickness = 0.5.dp,
                color = Color(0xFFCBD5E1)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Page ${page.pageNumber} / ${page.totalPages}",
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Rapport de stage professionnel",
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}
