package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.ReportViewModel
import com.example.ui.screens.ComplianceAuditScreen
import com.example.ui.screens.JurySimulatorScreen
import com.example.ui.screens.RapportBuilderScreen
import com.example.ui.screens.ReportPreviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

class MainActivity : ComponentActivity() {

  private val viewModel: ReportViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        ReportApp(viewModel = viewModel)
      }
    }
  }
}

private data class NavItem(
  val screen: AppScreen,
  val label: String,
  val icon: ImageVector,
  val testTag: String
)

@Composable
fun ReportApp(
  viewModel: ReportViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val navItems = listOf(
    NavItem(AppScreen.BUILDER, "Éditeur", Icons.Default.EditNote, "tab_builder"),
    NavItem(AppScreen.PREVIEW, "28 Pages", Icons.Default.Description, "tab_preview"),
    NavItem(AppScreen.AUDIT, "Audit", Icons.Default.Verified, "tab_audit"),
    NavItem(AppScreen.JURY_SIMULATOR, "Jury", Icons.Default.Psychology, "tab_jury"),
    NavItem(AppScreen.SETTINGS, "Normes", Icons.Default.Settings, "tab_settings")
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    bottomBar = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF070A18))
          .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
              colors = listOf(Color(0x3300F5FF), Color(0x1100F5FF))
            ),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
          )
          .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
      ) {
        NavigationBar(
          containerColor = Color(0xF20B0F24),
          contentColor = TextWhite,
          modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .testTag("main_bottom_nav"),
          tonalElevation = 0.dp
        ) {
          navItems.forEach { item ->
            val selected = uiState.currentScreen == item.screen
            NavigationBarItem(
              selected = selected,
              onClick = { viewModel.setScreen(item.screen) },
              icon = {
                Icon(
                  imageVector = item.icon,
                  contentDescription = item.label,
                  modifier = Modifier.size(22.dp)
                )
              },
              label = {
                Text(
                  text = item.label,
                  fontSize = 11.sp,
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                indicatorColor = Color(0x2800F5FF),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
              ),
              modifier = Modifier.testTag(item.testTag)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    AnimatedContent(
      targetState = uiState.currentScreen,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "ScreenTransition",
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = innerPadding.calculateBottomPadding())
    ) { screen ->
      when (screen) {
        AppScreen.BUILDER -> RapportBuilderScreen(
          viewModel = viewModel,
          uiState = uiState,
          modifier = Modifier.fillMaxSize()
        )
        AppScreen.PREVIEW -> ReportPreviewScreen(
          viewModel = viewModel,
          uiState = uiState,
          modifier = Modifier.fillMaxSize()
        )
        AppScreen.AUDIT -> ComplianceAuditScreen(
          viewModel = viewModel,
          uiState = uiState,
          modifier = Modifier.fillMaxSize()
        )
        AppScreen.JURY_SIMULATOR -> JurySimulatorScreen(
          viewModel = viewModel,
          uiState = uiState,
          modifier = Modifier.fillMaxSize()
        )
        AppScreen.SETTINGS -> SettingsScreen(
          viewModel = viewModel,
          uiState = uiState,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}


