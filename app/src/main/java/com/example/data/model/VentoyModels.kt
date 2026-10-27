package com.example.data.model

enum class DistroType(val displayName: String, val category: String) {
    WINDOWS("Windows 11 / 10", "Microsoft Windows"),
    UBUNTU("Ubuntu Linux", "Linux Desktop/Server"),
    KALI("Kali Linux", "Security & Pen-testing"),
    ARCH("Arch Linux", "Rolling Linux"),
    FEDORA("Fedora Workstation", "Linux Workstation"),
    TAILS("Tails OS (Amnesic)", "Privacy & Anonymity"),
    PROXMOX("Proxmox VE", "Virtualization & Hypervisor"),
    CLONEZILLA("Clonezilla Live", "Disk Backup & Imaging"),
    RESCUE("SystemRescue", "Emergency System Repair"),
    CUSTOM("Custom ISO / IMG", "Other Bootable Image")
}

enum class BootMode(val label: String) {
    UEFI_BIOS("UEFI + Legacy BIOS (Universal)"),
    UEFI_ONLY("UEFI Only (x86_64 / ARM64)"),
    BIOS_ONLY("Legacy BIOS (MBR)")
}

enum class PartitionStyle(val label: String) {
    GPT("GPT (Recommended for Modern UEFI)"),
    MBR("MBR (Legacy BIOS & CSM)")
}

enum class FileSystemType(val label: String) {
    EXFAT("exFAT (Recommended - Huge files & Cross-platform)"),
    NTFS("NTFS (Windows optimized)"),
    FAT32("FAT32 (Legacy compatibility)"),
    EXT4("ext4 (Linux native)")
}

enum class AutoInstallScript(val label: String, val filename: String) {
    NONE("None", ""),
    WIN11_BYPASS_TPM("Windows 11 Auto-Bypass TPM/RAM (autounattend.xml)", "autounattend.xml"),
    UBUNTU_CLOUD_INIT("Ubuntu Automated Cloud-Init", "user-data"),
    KICKSTART("RedHat / Fedora Kickstart (ks.cfg)", "ks.cfg")
}

data class IsoItem(
    val id: String,
    val name: String,
    val filename: String,
    val sizeBytes: Long,
    val distroType: DistroType,
    val version: String,
    val bootMode: BootMode = BootMode.UEFI_BIOS,
    val sha256Expected: String = "",
    val sha256Calculated: String = "",
    val isVerified: Boolean = false,
    val isEncrypted: Boolean = false,
    val isPinned: Boolean = false,
    val persistenceMb: Int = 0,
    val autoInstall: AutoInstallScript = AutoInstallScript.NONE,
    val isDefaultBoot: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val categoryName: String = "LINUX_DESKTOP",
    val architecture: String = "x86_64",
    val filePath: String = "",
    val md5Checksum: String = "",
    val verificationStatus: String = "UNVERIFIED",
    val verifiedTimestamp: Long = 0L
)

enum class UsbDriveStatus(val labelEn: String, val labelGe: String) {
    VENTOY_ACTIVE("Ventoy+ Active", "Ventoy+ აქტიურია"),
    READY_FOR_VENTOY("Ready for Ventoy+", "მზადაა ინსტალაციისთვის"),
    NEEDS_UPDATE("Update Available", "განახლება ხელმისაწვდომია"),
    WRITE_PROTECTED("Write-Protected", "ჩაწერა დაბლოკილია"),
    UNFORMATTED("Unformatted / RAW", "დაუფორმატებელი")
}

data class UsbInitializationConfig(
    val driveId: String,
    val partitionStyle: PartitionStyle = PartitionStyle.GPT,
    val fileSystem: FileSystemType = FileSystemType.EXFAT,
    val clusterAlignmentKb: Int = 128,
    val secureBoot: Boolean = true,
    val reservedSpaceMb: Int = 0
)

data class UsbDrive(
    val id: String,
    val name: String,
    val vendor: String,
    val model: String,
    val serialNumber: String,
    val capacityBytes: Long,
    val usedBytes: Long,
    val fsType: FileSystemType,
    val partitionStyle: PartitionStyle,
    val isVentoyInstalled: Boolean,
    val installedVentoyVersion: String,
    val latestVentoyVersion: String = "1.0.99-SECURE",
    val secureBootEnrolled: Boolean = true,
    val isWriteProtected: Boolean = false,
    val isMounted: Boolean = true,
    val mountPath: String = "/storage/0781-5588",
    val status: UsbDriveStatus = if (isVentoyInstalled) UsbDriveStatus.VENTOY_ACTIVE else UsbDriveStatus.READY_FOR_VENTOY,
    val clusterAlignmentKb: Int = 128,
    val speedRating: String = "USB 3.2 Gen 1 (5Gbps)",
    val reservedSpaceMb: Int = 0
)

data class SecurityStatus(
    val secureBootEnabled: Boolean = true,
    val mokKeyEnrolled: Boolean = true,
    val mokFingerprint: String = "4F:8A:2C:19:6E:9B:F0:3A:7C:1E:5D:82:11:AB:CC:90",
    val dbxRevocationActive: Boolean = true,
    val dbxVersion: String = "DBX-2026.08 (Revoked SBAT Keys Blocked)",
    val vaultEncryptionActive: Boolean = true,
    val vaultAlgorithm: String = "AES-256-XTS Hardware Accelerated",
    val writeProtectHardwareSwitch: Boolean = false,
    val zeroWipeSupported: Boolean = true
)

data class SecurityLog(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val description: String,
    val severity: String // INFO, SECURE, WARNING, CRITICAL
)

data class ChatMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

data class OsCatalogItem(
    val id: String,
    val name: String,
    val distro: DistroType,
    val latestVersion: String,
    val officialSha256: String,
    val downloadUrl: String,
    val description: String,
    val releaseDate: String,
    val estimatedSize: String,
    val secureBootCertified: Boolean = true
)
