package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.IsoFile
import com.example.data.local.VentoyDatabase
import com.example.data.network.OsMetadataApiClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OsMetadataApiClientTest {

    @Test
    fun `verify ISO filename cleaning to search query`() {
        val client = OsMetadataApiClient.getInstance()

        assertEquals("Windows 11", client.cleanIsoFilenameToQuery("Win11_24H2_English_x64.iso"))
        assertEquals("Ubuntu Server", client.cleanIsoFilenameToQuery("ubuntu-24.04-live-server-amd64.iso"))
        assertEquals("Kali Linux", client.cleanIsoFilenameToQuery("kali-linux-2024.3-live-amd64.iso"))
        assertEquals("Clonezilla", client.cleanIsoFilenameToQuery("clonezilla-live-3.1.2-22-amd64.iso"))
        assertEquals("Proxmox Virtual Environment", client.cleanIsoFilenameToQuery("proxmox-ve_8.2-1.iso"))
        assertEquals("Tails (operating system)", client.cleanIsoFilenameToQuery("tails-amd64-6.8.img"))
    }

    @Test
    fun `verify category inference from OS title and description`() {
        val client = OsMetadataApiClient.getInstance()

        val secCat = client.inferCategory("Kali Linux", "Advanced penetration testing and security auditing distribution.")
        assertEquals("Security & Forensics", secCat)

        val recCat = client.inferCategory("Clonezilla Live", "Partition and disk imaging/cloning program.")
        assertEquals("Recovery & Diagnostics", recCat)

        val hypCat = client.inferCategory("Proxmox VE", "Complete open-source platform for enterprise virtualization.")
        assertEquals("Virtualization & Hypervisors", hypCat)

        val winCat = client.inferCategory("Windows 11", "Microsoft Windows operating system with UEFI SecureBoot.")
        assertEquals("Microsoft Windows", winCat)
    }

    @Test
    fun `verify searchOsMetadata and autoPopulateMetadataForIso`() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = VentoyDatabase.getDatabase(app)
        val dao = db.isoFileDao()
        val client = OsMetadataApiClient.getInstance()

        val metaResult = client.searchOsMetadata("kali-linux-2024.3-live-amd64.iso").getOrThrow()
        assertNotNull(metaResult)
        assertEquals("Security & Forensics", metaResult.category)
        assertTrue(metaResult.description.isNotBlank())
        assertTrue(metaResult.title.contains("Kali", ignoreCase = true))

        // Insert an ISO without description
        val iso = IsoFile(
            path = "/storage/usb/ISOs/kali-linux-live.iso",
            filename = "kali-linux-live.iso",
            category = "Uncategorized",
            checksum = "dummyhash123",
            description = ""
        )
        val id = dao.insert(iso)
        val loaded = dao.getIsoFileById(id)!!

        // Auto-populate and persist metadata into database
        dao.updateCategoryAndDescription(
            id = loaded.id,
            category = metaResult.category,
            description = metaResult.description
        )

        // Verify updated category and description in DB
        val updated = dao.getIsoFileById(id)!!
        assertEquals("Security & Forensics", updated.category)
        assertTrue(updated.description.isNotBlank())
    }
}
