# Aurel — Architecture & System Design Blueprint

## 1. Audited Repository State
- **Workspace Path:** `c:\Intern\MyMobileApps\MyNotesApp`
- **Android SDK Path:** `C:\Users\user\AppData\Local\Android\Sdk` (Platforms: `android-34`, `android-37.0`, `android-37.1`; Build tools: `34.0.0`, `36.0.0`)
- **JDK:** OpenJDK 17 (`Microsoft.OpenJDK.17`)
- **Target Platform:** Android 14 (API 34) / minSdk 26
- **Architecture Pattern:** Clean Modular Single-App Architecture (MVI / Unidirectional Data Flow with Jetpack Compose, Room FTS4, Kotlin Coroutines & StateFlow).

## 2. Package Structure (`app/src/main/java/com/aurel/notes/`)
```
com.aurel.notes/
├── AurelApplication.kt          # Application class initializing notifications, WorkManager & encryption
├── MainActivity.kt              # Single Activity host with Edge-to-Edge & Biometric unlock
├── core/
│   ├── database/                # Room DB, DAOs, Entities, FTS4, TypeConverters, Migrations
│   │   ├── AurelDatabase.kt
│   │   ├── dao/                 # NoteDao, NotebookDao, TaskDao, AttachmentDao, CrossRefDao
│   │   ├── entity/              # NoteEntity, NoteFtsEntity, NotebookEntity, TaskEntity, AttachmentEntity, NoteCrossRefEntity
│   │   └── converter/           # Room type converters (Date, List<String>, etc.)
│   ├── datastore/               # UserPreferences DataStore (theme, font, security, layout settings)
│   ├── designsystem/            # AurelTheme, Color tokens, Typography, Shapes, Elevation, Dynamic Motion
│   │   ├── Color.kt             # Light warm paper, Dark, AMOLED, Pastel card tokens
│   │   ├── Type.kt              # Editorial Serif, Sans, Monospace typography tokens
│   │   ├── Theme.kt             # AurelTheme CompositionLocals
│   │   └── components/          # Reusable tokens: AurelCard, AurelButton, AurelTagChip, etc.
│   ├── security/                # AndroidKeyStore AES-GCM Vault encryption helper & Biometric prompt manager
│   ├── scheduler/               # Exact AlarmManager scheduler, BroadcastReceivers (Alarm, Boot, Actions)
│   ├── backup/                  # Zip portable export/import, JSON schema validation, SAF helpers
│   └── export/                  # Markdown, Plain Text, HTML, and Android PdfDocument generators
├── domain/                      # Repository interfaces and domain models
│   ├── model/                   # Note, Notebook, Task, Attachment, NoteCrossRef, Reminder
│   └── repository/              # NoteRepository, NotebookRepository, TaskRepository, UserPreferencesRepository
├── data/                        # Repository implementations & data sources
│   └── repository/              # NoteRepositoryImpl, NotebookRepositoryImpl, TaskRepositoryImpl, PreferencesRepositoryImpl
└── ui/                          # Presentation layer (Jetpack Compose)
    ├── navigation/              # NavGraph, AurelDestinations, Adaptive Navigation Shell (BottomBar + NavigationRail)
    ├── home/                    # Signature dashboard, quick capture, recent cards, streak, task progress
    ├── notes/                   # All notes grid/list, search snippets, batch selection, sorting, filters
    ├── editor/                  # Markdown-backed rich editor, formatting toolbar, wiki-links [[...]], attachments, focus timer
    ├── notebooks/               # Notebook management, color picker, notes count, nesting
    ├── tasks/                   # Tasks screen (Today, Upcoming, Overdue, Completed, Priority, Recurrence)
    ├── calendar/                # Offline monthly calendar & daily agenda linking notes and tasks
    ├── journal/                 # Daily reflection, mood tracker, prompts, reflection templates
    ├── canvas/                  # Vector drawing canvas with ink palette, stroke width, PNG export
    ├── voice/                   # Audio recorder/player with live amplitude and timer
    └── settings/                # Appearance, Fonts, Vault/Biometrics, Backup/Restore, Data management
```

## 3. Database Schema (`AurelDatabase`)
- **`notes` table**:
  - `id`: `Long` (Primary Key, AutoGenerate)
  - `uuid`: `String` (UUID for portable backup & cross-linking)
  - `title`: `String`
  - `content`: `String` (Markdown text / structured content)
  - `plain_text_summary`: `String`
  - `notebook_id`: `Long?` (Foreign Key -> `notebooks.id` ON DELETE SET NULL)
  - `card_color_hex`: `String` (Soft pastel token)
  - `is_pinned`: `Boolean`
  - `is_archived`: `Boolean`
  - `is_trashed`: `Boolean`
  - `trashed_at`: `Long?` (Timestamp for 30-day auto-purge)
  - `is_vault_locked`: `Boolean` (Requires biometric/PIN; content encrypted with AES-GCM)
  - `iv_hex`: `String?` (Initialization vector for AES-GCM vault notes)
  - `tags`: `String` (Comma-separated list or JSON array)
  - `created_at`: `Long`
  - `updated_at`: `Long`
- **`notes_fts` table** (`@Fts4` virtual table):
  - `rowid`: Integer Primary Key
  - `title`: Text
  - `content`: Text
  - `tags`: Text
