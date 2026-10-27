package com.example.data.analyzer

import com.example.data.model.AutoInstallScript
import com.example.data.model.BootMode
import com.example.data.model.DistroType
import com.example.data.model.IsoItem
import java.util.UUID

enum class OsCategory(val displayNameGe: String, val displayNameEn: String, val iconBadge: String) {
    WINDOWS("Microsoft Windows", "Microsoft Windows", "WIN"),
    LINUX_DESKTOP("Linux სამუშაო სისტემები", "Linux Desktop & Workstations", "LNX"),
    SECURITY_FORENSICS("კიბერუსაფრთხოება & ანონიმურობა", "Security, Pen-Testing & Privacy", "SEC"),
    RECOVERY_DIAGNOSTICS("აღდგენა & დიაგნოსტიკა", "Disk Recovery & Diagnostics", "REC"),
    HYPERVISORS_SERVERS("სერვერები & ვირტუალიზაცია", "Servers & Virtualization", "SRV"),
    UTILITIES("ტექნიკური ხელსაწყოები", "Utility Tools", "UTL")
}

data class IsoDetectionResult(
    val detectedName: String,
    val distroType: DistroType,
    val category: OsCategory,
    val detectedVersion: String,
    val architecture: String,
    val bootMode: BootMode,
    val suggestedPersistenceMb: Int,
    val suggestedAutoInstall: AutoInstallScript,
    val secureBootReady: Boolean,
    val confidenceScore: Int, // 0 - 100
    val analysisNotes: String
)

object IsoDetectionEngine {

