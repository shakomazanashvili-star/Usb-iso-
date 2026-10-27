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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.ChatMessage
import com.example.ui.components.CyberBadge
import com.example.ui.theme.VentoyBorder
import com.example.ui.theme.VentoyCyan
import com.example.ui.theme.VentoyCyanBright
import com.example.ui.theme.VentoyDarkBg
import com.example.ui.theme.VentoyDarkSurface
import com.example.ui.theme.VentoyDarkSurfaceVariant
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
fun AiChatScreen(
    viewModel: VentoyViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val isGe by viewModel.isGeorgian.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().imePadding(),
        containerColor = VentoyDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(VentoyCyanBright, VentoyIndigo))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BootGuard AI",
                                color = VentoyTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isGe) "Gemini 3.8 Flash • მულტიბუთ ექსპერტი" else "Gemini 3.8 Flash • Boot Architect",
                                color = VentoyCyanBright,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.generateVentoyJson() },
                        colors = ButtonDefaults.buttonColors(containerColor = VentoyIndigo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(34.dp)
                            .testTag("btn_gen_ventoy_json")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ventoy.json",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
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
        ) {
            // Chat message thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(msg = msg)
                }
            }

            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val prompts = if (isGe) listOf(
                    "როგორ გავიდე Windows 11 TPM/RAM შემოწმებას?",
                    "Security Violation (0x1A) შეცდომის მოგვარება",
                    "როგორ ჩავრთო Live Persistence Kali-ზე?",
                    "რა განსხვავებაა GPT და MBR-ს შორის?"
                ) else listOf(
                    "How to bypass Windows 11 TPM/RAM check?",
                    "Fix UEFI Security Violation (0x1A)",
                    "How to setup Kali Live Persistence?",
                    "Differences between GPT and MBR in Ventoy?"
                )

                items(prompts) { prompt ->
                    SuggestionChip(
                        onClick = {
                            inputText = prompt
                            viewModel.sendChatMessage(prompt)
                            inputText = ""
                        },
                        label = {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                color = VentoyTextSecondary
                            )
                        },
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

            // Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = VentoyDarkSurface,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (isGe) "ჰკითხე BootGuard AI-ს..." else "Ask BootGuard AI anything...",
                                color = VentoyTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_chat_prompt"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VentoyCyanBright,
                            unfocusedBorderColor = VentoyBorder,
                            focusedTextColor = VentoyTextPrimary,
                            unfocusedTextColor = VentoyTextPrimary,
                            focusedContainerColor = VentoyDarkSurfaceVariant,
                            unfocusedContainerColor = VentoyDarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val text = inputText
                            inputText = ""
                            viewModel.sendChatMessage(text)
                        },
                        enabled = !isThinking && inputText.isNotBlank(),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && !isThinking) VentoyCyanBright
                                else VentoyDarkSurfaceVariant
                            )
                            .testTag("btn_send_chat")
                    ) {
                        if (isThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = VentoyCyanBright,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) Color(0xFF00363F) else VentoyTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val isUser = msg.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(VentoyIndigo.copy(alpha = 0.3f))
                    .border(1.dp, VentoyIndigoBright, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = VentoyCyanBright,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(listOf(Color(0xFF00525E), Color(0xFF003844)))
                    else Brush.linearGradient(listOf(VentoySurfaceContainer, VentoyDarkSurface))
                )
                .border(
                    1.dp,
                    if (isUser) VentoyCyanBright.copy(alpha = 0.5f) else VentoyBorder,
                    RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isUser) "თქვენ / You" else "BootGuard AI",
                        color = if (isUser) VentoyCyanBright else VentoyIndigoBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (msg.isStreaming) {
                        Text(
                            text = "წერს...",
                            color = VentoyCyanBright,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (msg.text.isBlank() && msg.isStreaming) "..." else msg.text,
                    color = VentoyTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(VentoyCyan.copy(alpha = 0.2f))
                    .border(1.dp, VentoyCyanBright, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = VentoyCyanBright,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
