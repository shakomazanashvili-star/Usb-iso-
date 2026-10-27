package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberBadge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityVaultScreen(
    viewModel: VentoyViewModel,
    modifier: Modifier = Modifier
) {
    val securityStatus by viewModel.securityStatus.collectAsState()
    val usbDrive by viewModel.usbDrive.collectAsState()
    val logs by viewModel.securityLogs.collectAsState()
    val isGe by viewModel.isGeorgian.collectAsState()

    var showMokDialog by remember { mutableStateOf(false) }
    var showPanicWipeDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VentoyDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = VentoyCyanBright,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isGe) "უსაფრთხოების ცენტრი & Vault" else "Security Center & Vault",
                                color = VentoyTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "UEFI SecureBoot • AES-256 • Anti-Tamper",
                                color = VentoyCyanBright,
                                fontSize = 11.sp
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
                .padding(16.dp)
        ) {
            // Security Posture Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF003844),
                                Color(0xFF0F1E38),
                                VentoyDarkSurface
                            )
                        )
                    )
                    .border(1.5.dp, VentoyCyanBright.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        CyberBadge(
                            text = "SECURITY STATUS: HARDENED",
                            color = VentoyEmeraldBright,
                            icon = Icons.Default.VerifiedUser
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isGe) "მულტიბუთის დაცვის დონე: 98%" else "Security Hardening: 98%",
                            color = VentoyTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (isGe)
                                "აქტიურია UEFI MOK გასაღები, AES-256 Vault და SBAT ანტი-ბუთკიტ დაცვა."
                            else "UEFI MOK active, AES-256 Vault enabled, and SBAT bootkit mitigation active.",
                            color = VentoyTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(VentoyEmerald.copy(alpha = 0.2f))
                            .border(2.dp, VentoyEmeraldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = VentoyEmeraldBright,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // UEFI Secure Boot & MOK Module
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VentoyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = VentoyCyanBright,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isGe) "UEFI Secure Boot & MOK გასაღები" else "UEFI Secure Boot & MOK Key",
                                color = VentoyTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CyberBadge(
                            text = if (securityStatus.mokKeyEnrolled) "ENROLLED" else "UNENROLLED",
                            color = if (securityStatus.mokKeyEnrolled) VentoyEmeraldBright else VentoyAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isGe)
                            "Ventoy იყენებს X.509 სერტიფიკატს, რათა ჩაიტვირთოს კომპიუტერებზე ჩართული Secure Boot-ით BIOS-ში მისი გამორთვის გარეშე."
                        else "Ventoy uses an enrolled X.509 MOK certificate so you can boot on Secure Boot systems without disabling it in BIOS.",
                        color = VentoyTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VentoyDarkSurfaceVariant)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "MOK SHA-1 FINGERPRINT:",
                                color = VentoyTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = securityStatus.mokFingerprint,
                                color = VentoyCyanBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showMokDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VentoyCyanBright),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isGe) "როგორ დავადასტურო MOK ბუთისას?" else "How to Enroll MOK in UEFI?")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AES-256 Boot Vault & Anti-Ransomware
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VentoyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = VentoyIndigoBright,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isGe) "AES-256 ჩატვირთვის Vault" else "AES-256 Boot Vault",
                                color = VentoyTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CyberBadge(
                            text = "MIL-SPEC",
                            color = VentoyIndigoBright
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isGe)
                            "დაშიფრული ISO ფაილები მოითხოვს PIN კოდს ან პაროლს კომპიუტერის ჩატვირთვისას, რაც იცავს სენსიტიურ სისტემებს (Tails, Kali, Windows To Go)."
                        else "Encrypted ISO files require a pre-boot PIN/password when booting the PC, safeguarding private OS images.",
                        color = VentoyTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isGe) "USB აპარატურული ჩაწერის ბლოკი" else "Hardware Write-Protection Switch",
                                color = VentoyTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isGe) "იცავს ვირუსებისგან უცხო PC-ზე მიერთებისას" else "Prevents malware infection from guest PC",
                                color = VentoyTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = usbDrive.isWriteProtected,
                            onCheckedChange = { viewModel.toggleWriteProtection() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VentoyAmber,
                                checkedTrackColor = VentoyAmber.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Panic Wipe Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VentoyCrimson.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VentoyCrimson.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = VentoyCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isGe) "საგანგებო განადგურება (Panic Erase)" else "Emergency Zero-Wipe (Panic)",
                            color = VentoyCrimson,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isGe)
                            "კრიპტოგრაფიული ნულოვანი წაშლა (Zero-Fill): წაშლის დანაყოფების ცხრილს, MOK გასაღებებსა და ყველა ISO ფაილს შეუქცევადად."
                        else "Military Zero-Fill sanitization: permanently wipes partition headers, MOK keys, and all ISO payloads.",
                        color = VentoyTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showPanicWipeDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_panic_wipe"),
                        colors = ButtonDefaults.buttonColors(containerColor = VentoyCrimson),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isGe) "დისკის საგანგებო გასუფთავება" else "Initiate Emergency Zero-Wipe",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Real-time Security Event Audit Log
            Text(
                text = if (isGe) "უსაფრთხოების ჟურნალი (Audit Logs)" else "Security Event Audit Logs",
                color = VentoyTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VentoyDarkSurface)
                    .border(1.dp, VentoyBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (logs.isEmpty()) {
                    Text(
                        text = if (isGe) "ჟურნალი ცარიელია" else "No security events recorded.",
                        color = VentoyTextMuted,
                        fontSize = 12.sp
                    )
                } else {
                    logs.take(15).forEach { log ->
                        val (tintColor, icon) = when (log.severity) {
                            "SECURE" -> VentoyEmeraldBright to Icons.Default.CheckCircle
                            "WARNING" -> VentoyAmber to Icons.Default.Warning
                            "CRITICAL" -> VentoyCrimson to Icons.Default.Warning
                            else -> VentoyCyanBright to Icons.Default.Info
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tintColor,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.eventType,
                                        color = tintColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = dateFormat.format(Date(log.timestamp)),
                                        color = VentoyTextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = log.description,
                                    color = VentoyTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // MOK Enrollment Guide Dialog
    if (showMokDialog) {
        AlertDialog(
            onDismissRequest = { showMokDialog = false },
            containerColor = VentoyDarkSurface,
            title = {
                Text(
                    text = if (isGe) "MOK გასაღების რეგისტრაცია (MokManager)" else "UEFI MokManager Instructions",
                    color = VentoyTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isGe)
                            """
                            პირველი ჩატვირთვისას, როდესაც კომპიუტერზე Secure Boot ჩართულია, გამოჩნდება ლურჯი ეკრანი 'Verification failed: (0x1A) Security Violation':
                            
                            1. დააჭირე [OK] ღილაკს.
                            2. აირჩიე 'Enroll MOK'
                            3. აირჩიე 'Continue'
                            4. აირჩიე 'Yes'
                            5. როდესაც მოგთხოვს პაროლს, შეიყვანე:
                               ventoy
                            6. აირჩიე 'Reboot'.
                            
                            ამის შემდეგ Ventoy+ სამუდამოდ ჩაიტვირთება Secure Boot-ის გათიშვის გარეშე!
                            """.trimIndent()
                        else
                            """
                            When booting on a Secure Boot PC for the first time, you may see 'Verification failed: (0x1A) Security Violation':
                            
                            1. Press [OK] to launch MokManager.
                            2. Select 'Enroll MOK'.
                            3. Select 'Continue'.
                            4. Select 'Yes' to confirm enrollment.
                            5. When prompted for password, enter:
                               ventoy
                            6. Select 'Reboot'.
                            
                            Ventoy+ will now boot seamlessly with Secure Boot enabled!
                            """.trimIndent(),
                        color = VentoyTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMokDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan)
                ) {
                    Text("გასაგებია / Understood", color = Color(0xFF00363F))
                }
            }
        )
    }

    // Panic Wipe Confirmation Dialog
    if (showPanicWipeDialog) {
        AlertDialog(
            onDismissRequest = { showPanicWipeDialog = false },
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
                        text = if (isGe) "ნამდვილად გსურს დისკის განადგურება?" else "Confirm Panic Zero-Wipe",
                        color = VentoyCrimson,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = if (isGe)
                        "ყურადღება! '${usbDrive.name}'-ზე ყველა ფაილი, ISO და სერტიფიკატი ნულებით გადაიწერება. მონაცემების აღდგენა შეუძლებელი იქნება!"
                    else "All partitions, ISO images, and cryptographic keys will be overwritten with zeros. This action is irreversible!",
                    color = VentoyTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPanicWipeDialog = false
                        viewModel.emergencyWipe()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCrimson),
                    modifier = Modifier.testTag("btn_confirm_panic_wipe")
                ) {
                    Text(if (isGe) "გაანადგურე ახლავე" else "Wipe Clean")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPanicWipeDialog = false }) {
                    Text(if (isGe) "გაუქმება" else "Cancel")
                }
            }
        )
    }
}
