package com.aurel.notes.core.backup

import java.security.MessageDigest

object BackupChecksumHelper {
    fun computeSha256(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(content.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun verifyChecksum(content: String, expectedChecksum: String): Boolean {
        val actual = computeSha256(content)
        return actual.equals(expectedChecksum.trim(), ignoreCase = true)
    }
}
