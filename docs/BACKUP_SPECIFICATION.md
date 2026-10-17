# Aurel Portable Backup Specification (v1.0)

Aurel backups are created as self-contained `.zip` archives:

```
aurel_backup_2026-10-19.zip
├── manifest.json
└── attachments/
    ├── img_1729312000.jpg
    ├── audio_1729312500.m4a
    └── sketch_1729313000.png
```

## Manifest JSON Schema
```json
{
  "version": 1,
  "appVersion": "1.0.0",
  "exportedAt": 1729313200000,
  "notesCount": 42,
  "tasksCount": 18,
  "notebooksCount": 5,
  "notes": [ ... ],
  "tasks": [ ... ],
  "notebooks": [ ... ]
}
```
