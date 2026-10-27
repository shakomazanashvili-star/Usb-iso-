package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.ui.components.OsMetadataSearchBar
import com.example.ui.components.LiveBootPreviewComponent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.analyzer.IsoDetectionEngine
import com.example.data.analyzer.OsCategory
import com.example.data.model.AutoInstallScript
import com.example.data.model.DistroType
import com.example.data.model.IsoItem
import com.example.ui.components.CyberBadge
import com.example.ui.components.DistroIconBadge
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
import com.example.ui.theme.VentoySurfaceContainer
import com.example.ui.theme.VentoyTextMuted
import com.example.ui.theme.VentoyTextPrimary
import com.example.ui.theme.VentoyTextSecondary
import com.example.ui.viewmodel.VentoyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsoLibraryScreen(
    viewModel: VentoyViewModel,
    modifier: Modifier = Modifier
) {
    val isos by viewModel.isos.collectAsState()
    val isGe by viewModel.isGeorgian.collectAsState()
    val verifyingId by viewModel.verifyingIsoId.collectAsState()
    val onlineMetadataResult by viewModel.onlineMetadataResult.collectAsState()
    val isFetchingOnlineMetadata by viewModel.isFetchingOnlineMetadata.collectAsState()
    val usbDrive by viewModel.usbDrive.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showOnlineMetadataSearch by remember { mutableStateOf(false) }
    var showLiveBootPreview by remember { mutableStateOf(false) }

    var showAddIsoModal by remember { mutableStateOf(false) }
    var selectedIsoForPersistence by remember { mutableStateOf<IsoItem?>(null) }
    var selectedIsoForAutoInstall by remember { mutableStateOf<IsoItem?>(null) }

    // SAF Document Picker for zero-permission ISO selection
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val rawName = uri.lastPathSegment?.substringAfterLast('/') ?: "custom_distro.iso"
            viewModel.addCustomIso(
                uri = uri,
                filename = rawName,
                sizeBytes = 4_200_000_000L
            )
        }
    }

    val filteredIsos = isos.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.filename.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedFilter) {
            "ALL" -> true
            "WIN" -> item.categoryName == OsCategory.WINDOWS.name || item.distroType == DistroType.WINDOWS
            "LINUX" -> item.categoryName == OsCategory.LINUX_DESKTOP.name || item.distroType in listOf(DistroType.UBUNTU, DistroType.ARCH, DistroType.FEDORA)
            "SECURITY" -> item.categoryName == OsCategory.SECURITY_FORENSICS.name || item.distroType in listOf(DistroType.KALI, DistroType.TAILS)
            "RECOVERY" -> item.categoryName == OsCategory.RECOVERY_DIAGNOSTICS.name || item.distroType in listOf(DistroType.CLONEZILLA, DistroType.RESCUE)
            "VAULT" -> item.isEncrypted
            else -> true
        }
        matchesSearch && matchesCategory
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VentoyDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isGe) "მულტიბუთ ISO ბიბლიოთეკა" else "Multiboot ISO Vault",
                            color = VentoyTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${isos.size} ${if (isGe) "სისტემა • ავტო-კატეგორიზაციით" else "ISOs • Auto-Categorized"}",
                            color = VentoyCyanBright,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    // Open Live Boot Menu Simulator
                    Button(
                        onClick = { viewModel.openBootSimulator() },
                        colors = ButtonDefaults.buttonColors(containerColor = VentoyIndigo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .height(34.dp)
                            .testTag("btn_open_simulator")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isGe) "ბუთ მენიუ" else "Boot Menu",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Add ISO Header Button
                    IconButton(
                        onClick = { showAddIsoModal = true },
                        modifier = Modifier.testTag("btn_add_iso_header")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VentoyCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add ISO",
                                tint = VentoyCyanBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VentoyDarkSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDocumentLauncher.launch(arrayOf("*/*", "application/x-iso9660-image")) },
                containerColor = VentoyCyan,
                contentColor = Color(0xFF00363F),
                modifier = Modifier.testTag("fab_pick_iso")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGe) "ISO-ს დამატება (SAF)" else "Add ISO Image",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (isGe) "მოძებნე ISO, ოპერაციული სისტემა..." else "Search ISO or distribution...",
                        color = VentoyTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = VentoyCyanBright
                    )
                },
                trailingIcon = {
                    // Re-run Auto-Categorization on all items
                    IconButton(onClick = { viewModel.autoCategorizeLibrary() }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto-Categorize",
                            tint = VentoyCyanBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("input_search_iso"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VentoyCyanBright,
                    unfocusedBorderColor = VentoyBorder,
                    focusedContainerColor = VentoyDarkSurface,
                    unfocusedContainerColor = VentoyDarkSurface,
                    focusedTextColor = VentoyTextPrimary,
                    unfocusedTextColor = VentoyTextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to (if (isGe) "ყველა (${isos.size})" else "All (${isos.size})"),
                    "WIN" to "Windows",
                    "LINUX" to "Linux",
                    "SECURITY" to (if (isGe) "კიბერუსაფრთხოება" else "Security"),
                    "RECOVERY" to (if (isGe) "აღდგენა" else "Recovery"),
                    "VAULT" to (if (isGe) "🔒 დაცული" else "🔒 Vault")
                )
                items(filters) { (key, label) ->
                    val selected = selectedFilter == key
                    FilterChip(
                        selected = selected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
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
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Quick Feature Toggles: Web Metadata Search & Live Boot Preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showOnlineMetadataSearch,
                    onClick = {
                        showOnlineMetadataSearch = !showOnlineMetadataSearch
                        if (showOnlineMetadataSearch) showLiveBootPreview = false
                    },
                    label = {
                        Text(
                            text = if (isGe) "🌐 ვებ ძიება & ტეგირება" else "🌐 Web Search & Tagging",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VentoyCyan.copy(alpha = 0.25f),
                        selectedLabelColor = VentoyCyanBright,
                        containerColor = VentoyDarkSurfaceVariant,
                        labelColor = VentoyTextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_toggle_web_metadata")
                )

                FilterChip(
                    selected = showLiveBootPreview,
                    onClick = {
                        showLiveBootPreview = !showLiveBootPreview
                        if (showLiveBootPreview) showOnlineMetadataSearch = false
                    },
                    label = {
                        Text(
                            text = if (isGe) "🖥 Live Boot Preview" else "🖥 Live Boot Preview",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VentoyIndigo.copy(alpha = 0.3f),
                        selectedLabelColor = VentoyIndigoBright,
                        containerColor = VentoyDarkSurfaceVariant,
                        labelColor = VentoyTextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_toggle_boot_preview")
                )
            }

            // Interactive Web Metadata Search Bar Component
            AnimatedVisibility(visible = showOnlineMetadataSearch) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    OsMetadataSearchBar(
                        isGeorgian = isGe,
                        isoItems = isos,
                        onTagIsoItem = { targetIso, metadata ->
                            viewModel.applyOnlineMetadata(targetIso, metadata)
                        }
                    )
                }
            }

            // Interactive Live Boot Preview Component
            AnimatedVisibility(visible = showLiveBootPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    LiveBootPreviewComponent(
                        isos = isos,
                        isGeorgian = isGe,
                        usbDrive = usbDrive,
                        onBootIso = { iso ->
                            viewModel.simulateBoot(iso)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ISO Items List
            if (filteredIsos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = VentoyTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isGe) "ISO ფაილები ვერ მოიძებნა" else "No ISO images found",
                            color = VentoyTextSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isGe) "დააჭირე 'ISO-ს დამატება' ღილაკს" else "Tap 'Add ISO' to add from device",
                            color = VentoyTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredIsos, key = { it.id }) { iso ->
                        IsoItemCard(
                            iso = iso,
                            isGe = isGe,
                            isVerifying = verifyingId == iso.id,
                            onSetDefaultBoot = { viewModel.setDefaultBoot(iso.id) },
                            onToggleVault = { viewModel.toggleIsoVaultEncryption(iso) },
                            onTogglePin = { viewModel.toggleIsoPin(iso) },
                            onVerifyChecksum = { viewModel.verifyChecksum(iso) },
                            onAuditAi = { viewModel.auditIsoWithAi(iso) },
                            onAutoPopulateOnline = { viewModel.fetchOnlineMetadataForIso(iso) },
                            onConfigurePersistence = { selectedIsoForPersistence = iso },
                            onConfigureAutoInstall = { selectedIsoForAutoInstall = iso },
                            onDelete = { viewModel.deleteIso(iso) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp)) // padding for FAB
                    }
                }
            }
        }
    }

    // Add Custom ISO Dialog with Live Auto-Detection Preview
    if (showAddIsoModal) {
        var customFilename by remember { mutableStateOf("") }
        val detection = remember(customFilename) {
            IsoDetectionEngine.analyzeIso(customFilename.ifBlank { "ubuntu-24.04-desktop-amd64.iso" })
        }

        AlertDialog(
            onDismissRequest = { showAddIsoModal = false },
            containerColor = VentoyDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGe) "ISO-ს დამატება & ავტო-დეტექცია" else "Add ISO & Auto-Detection",
                        color = VentoyTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = customFilename,
                        onValueChange = { customFilename = it },
                        label = { Text("ISO ფაილის სახელი") },
                        placeholder = { Text("e.g. Win11_24H2_x64.iso ან kali.iso") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VentoyCyanBright,
                            unfocusedBorderColor = VentoyBorder,
                            focusedTextColor = VentoyTextPrimary,
                            unfocusedTextColor = VentoyTextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto-Detection Analysis Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, VentoyCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = VentoyDarkSurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isGe) "ავტომატური ანალიზი:" else "Auto-Detection Analysis:",
                                    color = VentoyTextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                CyberBadge(
                                    text = "${detection.confidenceScore}% CONFIDENCE",
                                    color = VentoyEmeraldBright
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = detection.detectedName,
                                color = VentoyTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                CyberBadge(text = detection.category.displayNameEn, color = VentoyIndigoBright)
                                CyberBadge(text = detection.architecture, color = VentoyCyanBright)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 ${detection.analysisNotes}",
                                color = VentoyTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddIsoModal = false
                        viewModel.addCustomIso(
                            uri = Uri.parse("file:///ventoy/${customFilename.ifBlank { "custom_os.iso" }}"),
                            filename = customFilename.ifBlank { "custom_os.iso" },
                            sizeBytes = 3_800_000_000L
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan)
                ) {
                    Text(if (isGe) "დამატება & შენახვა" else "Add to USB", color = Color(0xFF00363F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddIsoModal = false }) {
                    Text(if (isGe) "გაუქმება" else "Cancel")
                }
            }
        )
    }

    // Persistence Config Dialog
    selectedIsoForPersistence?.let { iso ->
        var mbValue by remember { mutableFloatStateOf(iso.persistenceMb.toFloat()) }
        AlertDialog(
            onDismissRequest = { selectedIsoForPersistence = null },
            containerColor = VentoyDarkSurface,
            title = {
                Text(
                    text = "${if (isGe) "Live Persistence კონფიგურაცია" else "Live OS Persistence"} - ${iso.name}",
                    color = VentoyTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isGe)
                            "Ventoy-ს Live Persistence ინახავს ცვლილებებს, დაყენებულ პროგრამებსა და ფაილებს USB-ზე გადატვირთვის შემდეგაც."
                        else "Persistence retains installed packages and user documents across reboots on the USB drive.",
                        color = VentoyTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "ზომა / Capacity: ${mbValue.toInt()} MB (${mbValue.toInt() / 1024} GB)",
                        color = VentoyCyanBright,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Slider(
                        value = mbValue,
                        onValueChange = { mbValue = it },
                        valueRange = 0f..16384f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = VentoyCyanBright,
                            activeTrackColor = VentoyCyan
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(0 to "Off", 2048 to "2GB", 4096 to "4GB", 8192 to "8GB").forEach { (valMb, label) ->
                            OutlinedButton(
                                onClick = { mbValue = valMb.toFloat() },
                                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                            ) {
                                Text(label, fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePersistence(iso, mbValue.toInt())
                        selectedIsoForPersistence = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan)
                ) {
                    Text(if (isGe) "შენახვა" else "Save", color = Color(0xFF00363F))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedIsoForPersistence = null }) {
                    Text(if (isGe) "გაუქმება" else "Cancel")
                }
            }
        )
    }

    // Auto-install script Dialog
    selectedIsoForAutoInstall?.let { iso ->
        AlertDialog(
            onDismissRequest = { selectedIsoForAutoInstall = null },
            containerColor = VentoyDarkSurface,
            title = {
                Text(
                    text = if (isGe) "ავტომატური ინსტალაციის სკრიპტი" else "Auto-Install Template",
                    color = VentoyTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AutoInstallScript.entries.forEach { script ->
                        val selected = iso.autoInstall == script
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.updateAutoInstall(iso, script)
                                    selectedIsoForAutoInstall = null
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) VentoyCyan.copy(alpha = 0.2f) else VentoyDarkSurfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = VentoyCyanBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = script.label,
                                    color = if (selected) VentoyCyanBright else VentoyTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { selectedIsoForAutoInstall = null }) {
                    Text(if (isGe) "დახურვა" else "Close")
                }
            }
        )
    }

    // Online OS Metadata Dialog (Retrofit API Client)
    onlineMetadataResult?.let { (iso, meta) ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissOnlineMetadataDialog() },
            containerColor = VentoyDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGe) "ონლაინ OS მეტამონაცემები" else "Online OS Metadata",
                        color = VentoyTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = meta.title,
                        color = VentoyTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CyberBadge(text = meta.category, color = VentoyIndigoBright)
                        CyberBadge(text = meta.source, color = VentoyCyanBright)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = meta.description,
                        color = VentoyTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    if (!meta.officialWebsite.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🔗 ${meta.officialWebsite}",
                            color = VentoyCyanBright,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isGe) "გსურთ ამ კატეგორიისა და აღწერის ISO-ზე შენახვა?" else "Auto-populate and apply this category & description to ${iso.filename}?",
                        color = VentoyTextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.applyOnlineMetadata(iso, meta) },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan)
                ) {
                    Text(if (isGe) "მონაცემების შენახვა" else "Apply Metadata", color = Color(0xFF00363F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissOnlineMetadataDialog() }) {
                    Text(if (isGe) "დახურვა" else "Close")
                }
            }
        )
    }

    // Loading indicator when querying Retrofit API
    if (isFetchingOnlineMetadata) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = VentoyDarkSurface,
            confirmButton = {},
            text = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = VentoyCyanBright, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isGe) "Retrofit ეძებს OS მეტამონაცემებს..." else "Fetching OS metadata via Retrofit API...",
                        color = VentoyTextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        )
    }
}

