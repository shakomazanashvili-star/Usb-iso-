package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IsoFile
import com.example.data.model.DistroType
import com.example.data.model.IsoItem
import com.example.data.network.OsMetadataApiClient
import com.example.data.network.model.OsMetadataResult
import com.example.ui.theme.VentoyAmber
import com.example.ui.theme.VentoyBorder
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
import kotlinx.coroutines.launch

/**
 * Interactive OS Metadata Search Bar Component
 *
 * Integrates with the Retrofit OS metadata service (`OsMetadataApiClient` / `WikipediaApiService`
 * / `GoogleSearchApiService`) allowing users to look up real-time web-sourced OS metadata
 * (official title, category, description, recommended boot mode, source website) and
 * immediately tag/update stored ISO files in the database with one click.
 */
@Composable
fun OsMetadataSearchBar(
    modifier: Modifier = Modifier,
    isGeorgian: Boolean = false,
    isoFiles: List<IsoFile> = emptyList(),
    isoItems: List<IsoItem> = emptyList(),
    preselectedIsoId: String? = null,
    apiClient: OsMetadataApiClient = remember { OsMetadataApiClient.getInstance() },
    onTagIsoFile: ((IsoFile, OsMetadataResult) -> Unit)? = null,
    onTagIsoItem: ((IsoItem, OsMetadataResult) -> Unit)? = null,
    onSearchCompleted: ((OsMetadataResult) -> Unit)? = null
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResult by remember { mutableStateOf<OsMetadataResult?>(null) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var recentlyTaggedNotice by remember { mutableStateOf<String?>(null) }

    // Dropdown target selection
    var selectedIsoItemId by remember(isoItems, preselectedIsoId) {
        mutableStateOf(preselectedIsoId ?: isoItems.firstOrNull()?.id)
    }
    var selectedIsoFileId by remember(isoFiles) {
        mutableStateOf(isoFiles.firstOrNull()?.id)
    }
    var isIsoDropdownExpanded by remember { mutableStateOf(false) }

    // Quick suggestion distros for one-tap query
    val quickSuggestions = remember {
        listOf(
            "Ubuntu 24.04",
            "Windows 11",
            "Kali Linux",
            "Arch Linux",
            "Debian",
            "Fedora",
            "Proxmox VE",
            "Clonezilla",
            "Tails OS",
            "SystemRescue"
        )
    }

    fun executeSearch(query: String) {
        val clean = query.trim()
        if (clean.isBlank()) return
        focusManager.clearFocus()
        isSearching = true
        searchError = null
        recentlyTaggedNotice = null

        coroutineScope.launch {
            try {
                val result = apiClient.searchOsMetadata(clean).getOrNull()
                if (result != null) {
                    searchResult = result
                    onSearchCompleted?.invoke(result)
                } else {
                    searchError = if (isGeorgian) "მონაცემები ვერ მოიძებნა" else "No OS metadata found for '$clean'"
                }
            } catch (e: Exception) {
                searchError = e.message ?: if (isGeorgian) "ძებნის შეცდომა" else "Search error occurred"
            } finally {
                isSearching = false
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, VentoyCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .animateContentSize()
            .testTag("os_metadata_search_component"),
        colors = CardDefaults.cardColors(containerColor = VentoyDarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Component identity & Online Tagging label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VentoyCyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Online OS Search",
                            tint = VentoyCyanBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isGeorgian) "ონლაინ OS მეტამონაცემების ძებნა" else "Live OS Metadata & Tagging",
                            color = VentoyTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isGeorgian) "Wikipedia REST & Google OS რეალურ დროში" else "Wikipedia REST & Real-time Distro Tags",
                            color = VentoyCyanBright,
                            fontSize = 10.sp
                        )
                    }
                }

                CyberBadge(
                    text = "RETROFIT SERVICE",
                    color = VentoyCyanBright
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (searchError != null) searchError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("os_metadata_search_bar"),
                placeholder = {
                    Text(
                        text = if (isGeorgian) "შეიყვანე დისტრიბუტივის ან ISO-ს სახელი..." else "Enter OS name or distro to lookup & tag...",
                        color = VentoyTextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search OS",
                        tint = VentoyCyanBright
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                                    .testTag("search_loading_indicator"),
                                color = VentoyCyanBright,
                                strokeWidth = 2.dp
                            )
                        } else if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    searchResult = null
                                    searchError = null
                                },
                                modifier = Modifier.testTag("btn_clear_search")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = VentoyTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Search Action Button
                        IconButton(
                            onClick = { executeSearch(searchQuery) },
                            modifier = Modifier.testTag("btn_search_metadata")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Lookup Metadata",
                                tint = VentoyCyanBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { executeSearch(searchQuery) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VentoyCyanBright,
                    unfocusedBorderColor = VentoyBorder,
                    focusedContainerColor = VentoyDarkSurfaceVariant,
                    unfocusedContainerColor = VentoyDarkSurfaceVariant,
                    focusedTextColor = VentoyTextPrimary,
                    unfocusedTextColor = VentoyTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick suggestion chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickSuggestions.forEach { suggestion ->
                    SuggestionChip(
                        onClick = {
                            searchQuery = suggestion
                            executeSearch(suggestion)
                        },
                        label = {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                color = if (searchQuery.equals(suggestion, ignoreCase = true)) VentoyCyanBright else VentoyTextSecondary
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (searchQuery.equals(suggestion, ignoreCase = true))
                                VentoyCyan.copy(alpha = 0.25f) else VentoyDarkSurfaceVariant
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = if (searchQuery.equals(suggestion, ignoreCase = true))
                                VentoyCyanBright else VentoyBorder.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("suggestion_chip_$suggestion")
                    )
                }
            }

            // Success feedback notification
            AnimatedVisibility(
                visible = recentlyTaggedNotice != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                recentlyTaggedNotice?.let { msg ->
                    Surface(
                        color = VentoyEmerald.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .border(1.dp, VentoyEmeraldBright.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .testTag("tagging_success_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = VentoyEmeraldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = msg,
                                color = VentoyEmeraldBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Error display
            if (searchError != null) {
                Text(
                    text = searchError ?: "",
                    color = VentoyAmber,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // Search Result and Tagging Card
            searchResult?.let { result ->
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, VentoyIndigoBright.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .testTag("card_search_result"),
                    colors = CardDefaults.cardColors(containerColor = VentoyDarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Title and Source
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = result.title,
                                    color = VentoyTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${if (isGeorgian) "წყარო:" else "Source:"} ${result.source}",
                                    color = VentoyTextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            CyberBadge(
                                text = result.category,
                                color = when (result.category) {
                                    "Microsoft Windows" -> Color(0xFF0078D4)
                                    "Security & Forensics" -> Color(0xFFE91E63)
                                    "Recovery & Diagnostics" -> VentoyAmber
                                    "Virtualization & Hypervisors" -> VentoyIndigoBright
                                    else -> VentoyEmeraldBright
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category & Boot Recommendation Tags
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CyberBadge(
                                text = "BOOT: ${result.recommendedBootMode}",
                                color = VentoyCyanBright
                            )
                            CyberBadge(
                                text = "ARCH: x86_64 / ARM64",
                                color = VentoyIndigo
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Extracted Summary / Description
                        Text(
                            text = result.description,
                            color = VentoyTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Official Website Link
                        if (!result.officialWebsite.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        try {
                                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(result.officialWebsite))
                                            context.startActivity(browserIntent)
                                        } catch (_: Exception) {}
                                    }
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = "Open Website",
                                    tint = VentoyCyanBright,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = result.officialWebsite,
                                    color = VentoyCyanBright,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // --- ISO Tagging Section ---
                        Text(
                            text = if (isGeorgian) "მონიშნე / მიანიჭე ტეგები ISO ფაილს:" else "Select target ISO to tag with this metadata:",
                            color = VentoyTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Determine available ISO targets (isoItems or isoFiles)
                        val hasIsoItems = isoItems.isNotEmpty()
                        val hasIsoFiles = isoFiles.isNotEmpty()

                        if (hasIsoItems || hasIsoFiles) {
                            // Target ISO Dropdown Trigger
                            Box(modifier = Modifier.fillMaxWidth()) {
                                val currentIsoItem = isoItems.find { it.id == selectedIsoItemId }
                                val currentIsoFile = isoFiles.find { it.id == selectedIsoFileId }
                                val displayLabel = currentIsoItem?.filename
                                    ?: currentIsoFile?.filename
                                    ?: (if (isGeorgian) "აირჩიე ISO ფაილი..." else "Select ISO file...")

                                Surface(
                                    color = VentoyDarkSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, VentoyBorder, RoundedCornerShape(8.dp))
                                        .clickable { isIsoDropdownExpanded = true }
                                        .testTag("dropdown_iso_selector")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Folder,
                                                contentDescription = null,
                                                tint = VentoyCyanBright,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = displayLabel,
                                                color = VentoyTextPrimary,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Expand",
                                            tint = VentoyCyanBright
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = isIsoDropdownExpanded,
                                    onDismissRequest = { isIsoDropdownExpanded = false },
                                    modifier = Modifier
                                        .background(VentoyDarkSurface)
                                        .border(1.dp, VentoyBorder, RoundedCornerShape(8.dp))
                                ) {
                                    if (hasIsoItems) {
                                        isoItems.forEach { iso ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = iso.filename,
                                                            color = VentoyTextPrimary,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "${iso.name} • ${iso.categoryName}",
                                                            color = VentoyTextMuted,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedIsoItemId = iso.id
                                                    isIsoDropdownExpanded = false
                                                },
                                                modifier = Modifier.testTag("dropdown_item_${iso.id}")
                                            )
                                        }
                                    }

                                    if (hasIsoFiles) {
                                        isoFiles.forEach { file ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = file.filename,
                                                            color = VentoyTextPrimary,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "${file.category} • ${file.path}",
                                                            color = VentoyTextMuted,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedIsoFileId = file.id
                                                    isIsoDropdownExpanded = false
                                                },
                                                modifier = Modifier.testTag("dropdown_item_file_${file.id}")
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action button: Tag and Apply Metadata
                            Button(
                                onClick = {
                                    val targetItem = isoItems.find { it.id == selectedIsoItemId }
                                    val targetFile = isoFiles.find { it.id == selectedIsoFileId }

                                    if (targetItem != null && onTagIsoItem != null) {
                                        onTagIsoItem(targetItem, result)
                                        recentlyTaggedNotice = if (isGeorgian)
                                            "წარმატებით მიენიჭა '${result.category}' ${targetItem.filename}-ს"
                                        else
                                            "Successfully tagged '${targetItem.filename}' as '${result.category}'!"
                                    } else if (targetFile != null && onTagIsoFile != null) {
                                        onTagIsoFile(targetFile, result)
                                        recentlyTaggedNotice = if (isGeorgian)
                                            "წარმატებით მიენიჭა '${result.category}' ${targetFile.filename}-ს"
                                        else
                                            "Successfully tagged '${targetFile.filename}' as '${result.category}'!"
                                    } else if (targetItem != null) {
                                        onTagIsoItem?.invoke(targetItem, result)
                                        recentlyTaggedNotice = "Tagged ${targetItem.filename} with ${result.category}"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_apply_os_tags"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VentoyCyan,
                                    contentColor = Color(0xFF00363F)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tag,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isGeorgian) "ტეგების მინიჭება ISO-ზე" else "Apply Online Tags to ISO",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            // When no ISOs are loaded yet
                            Text(
                                text = if (isGeorgian) "ISO ფაილები არ არის დამატებული ბიბლიოთეკაში." else "No local ISO files in library to tag yet. Add an ISO using SAF picker.",
                                color = VentoyTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
