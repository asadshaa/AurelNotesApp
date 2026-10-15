package com.aurel.notes

import com.aurel.notes.core.database.entity.NoteEntity
import com.aurel.notes.core.export.NoteExporter
import com.aurel.notes.core.security.VaultEncryptionHelper
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import javax.crypto.KeyGenerator

class SecurityAndPortabilityUnitTest {

    @Test
    fun vaultEncryption_roundTrip_encryptsAndDecryptsAccurately() {
        // Generate pure JVM AES key for testing
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()

        val helper = VaultEncryptionHelper(customSecretKey = secretKey)
        val originalText = "Top secret personal notes & cryptographic seed words 12345"

        val encrypted = helper.encrypt(originalText)
        assertTrue(encrypted.startsWith(VaultEncryptionHelper.VAULT_PREFIX))
        assertNotEquals(originalText, encrypted)

        val decrypted = helper.decrypt(encrypted)
        assertEquals(originalText, decrypted)
    }

    @Test
    fun vaultEncryption_tamperedPayload_failsAuthentication() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()

        val helper = VaultEncryptionHelper(customSecretKey = secretKey)
        val originalText = "Sensitive financial records"
        val encrypted = helper.encrypt(originalText)

        // Tamper with base64 payload
        val tampered = encrypted.substring(0, encrypted.length - 2) + "AB"
        try {
            helper.decrypt(tampered)
            fail("Expected exception on tampered ciphertext auth tag verification")
        } catch (_: Exception) {
            // Success: AEAD auth tag verification caught the tampering
        }
    }

    @Test
    fun noteExporter_markdownExport_formatsHeadersAndTagsCorrectly() {
        val note = NoteEntity(
            id = 1L,
            title = "Aurel Architecture Plan",
            content = "## Component Design\nLocal-first Room database.",
            tags = listOf("architecture", "offline-first"),
            updatedAt = 1729000000000L
        )

        val markdown = NoteExporter.toMarkdown(note)
        assertTrue(markdown.contains("# Aurel Architecture Plan"))
        assertTrue(markdown.contains("#architecture"))
        assertTrue(markdown.contains("#offline-first"))
        assertTrue(markdown.contains("## Component Design"))
        assertTrue(markdown.contains("Local-first Room database."))
    }

    @Test
    fun noteExporter_plainTextExport_formatsCleanPlainText() {
        val note = NoteEntity(
            id = 2L,
            title = "Grocery List",
            content = "- Milk\n- Organic Eggs\n- Sourdough Bread",
            tags = listOf("shopping"),
            updatedAt = 1729000000000L
        )

        val plainText = NoteExporter.toPlainText(note)
        assertTrue(plainText.startsWith("Grocery List\n"))
        assertTrue(plainText.contains("Tags: shopping"))
        assertTrue(plainText.contains("- Milk"))
        assertTrue(plainText.contains("- Sourdough Bread"))
    }

    @Test
    fun noteExporter_htmlExport_generatesValidHtmlDocument() {
        val note = NoteEntity(
            id = 3L,
            title = "Deep Work Session",
            content = "**Important:** Complete Phase 6 implementation.\n`val status = OK`",
            tags = listOf("focus", "productivity"),
            updatedAt = 1729000000000L
        )

        val html = NoteExporter.toHtml(note)
        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("<title>Deep Work Session</title>"))
        assertTrue(html.contains("<h1>Deep Work Session</h1>"))
        assertTrue(html.contains("<strong>Important:</strong>"))
        assertTrue(html.contains("<code>val status = OK</code>"))
        assertTrue(html.contains("#focus"))
    }

    @Test
    fun backupManifest_schemaValidation_correctStructure() {
        val manifest = JSONObject().apply {
            put("version", 1)
            put("app", "Aurel")
            put("created_at", System.currentTimeMillis())
            put("notes", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 101L)
                    put("title", "Backed up note")
                    put("content", "Safe and secure")
                })
            })
            put("notebooks", JSONArray())
            put("tasks", JSONArray())
            put("attachments", JSONArray())
        }

        assertEquals(1, manifest.getInt("version"))
        assertEquals("Aurel", manifest.getString("app"))
        assertEquals(1, manifest.getJSONArray("notes").length())
        assertEquals("Backed up note", manifest.getJSONArray("notes").getJSONObject(0).getString("title"))
    }
}