@Composable
fun IsoItemCard(
    iso: IsoItem,
    isGe: Boolean,
    isVerifying: Boolean,
    onSetDefaultBoot: () -> Unit,
    onToggleVault: () -> Unit,
    onTogglePin: () -> Unit,
    onVerifyChecksum: () -> Unit,
    onAuditAi: () -> Unit,
    onAutoPopulateOnline: () -> Unit,
    onConfigurePersistence: () -> Unit,
    onConfigureAutoInstall: () -> Unit,
    onDelete: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (iso.isDefaultBoot) VentoyCyanBright.copy(alpha = 0.8f)
                else if (iso.isEncrypted) VentoyAmber.copy(alpha = 0.6f)
                else VentoyBorder,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Distro Icon + Title + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DistroIconBadge(distro = iso.distroType)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = iso.name,
                            color = VentoyTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (iso.isDefaultBoot) {
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(
                                text = "DEFAULT BOOT",
                                color = VentoyCyanBright,
                                icon = Icons.Default.FlashOn
                            )
                        }
                    }
                    Text(
                        text = "${iso.filename} • ${(iso.sizeBytes / (1024 * 1024 * 1024.0)).let { "%.2f".format(it) }} GB • ${iso.architecture}",
                        color = VentoyTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = VentoyTextSecondary
                        )
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false },
                        modifier = Modifier.background(VentoyDarkSurfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isGe) "⚡ მთავარ ბუთად დაყენება" else "Set as Default Boot") },
                            onClick = {
                                expandedMenu = false
                                onSetDefaultBoot()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (iso.isEncrypted) (if (isGe) "🔓 Vault-იდან ამოღება" else "Remove from Vault")
                                    else (if (isGe) "🔒 AES-256 Vault დაცვა" else "Encrypt with AES-256")
                                )
                            },
                            onClick = {
                                expandedMenu = false
                                onToggleVault()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isGe) "🛡️ AI უსაფრთხოების აუდიტი" else "AI Security & CVE Audit") },
                            onClick = {
                                expandedMenu = false
                                onAuditAi()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isGe) "🌐 ონლაინ OS მეტამონაცემები (Retrofit)" else "🌐 Fetch Online Metadata (Retrofit)") },
                            onClick = {
                                expandedMenu = false
                                onAutoPopulateOnline()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isGe) "⚙️ Live Persistence ზომა" else "Configure Persistence") },
                            onClick = {
                                expandedMenu = false
                                onConfigurePersistence()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isGe) "📜 Auto-install შაბლონი" else "Auto-Install Script") },
                            onClick = {
                                expandedMenu = false
                                onConfigureAutoInstall()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isGe) "🗑️ წაშლა" else "Delete ISO", color = VentoyCrimson) },
                            onClick = {
                                expandedMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges Row (Category, Vault, SHA256, Persistence, Architecture)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CyberBadge(
                    text = iso.categoryName,
                    color = VentoyIndigoBright
                )
                if (iso.isEncrypted) {
                    CyberBadge(
                        text = "AES-256 VAULT",
                        color = VentoyAmber,
                        icon = Icons.Default.Lock
                    )
                }
                if (iso.isVerified) {
                    CyberBadge(
                        text = "SHA-256 OK",
                        color = VentoyEmeraldBright,
                        icon = Icons.Default.CheckCircle
                    )
                }
                if (iso.persistenceMb > 0) {
                    CyberBadge(
                        text = "${iso.persistenceMb / 1024}GB PERSISTENCE",
                        color = VentoyCyanBright
                    )
                }
                if (iso.autoInstall != AutoInstallScript.NONE) {
                    CyberBadge(
                        text = "AUTO-INSTALL",
                        color = VentoyEmeraldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Verify Checksum Button
                OutlinedButton(
                    onClick = onVerifyChecksum,
                    enabled = !isVerifying,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (iso.isVerified) VentoyEmeraldBright else VentoyTextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = VentoyCyanBright,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (isVerifying) "მოწმდება..."
                        else if (iso.isVerified) "დადასტურებულია"
                        else (if (isGe) "ჰეშის შემოწმება" else "Verify SHA256"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // AI Audit Button
                Button(
                    onClick = onAuditAi,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyIndigo),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isGe) "AI აუდიტი" else "AI Audit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