    fun analyzeIso(filename: String, sizeBytes: Long = 0): IsoDetectionResult {
        val lower = filename.lowercase()

        // 1. Windows detection
        if (lower.contains("win11") || (lower.contains("windows") && lower.contains("11"))) {
            return IsoDetectionResult(
                detectedName = "Windows 11 " + extractBuild(filename, "24H2"),
                distroType = DistroType.WINDOWS,
                category = OsCategory.WINDOWS,
                detectedVersion = "Windows 11 " + extractBuild(filename, "24H2 Pro/Home"),
                architecture = if (lower.contains("arm64")) "ARM64" else "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 0,
                suggestedAutoInstall = AutoInstallScript.WIN11_BYPASS_TPM,
                secureBootReady = true,
                confidenceScore = 98,
                analysisNotes = "Windows 11 detected. Auto-configured TPM/RAM bypass and autounattend.xml integration."
            )
        }

        if (lower.contains("win10") || (lower.contains("windows") && lower.contains("10"))) {
            return IsoDetectionResult(
                detectedName = "Windows 10 " + extractBuild(filename, "22H2"),
                distroType = DistroType.WINDOWS,
                category = OsCategory.WINDOWS,
                detectedVersion = "Windows 10 22H2",
                architecture = if (lower.contains("x86") || lower.contains("32bit")) "x86 (32-bit)" else "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 0,
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = true,
                confidenceScore = 95,
                analysisNotes = "Windows 10 standard installation image. Fully compatible with both UEFI and MBR."
            )
        }

        if (lower.contains("win") || lower.contains("windows")) {
            return IsoDetectionResult(
                detectedName = "Windows OS",
                distroType = DistroType.WINDOWS,
                category = OsCategory.WINDOWS,
                detectedVersion = "Microsoft Windows",
                architecture = "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 0,
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = true,
                confidenceScore = 90,
                analysisNotes = "Generic Windows image detected. Recommended WIMBoot mode if RAM is constrained."
            )
        }

        // 2. Security & Forensics & Privacy
        if (lower.contains("kali")) {
            val ver = extractRegex(filename, """202\d\.\d""", "2024.3")
            return IsoDetectionResult(
                detectedName = "Kali Linux $ver Live",
                distroType = DistroType.KALI,
                category = OsCategory.SECURITY_FORENSICS,
                detectedVersion = ver,
                architecture = if (lower.contains("arm")) "ARM64" else "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 4096,
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = true,
                confidenceScore = 99,
                analysisNotes = "Offensive Security live distro detected. Recommended with encrypted persistence partition."
            )
        }

        if (lower.contains("tails")) {
            val ver = extractRegex(filename, """\d+\.\d+""", "6.8")
            return IsoDetectionResult(
                detectedName = "Tails OS $ver (The Amnesic Incognito Live System)",
                distroType = DistroType.TAILS,
                category = OsCategory.SECURITY_FORENSICS,
                detectedVersion = ver,
                architecture = "x86_64",
                bootMode = BootMode.UEFI_ONLY,
                suggestedPersistenceMb = 0, // Tails handles LUKS internal persistence
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = true,
                confidenceScore = 98,
                analysisNotes = "Amnesic security OS. Encrypted storage partition and read-only hardware switch suggested."
            )
        }

        // 3. Desktop Linux
        if (lower.contains("ubuntu")) {
            val ver = extractRegex(filename, """\d{2}\.\d{2}(\.\d)?""", "24.04 LTS")
            return IsoDetectionResult(
                detectedName = "Ubuntu Desktop $ver",
                distroType = DistroType.UBUNTU,
                category = OsCategory.LINUX_DESKTOP,
                detectedVersion = ver,
                architecture = if (lower.contains("arm64")) "ARM64" else "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 4096,
                suggestedAutoInstall = AutoInstallScript.UBUNTU_CLOUD_INIT,
                secureBootReady = true,
                confidenceScore = 98,
                analysisNotes = "Canonical Ubuntu with Linux 6.x kernel and signed Canonical Shim bootloader."
            )
        }

        if (lower.contains("arch")) {
            return IsoDetectionResult(
                detectedName = "Arch Linux Rolling",
                distroType = DistroType.ARCH,
                category = OsCategory.LINUX_DESKTOP,
                detectedVersion = "Rolling Release",
                architecture = "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 2048,
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = false,
                confidenceScore = 96,
                analysisNotes = "Arch Linux rolling snapshot. Requires disabling Secure Boot or MokManager enrollment."
            )
        }

        if (lower.contains("fedora")) {
            val ver = extractRegex(filename, """\d{2}""", "40")
            return IsoDetectionResult(
                detectedName = "Fedora Workstation $ver",
                distroType = DistroType.FEDORA,
                category = OsCategory.LINUX_DESKTOP,
                detectedVersion = "Fedora $ver",
                architecture = "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 2048,
                suggestedAutoInstall = AutoInstallScript.KICKSTART,
                secureBootReady = true,
                confidenceScore = 95,
                analysisNotes = "Fedora Workstation with SELinux and Red Hat signed EFI shim."
            )
        }

        // 4. Recovery & Diagnostics
        if (lower.contains("clonezilla")) {
            return IsoDetectionResult(
                detectedName = "Clonezilla Live Backup & Clone",
                distroType = DistroType.CLONEZILLA,
                category = OsCategory.RECOVERY_DIAGNOSTICS,
                detectedVersion = "Live Release",
                architecture = "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 0,
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = true,
                confidenceScore = 99,
                analysisNotes = "Disk imaging, partition cloning, and bare-metal recovery tool."
            )
        }

        if (lower.contains("rescue") || lower.contains("systemrescue") || lower.contains("sysresccd")) {
            return IsoDetectionResult(
                detectedName = "SystemRescue Emergency Toolkit",
                distroType = DistroType.RESCUE,
                category = OsCategory.RECOVERY_DIAGNOSTICS,
                detectedVersion = "Latest",
                architecture = "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 0,
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = true,
                confidenceScore = 97,
                analysisNotes = "Emergency admin rescue tool: GParted, testdisk, ddrescue, chroot repair."
            )
        }

        // 5. Hypervisors & Servers
        if (lower.contains("proxmox") || lower.contains("pve")) {
            return IsoDetectionResult(
                detectedName = "Proxmox Virtual Environment",
                distroType = DistroType.PROXMOX,
                category = OsCategory.HYPERVISORS_SERVERS,
                detectedVersion = "8.x",
                architecture = "x86_64",
                bootMode = BootMode.UEFI_BIOS,
                suggestedPersistenceMb = 0,
                suggestedAutoInstall = AutoInstallScript.NONE,
                secureBootReady = true,
                confidenceScore = 97,
                analysisNotes = "Enterprise bare-metal hypervisor (KVM + LXC virtualization)."
            )
        }

        // Fallback / Custom
        val prettyName = filename.substringBeforeLast('.').replace('_', ' ').replace('-', ' ')
        return IsoDetectionResult(
            detectedName = prettyName.replaceFirstChar { it.uppercase() },
            distroType = DistroType.CUSTOM,
            category = if (lower.contains("tool") || lower.contains("diag") || lower.contains("memtest"))
                OsCategory.UTILITIES else OsCategory.LINUX_DESKTOP,
            detectedVersion = "Universal",
            architecture = if (lower.contains("64")) "x86_64" else "Universal",
            bootMode = BootMode.UEFI_BIOS,
            suggestedPersistenceMb = 0,
            suggestedAutoInstall = AutoInstallScript.NONE,
            secureBootReady = true,
            confidenceScore = 75,
            analysisNotes = "Standard bootable image. Ventoy handles universal ISO9660 / UDF loopback booting."
        )
    }

    private fun extractRegex(input: String, pattern: String, defaultVal: String): String {
        val regex = Regex(pattern)
        return regex.find(input)?.value ?: defaultVal
    }

    private fun extractBuild(input: String, defaultVal: String): String {
        val regex = Regex("""(2[234]h\d|build[\s_\-]?\d{5})""", RegexOption.IGNORE_CASE)
        return regex.find(input)?.value?.uppercase() ?: defaultVal
    }
}
