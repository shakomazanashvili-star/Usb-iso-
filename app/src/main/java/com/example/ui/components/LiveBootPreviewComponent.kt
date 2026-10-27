package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DistroType
import com.example.data.model.IsoItem
import com.example.data.model.UsbDrive
import com.example.ui.theme.VentoyAmber
import com.example.ui.theme.VentoyBorder
import com.example.ui.theme.VentoyCrimson
import com.example.ui.theme.VentoyCyan
import com.example.ui.theme.VentoyCyanBright
import com.example.ui.theme.VentoyDarkBg
import com.example.ui.theme.VentoyDarkSurface
import com.example.ui.theme.VentoyDarkSurfaceVariant
import com.example.ui.theme.VentoyEmerald
import com.example.ui.theme.VentoyEmeraldBright
import com.example.ui.theme.VentoyIndigo
import com.example.ui.theme.VentoyIndigoBright
import com.example.ui.theme.VentoyTextMuted
import com.example.ui.theme.VentoyTextPrimary
import com.example.ui.theme.VentoyTextSecondary
import kotlinx.coroutines.delay

/**
 * Supported Visual Themes for the GRUB-style Live Boot Preview
 */
enum class GrubBootTheme(
    val id: String,
    val titleEn: String,
    val titleGe: String,
    val backgroundBrush: Brush,
    val headerColor: Color,
    val highlightBgColor: Color,
    val highlightTextColor: Color,
    val borderColor: Color,
    val terminalTextColor: Color
) {
    VENTOY_CYBERPUNK(
        id = "CYBERPUNK",
        titleEn = "Ventoy Cyberpunk",
        titleGe = "კიბერპანკი",
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF07132B), Color(0xFF030712))),
        headerColor = VentoyCyanBright,
        highlightBgColor = VentoyCyan.copy(alpha = 0.35f),
        highlightTextColor = VentoyCyanBright,
        borderColor = VentoyCyan.copy(alpha = 0.6f),
        terminalTextColor = Color(0xFFE2E8F0)
    ),
    CLASSIC_GRUB(
        id = "CLASSIC_GRUB",
        titleEn = "GNU GRUB Classic",
        titleGe = "კლასიკური GRUB",
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF001F3F), Color(0xFF000C1A))),
        headerColor = Color(0xFF38BDF8),
        highlightBgColor = Color(0xFF0074D9),
        highlightTextColor = Color.White,
        borderColor = Color(0xFF1E3A8A),
        terminalTextColor = Color(0xFFD1D5DB)
    ),
    DARK_OBSIDIAN(
        id = "DARK_OBSIDIAN",
        titleEn = "Dark Obsidian",
        titleGe = "ობსიდიანი",
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF121214), Color(0xFF050505))),
        headerColor = Color(0xFFF1F5F9),
        highlightBgColor = Color(0xFF27272A),
        highlightTextColor = Color.White,
        borderColor = Color(0xFF3F3F46),
        terminalTextColor = Color(0xFFA1A1AA)
    ),
    RETRO_BIOS_GREEN(
        id = "RETRO_BIOS",
        titleEn = "Retro BIOS CRT",
        titleGe = "რეტრო CRT BIOS",
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF031406), Color(0xFF000802))),
        headerColor = Color(0xFF22C55E),
        highlightBgColor = Color(0xFF15803D),
        highlightTextColor = Color(0xFFDCFCE7),
        borderColor = Color(0xFF166534),
        terminalTextColor = Color(0xFF86EFAC)
    )
}

/**
 * Live Boot Preview Component
 *
 * Renders a visual representation of how the ISO files will appear in the
 * GRUB-style boot menu on the USB drive, complete with theme switching,
 * navigation keys simulation, boot countdown, and simulated kernel/OS boot execution.
 */
