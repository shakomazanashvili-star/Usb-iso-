package com.example.data.repository

import android.util.Log
import com.example.data.model.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiAiRepository"

class GeminiAiRepository {

    // Lazy initialization of generativeModel as required by firebase-ai-logic-android
    private val generalModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are "BootGuard AI", the elite multi-boot, UEFI, and security engineer built into Ventoy+.
                    Your expertise covers:
                    1. Ventoy architecture: MBR vs GPT, VTOYEFI partition, exFAT/NTFS, GRUB2 theme, persistence image files (.dat), auto-install (autounattend.xml, kickstart, preseed).
                    2. UEFI Secure Boot: MokManager key enrollment (ENROLL MOK), Shim bootloader, SBAT revocation table, DB/DBX database, signature checking.
                    3. Troubleshooting boot errors: "Security Violation", "Invalid signature detected", "Black screen on boot", "VMD/Intel RST RAID driver missing", "ACPI BIOS Error", "BitLocker recovery prompt".
                    4. Security features: AES-256 boot vault encryption, write-protect hardware isolation, anti-tamper checksums (SHA256, MD5), and forensic isolation.
                    5. Bilingual support: Understand and reply fluently in Georgian (ქართული) if the user asks in Georgian, or English if asked in English.
                    Always provide clear, technical, step-by-step solutions with exact code or JSON blocks when applicable. Keep your tone confident, cyber-focused, and highly helpful.
                    """.trimIndent()
                )
            }
        )
    }

    private val searchGroundingModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    /**
     * Stream multi-turn chat responses to minimize Time-To-First-Token (TTFT)
     */
    fun sendChatMessageStream(
        conversationHistory: List<ChatMessage>,
        userMessage: String
    ): Flow<String> = flow {
        try {
            // Build chat history for model
            val historyContents: List<Content> = conversationHistory.takeLast(10).map { msg: ChatMessage ->
                content(if (msg.isFromUser) "user" else "model") {
                    text(msg.text)
                }
            }

            val chat = generalModel.startChat(history = historyContents)

            withTimeout(60_000L) {
                chat.sendMessageStream(userMessage).collect { chunk ->
                    val textChunk = chunk.text.orEmpty()
                    if (textChunk.isNotEmpty()) {
                        emit(textChunk)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chat request timed out after 60s", e)
            emit("\n[Error: Request timed out. Please check network connection.]")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chat generation failed", e)
            emit("\n[Error: ${e.localizedMessage ?: "AI generation failed."}]")
        }
    }

    /**
     * AI ISO Security & Integrity Audit
     */
    suspend fun auditIsoSecurity(
        isoName: String,
        distro: String,
        sha256: String,
        sizeBytes: Long
    ): Result<String> {
        return try {
            val prompt = """
                Perform an in-depth security and boot integrity audit on the following operating system image:
                - Image Name: $isoName
                - Distribution: $distro
                - SHA-256 Checksum: $sha256
                - Size: ${sizeBytes / (1024 * 1024)} MB

                Please analyze:
                1. Checksum authenticity: Does this hash match known official releases?
                2. Secure Boot compatibility: Is this image signed with Microsoft 3rd Party UEFI CA or requires MOK enrollment?
                3. Security advisories: Are there any known vulnerabilities (e.g. GRUB BootHole, CVEs, or SBAT revocations)?
                4. Recommended Ventoy parameters: GRUB2 mode vs Memdisk mode, recommended persistence file size, and recommended auto-install options.
                Provide structured, executive output.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                generalModel.generateContent(prompt)
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(IllegalStateException("Empty response from AI"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Audit failed", e)
            Result.failure(e)
        }
    }

    /**
     * Search live operating system releases with Google Search data
     */
    suspend fun searchLiveOsData(query: String): Result<String> {
        return try {
            val prompt = """
                Search and fetch the latest, up-to-date release details for the following OS or ISO:
                "$query"

                Please include:
                - Latest stable version number and release date
                - Official verified SHA-256 checksum (exact hash)
                - Official download mirror link
                - Major new features, kernel version, or security improvements
                - Ventoy multiboot compatibility notes (UEFI, Secure Boot, or persistence requirements)
                Format the answer clearly with bullet points.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                searchGroundingModel.generateContent(prompt)
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(IllegalStateException("No search result returned"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Search OS data failed", e)
            Result.failure(e)
        }
    }

    /**
     * Generate customized ventoy.json configuration
     */
    suspend fun generateVentoyJson(
        themeName: String,
        bypassWindows11Tpm: Boolean,
        enableAutoInstall: Boolean,
        passwordProtected: Boolean
    ): Result<String> {
        return try {
            val prompt = """
                Generate a complete, production-ready `ventoy.json` configuration file for Ventoy.
                Requirements:
                - Theme: $themeName
                - Windows 11 bypass TPM, RAM, CPU & Microsoft Account requirements: $bypassWindows11Tpm (use VTOY_WIN11_BYPASS_CHECK and VTOY_WIN11_BYPASS_NRO)
                - Auto installation scripts enabled: $enableAutoInstall
                - Menu password protection: $passwordProtected (with SHA-256 hash placeholder)
                - Custom menu aliases and hotkeys
                Output only valid JSON within code block and explain each parameter briefly.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                generalModel.generateContent(prompt)
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(IllegalStateException("Empty ventoy.json returned"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "ventoy.json generation failed", e)
            Result.failure(e)
        }
    }
}
