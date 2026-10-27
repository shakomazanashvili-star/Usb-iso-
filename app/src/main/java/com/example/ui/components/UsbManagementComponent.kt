package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eject
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.FileSystemType
import com.example.data.model.PartitionStyle
import com.example.data.model.UsbDrive
import com.example.data.model.UsbDriveStatus
import com.example.data.model.UsbInitializationConfig
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UsbManagementComponent(
    drives: List<UsbDrive>,
    selectedDrive: UsbDrive,
    isGeorgian: Boolean,
    onSelectDrive: (String) -> Unit,
    onRefreshDrives: () -> Unit,
    onEjectDrive: (String) -> Unit,
    onInitializeDrive: (UsbInitializationConfig) -> Unit,
    onUpdateVentoy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showInitDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(VentoyCyanBright, VentoyIndigo))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Usb,
                        contentDescription = "USB Management",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isGeorgian) "USB დისკების მართვა" else "USB Drive Management",
                        color = VentoyTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isGeorgian) "მიერთებული OTG და Flash მეხსიერება" else "Connected OTG & Flash Storage",
                        color = VentoyTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                CyberBadge(
                    text = "${drives.size} " + (if (isGeorgian) "დისკი" else "Drives"),
                    color = VentoyCyanBright
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onRefreshDrives,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_rescan_drives")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rescan USB Drives",
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Drive List (Cards)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            drives.forEach { drive ->
                val isSelected = drive.id == selectedDrive.id
                UsbDriveCard(
                    drive = drive,
                    isSelected = isSelected,
                    isGeorgian = isGeorgian,
                    onClick = { onSelectDrive(drive.id) },
                    onInitializeClick = {
                        onSelectDrive(drive.id)
                        showInitDialog = true
                    },
                    onUpdateClick = {
                        onSelectDrive(drive.id)
                        showUpdateDialog = true
                    },
                    onEjectClick = { onEjectDrive(drive.id) }
                )
            }
        }
    }

    // Initialization Dialog
    if (showInitDialog) {
        UsbInitializationDialog(
            drive = selectedDrive,
            isGeorgian = isGeorgian,
            onDismiss = { showInitDialog = false },
            onConfirm = { config ->
                showInitDialog = false
                onInitializeDrive(config)
            }
        )
    }

    // Non-destructive update confirmation dialog
    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            containerColor = VentoyDarkSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.SystemUpdate,
                    contentDescription = null,
                    tint = VentoyCyanBright,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = if (isGeorgian) "Ventoy+ ბირთვის განახლება" else "Update Ventoy+ Kernel",
                    color = VentoyTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isGeorgian)
                        "განახლება განხორციელდება უსაფრთხოდ (${selectedDrive.name}). არსებული ISO ფაილები, ventoy.json კონფიგურაცია და პერსისტენტული მონაცემები შენარჩუნდება. განვაახლოთ?"
                    else
                        "Non-destructive kernel update on '${selectedDrive.name}'. All existing ISOs, ventoy.json scripts, and persistent storage will be preserved. Proceed?",
                    color = VentoyTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUpdateDialog = false
                        onUpdateVentoy()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCyanBright),
                    modifier = Modifier.testTag("btn_confirm_update_ventoy")
                ) {
                    Text(
                        text = if (isGeorgian) "განახლება" else "Update Kernel",
                        color = Color(0xFF00363F),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text(text = if (isGeorgian) "გაუქმება" else "Cancel", color = VentoyTextMuted)
                }
            }
        )
    }
}

