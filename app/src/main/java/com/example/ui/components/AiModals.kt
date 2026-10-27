package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IsoItem
import com.example.ui.theme.VentoyCyan
import com.example.ui.theme.VentoyCyanBright
import com.example.ui.theme.VentoyDarkSurface
import com.example.ui.theme.VentoyDarkSurfaceVariant
import com.example.ui.theme.VentoyEmeraldBright
import com.example.ui.theme.VentoyIndigo
import com.example.ui.theme.VentoyTextPrimary
import com.example.ui.theme.VentoyTextSecondary

@Composable
fun AuditResultDialog(
    iso: IsoItem?,
    isAuditing: Boolean,
    resultText: String?,
    isGe: Boolean,
    onDismiss: () -> Unit
) {
    if (iso != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = VentoyDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isGe) "AI უსაფრთხოების აუდიტი: ${iso.name}" else "AI Security Audit: ${iso.name}",
                        color = VentoyTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (isAuditing) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = VentoyCyanBright)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isGe) "მოწმდება CVE მონაცემთა ბაზა, SBAT და SecureBoot ხელმოწერა..."
                                else "Auditing CVE database, SBAT revocation, and UEFI signature...",
                                color = VentoyTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Text(
                            text = resultText ?: if (isGe) "მონაცემები არ არის" else "No audit results",
                            color = VentoyTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan)
                ) {
                    Text(if (isGe) "დახურვა" else "Close", color = Color(0xFF00363F))
                }
            }
        )
    }
}

@Composable
fun VentoyJsonDialog(
    jsonContent: String?,
    isGe: Boolean,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    if (jsonContent != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = VentoyDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ventoy.json კონფიგურაცია",
                        color = VentoyTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (isGe)
                            "ეს ფაილი უნდა მოთავსდეს USB დისკის 'ventoy/' საქაღალდეში. ის ავტომატურად გაივლის Windows 11 TPM შემოწმებას და გაააქტიურებს უსაფრთხოების ფუნქციებს."
                        else "Place this file in the 'ventoy/' folder on your USB data partition.",
                        color = VentoyTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VentoyDarkSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = jsonContent,
                            color = VentoyEmeraldBright,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(jsonContent))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VentoyIndigo)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isGe) "კოპირება" else "Copy JSON")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text(if (isGe) "დახურვა" else "Close")
                }
            }
        )
    }
}
