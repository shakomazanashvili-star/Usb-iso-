package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.OsMetadataSearchBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OsCatalogItem
import com.example.ui.components.CyberBadge
import com.example.ui.components.DistroIconBadge
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
import com.example.ui.viewmodel.VentoyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsCatalogScreen(
    viewModel: VentoyViewModel,
    modifier: Modifier = Modifier
) {
    val catalog = viewModel.catalogList
    val isos by viewModel.isos.collectAsState()
    val isGe by viewModel.isGeorgian.collectAsState()
    val searchQuery by viewModel.liveSearchQuery.collectAsState()
    val isSearching by viewModel.isSearchingOs.collectAsState()
    val searchResult by viewModel.liveSearchResult.collectAsState()
    var showRetrofitMetadataBar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VentoyDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = VentoyCyanBright,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isGe) "ონლაინ OS კატალოგი" else "Live OS Catalog & Search",
                                color = VentoyTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isGe) "Google Search & ოფიციალური SHA-256 ჰეშები" else "Google Search Data & Official Hashes",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Search Box with Google Search Grounding
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setLiveSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = if (isGe) "მოიძიე ნებისმიერი OS (e.g. Debian 12, SteamOS)..." else "Search any OS with Google Search data...",
                            color = VentoyTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    trailingIcon = {
                        Button(
                            onClick = { viewModel.searchOsLive(searchQuery) },
                            enabled = !isSearching && searchQuery.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = VentoyCyan),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .height(38.dp)
                                .testTag("btn_search_grounding")
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF00363F),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF00363F),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isGe) "ძიება" else "Search",
                                        color = Color(0xFF00363F),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Suggestion Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val presets = listOf("Ubuntu 24.04", "Windows 11 24H2", "Kali Linux", "Arch Linux", "Tails OS", "Proxmox")
                    items(presets) { preset ->
                        SuggestionChip(
                            onClick = {
                                viewModel.setLiveSearchQuery(preset)
                                viewModel.searchOsLive(preset)
                            },
                            label = { Text(preset, fontSize = 11.sp, color = VentoyTextSecondary) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = VentoyDarkSurfaceVariant
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = VentoyBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle for Retrofit OS Metadata Service
                FilterChip(
                    selected = showRetrofitMetadataBar,
                    onClick = { showRetrofitMetadataBar = !showRetrofitMetadataBar },
                    label = {
                        Text(
                            text = if (isGe) "🌐 Retrofit OS მეტამონაცემების სერვისი & ტეგირება" else "🌐 Retrofit OS Metadata Service & Tagging",
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
                    modifier = Modifier.testTag("btn_toggle_catalog_retrofit_search")
                )

                AnimatedVisibility(visible = showRetrofitMetadataBar) {
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        OsMetadataSearchBar(
                            isGeorgian = isGe,
                            isoItems = isos,
                            onTagIsoItem = { iso, metadata ->
                                viewModel.applyOnlineMetadata(iso, metadata)
                            }
                        )
                    }
                }
            }

            // Real-time AI Search Result Card
            if (isSearching || searchResult != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, VentoyCyanBright.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = VentoyDarkSurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = VentoyCyanBright,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isGe) "Google Search მონაცემები" else "Google Search Grounded Result",
                                        color = VentoyCyanBright,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                CyberBadge(text = "LIVE DATA", color = VentoyCyanBright)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isSearching) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = VentoyCyanBright,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isGe) "მოიძიება უახლესი ვერსიები და SHA256 ჰეშები..." else "Fetching latest release data and official checksums...",
                                        color = VentoyTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = searchResult.orEmpty(),
                                    color = VentoyTextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isGe) "ვერიფიცირებული დისტრიბუციები" else "Verified Official Distributions",
                        color = VentoyTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CyberBadge(text = "OFFICIAL HASHES", color = VentoyEmeraldBright)
                }
            }

            // Official Catalog Items
            items(catalog, key = { it.id }) { item ->
                OsCatalogCard(
                    item = item,
                    isGe = isGe,
                    onOpenUrl = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.downloadUrl))
                        context.startActivity(intent)
                    },
                    onCopyHash = {
                        clipboardManager.setText(AnnotatedString(item.officialSha256))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun OsCatalogCard(
    item: OsCatalogItem,
    isGe: Boolean,
    onOpenUrl: () -> Unit,
    onCopyHash: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, VentoyBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DistroIconBadge(distro = item.distro)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        color = VentoyTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.latestVersion} • ${item.estimatedSize} • ${item.releaseDate}",
                        color = VentoyTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (item.secureBootCertified) {
                    CyberBadge(
                        text = "SECURE BOOT",
                        color = VentoyEmeraldBright,
                        icon = Icons.Default.Shield
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                color = VentoyTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Checksum row with copy
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(VentoyDarkSurfaceVariant)
                    .clickable { onCopyHash() }
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OFFICIAL SHA-256 (დააჭირე კოპირებისთვის):",
                            color = VentoyTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = item.officialSha256,
                            color = VentoyCyanBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Hash",
                        tint = VentoyCyanBright,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Download Mirror button
            Button(
                onClick = onOpenUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VentoyIndigo),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isGe) "ოფიციალური ჩამოტვირთვა / Mirror" else "Open Official Download Mirror",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