@Composable
fun LiveBootPreviewComponent(
    modifier: Modifier = Modifier,
    isos: List<IsoItem>,
    isGeorgian: Boolean = false,
    usbDrive: UsbDrive? = null,
    onBootIso: ((IsoItem) -> Unit)? = null
) {
    var selectedTheme by remember { mutableStateOf(GrubBootTheme.VENTOY_CYBERPUNK) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isUefiMode by remember { mutableStateOf(true) }
    var isSecureBootActive by remember { mutableStateOf(usbDrive?.secureBootEnrolled ?: true) }
    var countdownSeconds by remember { mutableIntStateOf(10) }
    var isCountdownPaused by remember { mutableStateOf(false) }
    var isFullScreenDialog by remember { mutableStateOf(false) }

    // Simulated Booting sequence state
    var isBooting by remember { mutableStateOf(false) }
    var bootingIso by remember { mutableStateOf<IsoItem?>(null) }
    val bootLogs = remember { mutableStateListOf<String>() }

    // GRUB Command line / Console modal toggle
    var showEditCmdline by remember { mutableStateOf(false) }
    var editedCmdline by remember { mutableStateOf("") }
    var showGrubCli by remember { mutableStateOf(false) }

    // Safety index boundary check
    LaunchedEffect(isos.size) {
        if (selectedIndex >= isos.size && isos.isNotEmpty()) {
            selectedIndex = isos.size - 1
        }
    }

    // Auto-countdown effect
    LaunchedEffect(isBooting, isCountdownPaused, isos.size) {
        if (!isBooting && !isCountdownPaused && isos.isNotEmpty()) {
            countdownSeconds = 10
            while (countdownSeconds > 0 && !isCountdownPaused && !isBooting) {
                delay(1000)
                countdownSeconds--
            }
            if (countdownSeconds == 0 && !isBooting && isos.isNotEmpty()) {
                val targetIso = isos.getOrNull(selectedIndex) ?: isos.first()
                bootIsoSimulated(targetIso, bootLogs) { isBooting = true; bootingIso = it }
            }
        }
    }

    // Full screen dialog wrapper if user toggles expansion
    if (isFullScreenDialog) {
        Dialog(
            onDismissRequest = { isFullScreenDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = VentoyDarkBg
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LiveBootPreviewContent(
                        isos = isos,
                        selectedIndex = selectedIndex,
                        onSelectIndex = { selectedIndex = it; isCountdownPaused = true },
                        theme = selectedTheme,
                        onThemeChange = { selectedTheme = it },
                        isUefi = isUefiMode,
                        onToggleUefi = { isUefiMode = !isUefiMode },
                        isSecureBoot = isSecureBootActive,
                        onToggleSecureBoot = { isSecureBootActive = !isSecureBootActive },
                        countdownSeconds = countdownSeconds,
                        isCountdownPaused = isCountdownPaused,
                        onTogglePauseCountdown = { isCountdownPaused = !isCountdownPaused },
                        usbDrive = usbDrive,
                        isGeorgian = isGeorgian,
                        isFullScreen = true,
                        onToggleFullScreen = { isFullScreenDialog = false },
                        isBooting = isBooting,
                        bootingIso = bootingIso,
                        bootLogs = bootLogs,
                        onTriggerBoot = { iso ->
                            onBootIso?.invoke(iso)
                            bootIsoSimulated(iso, bootLogs) { isBooting = true; bootingIso = it }
                        },
                        onResetBoot = {
                            isBooting = false
                            bootingIso = null
                            bootLogs.clear()
                            countdownSeconds = 10
                            isCountdownPaused = false
                        },
                        onShowCmdline = { iso ->
                            editedCmdline = "linux (hd0,gpt1)/${iso.filename} quiet splash vt.global_cursor_default=0"
                            showEditCmdline = true
                        },
                        onShowCli = { showGrubCli = true }
                    )

                    // Close Fullscreen floating button
                    IconButton(
                        onClick = { isFullScreenDialog = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Embedded Card View
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, VentoyBorder, RoundedCornerShape(16.dp))
                .testTag("component_live_boot_preview"),
            colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
        ) {
            LiveBootPreviewContent(
                isos = isos,
                selectedIndex = selectedIndex,
                onSelectIndex = { selectedIndex = it; isCountdownPaused = true },
                theme = selectedTheme,
                onThemeChange = { selectedTheme = it },
                isUefi = isUefiMode,
                onToggleUefi = { isUefiMode = !isUefiMode },
                isSecureBoot = isSecureBootActive,
                onToggleSecureBoot = { isSecureBootActive = !isSecureBootActive },
                countdownSeconds = countdownSeconds,
                isCountdownPaused = isCountdownPaused,
                onTogglePauseCountdown = { isCountdownPaused = !isCountdownPaused },
                usbDrive = usbDrive,
                isGeorgian = isGeorgian,
                isFullScreen = false,
                onToggleFullScreen = { isFullScreenDialog = true },
                isBooting = isBooting,
                bootingIso = bootingIso,
                bootLogs = bootLogs,
                onTriggerBoot = { iso ->
                    onBootIso?.invoke(iso)
                    bootIsoSimulated(iso, bootLogs) { isBooting = true; bootingIso = it }
                },
                onResetBoot = {
                    isBooting = false
                    bootingIso = null
                    bootLogs.clear()
                    countdownSeconds = 10
                    isCountdownPaused = false
                },
                onShowCmdline = { iso ->
                    editedCmdline = "linux (hd0,gpt1)/${iso.filename} quiet splash vt.global_cursor_default=0"
                    showEditCmdline = true
                },
                onShowCli = { showGrubCli = true }
            )
        }
    }

    // Modal: Simulated Edit Cmdline
    if (showEditCmdline) {
        Dialog(onDismissRequest = { showEditCmdline = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, VentoyCyanBright)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = VentoyCyanBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isGeorgian) "GRUB ბრძანების რედაქტირება (Key: 'e')" else "GRUB Parameters Editor (Key: 'e')",
                            color = VentoyCyanBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = editedCmdline,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isGeorgian) "დააჭირეთ F10 ან Boot-ს ჩატვირთვისთვის" else "Press Ctrl+X or Boot to execute with customized parameters.",
                        color = VentoyTextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showEditCmdline = false }) {
                            Text(if (isGeorgian) "დახურვა" else "Cancel", color = VentoyTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showEditCmdline = false
                                val iso = isos.getOrNull(selectedIndex) ?: isos.firstOrNull()
                                if (iso != null) {
                                    bootIsoSimulated(iso, bootLogs) { isBooting = true; bootingIso = it }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan)
                        ) {
                            Text(if (isGeorgian) "ჩატვირთვა (Ctrl+X)" else "Boot (Ctrl+X)", color = Color(0xFF00363F))
                        }
                    }
                }
            }
        }
    }

    // Modal: GRUB CLI Console
    if (showGrubCli) {
        Dialog(onDismissRequest = { showGrubCli = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.Black,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GNU GRUB  version 2.06  (Ventoy+ EFI x86_64)",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF22C55E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Minimal BASH-like line editing is supported. For the first word, TAB lists possible command completions.\n\n" +
                                "grub> ls\n" +
                                "(hd0) (hd0,gpt1) (hd0,gpt2) [VTOYEFI]\n" +
                                "grub> ventoy_dump_isos\n" +
                                isos.joinToString("\n") { "  * /ventoy/${it.filename} (${it.categoryName})" } + "\n" +
                                "grub> _",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF86EFAC),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showGrubCli = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Exit to Menu (ESC)", color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Inner Layout for the GRUB Live Boot Preview Content
 */
@Composable
private fun LiveBootPreviewContent(
    isos: List<IsoItem>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    theme: GrubBootTheme,
    onThemeChange: (GrubBootTheme) -> Unit,
    isUefi: Boolean,
    onToggleUefi: () -> Unit,
    isSecureBoot: Boolean,
    onToggleSecureBoot: () -> Unit,
    countdownSeconds: Int,
    isCountdownPaused: Boolean,
    onTogglePauseCountdown: () -> Unit,
    usbDrive: UsbDrive?,
    isGeorgian: Boolean,
    isFullScreen: Boolean,
    onToggleFullScreen: () -> Unit,
    isBooting: Boolean,
    bootingIso: IsoItem?,
    bootLogs: List<String>,
    onTriggerBoot: (IsoItem) -> Unit,
    onResetBoot: () -> Unit,
    onShowCmdline: (IsoItem) -> Unit,
    onShowCli: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .animateContentSize()
    ) {
        // Section Header & Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VentoyCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isGeorgian) "Live Boot Preview (GRUB მენიუ)" else "Live Boot Preview",
                        color = VentoyTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isGeorgian) "როგორ გამოჩნდება USB ჩატვირთვისას" else "Simulates USB GRUB2 Boot Screen",
                        color = VentoyTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Fullscreen toggle
                IconButton(
                    onClick = onToggleFullScreen,
                    modifier = Modifier.size(32.dp).testTag("btn_toggle_preview_fullscreen")
                ) {
                    Icon(
                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Fullscreen",
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Configuration Toolbar (Theme Selector + UEFI/BIOS + SecureBoot)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme Chips
            GrubBootTheme.entries.forEach { t ->
                val selected = theme == t
                FilterChip(
                    selected = selected,
                    onClick = { onThemeChange(t) },
                    label = {
                        Text(
                            text = if (isGeorgian) t.titleGe else t.titleEn,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (selected) VentoyCyanBright else VentoyTextMuted
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VentoyCyan.copy(alpha = 0.25f),
                        selectedLabelColor = VentoyCyanBright,
                        containerColor = VentoyDarkSurfaceVariant,
                        labelColor = VentoyTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = if (selected) VentoyCyanBright else VentoyBorder
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // UEFI vs BIOS toggle
            FilterChip(
                selected = isUefi,
                onClick = onToggleUefi,
                label = {
                    Text(
                        text = if (isUefi) "UEFI (x86_64)" else "BIOS (MBR)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VentoyIndigo.copy(alpha = 0.3f),
                    selectedLabelColor = VentoyIndigoBright,
                    containerColor = VentoyDarkSurfaceVariant,
                    labelColor = VentoyTextMuted
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // SecureBoot Toggle
            FilterChip(
                selected = isSecureBoot,
                onClick = onToggleSecureBoot,
                label = {
                    Text(
                        text = if (isSecureBoot) "SecureBoot: ON" else "SecureBoot: OFF",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (isSecureBoot) VentoyEmeraldBright else VentoyTextMuted
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VentoyEmerald.copy(alpha = 0.25f),
                    selectedLabelColor = VentoyEmeraldBright,
                    containerColor = VentoyDarkSurfaceVariant,
                    labelColor = VentoyTextMuted
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // THE VIRTUAL MONITOR / GRUB BOOT SCREEN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(theme.backgroundBrush)
                .border(2.dp, theme.borderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (isBooting && bootingIso != null) {
                // SIMULATED OS BOOTING SCREEN
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 260.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(VentoyCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = VentoyCyanBright,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isGeorgian) "იტვირთება ${bootingIso.name}..." else "Booting ${bootingIso.name}...",
                        color = theme.headerColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = theme.headerColor,
                        trackColor = theme.borderColor.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Boot Log Console
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn {
                            items(bootLogs.size) { idx ->
                                Text(
                                    text = bootLogs[idx],
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = if (bootLogs[idx].contains("OK")) VentoyEmeraldBright else Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onResetBoot,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.borderColor),
                        modifier = Modifier.testTag("btn_reset_boot_preview")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGeorgian) "მენიუში დაბრუნება" else "Back to GRUB Menu",
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // STANDARD GRUB MENU DISPLAY
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Ventoy Top Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ventoy 1.0.99  (${if (isUefi) "UEFI-x86_64" else "BIOS-MBR"})",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = theme.headerColor,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "www.ventoy.net",
                            fontFamily = FontFamily.Monospace,
                            color = theme.terminalTextColor.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }

                    // Device & Mode Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Device: ${usbDrive?.name ?: "USB Drive"} [${usbDrive?.fsType?.name ?: "exFAT"}]",
                            fontFamily = FontFamily.Monospace,
                            color = theme.terminalTextColor,
                            fontSize = 10.sp
                        )
                        if (isSecureBoot) {
                            Text(
                                text = "[SecureBoot: Active]",
                                fontFamily = FontFamily.Monospace,
                                color = VentoyEmeraldBright,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // GRUB Border Top
                    Text(
                        text = "┌────────────────────────────────────────────────────────────┐",
                        fontFamily = FontFamily.Monospace,
                        color = theme.borderColor,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )

                    // ISO LIST ENTRIES
                    if (isos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isGeorgian) "ISO ფაილები არ არის ჩაწერილი USB-ზე" else "No ISO files found in /ventoy on USB drive",
                                fontFamily = FontFamily.Monospace,
                                color = theme.terminalTextColor,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = if (isFullScreen) 400.dp else 220.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            isos.forEachIndexed { index, iso ->
                                val isSelected = index == selectedIndex

                                val rowBg = if (isSelected) theme.highlightBgColor else Color.Transparent
                                val rowTextColor = if (isSelected) theme.highlightTextColor else theme.terminalTextColor

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .clickable { onSelectIndex(index) }
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                        .testTag("boot_entry_$index"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // GRUB Selector arrow
                                        Text(
                                            text = if (isSelected) "> *" else "   ",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) theme.highlightTextColor else Color.Transparent,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = iso.name.ifBlank { iso.filename },
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = rowTextColor,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Entry Badges (Default, Persistence, Locked)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (iso.isDefaultBoot) {
                                            Text(
                                                text = "[DEFAULT]",
                                                fontFamily = FontFamily.Monospace,
                                                color = VentoyAmber,
                                                fontSize = 9.sp
                                            )
                                        }
                                        if (iso.persistenceMb > 0) {
                                            Text(
                                                text = "[${iso.persistenceMb / 1024}G]",
                                                fontFamily = FontFamily.Monospace,
                                                color = VentoyIndigoBright,
                                                fontSize = 9.sp
                                            )
                                        }
                                        if (iso.isEncrypted) {
                                            Text(
                                                text = "[VAULT]",
                                                fontFamily = FontFamily.Monospace,
                                                color = VentoyCrimson,
                                                fontSize = 9.sp
                                            )
                                        }
                                        Text(
                                            text = "${"%.1f".format(iso.sizeBytes / (1024.0 * 1024.0 * 1024.0))}G",
                                            fontFamily = FontFamily.Monospace,
                                            color = theme.terminalTextColor.copy(alpha = 0.6f),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // GRUB Border Bottom
                    Text(
                        text = "└────────────────────────────────────────────────────────────┘",
                        fontFamily = FontFamily.Monospace,
                        color = theme.borderColor,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Selected Entry Details & Auto-boot Countdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val selectedIso = isos.getOrNull(selectedIndex)
                        if (selectedIso != null) {
                            Text(
                                text = if (!isCountdownPaused && countdownSeconds > 0) {
                                    if (isGeorgian) "ავტო-ჩატვირთვა: ${countdownSeconds} წმ-ში"
                                    else "The highlighted entry will boot in ${countdownSeconds}s"
                                } else {
                                    if (isGeorgian) "ავტო-ტაიმერი შეჩერებულია" else "Countdown paused"
                                },
                                fontFamily = FontFamily.Monospace,
                                color = if (countdownSeconds <= 3) VentoyAmber else theme.terminalTextColor,
                                fontSize = 10.sp
                            )
                        } else {
                            Text(
                                text = if (isGeorgian) "არცერთი ISO არაა არჩეული" else "No ISO entry selected",
                                fontFamily = FontFamily.Monospace,
                                color = theme.terminalTextColor,
                                fontSize = 10.sp
                            )
                        }

                        // Pause/Resume timer
                        TextButton(
                            onClick = onTogglePauseCountdown,
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (isCountdownPaused) {
                                    if (isGeorgian) "▶ განახლება" else "▶ Resume"
                                } else {
                                    if (isGeorgian) "⏸ შეჩერება" else "⏸ Pause"
                                },
                                color = theme.headerColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Navigation Controls & Boot Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Up/Down simulated keys
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = {
                                    if (selectedIndex > 0) onSelectIndex(selectedIndex - 1)
                                },
                                enabled = selectedIndex > 0,
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_grub_key_up"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.headerColor),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.borderColor)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Up",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("▲", fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    if (selectedIndex < isos.size - 1) onSelectIndex(selectedIndex + 1)
                                },
                                enabled = selectedIndex < isos.size - 1,
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_grub_key_down"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.headerColor),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.borderColor)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Down",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("▼", fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                        }

                        // Boot Selected Button
                        val activeIso = isos.getOrNull(selectedIndex)
                        Button(
                            onClick = {
                                if (activeIso != null) onTriggerBoot(activeIso)
                            },
                            enabled = activeIso != null,
                            colors = ButtonDefaults.buttonColors(containerColor = theme.highlightTextColor),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("btn_grub_boot_selected")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(0xFF00363F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isGeorgian) "ჩატვირთვა (Enter)" else "Boot Entry",
                                color = Color(0xFF00363F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // GRUB Bottom Function Keys Legend
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "[Enter] Boot",
                                fontFamily = FontFamily.Monospace,
                                color = theme.terminalTextColor,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "[e] Edit",
                                fontFamily = FontFamily.Monospace,
                                color = theme.headerColor,
                                fontSize = 9.sp,
                                modifier = Modifier.clickable {
                                    val iso = isos.getOrNull(selectedIndex)
                                    if (iso != null) onShowCmdline(iso)
                                }
                            )
                            Text(
                                text = "[c] CLI",
                                fontFamily = FontFamily.Monospace,
                                color = theme.headerColor,
                                fontSize = 9.sp,
                                modifier = Modifier.clickable { onShowCli() }
                            )
                            Text(
                                text = "[F1] Memdisk",
                                fontFamily = FontFamily.Monospace,
                                color = theme.terminalTextColor,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "[F2] Power",
                                fontFamily = FontFamily.Monospace,
                                color = theme.terminalTextColor,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "[F4] LocalBoot",
                                fontFamily = FontFamily.Monospace,
                                color = theme.terminalTextColor,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "[F5] Refresh",
                                fontFamily = FontFamily.Monospace,
                                color = theme.terminalTextColor,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simulates the OS and kernel boot log sequence
 */
private fun bootIsoSimulated(
    iso: IsoItem,
    logList: MutableList<String>,
    onStarted: (IsoItem) -> Unit
) {
    onStarted(iso)
    logList.clear()
    logList.add("Probing EFI System Partition (hd0,gpt1)...")
    logList.add("Ventoy Chainloader: Initializing kernel image for ${iso.filename}...")
    logList.add("Loading Linux kernel /casper/vmlinuz... [ OK ]")
    logList.add("Loading Initial RAMDisk /casper/initrd... [ OK ]")
    logList.add("Mounted rootfs with overlayfs...")
    if (iso.persistenceMb > 0) {
        logList.add("Found persistence loopback file: /ventoy/persistence.dat (${iso.persistenceMb} MB) [ ATTACHED ]")
    }
    if (iso.isEncrypted) {
        logList.add("Unlocking LUKS volume container via Ventoy Vault key... [ SUCCESS ]")
    }
    logList.add("Starting systemd-udevd v255.4...")
    logList.add("[ OK ] Reached target System Initialization.")
    logList.add("[ OK ] Reached target Graphical Interface.")
    logList.add("Welcome to ${iso.name.ifBlank { iso.distroType.displayName }} (Live Environment)!")
}
