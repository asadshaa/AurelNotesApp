# Security Architecture & Cryptographic Design

Aurel implements a zero-knowledge local-first security model:

## 1. Hardware-Backed Vault Notes
- Vault notes are encrypted using AES-256 in Galois/Counter Mode (GCM).
- Master keys are generated inside the Android hardware-backed KeyStore (`AndroidKeyStore`).
- Symmetric initialization vectors (IVs) are uniquely generated per encryption cycle and stored alongside ciphertext.

## 2. Memory Protection & Screen Privacy
- When Privacy Mode is enabled, `FLAG_SECURE` prevents Android system screenshots and blanks out the app thumbnail in the Recents App Switcher.
- Decrypted vault content is purged from Compose state when the note is closed.

## 3. Biometric Authentication
- Implements `BiometricPrompt` with `BIOMETRIC_STRONG` crypto-object binding.