@Composable
fun UsbDriveCard(
    drive: UsbDrive,
    isSelected: Boolean,
    isGeorgian: Boolean,
    onClick: () -> Unit,
    onInitializeClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onEjectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalGb = drive.capacityBytes / (1024 * 1024 * 1024)
    val usedGb = drive.usedBytes / (1024 * 1024 * 1024)
    val freeGb = (totalGb - usedGb).coerceAtLeast(0)
    val usedPercent = if (totalGb > 0) (usedGb.toFloat() / totalGb.toFloat()).coerceIn(0f, 1f) else 0f

    val borderColor = if (isSelected) VentoyCyanBright else VentoyBorder
    val borderWidth = if (isSelected) 1.5.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("card_usb_drive_${drive.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) VentoySurfaceContainer else VentoyDarkSurface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon + Name + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (drive.isMounted) VentoyCyan.copy(alpha = 0.2f)
                                else VentoyCrimson.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Usb,
                            contentDescription = null,
                            tint = if (drive.isMounted) VentoyCyanBright else VentoyCrimson,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = drive.name,
                                color = VentoyTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(VentoyCyanBright),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected Drive",
                                        tint = Color.Black,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${drive.vendor} • ${drive.mountPath}",
                            color = VentoyTextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Status Badge
                val statusColor = when {
                    !drive.isMounted -> VentoyCrimson
                    drive.isWriteProtected -> VentoyAmber
                    drive.isVentoyInstalled -> VentoyEmeraldBright
                    else -> VentoyCyanBright
                }
                val statusText = when {
                    !drive.isMounted -> if (isGeorgian) "გამორთულია" else "Unmounted"
                    drive.isWriteProtected -> if (isGeorgian) "ჩაწერა დაბლოკილია" else "Read-Only"
                    drive.isVentoyInstalled -> "VENTOY+ v${drive.installedVentoyVersion}"
                    else -> if (isGeorgian) "მზადაა ინსტალაციისთვის" else "Ready for Ventoy+"
                }
                CyberBadge(text = statusText, color = statusColor)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Capacity & Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isGeorgian) "მოცულობა: $usedGb GB / $totalGb GB" else "Capacity: $usedGb GB / $totalGb GB",
                    color = VentoyTextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = if (isGeorgian) "თავისუფალი: $freeGb GB" else "Free: $freeGb GB",
                    color = VentoyCyanBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { usedPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (usedPercent > 0.9f) VentoyCrimson else VentoyCyanBright,
                trackColor = VentoyDarkSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Technical Specs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberBadge(
                    text = drive.fsType.name,
                    color = VentoyIndigoBright
                )
                CyberBadge(
                    text = drive.partitionStyle.name,
                    color = VentoyEmeraldBright
                )
                CyberBadge(
                    text = "${drive.clusterAlignmentKb}KB Align",
                    color = VentoyAmber
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = drive.speedRating,
                    color = VentoyTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Interactive Actions if selected
            AnimatedVisibility(visible = isSelected) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(VentoyBorder)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Initialize / Install Button
                        Button(
                            onClick = onInitializeClick,
                            modifier = Modifier
                                .weight(1.3f)
                                .heightIn(min = 44.dp)
                                .testTag("btn_initialize_drive_${drive.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = VentoyCyanBright),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color(0xFF00363F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (drive.isVentoyInstalled)
                                    (if (isGeorgian) "ხელახალი ფორმატირება" else "Re-Initialize")
                                else
                                    (if (isGeorgian) "Ventoy+ ინსტალაცია" else "Initialize Ventoy+"),
                                color = Color(0xFF00363F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Non-destructive update button if already installed
                        if (drive.isVentoyInstalled) {
                            FilledTonalButton(
                                onClick = onUpdateClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 44.dp)
                                    .testTag("btn_update_kernel_${drive.id}"),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = VentoyIndigo.copy(alpha = 0.35f),
                                    contentColor = VentoyCyanBright
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isGeorgian) "განახლება" else "Update",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Eject Button
                        OutlinedButton(
                            onClick = onEjectClick,
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("btn_eject_drive_${drive.id}"),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(VentoyBorder, VentoyBorder))
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eject,
                                contentDescription = "Eject Drive",
                                tint = if (drive.isMounted) VentoyTextSecondary else VentoyTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UsbInitializationDialog(
    drive: UsbDrive,
    isGeorgian: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (UsbInitializationConfig) -> Unit
) {
    var selectedStyle by remember { mutableStateOf(drive.partitionStyle) }
    var selectedFs by remember { mutableStateOf(drive.fsType) }
    var selectedClusterKb by remember { mutableIntStateOf(drive.clusterAlignmentKb) }
    var enableSecureBoot by remember { mutableStateOf(drive.secureBootEnrolled) }
    var reservedSpaceMb by remember { mutableIntStateOf(drive.reservedSpaceMb) }
    var confirmWipeData by remember { mutableStateOf(false) }

    val totalGb = drive.capacityBytes / (1024 * 1024 * 1024)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_usb_initialization"),
        containerColor = VentoyDarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VentoyCyan.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isGeorgian) "დისკის ინიციალიზაცია" else "Initialize Ventoy+ Media",
                        color = VentoyTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${drive.name} ($totalGb GB)",
                        color = VentoyCyanBright,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Partition Scheme
                Text(
                    text = if (isGeorgian) "დანაყოფის სტილი (Partition Style)" else "Partition Style",
                    color = VentoyTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PartitionStyle.entries.forEach { style ->
                        val selected = selectedStyle == style
                        FilledTonalButton(
                            onClick = { selectedStyle = style },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 40.dp)
                                .testTag("btn_select_style_${style.name}"),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (selected) VentoyCyan.copy(alpha = 0.25f) else VentoyDarkSurfaceVariant,
                                contentColor = if (selected) VentoyCyanBright else VentoyTextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = style.name,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Text(
                    text = if (selectedStyle == PartitionStyle.GPT)
                        (if (isGeorgian) "GPT: რეკომენდებულია თანამედროვე UEFI კომპიუტერებისთვის და SecureBoot-ისთვის."
                        else "GPT: Recommended for modern UEFI, SecureBoot, and >2TB drives.")
                    else
                        (if (isGeorgian) "MBR: მაქსიმალური თავსებადობა ძველ Legacy BIOS კომპიუტერებთან."
                        else "MBR: Legacy BIOS & CSM compatibility mode."),
                    color = VentoyTextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Filesystem Selection
                Text(
                    text = if (isGeorgian) "ფაილური სისტემა (ISO Data)" else "File System (ISO Partition)",
                    color = VentoyTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FileSystemType.entries.forEach { fs ->
                        val selected = selectedFs == fs
                        FilterChip(
                            selected = selected,
                            onClick = { selectedFs = fs },
                            label = { Text(text = fs.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VentoyCyanBright,
                                selectedLabelColor = Color(0xFF00363F),
                                containerColor = VentoyDarkSurfaceVariant,
                                labelColor = VentoyTextSecondary
                            ),
                            modifier = Modifier.testTag("chip_fs_${fs.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cluster Alignment
                Text(
                    text = if (isGeorgian) "კლასტერის სწორება (Cluster Alignment)" else "Cluster Alignment",
                    color = VentoyTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(4, 128, 1024, 2048).forEach { kb ->
                        val selected = selectedClusterKb == kb
                        FilledTonalButton(
                            onClick = { selectedClusterKb = kb },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 36.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (selected) VentoyIndigo.copy(alpha = 0.5f) else VentoyDarkSurfaceVariant,
                                contentColor = if (selected) VentoyIndigoBright else VentoyTextSecondary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (kb >= 1024) "${kb / 1024}MB" else "${kb}KB",
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SecureBoot MOK Support Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VentoyDarkSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isGeorgian) "UEFI SecureBoot MOK მხარდაჭერა" else "UEFI SecureBoot Support",
                            color = VentoyTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isGeorgian) "ჩატვირთვის ციფრული გასაღების ჩაწერა" else "Injects Ventoy+ Machine Owner Key",
                            color = VentoyTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = enableSecureBoot,
                        onCheckedChange = { enableSecureBoot = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VentoyCyanBright,
                            checkedTrackColor = VentoyCyan.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("switch_secure_boot")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Destructive Erase Warning Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VentoyCrimson.copy(alpha = 0.12f))
                        .border(1.dp, VentoyCrimson.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = VentoyCrimson,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isGeorgian) "გაფრთხილება: მონაცემების წაშლა!" else "WARNING: ALL DATA WILL BE ERASED!",
                                color = VentoyCrimson,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isGeorgian)
                                "დისკი '${drive.name}' მთლიანად გასუფთავდება. შეიქმნება 2 დანაყოფი (ISO მონაცემები + VTOYEFI ჩამტვირთავი). დარწმუნდით, რომ საჭირო ფაილები დარეზერვებულია."
                            else
                                "Formatting will delete all files on '${drive.name}'. Two partitions will be created (Data + VTOYEFI bootloader). Ensure you have backed up important files.",
                            color = VentoyTextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { confirmWipeData = !confirmWipeData }
                        ) {
                            Checkbox(
                                checked = confirmWipeData,
                                onCheckedChange = { confirmWipeData = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = VentoyCrimson,
                                    uncheckedColor = VentoyTextMuted
                                ),
                                modifier = Modifier.testTag("checkbox_confirm_format")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isGeorgian) "ვადასტურებ დისკის ფორმატირებას" else "I confirm and accept formatting this drive",
                                color = VentoyTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        UsbInitializationConfig(
                            driveId = drive.id,
                            partitionStyle = selectedStyle,
                            fileSystem = selectedFs,
                            clusterAlignmentKb = selectedClusterKb,
                            secureBoot = enableSecureBoot,
                            reservedSpaceMb = reservedSpaceMb
                        )
                    )
                },
                enabled = confirmWipeData,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VentoyCyanBright,
                    disabledContainerColor = VentoyDarkSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_start_initialization")
            ) {
                Text(
                    text = if (isGeorgian) "ინიციალიზაციის დაწყება" else "Initialize Media",
                    color = if (confirmWipeData) Color(0xFF00363F) else VentoyTextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isGeorgian) "გაუქმება" else "Cancel",
                    color = VentoyTextMuted
                )
            }
        }
    )
}
