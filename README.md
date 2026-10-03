# Aurel 🌿 — Local-First Notes & Thinking Space

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room%20DB-FTS4%20Search-brightgreen.svg)](https://developer.android.com/training/data-storage/room)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Aurel** is a quiet, editorial, local-first thinking space and personal knowledge garden built with 100% Jetpack Compose and modern Android architecture. It combines distraction-free writing, bi-directional backlinks, offline calendar scheduling, drawing canvas, encrypted vault notes, and voice memos.

---

## ✨ Features
- **Local-First & Private:** Fully offline Room SQLite database with full-text search (FTS4). No external tracking.
- **Bi-Directional Wiki Links:** Connect thoughts with `[[Wiki-Style Links]]` and explore live backlinks.
- **Biometric Vault Notes:** Hardware-backed `AndroidKeyStore` AES-GCM encryption with fingerprint unlock and `FLAG_SECURE` screen protection.
- **Rich Formatting Engine:** Markdown parser with live shortcuts, code blocks, quote callouts, checklists, and template insertion.
- **Voice Memos & Canvas:** Audio recording with live waveform and a vector sketch drawing canvas.
- **Reminders & Calendar:** Offline monthly agenda with exact `AlarmManager` reminders that persist across device reboots.
- **Adaptive UI:** Responsive layout that seamlessly adapts across phones, foldables, and tablets with dynamic particles background.
