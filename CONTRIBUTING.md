# Contributing to Aurel Notes

Thank you for your interest in contributing to Aurel! We welcome all contributions including bug reports, documentation updates, and new features.

## Development Workflow
1. Fork the repository and create your branch from `main`.
2. Ensure you have Android Studio (Koala/Ladybug or newer) and JDK 17+ installed.
3. Keep code compliant with Material 3 design and Jetpack Compose best practices.
4. Run `./gradlew testDebugUnitTest` to verify that all unit tests pass before opening a PR.

## Code Conventions
- Follow official Kotlin coding style guidelines.
- Use explicit descriptive names for Compose state and lambdas.
- Adhere to the Unidirectional Data Flow (UDF) pattern across all ViewModels.
