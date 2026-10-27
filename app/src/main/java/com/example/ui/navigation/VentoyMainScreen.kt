package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AuditResultDialog
import com.example.ui.components.OperationProgressDialog
import com.example.ui.components.VentoyBootSimulator
import com.example.ui.components.VentoyJsonDialog
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.IsoLibraryScreen
import com.example.ui.screens.OsCatalogScreen
import com.example.ui.screens.SecurityVaultScreen
import com.example.ui.screens.VentoyDashboardScreen
import com.example.ui.theme.VentoyBorder
import com.example.ui.theme.VentoyCyan
import com.example.ui.theme.VentoyCyanBright
import com.example.ui.theme.VentoyDarkBg
import com.example.ui.theme.VentoyDarkSurface
import com.example.ui.theme.VentoyTextMuted
import com.example.ui.theme.VentoyTextPrimary
import com.example.ui.theme.VentoyTextSecondary
import com.example.ui.viewmodel.VentoyViewModel

enum class VentoyTab(
    val titleGe: String,
    val titleEn: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val tag: String
) {
    DRIVE("დისკი", "Drive", Icons.Filled.Usb, Icons.Outlined.Usb, "tab_drive"),
    ISOS("ISO-ები", "ISOs", Icons.Filled.Folder, Icons.Outlined.Folder, "tab_isos"),
    SECURITY("დაცვა", "Vault", Icons.Filled.Shield, Icons.Outlined.Security, "tab_security"),
    CATALOG("კატალოგი", "Catalog", Icons.Filled.CloudDownload, Icons.Outlined.CloudDownload, "tab_catalog"),
    AI("Boot AI", "AI Bot", Icons.Filled.SmartToy, Icons.Outlined.SmartToy, "tab_ai")
}

@Composable
fun VentoyMainScreen(
    viewModel: VentoyViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(VentoyTab.DRIVE) }
    val isGe by viewModel.isGeorgian.collectAsState()
    val progress by viewModel.operationProgress.collectAsState()
    val auditedIso by viewModel.auditedIso.collectAsState()
    val isAuditing by viewModel.isAuditing.collectAsState()
    val auditResult by viewModel.auditResult.collectAsState()
    val ventoyJson by viewModel.generatedVentoyJson.collectAsState()
    val isSimulatorOpen by viewModel.isBootSimulatorOpen.collectAsState()
    val simulatedBootingIso by viewModel.simulatedBootingIso.collectAsState()
    val simulatorLogs by viewModel.simulatorLogs.collectAsState()
    val simulatorTheme by viewModel.simulatorTheme.collectAsState()
    val isos by viewModel.isos.collectAsState()

    // BackHandler: Return to Drive tab if on another screen
    BackHandler(enabled = currentTab != VentoyTab.DRIVE) {
        currentTab = VentoyTab.DRIVE
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = VentoyDarkBg,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .border(1.dp, VentoyBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                containerColor = VentoyDarkSurface,
                tonalElevation = 8.dp
            ) {
                VentoyTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
                                contentDescription = if (isGe) tab.titleGe else tab.titleEn,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isGe) tab.titleGe else tab.titleEn,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00363F),
                            selectedTextColor = VentoyCyanBright,
                            indicatorColor = VentoyCyanBright,
                            unselectedIconColor = VentoyTextSecondary,
                            unselectedTextColor = VentoyTextMuted
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                VentoyTab.DRIVE -> VentoyDashboardScreen(viewModel = viewModel)
                VentoyTab.ISOS -> IsoLibraryScreen(viewModel = viewModel)
                VentoyTab.SECURITY -> SecurityVaultScreen(viewModel = viewModel)
                VentoyTab.CATALOG -> OsCatalogScreen(viewModel = viewModel)
                VentoyTab.AI -> AiChatScreen(viewModel = viewModel)
            }
        }
    }

    // Global Operation Progress Dialog (Installing, Updating, Wiping)
    OperationProgressDialog(
        progress = progress,
        onDismiss = { viewModel.dismissOperationProgress() }
    )

    // Global AI Audit Result Dialog
    AuditResultDialog(
        iso = auditedIso,
        isAuditing = isAuditing,
        resultText = auditResult,
        isGe = isGe,
        onDismiss = { viewModel.dismissAudit() }
    )

    // Global ventoy.json Dialog
    VentoyJsonDialog(
        jsonContent = ventoyJson,
        isGe = isGe,
        onDismiss = { viewModel.dismissVentoyJson() }
    )

    // Interactive Live Ventoy Boot Menu Simulator
    VentoyBootSimulator(
        isOpen = isSimulatorOpen,
        isos = isos,
        isGe = isGe,
        simulatedBootingIso = simulatedBootingIso,
        simulatorLogs = simulatorLogs,
        currentTheme = simulatorTheme,
        onSelectTheme = { viewModel.setSimulatorTheme(it) },
        onBootIso = { viewModel.simulateBoot(it) },
        onDismiss = { viewModel.closeBootSimulator() }
    )
}
