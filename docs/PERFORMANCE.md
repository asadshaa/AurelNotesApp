# Aurel Performance & Recomposition Optimization

To guarantee 120Hz smooth scrolling across large note libraries:

1. **Stable Keys in Lazy Layouts:** All `LazyColumn` and `LazyVerticalStaggeredGrid` items provide unique persistent keys (`key = { it.id }`).
2. **Immutable UI Models:** All data classes passed into Compose slots are marked with `@Immutable`.
3. **Derived State:** Computed values (e.g., search filtering, completed task count) leverage `remember { derivedStateOf { ... } }` to avoid redundant recomposition passes.
4. **Room Indexing:** Compound indexes on `(is_trashed, updated_at DESC)` and `(notebook_id)` prevent full-table scans.
