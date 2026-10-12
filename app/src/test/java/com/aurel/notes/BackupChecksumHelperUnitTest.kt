package com.aurel.notes

import com.aurel.notes.core.backup.BackupChecksumHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupChecksumHelperUnitTest {

    @Test
    fun testSha256_computesConsistentDigest() {
        val payload = "{\"version\":1,\"notesCount\":10}"
        val hash1 = BackupChecksumHelper.computeSha256(payload)
        val hash2 = BackupChecksumHelper.computeSha256(payload)

        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length)
    }

    @Test
    fun testVerifyChecksum_matchesValidChecksum() {
        val payload = "Aurel backup test string"
        val hash = BackupChecksumHelper.computeSha256(payload)

        assertTrue(BackupChecksumHelper.verifyChecksum(payload, hash))
    }
}
