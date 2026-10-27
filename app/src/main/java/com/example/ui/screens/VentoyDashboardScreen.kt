package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FileSystemType
import com.example.data.model.PartitionStyle
import com.example.ui.components.CyberBadge
import com.example.ui.components.LiveBootPreviewComponent
import com.example.ui.components.UsbManagementComponent
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
fun VentoyDashboardScreen(
    viewModel: VentoyViewModel,
    modifier: Modifier = Modifier
) {
    val usbDrive by viewModel.usbDrive.collectAsState()
    val connectedDrives by viewModel.connectedDrives.collectAsState()
    val securityStatus by viewModel.securityStatus.collectAsState()
    val isos by viewModel.isos.collectAsState()
    val isGe by viewModel.isGeorgian.collectAsState()

    var showInstallConfirmDialog by remember { mutableStateOf(false) }
    var showUpdateConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VentoyDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(VentoyCyanBright, VentoyIndigo)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "V+",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ventoy+",
                                color = VentoyTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isGe) "მულტიბუთ USB და უსაფრთხოების ცენტრი" else "Next-Gen Multiboot & Security Suite",
                                color = VentoyCyanBright,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("btn_lang_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VentoyDarkSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = VentoyCyanBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isGe) "GE" else "EN",
                                color = VentoyTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VentoyDarkSurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Visual Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, VentoyCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ventoy_banner),
                    contentDescription = "Ventoy Hero Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient Overlay for legible text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xEE090D16),
                                    Color(0x99090D16),
                                    Color(0x33090D16)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CyberBadge(
                            text = "VENTOY+ SECURE v1.0.99",
                            color = VentoyCyanBright,
                            icon = Icons.Default.Shield
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isGe) "უნივერსალური მულტიბუთი" else "Universal Multiboot Engine",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (isGe) "ჩაწერე ISO-ები და ჩატვირთე პირდაპირ!" else "Just copy ISO files & boot directly with SecureBoot!",
                            color = VentoyTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connected USB Drive Management Component
            UsbManagementComponent(
                drives = connectedDrives,
                selectedDrive = usbDrive,
                isGeorgian = isGe,
                onSelectDrive = { driveId -> viewModel.selectDrive(driveId) },
                onRefreshDrives = { viewModel.refreshConnectedDrives() },
                onEjectDrive = { driveId -> viewModel.ejectDrive(driveId) },
                onInitializeDrive = { config -> viewModel.initializeDrive(config) },
                onUpdateVentoy = { viewModel.updateVentoy() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live Boot Preview Component (GRUB-style Boot Menu on USB)
            LiveBootPreviewComponent(
                isos = isos,
                isGeorgian = isGe,
                usbDrive = usbDrive,
                onBootIso = { iso ->
                    viewModel.simulateBoot(iso)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Configuration Options Section
            Text(
                text = if (isGe) "პარამეტრები და უსაფრთხოება" else "Configuration & Boot Security",
                color = VentoyTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Partition Style (GPT vs MBR)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VentoyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isGe) "დანაყოფის სტილი (Partition Style)" else "Partition Style",
                        color = VentoyTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PartitionStyle.entries.forEach { style ->
                            val selected = usbDrive.partitionStyle == style
                            FilledTonalButton(
                                onClick = { viewModel.setPartitionStyle(style) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_style_${style.name}"),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (selected) VentoyCyan.copy(alpha = 0.25f) else VentoyDarkSurfaceVariant,
                                    contentColor = if (selected) VentoyCyanBright else VentoyTextSecondary
                                )
                            ) {
                                Text(
                                    text = style.name,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Text(
                        text = if (usbDrive.partitionStyle == PartitionStyle.GPT)
                            (if (isGe) "GPT რეკომენდებულია თანამედროვე UEFI კომპიუტერებისთვის და SecureBoot-ისთვის."
                            else "GPT is recommended for modern UEFI PCs, SecureBoot, and >2TB drives.")
                        else (if (isGe) "MBR განკუთვნილია ძველი Legacy BIOS და CSM სისტემებისთვის."
                        else "MBR is suitable for older Legacy BIOS and CSM systems."),
                        color = VentoyTextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filesystem Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VentoyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isGe) "მონაცემთა ფაილური სისტემა" else "Data Partition Filesystem",
                        color = VentoyTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(FileSystemType.EXFAT, FileSystemType.NTFS, FileSystemType.FAT32).forEach { fs ->
                            val selected = usbDrive.fsType == fs
                            FilledTonalButton(
                                onClick = { viewModel.setFileSystem(fs) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_fs_${fs.name}"),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (selected) VentoyIndigo.copy(alpha = 0.3f) else VentoyDarkSurfaceVariant,
                                    contentColor = if (selected) VentoyIndigoBright else VentoyTextSecondary
                                )
                            ) {
                                Text(
                                    text = fs.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Security Toggles (Secure Boot & Hardware Write Protection)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VentoyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // SecureBoot switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = VentoyCyanBright,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isGe) "UEFI Secure Boot მხარდაჭერა" else "UEFI Secure Boot Support",
                                    color = VentoyTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isGe) "MokManager გასაღების ავტომატური ინტეგრაცია" else "Ventoy MokManager key enrollment",
                                    color = VentoyTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = usbDrive.secureBootEnrolled,
                            onCheckedChange = { viewModel.toggleSecureBoot() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VentoyCyanBright,
                                checkedTrackColor = VentoyCyan.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("switch_secure_boot")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Write protection switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (usbDrive.isWriteProtected) VentoyAmber else VentoyEmeraldBright,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isGe) "USB ჩაწერის დაცვა (Anti-Ransomware)" else "Hardware Write-Protection Lock",
                                    color = VentoyTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (usbDrive.isWriteProtected)
                                        (if (isGe) "აქტიურია: მხოლოდ კითხვა (Read-Only)" else "Active: Read-Only mode (Protected)")
                                    else (if (isGe) "გამორთულია: ჩაწერა დაშვებულია" else "Disabled: Read & Write enabled"),
                                    color = if (usbDrive.isWriteProtected) VentoyAmber else VentoyTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = usbDrive.isWriteProtected,
                            onCheckedChange = { viewModel.toggleWriteProtection() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VentoyAmber,
                                checkedTrackColor = VentoyAmber.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("switch_write_protect")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Install & Non-destructive Update)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showInstallConfirmDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("btn_install_ventoy"),
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = Color(0xFF00363F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGe) "ინსტალაცია" else "Install",
                        color = Color(0xFF00363F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = { showUpdateConfirmDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("btn_update_ventoy"),
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGe) "განახლება" else "Update (Safe)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isGe) "ℹ️ 'განახლება' მხოლოდ აახლებს Ventoy-ს ბირთვს და არ შლის შენს ISO ფაილებს."
                else "ℹ️ 'Update' safely updates Ventoy EFI boot files without deleting your ISO images.",
                color = VentoyTextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }

    // Install Confirmation Dialog
    if (showInstallConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showInstallConfirmDialog = false },
            containerColor = VentoyDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = VentoyCrimson,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isGe) "დისკის ფორმატირება" else "Clean Format Warning",
                        color = VentoyTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = if (isGe)
                        "ყურადღება! Ventoy+-ის სუფთა ინსტალაციისას '${usbDrive.name}'-ზე არსებული ყველა მონაცემი წაიშლება და შეიქმნება 2 დანაყოფი (${usbDrive.partitionStyle.name}, ${usbDrive.fsType.name} + VTOYEFI). დარწმუნებული ხარ?"
                    else "Warning: Fresh installation will format '${usbDrive.name}' and create 2 partitions (${usbDrive.partitionStyle.name}, ${usbDrive.fsType.name} + VTOYEFI). Proceed?",
                    color = VentoyTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInstallConfirmDialog = false
                        viewModel.installVentoy(usbDrive.partitionStyle, usbDrive.fsType, usbDrive.secureBootEnrolled)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCrimson),
                    modifier = Modifier.testTag("btn_confirm_install")
                ) {
                    Text(if (isGe) "დიახ, დააფორმატე" else "Format & Install")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showInstallConfirmDialog = false }) {
                    Text(if (isGe) "გაუქმება" else "Cancel")
                }
            }
        )
    }

    // Update Confirmation Dialog
    if (showUpdateConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateConfirmDialog = false },
            containerColor = VentoyDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isGe) "უსაფრთხო განახლება" else "Non-destructive Update",
                        color = VentoyTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = if (isGe)
                        "Ventoy+ ბირთვი განახლდება 1.0.99-SECURE ვერსიამდე. არსებული ISO ფაილები და კონფიგურაცია ხელუხლებელი დარჩება."
                    else "Ventoy core will be safely upgraded to 1.0.99-SECURE. All your existing ISO files will be preserved.",
                    color = VentoyTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUpdateConfirmDialog = false
                        viewModel.updateVentoy()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyIndigo),
                    modifier = Modifier.testTag("btn_confirm_update")
                ) {
                    Text(if (isGe) "განახლება" else "Update Now")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showUpdateConfirmDialog = false }) {
                    Text(if (isGe) "გაუქმება" else "Cancel")
                }
            }
        )
    }
}
