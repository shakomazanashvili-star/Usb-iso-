package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VentoyDatabase
import com.example.data.model.AutoInstallScript
import com.example.data.model.ChatMessage
import com.example.data.model.DistroType
import com.example.data.model.FileSystemType
import com.example.data.model.IsoItem
import com.example.data.model.OsCatalogItem
import com.example.data.model.PartitionStyle
import com.example.data.model.SecurityStatus
import com.example.data.model.UsbDrive
import com.example.data.repository.GeminiAiRepository
import com.example.data.repository.VentoyRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface OperationProgress {
    data object Idle : OperationProgress
    data class InProgress(val percent: Int, val stepDescription: String) : OperationProgress
    data class Success(val message: String) : OperationProgress
    data class Error(val errorMessage: String) : OperationProgress
}

class VentoyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VentoyDatabase.getDatabase(application)
    private val repository = VentoyRepository(
        context = application,
        isoDao = db.isoDao(),
        securityLogDao = db.securityLogDao()
    )
    private val aiRepository = GeminiAiRepository()

    val usbDrive: StateFlow<UsbDrive> = repository.usbDriveState
    val connectedDrives: StateFlow<List<UsbDrive>> = repository.connectedDrives
    val securityStatus: StateFlow<SecurityStatus> = repository.securityStatus

    val isos: StateFlow<List<IsoItem>> = repository.allIsos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityLogs = repository.securityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val catalogList: List<OsCatalogItem> = repository.getOfficialCatalog()

    // Language toggle: Georgian (ქართული) or English
    private val _isGeorgian = MutableStateFlow(true)
    val isGeorgian: StateFlow<Boolean> = _isGeorgian.asStateFlow()

    fun toggleLanguage() {
        _isGeorgian.value = !_isGeorgian.value
    }

    // Progress State for USB Formats / Installs / Wipes
    private val _operationProgress = MutableStateFlow<OperationProgress>(OperationProgress.Idle)
    val operationProgress: StateFlow<OperationProgress> = _operationProgress.asStateFlow()

    // Verification progress
    private val _verifyingIsoId = MutableStateFlow<String?>(null)
    val verifyingIsoId: StateFlow<String?> = _verifyingIsoId.asStateFlow()

    // AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_msg",
                isFromUser = false,
                text = "გამარჯობა! მე ვარ BootGuard AI — შენი პირადი მულტიბუთ და უსაფრთხოების ასისტენტი. შემიძლია დაგეხმარო Ventoy-ს ინსტალაციაში, UEFI SecureBoot-ის გამართვაში, ISO შეცდომების მოგვარებაში, ventoy.json-ის გენერირებასა და Windows 11 TPM/RAM მოთხოვნების ავტომატურ გვერდის ავლაში. რით შემიძლია დაგეხმარო დღეს?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // AI ISO Security Audit
    private val _isAuditing = MutableStateFlow(false)
    val isAuditing: StateFlow<Boolean> = _isAuditing.asStateFlow()

    private val _auditResult = MutableStateFlow<String?>(null)
    val auditResult: StateFlow<String?> = _auditResult.asStateFlow()

    private val _auditedIso = MutableStateFlow<IsoItem?>(null)
    val auditedIso: StateFlow<IsoItem?> = _auditedIso.asStateFlow()

    // Live OS Search
    private val _liveSearchQuery = MutableStateFlow("")
    val liveSearchQuery: StateFlow<String> = _liveSearchQuery.asStateFlow()

    private val _isSearchingOs = MutableStateFlow(false)
    val isSearchingOs: StateFlow<Boolean> = _isSearchingOs.asStateFlow()

    private val _liveSearchResult = MutableStateFlow<String?>(null)
    val liveSearchResult: StateFlow<String?> = _liveSearchResult.asStateFlow()

    // ventoy.json Generated Content
    private val _generatedVentoyJson = MutableStateFlow<String?>(null)
    val generatedVentoyJson: StateFlow<String?> = _generatedVentoyJson.asStateFlow()

    // Retrofit Online OS Metadata Search State
    private val osMetadataApiClient = com.example.data.network.OsMetadataApiClient.getInstance()
    private val _onlineMetadataResult = MutableStateFlow<Pair<IsoItem, com.example.data.network.model.OsMetadataResult>?>(null)
    val onlineMetadataResult: StateFlow<Pair<IsoItem, com.example.data.network.model.OsMetadataResult>?> = _onlineMetadataResult.asStateFlow()

    private val _isFetchingOnlineMetadata = MutableStateFlow(false)
    val isFetchingOnlineMetadata: StateFlow<Boolean> = _isFetchingOnlineMetadata.asStateFlow()

    fun fetchOnlineMetadataForIso(iso: IsoItem) {
        viewModelScope.launch {
            _isFetchingOnlineMetadata.value = true
            val res = osMetadataApiClient.searchOsMetadata(iso.filename).getOrNull()
            if (res != null) {
                _onlineMetadataResult.value = Pair(iso, res)
            }
            _isFetchingOnlineMetadata.value = false
        }
    }

    fun applyOnlineMetadata(iso: IsoItem, metadata: com.example.data.network.model.OsMetadataResult) {
        viewModelScope.launch {
            repository.updateIsoMetadataOnline(
                isoId = iso.id,
                newName = metadata.title,
                categoryName = metadata.category
            )
            _onlineMetadataResult.value = null
        }
    }

    fun dismissOnlineMetadataDialog() {
        _onlineMetadataResult.value = null
    }

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun dismissOperationProgress() {
        _operationProgress.value = OperationProgress.Idle
    }

    fun selectDrive(driveId: String) {
        repository.selectDrive(driveId)
    }

    fun refreshConnectedDrives() {
        viewModelScope.launch {
            repository.refreshConnectedDrives()
        }
    }

    fun ejectDrive(driveId: String) {
        viewModelScope.launch {
            repository.ejectDrive(driveId)
        }
    }

    fun initializeDrive(config: com.example.data.model.UsbInitializationConfig) {
        viewModelScope.launch {
            _operationProgress.value = OperationProgress.InProgress(0, "Initializing drive partitions...")
            try {
                repository.initializeDrive(config) { pct, desc ->
                    _operationProgress.value = OperationProgress.InProgress(pct, desc)
                }
                _operationProgress.value = OperationProgress.Success("Ventoy+ 1.0.99-SECURE წარმატებით დაინსტალირდა!")
            } catch (e: Exception) {
                _operationProgress.value = OperationProgress.Error(e.localizedMessage ?: "Drive initialization failed")
            }
        }
    }

    fun installVentoy(partitionStyle: PartitionStyle, fileSystem: FileSystemType, secureBoot: Boolean) {
        viewModelScope.launch {
            _operationProgress.value = OperationProgress.InProgress(0, "Preparing disk structure...")
            try {
                repository.installVentoy(partitionStyle, fileSystem, secureBoot) { pct, desc ->
                    _operationProgress.value = OperationProgress.InProgress(pct, desc)
                }
                _operationProgress.value = OperationProgress.Success("Ventoy+ 1.0.99-SECURE წარმატებით დაინსტალირდა!")
            } catch (e: Exception) {
                _operationProgress.value = OperationProgress.Error(e.localizedMessage ?: "Installation failed")
            }
        }
    }

    fun updateVentoy() {
        viewModelScope.launch {
            _operationProgress.value = OperationProgress.InProgress(0, "Starting non-destructive update...")
            try {
                repository.updateVentoy { pct, desc ->
                    _operationProgress.value = OperationProgress.InProgress(pct, desc)
                }
                _operationProgress.value = OperationProgress.Success("Ventoy+ ბირთვი განახლდა მონაცემების შენარჩუნებით!")
            } catch (e: Exception) {
                _operationProgress.value = OperationProgress.Error(e.localizedMessage ?: "Update failed")
            }
        }
    }

    fun toggleWriteProtection() {
        viewModelScope.launch {
            repository.toggleWriteProtection()
        }
    }

    fun toggleSecureBoot() {
        viewModelScope.launch {
            repository.toggleSecureBoot()
        }
    }

    fun setPartitionStyle(style: PartitionStyle) {
        viewModelScope.launch {
            repository.setPartitionStyle(style)
        }
    }

    fun setFileSystem(fs: FileSystemType) {
        viewModelScope.launch {
            repository.setFileSystem(fs)
        }
    }

    fun addCustomIso(uri: Uri, filename: String, sizeBytes: Long, distroType: DistroType? = null) {
        viewModelScope.launch {
            repository.addCustomIsoFromUri(uri, filename, sizeBytes, distroType)
        }
    }

    fun autoCategorizeLibrary() {
        viewModelScope.launch {
            repository.autoCategorizeAllIsos()
        }
    }

    // Live Ventoy Boot Menu Simulator
    private val _isBootSimulatorOpen = MutableStateFlow(false)
    val isBootSimulatorOpen: StateFlow<Boolean> = _isBootSimulatorOpen.asStateFlow()

    private val _simulatedBootingIso = MutableStateFlow<IsoItem?>(null)
    val simulatedBootingIso: StateFlow<IsoItem?> = _simulatedBootingIso.asStateFlow()

    private val _simulatorLogs = MutableStateFlow<List<String>>(emptyList())
    val simulatorLogs: StateFlow<List<String>> = _simulatorLogs.asStateFlow()

    private val _simulatorTheme = MutableStateFlow("CYBER_NEON")
    val simulatorTheme: StateFlow<String> = _simulatorTheme.asStateFlow()

    fun openBootSimulator() {
        _isBootSimulatorOpen.value = true
        _simulatedBootingIso.value = null
        _simulatorLogs.value = emptyList()
    }

    fun closeBootSimulator() {
        _isBootSimulatorOpen.value = false
        _simulatedBootingIso.value = null
        _simulatorLogs.value = emptyList()
    }

    fun setSimulatorTheme(theme: String) {
        _simulatorTheme.value = theme
    }

    fun simulateBoot(iso: IsoItem) {
        viewModelScope.launch {
            _simulatedBootingIso.value = iso
            _simulatorLogs.value = listOf(
                "Ventoy+ 1.0.99-SECURE Bootloader initializing...",
                "Scanning ACPI RSDP table and UEFI firmware...",
                "Loading image: /ventoy/${iso.filename}",
                "Verifying digital signature with enrolled MOK key...",
                "Status: SECURE_BOOT_VERIFIED (OK)",
                "Allocating loopback memory blocks: ${iso.sizeBytes / (1024 * 1024)} MB",
                if (iso.isEncrypted) "AES-256 Vault: Decrypting payload with master session key..." else "Vault: Standard bootloader pass-through.",
                if (iso.persistenceMb > 0) "Mounting persistence image: /ventoy/persistence.dat (${iso.persistenceMb} MB rw)" else "Persistence: read-only squashfs.",
                "Executing kernel handoff: BOOTX64.EFI -> OS Kernel...",
                "Welcome to ${iso.name}!"
            )
        }
    }

    fun toggleIsoVaultEncryption(iso: IsoItem) {
        viewModelScope.launch {
            repository.toggleIsoVaultEncryption(iso)
        }
    }

    fun toggleIsoPin(iso: IsoItem) {
        viewModelScope.launch {
            repository.toggleIsoPin(iso)
        }
    }

    fun setDefaultBoot(isoId: String) {
        viewModelScope.launch {
            repository.setDefaultBoot(isoId)
        }
    }

    fun updatePersistence(iso: IsoItem, mb: Int) {
        viewModelScope.launch {
            repository.updateIsoPersistence(iso, mb)
        }
    }

    fun updateAutoInstall(iso: IsoItem, script: AutoInstallScript) {
        viewModelScope.launch {
            repository.updateIsoAutoInstall(iso, script)
        }
    }

    fun verifyChecksum(iso: IsoItem) {
        viewModelScope.launch {
            _verifyingIsoId.value = iso.id
            repository.verifyIsoChecksum(iso)
            _verifyingIsoId.value = null
        }
    }

    fun deleteIso(iso: IsoItem) {
        viewModelScope.launch {
            repository.deleteIso(iso)
        }
    }

    fun emergencyWipe() {
        viewModelScope.launch {
            _operationProgress.value = OperationProgress.InProgress(0, "Initiating secure zero-wipe...")
            try {
                repository.emergencyZeroWipe { pct, desc ->
                    _operationProgress.value = OperationProgress.InProgress(pct, desc)
                }
                _operationProgress.value = OperationProgress.Success("დისკი მთლიანად გასუფთავდა!")
            } catch (e: Exception) {
                _operationProgress.value = OperationProgress.Error(e.localizedMessage ?: "Wipe failed")
            }
        }
    }

    // AI Chat
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            isFromUser = true,
            text = userText.trim()
        )
        val assistantMsgId = UUID.randomUUID().toString()
        val initialAssistantMsg = ChatMessage(
            id = assistantMsgId,
            isFromUser = false,
            text = "",
            isStreaming = true
        )

        _chatMessages.value = _chatMessages.value + userMsg + initialAssistantMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            try {
                val fullResponse = StringBuilder()
                aiRepository.sendChatMessageStream(_chatMessages.value.dropLast(1), userText).collect { chunk ->
                    fullResponse.append(chunk)
                    _chatMessages.value = _chatMessages.value.map { msg ->
                        if (msg.id == assistantMsgId) {
                            msg.copy(text = fullResponse.toString(), isStreaming = true)
                        } else msg
                    }
                }
                _chatMessages.value = _chatMessages.value.map { msg ->
                    if (msg.id == assistantMsgId) {
                        msg.copy(text = fullResponse.toString(), isStreaming = false)
                    } else msg
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value.map { msg ->
                    if (msg.id == assistantMsgId) {
                        msg.copy(text = "შეცდომა: ${e.localizedMessage ?: "პასუხის მიღება ვერ მოხერხდა"}", isStreaming = false)
                    } else msg
                }
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    // AI Audit
    fun auditIsoWithAi(iso: IsoItem) {
        _auditedIso.value = iso
        _isAuditing.value = true
        _auditResult.value = null
        viewModelScope.launch {
            val result = aiRepository.auditIsoSecurity(
                isoName = iso.name,
                distro = iso.distroType.displayName,
                sha256 = iso.sha256Calculated.ifBlank { iso.sha256Expected },
                sizeBytes = iso.sizeBytes
            )
            result.onSuccess {
                _auditResult.value = it
            }.onFailure {
                _auditResult.value = "აუდიტის შეცდომა: ${it.localizedMessage}"
            }
            _isAuditing.value = false
        }
    }

    fun dismissAudit() {
        _auditedIso.value = null
        _auditResult.value = null
    }

    // Live Search
    fun setLiveSearchQuery(q: String) {
        _liveSearchQuery.value = q
    }

    fun searchOsLive(query: String) {
        val q = query.ifBlank { _liveSearchQuery.value }
        if (q.isBlank()) return
        _isSearchingOs.value = true
        _liveSearchResult.value = null
        viewModelScope.launch {
            val res = aiRepository.searchLiveOsData(q)
            res.onSuccess {
                _liveSearchResult.value = it
            }.onFailure {
                _liveSearchResult.value = "ძიების შეცდომა: ${it.localizedMessage}"
            }
            _isSearchingOs.value = false
        }
    }

    // Generate ventoy.json
    fun generateVentoyJson(theme: String = "CyberDark", bypassTpm: Boolean = true, autoInstall: Boolean = true, pwd: Boolean = false) {
        viewModelScope.launch {
            _isAiThinking.value = true
            val res = aiRepository.generateVentoyJson(theme, bypassTpm, autoInstall, pwd)
            res.onSuccess {
                _generatedVentoyJson.value = it
            }.onFailure {
                _generatedVentoyJson.value = "შეცდომა: ${it.localizedMessage}"
            }
            _isAiThinking.value = false
        }
    }

    fun dismissVentoyJson() {
        _generatedVentoyJson.value = null
    }
}
