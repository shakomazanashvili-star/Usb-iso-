package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.analyzer.OsCategory
import com.example.data.model.IsoItem
import com.example.ui.theme.VentoyAmber
import com.example.ui.theme.VentoyBorder
import com.example.ui.theme.VentoyCyan
import com.example.ui.theme.VentoyCyanBright
import com.example.ui.theme.VentoyDarkBg
import com.example.ui.theme.VentoyDarkSurface
import com.example.ui.theme.VentoyDarkSurfaceVariant
import com.example.ui.theme.VentoyEmeraldBright
import com.example.ui.theme.VentoyIndigo
import com.example.ui.theme.VentoyIndigoBright
import com.example.ui.theme.VentoyTextMuted
import com.example.ui.theme.VentoyTextPrimary
import com.example.ui.theme.VentoyTextSecondary
import kotlinx.coroutines.delay

@Composable
fun VentoyBootSimulator(
    isOpen: Boolean,
    isos: List<IsoItem>,
    isGe: Boolean,
    simulatedBootingIso: IsoItem?,
    simulatorLogs: List<String>,
    currentTheme: String,
    onSelectTheme: (String) -> Unit,
    onBootIso: (IsoItem) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>("ALL") }
    var countdownSeconds by remember { mutableIntStateOf(10) }

    val filteredList = isos.filter { item ->
        selectedCategory == null || selectedCategory == "ALL" || item.categoryName == selectedCategory
    }

    LaunchedEffect(filteredList.size) {
        if (selectedIndex >= filteredList.size) {
            selectedIndex = (filteredList.size - 1).coerceAtLeast(0)
        }
    }

    // Auto-boot countdown
    LaunchedEffect(simulatedBootingIso) {
        if (simulatedBootingIso == null) {
            countdownSeconds = 10
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds--
            }
            val defaultItem = filteredList.firstOrNull { it.isDefaultBoot } ?: filteredList.firstOrNull()
            if (defaultItem != null) {
                onBootIso(defaultItem)
            }
        }
    }

    // Theme Background & Accents
    val (themeBg, themeHeaderColor, themeBorderColor) = when (currentTheme) {
        "CLASSIC_BLUE" -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF0F2042), Color(0xFF081026))),
            Color(0xFF38BDF8),
            Color(0xFF1E3A8A)
        )
        "DARK_OBSIDIAN" -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF111111), Color(0xFF000000))),
            Color(0xFFE2E8F0),
            Color(0xFF27272A)
        )
        else -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF07132B), Color(0xFF030712))),
            VentoyCyanBright,
            VentoyCyan.copy(alpha = 0.5f)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, themeBorderColor, RoundedCornerShape(16.dp)),
            color = Color.Black
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // Monitor Title & Header Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(VentoyEmeraldBright)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VENTOY+ 1.0.99-SECURE (UEFI x86_64)",
                                color = themeHeaderColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Theme Switcher Buttons
                            listOf(
                                "CYBER_NEON" to "Neon",
                                "CLASSIC_BLUE" to "Classic",
                                "DARK_OBSIDIAN" to "Stealth"
                            ).forEach { (tKey, tLabel) ->
                                val isSel = currentTheme == tKey
                                Text(
                                    text = tLabel,
                                    color = if (isSel) themeHeaderColor else VentoyTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier
                                        .clickable { onSelectTheme(tKey) }
                                        .padding(horizontal = 6.dp)
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = VentoyTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Simulated Terminal Boot View if booting
                    if (simulatedBootingIso != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF050914))
                                .border(1.dp, VentoyCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = ">>> BOOTING: ${simulatedBootingIso.name} <<<",
                                        color = VentoyEmeraldBright,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "UEFI 64-BIT HANDOFF",
                                        color = VentoyCyanBright,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(simulatorLogs) { logLine ->
                                        Text(
                                            text = "> $logLine",
                                            color = if (logLine.contains("Welcome")) VentoyEmeraldBright
                                            else if (logLine.contains("SECURE")) VentoyCyanBright
                                            else VentoyTextSecondary,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { onBootIso(simulatedBootingIso) },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VentoyCyanBright)
                                ) {
                                    Text("გადატვირთვა / Reboot Menu", fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // Category Tabs inside Ventoy Boot Menu
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val tabs = listOf(
                                "ALL" to (if (isGe) "ყველა ISO" else "All Images"),
                                OsCategory.WINDOWS.name to "Windows",
                                OsCategory.LINUX_DESKTOP.name to "Linux Desktop",
                                OsCategory.SECURITY_FORENSICS.name to "Security & PenTest",
                                OsCategory.RECOVERY_DIAGNOSTICS.name to "Recovery Tools"
                            )
                            items(tabs) { (catKey, catLabel) ->
                                val isSel = selectedCategory == catKey
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) themeHeaderColor.copy(alpha = 0.25f) else Color(0x331E293B))
                                        .border(1.dp, if (isSel) themeHeaderColor else Color(0x33334155), RoundedCornerShape(6.dp))
                                        .clickable {
                                            selectedCategory = catKey
                                            selectedIndex = 0
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = catLabel,
                                        color = if (isSel) themeHeaderColor else VentoyTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // GRUB2 Menu Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xDD030712))
                                .border(1.5.dp, themeBorderColor, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            if (filteredList.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "ამ კატეგორიაში ISO ფაილები არ არის",
                                        color = VentoyTextMuted,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(filteredList.indices.toList()) { idx ->
                                        val item = filteredList[idx]
                                        val isSelected = selectedIndex == idx

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (isSelected) themeHeaderColor.copy(alpha = 0.35f)
                                                    else Color.Transparent
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) themeHeaderColor else Color.Transparent,
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .clickable {
                                                    selectedIndex = idx
                                                    onBootIso(item)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = if (isSelected) ">" else " ",
                                                    color = themeHeaderColor,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${idx + 1}. ${item.name}",
                                                    color = if (isSelected) Color.White else VentoyTextPrimary,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    maxLines = 1
                                                )
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                if (item.isEncrypted) {
                                                    Text(
                                                        text = "[VAULT]",
                                                        color = VentoyAmber,
                                                        fontSize = 10.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                                if (item.persistenceMb > 0) {
                                                    Text(
                                                        text = "[${item.persistenceMb / 1024}G]",
                                                        color = VentoyIndigoBright,
                                                        fontSize = 10.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                                Text(
                                                    text = "${(item.sizeBytes / (1024.0 * 1024.0 * 1024.0)).let { "%.1fG".format(it) }}",
                                                    color = VentoyTextMuted,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Countdown & Selected Details
                        val currentItem = filteredList.getOrNull(selectedIndex)
                        if (currentItem != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (countdownSeconds > 0)
                                        (if (isGe) "ავტო-ბუთი: ${countdownSeconds} წამში..." else "Auto-booting in ${countdownSeconds}s...")
                                    else "",
                                    color = VentoyCyanBright,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )

                                Button(
                                    onClick = { onBootIso(currentItem) },
                                    colors = ButtonDefaults.buttonColors(containerColor = themeHeaderColor),
                                    modifier = Modifier.height(36.dp).testTag("btn_simulator_boot")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color(0xFF00363F),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isGe) "ჩატვირთვა (Enter)" else "Boot Selected",
                                        color = Color(0xFF00363F),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hotkey Legend at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0A0F1D))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "[F1] Help  [F2] Power  [F3] TreeView  [F4] Memdisk  [F5] Refresh  [F6] MOK",
                            color = VentoyTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