- **`notebooks` table**:
  - `id`: `Long` (Primary Key, AutoGenerate)
  - `name`: `String`
  - `color_hex`: `String`
  - `icon_name`: `String`
  - `is_default`: `Boolean`
  - `created_at`: `Long`
- **`tasks` table**:
  - `id`: `Long` (Primary Key, AutoGenerate)
  - `parent_note_id`: `Long?` (Foreign Key -> `notes.id` ON DELETE CASCADE)
  - `title`: `String`
  - `is_completed`: `Boolean`
  - `priority`: `String` (LOW, MEDIUM, HIGH)
  - `due_date_time`: `Long?` (Epoch millis for exact reminder)
  - `reminder_enabled`: `Boolean`
  - `recurrence`: `String` (NONE, DAILY, WEEKLY, MONTHLY)
  - `completed_at`: `Long?`
  - `created_at`: `Long`
- **`attachments` table**:
  - `id`: `Long` (Primary Key, AutoGenerate)
  - `note_id`: `Long` (Foreign Key -> `notes.id` ON DELETE CASCADE)
  - `type`: `String` (IMAGE, AUDIO, DRAWING, FILE)
  - `file_path`: `String` (Relative to `context.filesDir/attachments/`)
  - `file_name`: `String`
  - `file_size_bytes`: `Long`
  - `duration_ms`: `Long?` (For audio)
  - `created_at`: `Long`
- **`note_cross_refs` table**:
  - `source_note_id`: `Long`
  - `target_note_id`: `Long`
  - `link_text`: `String`
  - Primary Key: `(source_note_id, target_note_id)`

## 4. Design Tokens & AurelTheme
- **Light Palette (Warm Editorial Paper):**
  - Warm Canvas: `#F8F6F1`
  - Clean Surface: `#FFFFFF`
  - Ink Text Primary: `#25232B`
  - Ink Text Secondary: `#777481`
  - Editorial Violet Accent: `#7564D8`
  - Card Pastels: Soft Lavender (`#EAE5FF`), Soft Sage (`#E1EDDF`), Soft Peach (`#F9E4D7`), Warm Sand (`#F5EBE1`), Mist Blue (`#E3EFF8`), Pure Paper (`#FFFFFF`)
  - Border/Divider: `#E9E5E0`
- **Dark Palette:** Surface `#141318`, Card `#1E1C24`, Text `#EDE9F5`, Subtext `#A29DB1`
- **AMOLED Palette:** Surface `#000000`, Card `#121116`, Pure Black power saving
- **Typography Tokens:**
  - Screen Titles / Display: Editorial Serif font family
  - UI / Body: Clean Geometric Sans font family
  - Code / Snippets: Monospace font family

## 5. Security, Scheduling & Data Portability
- **Vault Encryption:** Hardware-backed `AndroidKeyStore` AES-256 GCM (`AES/GCM/NoPadding`) cipher with randomized 12-byte IV and authenticated AEAD tag verification. Automatic fallback for pure JVM unit test runners.
- **Biometric App Lock:** `BiometricPrompt` supporting `BIOMETRIC_STRONG` and `DEVICE_CREDENTIAL` authentication for app entry and private vault unlock.
- **Privacy Mode:** Dynamic `WindowManager.LayoutParams.FLAG_SECURE` toggled through DataStore to blank out recent-apps screenshots.
- **Exact Reminders & Background Work:**
  - `AlarmManager.setExactAndAllowWhileIdle()` via `ExactAlarmScheduler` with custom notification actions (Complete, Snooze 15m, Snooze 1h).
  - `BOOT_COMPLETED` recovery receiver (`BootReceiver`) to restore active reminders after reboot.
  - `TrashCleanupWorker` periodic `WorkManager` job to auto-purge trashed notes older than 30 days.
- **Single-Note Exporters:**
  - Markdown (`.md`): Structured headers, YAML-like tags, and body.
  - Plain Text (`.txt`): Clean, portable text with divider metadata.
  - HTML (`.html`): Self-contained styled HTML5 document with inline Aurel editorial CSS.
  - PDF (`.pdf`): Multi-line formatted A4 document rendered via native Android `PdfDocument`.
- **Full Portable Backup & Restore:**
  - ZIP archive containing versioned `aurel_backup_v1.json` + `/attachments/` directory.
  - SHA-256 manifest integrity checksum and schema version verification.
  - Conflict resolution strategies: `KEEP_BOTH` (safe clone appending `(Restored)`) vs `REPLACE` (in-place entity overwrite).

## 6. Verification Gates & Quality Summary
- **Compilation:** Clean build with zero resource-linking or Kotlin compiler errors (`./gradlew assembleDebug` PASSED).
- **Unit & DAO Tests:** Full suite of 19 tests covering Room type converters, entity mapping, markdown formatting, wiki-links, templates, recurrence, vault AES-GCM encryption/decryption, tampered ciphertext detection, single-note exporters, and backup manifest schemas (`./gradlew testDebugUnitTest` PASSED).
- **Android Lint:** Strict lint pass with zero errors (`./gradlew lintDebug` PASSED).
- **Zero Cloud Guarantee:** Zero telemetry or analytics dependencies, no remote accounts or logins, zero network requests, 100% offline-first.
