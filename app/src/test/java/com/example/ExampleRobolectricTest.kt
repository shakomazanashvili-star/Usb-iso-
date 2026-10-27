package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.analyzer.IsoDetectionEngine
import com.example.data.analyzer.OsCategory
import com.example.data.local.IsoEntity
import com.example.data.local.IsoFile
import com.example.data.local.VentoyDatabase
import com.example.data.model.DistroType
import com.example.data.model.PartitionStyle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ventoy+", appName)
  }

  @Test
  fun `verify distro types and partition enum`() {
    assertEquals("Windows 11 / 10", DistroType.WINDOWS.displayName)
    assertEquals("GPT (Recommended for Modern UEFI)", PartitionStyle.GPT.label)
  }

  @Test
  fun `verify automatic ISO detection and categorization`() {
    val winResult = IsoDetectionEngine.analyzeIso("Win11_24H2_English_x64.iso")
    assertEquals(DistroType.WINDOWS, winResult.distroType)
    assertEquals(OsCategory.WINDOWS, winResult.category)
    assertTrue(winResult.detectedName.contains("Windows 11"))

    val kaliResult = IsoDetectionEngine.analyzeIso("kali-linux-2024.3-live-amd64.iso")
    assertEquals(DistroType.KALI, kaliResult.distroType)
    assertEquals(OsCategory.SECURITY_FORENSICS, kaliResult.category)
    assertEquals(4096, kaliResult.suggestedPersistenceMb)

    val tailsResult = IsoDetectionEngine.analyzeIso("tails-amd64-6.8.img")
    assertEquals(DistroType.TAILS, tailsResult.distroType)
    assertEquals(OsCategory.SECURITY_FORENSICS, tailsResult.category)

    val clonezillaResult = IsoDetectionEngine.analyzeIso("clonezilla-live-3.1.2-22-amd64.iso")
    assertEquals(DistroType.CLONEZILLA, clonezillaResult.distroType)
    assertEquals(OsCategory.RECOVERY_DIAGNOSTICS, clonezillaResult.category)
  }

  @Test
  fun `verify Room IsoEntity and IsoDao operations`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = VentoyDatabase.getDatabase(context)
    val dao = db.isoDao()

    val testEntity = IsoEntity(
        id = "test_iso_1",
        name = "Ubuntu 24.04 Server",
        filename = "ubuntu-24.04-live-server-amd64.iso",
        filePath = "/ventoy/ubuntu-24.04-live-server-amd64.iso",
        sizeBytes = 2_100_000_000L,
        distroType = "UBUNTU",
        categoryName = "LINUX_DESKTOP",
        architecture = "x86_64",
        version = "24.04 LTS",
        bootMode = "UEFI_BIOS",
        sha256Expected = "abc123expectedhash",
        sha256Calculated = "",
        md5Checksum = "",
        isVerified = false,
        verificationStatus = "UNVERIFIED",
        verifiedTimestamp = 0L,
        isEncrypted = false,
        isPinned = true,
        persistenceMb = 0,
        autoInstall = "NONE",
        isDefaultBoot = false,
        dateAdded = System.currentTimeMillis()
    )

    dao.insertIso(testEntity)

    val loaded = dao.getIsoById("test_iso_1")
    assertNotNull(loaded)
    assertEquals("/ventoy/ubuntu-24.04-live-server-amd64.iso", loaded?.filePath)
    assertEquals("LINUX_DESKTOP", loaded?.categoryName)
    assertEquals("UNVERIFIED", loaded?.verificationStatus)

    val byPath = dao.getIsoByPath("/ventoy/ubuntu-24.04-live-server-amd64.iso")
    assertNotNull(byPath)
    assertEquals("test_iso_1", byPath?.id)

    // Test Checksum & Verification Update
    dao.updateChecksumAndVerification(
        id = "test_iso_1",
        calculatedSha256 = "abc123expectedhash",
        calculatedMd5 = "md5hash123",
        isVerified = true,
        status = "VERIFIED",
        verifiedTimestamp = 1729860000L
    )

    val verified = dao.getIsoById("test_iso_1")
    assertEquals("abc123expectedhash", verified?.sha256Calculated)
    assertEquals("md5hash123", verified?.md5Checksum)
    assertEquals(true, verified?.isVerified)
    assertEquals("VERIFIED", verified?.verificationStatus)
    assertEquals(1729860000L, verified?.verifiedTimestamp)

    // Clean up
    dao.deleteById("test_iso_1")
    assertEquals(null, dao.getIsoById("test_iso_1"))
  }

  @Test
  fun `verify IsoFile entity and IsoFileDao CRUD operations`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = VentoyDatabase.getDatabase(context)
    val fileDao = db.isoFileDao()

    // 1. Create (Insert)
    val file = IsoFile(
        path = "/storage/usb/ISOs/archlinux-2026.10.iso",
        filename = "archlinux-2026.10.iso",
        category = "Linux Desktop",
        checksum = "3dbfa33c7fbe886b68e92f23b28b7e7195dc85775837130a11a8775f05353e12",
        sizeBytes = 1_180_000_000L,
        isVerified = true
    )
    val insertedId = fileDao.insert(file)
    assertTrue(insertedId > 0)

    // 2. Read (Query)
    val loadedById = fileDao.getIsoFileById(insertedId)
    assertNotNull(loadedById)
    assertEquals("archlinux-2026.10.iso", loadedById?.filename)
    assertEquals("/storage/usb/ISOs/archlinux-2026.10.iso", loadedById?.path)
    assertEquals("Linux Desktop", loadedById?.category)
    assertEquals("3dbfa33c7fbe886b68e92f23b28b7e7195dc85775837130a11a8775f05353e12", loadedById?.checksum)
    assertEquals(true, loadedById?.isVerified)

    val loadedByPath = fileDao.getIsoFileByPath("/storage/usb/ISOs/archlinux-2026.10.iso")
    assertNotNull(loadedByPath)
    assertEquals(insertedId, loadedByPath?.id)

    val categoryList = fileDao.getIsoFilesByCategory("Linux Desktop").first()
    assertTrue(categoryList.any { it.id == insertedId })

    // 3. Update
    val updated = loadedById!!.copy(category = "Rolling Linux")
    fileDao.update(updated)
    val reloaded = fileDao.getIsoFileById(insertedId)
    assertEquals("Rolling Linux", reloaded?.category)

    // Update checksum specifically
    fileDao.updateChecksum(insertedId, "newcalculatedchecksumhash", true)
    val checksumUpdated = fileDao.getIsoFileById(insertedId)
    assertEquals("newcalculatedchecksumhash", checksumUpdated?.checksum)
    assertEquals(true, checksumUpdated?.isVerified)

    // 4. Delete
    fileDao.deleteById(insertedId)
    assertNull(fileDao.getIsoFileById(insertedId))
  }

  @Test
  fun `verify IsoFileViewModel reactive flow and CRUD operations`() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val db = VentoyDatabase.getDatabase(app)
    val dao = db.isoFileDao()
    val viewModel = com.example.ui.viewmodel.IsoFileViewModel(app, dao)

    // Clear any previous records
    dao.deleteAll()

    // 1. Add ISO via ViewModel
    val testPath = "/storage/usb/ISOs/ubuntu-24.04-desktop.iso"
    val testFilename = "ubuntu-24.04-desktop.iso"
    val testCategory = "Linux Desktop"
    val testChecksum = "sha256_mock_hash_ubuntu_12345"

    viewModel.addIsoFile(
        path = testPath,
        filename = testFilename,
        category = testCategory,
        checksum = testChecksum,
        sizeBytes = 4_500_000_000L,
        isVerified = false
    )

    // Wait for insertion and verify allIsoFiles Flow
    val allFiles = viewModel.allIsoFiles.first { it.isNotEmpty() }
    val inserted = allFiles.find { it.filename == testFilename }
    assertNotNull(inserted)
    assertEquals(testPath, inserted?.path)
    assertEquals(testCategory, inserted?.category)
    assertEquals(testChecksum, inserted?.checksum)
    assertEquals(false, inserted?.isVerified)

    val insertedId = inserted!!.id

    // 2. Update checksum
    viewModel.updateChecksum(insertedId, "sha256_verified_ubuntu_99999", true)
    val updatedFiles = viewModel.allIsoFiles.first { list ->
      list.any { it.id == insertedId && it.isVerified }
    }
    val updatedItem = updatedFiles.first { it.id == insertedId }
    assertEquals("sha256_verified_ubuntu_99999", updatedItem.checksum)
    assertTrue(updatedItem.isVerified)

    // 3. Update category
    viewModel.updateCategory(insertedId, "Workstation")
    val catUpdatedFiles = viewModel.allIsoFiles.first { list ->
      list.any { it.id == insertedId && it.category == "Workstation" }
    }
    assertEquals("Workstation", catUpdatedFiles.first { it.id == insertedId }.category)

    // 4. Delete record
    viewModel.deleteIsoFileById(insertedId)
    val afterDelete = viewModel.allIsoFiles.first { list ->
      list.none { it.id == insertedId }
    }
    assertTrue(afterDelete.none { it.id == insertedId })
  }

  @Test
  fun `verify UsbDrive model capacity and status properties`() {
    val drive = com.example.data.model.UsbDrive(
        id = "test_usb_1",
        name = "Corsair Flash Survivor 64GB",
        vendor = "Corsair",
        model = "Survivor Stealth",
        serialNumber = "CS20268899",
        capacityBytes = 64L * 1024 * 1024 * 1024,
        usedBytes = 16L * 1024 * 1024 * 1024,
        fsType = com.example.data.model.FileSystemType.EXFAT,
        partitionStyle = com.example.data.model.PartitionStyle.GPT,
        isVentoyInstalled = false,
        installedVentoyVersion = "None",
        mountPath = "/storage/TEST-0001",
        status = com.example.data.model.UsbDriveStatus.READY_FOR_VENTOY,
        clusterAlignmentKb = 128
    )

    val totalGb = drive.capacityBytes / (1024 * 1024 * 1024)
    val usedGb = drive.usedBytes / (1024 * 1024 * 1024)
    val freeGb = totalGb - usedGb

    assertEquals(64L, totalGb)
    assertEquals(16L, usedGb)
    assertEquals(48L, freeGb)
    assertEquals(com.example.data.model.UsbDriveStatus.READY_FOR_VENTOY, drive.status)
    assertEquals(com.example.data.model.FileSystemType.EXFAT, drive.fsType)
    assertEquals(com.example.data.model.PartitionStyle.GPT, drive.partitionStyle)
    assertEquals(128, drive.clusterAlignmentKb)
  }

  @Test
  fun `verify VentoyRepository drive listing selection and initialization`() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val db = VentoyDatabase.getDatabase(app)
    val repository = com.example.data.repository.VentoyRepository(
        context = app,
        isoDao = db.isoDao(),
        securityLogDao = db.securityLogDao()
    )

    // 1. Verify connected drives list has items
    val drives = repository.connectedDrives.value
    assertTrue("Should have default connected drives", drives.size >= 2)
    val firstDrive = drives[0]
    val secondDrive = drives[1]

    // 2. Drive selection
    repository.selectDrive(secondDrive.id)
    assertEquals(secondDrive.id, repository.usbDriveState.value.id)

    // 3. Drive ejection
    repository.ejectDrive(secondDrive.id)
    val driveAfterEject = repository.connectedDrives.value.find { it.id == secondDrive.id }
    assertNotNull(driveAfterEject)
    assertEquals(false, driveAfterEject?.isMounted)

    // 4. Drive initialization for Ventoy+ bootable media
    val initConfig = com.example.data.model.UsbInitializationConfig(
        driveId = secondDrive.id,
        partitionStyle = com.example.data.model.PartitionStyle.GPT,
        fileSystem = com.example.data.model.FileSystemType.EXFAT,
        clusterAlignmentKb = 128,
        secureBoot = true,
        reservedSpaceMb = 0
    )

    var lastProgress = 0
    repository.initializeDrive(initConfig) { pct, _ ->
      lastProgress = pct
    }

    assertEquals(100, lastProgress)
    val initializedDrive = repository.connectedDrives.value.find { it.id == secondDrive.id }
    assertNotNull(initializedDrive)
    assertTrue("Ventoy should be marked installed", initializedDrive!!.isVentoyInstalled)
    assertEquals("1.0.99-SECURE", initializedDrive.installedVentoyVersion)
    assertEquals(com.example.data.model.PartitionStyle.GPT, initializedDrive.partitionStyle)
    assertEquals(com.example.data.model.FileSystemType.EXFAT, initializedDrive.fsType)
    assertEquals(com.example.data.model.UsbDriveStatus.VENTOY_ACTIVE, initializedDrive.status)
    assertTrue(initializedDrive.isMounted)
  }
}

