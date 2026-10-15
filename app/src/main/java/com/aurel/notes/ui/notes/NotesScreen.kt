package com.aurel.notes.ui.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.components.AurelCard
import com.aurel.notes.core.designsystem.components.AurelTagChip
import com.aurel.notes.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(
    onNavigateToEditor: (Long) -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background)
    ) {
        // Top Action Bar / Selection Bar
        if (uiState.isSelectionMode) {
            SelectionActionBar(
                selectedCount = uiState.selectedNoteIds.size,
                currentTab = uiState.currentTab,
                onCloseSelection = { viewModel.clearSelection() },
                onTrashSelected = { viewModel.batchMoveToTrash() },
                onRestoreSelected = { viewModel.batchRestore() }
            )
        } else {
            NotesTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                isGridLayout = uiState.isGridLayout,
                onToggleLayout = { viewModel.toggleLayout() },
                onSortClick = { showSortMenu = true }
            )

            // Sort Dropdown
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Last Updated (Newest)") },
                    onClick = {
                        viewModel.setSortOrder(NotesSortOrder.UPDATED_DESC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Created Date") },
                    onClick = {
                        viewModel.setSortOrder(NotesSortOrder.CREATED_DESC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Title (A-Z)") },
                    onClick = {
                        viewModel.setSortOrder(NotesSortOrder.TITLE_ASC)
                        showSortMenu = false
                    }
                )
            }
        }

        // Category Tabs (All, Pinned, Archived, Trash)
        NotesCategoryTabs(
            currentTab = uiState.currentTab,
            onTabSelected = { viewModel.setTab(it) }
        )

        // Tags & Notebook Filter Chips
        if (uiState.availableTags.isNotEmpty() || uiState.notebooks.isNotEmpty()) {
            FiltersHorizontalRow(
                tags = uiState.availableTags,
                selectedTag = uiState.selectedTag,
                onTagSelected = { viewModel.selectTag(it) },
                notebooks = uiState.notebooks,
                selectedNotebookId = uiState.selectedNotebookId,
                onNotebookSelected = { viewModel.selectNotebook(it) }
            )
        }

        // Trash Warning & Purge Header
        if (uiState.currentTab == NotesTab.TRASH && uiState.notes.isNotEmpty()) {
            TrashHeader(
                onEmptyTrashClick = { viewModel.emptyTrash() }
            )
        }

        // Main List / Grid
        if (uiState.notes.isEmpty()) {
            EmptyNotesPlaceholder(tab = uiState.currentTab, hasQuery = uiState.searchQuery.isNotBlank())
        } else {
            if (uiState.isGridLayout) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(uiState.notes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            isSelected = uiState.selectedNoteIds.contains(note.id),
                            isSelectionMode = uiState.isSelectionMode,
                            currentTab = uiState.currentTab,
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleNoteSelection(note.id)
                                } else {
                                    onNavigateToEditor(note.id)
                                }
                            },
                            onLongClick = {
                                viewModel.toggleNoteSelection(note.id)
                            },
                            onPinClick = { viewModel.togglePin(note) },
                            onArchiveClick = { viewModel.toggleArchive(note) },
                            onTrashClick = { viewModel.moveToTrash(note.id) },
                            onRestoreClick = { viewModel.restoreFromTrash(note.id) },
                            onPermanentDeleteClick = { viewModel.deletePermanently(note.id) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.notes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            isSelected = uiState.selectedNoteIds.contains(note.id),
                            isSelectionMode = uiState.isSelectionMode,
                            currentTab = uiState.currentTab,
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleNoteSelection(note.id)
                                } else {
                                    onNavigateToEditor(note.id)
                                }
                            },
                            onLongClick = {
                                viewModel.toggleNoteSelection(note.id)
                            },
                            onPinClick = { viewModel.togglePin(note) },
                            onArchiveClick = { viewModel.toggleArchive(note) },
                            onTrashClick = { viewModel.moveToTrash(note.id) },
                            onRestoreClick = { viewModel.restoreFromTrash(note.id) },
                            onPermanentDeleteClick = { viewModel.deletePermanently(note.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isGridLayout: Boolean,
    onToggleLayout: () -> Unit,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AurelTheme.spacing.screenHorizontal)
            .padding(top = 18.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Search notes, tags...", style = AurelTheme.typography.bodyMedium) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = AurelTheme.colors.secondaryMuted) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = AurelTheme.colors.secondaryMuted)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AurelTheme.colors.cardSurface,
                unfocusedContainerColor = AurelTheme.colors.cardSurface,
                focusedBorderColor = AurelTheme.colors.primaryAccent,
                unfocusedBorderColor = AurelTheme.colors.divider
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onToggleLayout) {
            Icon(
                imageVector = if (isGridLayout) Icons.Filled.ViewList else Icons.Filled.GridView,
                contentDescription = "Toggle Layout",
                tint = AurelTheme.colors.primaryInk
            )
        }

        IconButton(onClick = onSortClick) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Sort Notes",
                tint = AurelTheme.colors.primaryInk
            )
        }
    }
}

@Composable
private fun SelectionActionBar(
    selectedCount: Int,
    currentTab: NotesTab,
    onCloseSelection: () -> Unit,
    onTrashSelected: () -> Unit,
    onRestoreSelected: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        color = AurelTheme.colors.cardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCloseSelection) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$selectedCount selected",
                    style = AurelTheme.typography.titleMedium,
                    color = AurelTheme.colors.primaryInk
                )
            }

            Row {
                if (currentTab == NotesTab.TRASH) {
                    IconButton(onClick = onRestoreSelected) {
                        Icon(Icons.Filled.Restore, contentDescription = "Restore", tint = AurelTheme.colors.primaryAccent)
                    }
                } else {
                    IconButton(onClick = onTrashSelected) {
                        Icon(Icons.Filled.Delete, contentDescription = "Move to Trash", tint = Color(0xFFD94848))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesCategoryTabs(
    currentTab: NotesTab,
    onTabSelected: (NotesTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotesTab.values().forEach { tab ->
            val label = when (tab) {
                NotesTab.ALL -> "All"
                NotesTab.PINNED -> "Pinned"
                NotesTab.VAULT -> "Vault"
                NotesTab.ARCHIVED -> "Archived"
                NotesTab.TRASH -> "Trash"
            }
            AurelTagChip(
                text = label,
                isSelected = currentTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun FiltersHorizontalRow(
    tags: List<String>,
    selectedTag: String?,
    onTagSelected: (String?) -> Unit,
    notebooks: List<com.aurel.notes.domain.model.Notebook>,
    selectedNotebookId: Long?,
    onNotebookSelected: (Long?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags, key = { it }) { tag ->
            AurelTagChip(
                text = "#$tag",
                isSelected = selectedTag == tag,
                onClick = { onTagSelected(tag) }
            )
        }
    }
}

@Composable
private fun TrashHeader(
    onEmptyTrashClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFD94848).copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD94848).copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notes in Trash expire after 30 days.",
                style = AurelTheme.typography.bodySmall,
                color = AurelTheme.colors.primaryInk
            )
            Text(
                text = "Empty Trash",
                style = AurelTheme.typography.labelSmall,
                color = Color(0xFFD94848),
                modifier = Modifier.clickable(onClick = onEmptyTrashClick)
            )
        }
    }
}

@Composable
private fun NoteCardItem(
    note: Note,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    currentTab: NotesTab,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPinClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onTrashClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onPermanentDeleteClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(note.updatedAt))

    AurelCard(
        colorHex = note.cardColorHex,
        onClick = onClick,
        borderWidth = if (isSelected) 2.dp else 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onClick() },
                        colors = CheckboxDefaults.colors(checkedColor = AurelTheme.colors.primaryAccent)
                    )
                } else {
                    Text(
                        text = dateStr,
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.secondaryMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isVaultLocked) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Vault Locked",
                            tint = AurelTheme.colors.primaryAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (note.isPinned && currentTab != NotesTab.TRASH) {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "Pinned",
                            tint = AurelTheme.colors.primaryAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (note.title.isNotBlank()) note.title else "Untitled",
                style = AurelTheme.typography.titleMedium,
                color = AurelTheme.colors.primaryInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (note.plainTextSummary.isNotBlank()) note.plainTextSummary else note.content.take(160),
                style = AurelTheme.typography.bodySmall,
                color = AurelTheme.colors.secondaryMuted,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            if (note.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    note.tags.take(2).forEach { tag ->
                        Text(
                            text = "#$tag",
                            style = AurelTheme.typography.labelSmall,
                            color = AurelTheme.colors.primaryAccent
                        )
                    }
                }
            }

            if (currentTab == NotesTab.TRASH) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onRestoreClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Restore, contentDescription = "Restore", tint = AurelTheme.colors.primaryAccent, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onPermanentDeleteClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = "Delete", tint = Color(0xFFD94848), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNotesPlaceholder(tab: NotesTab, hasQuery: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when {
                    hasQuery -> "No notes matching your search"
                    tab == NotesTab.PINNED -> "No pinned notes yet"
                    tab == NotesTab.VAULT -> "Private Vault is empty"
                    tab == NotesTab.ARCHIVED -> "No archived notes"
                    tab == NotesTab.TRASH -> "Trash is empty"
                    else -> "No notes yet"
                },
                style = AurelTheme.typography.titleMedium,
                color = AurelTheme.colors.primaryInk
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when {
                    hasQuery -> "Try a different search keyword or tag."
                    tab == NotesTab.PINNED -> "Tap the pin icon in the editor to feature notes here."
                    tab == NotesTab.VAULT -> "Lock notes from the editor menu to secure them in the vault."
                    tab == NotesTab.TRASH -> "Items deleted will appear here for 30 days."
                    else -> "Tap the pen icon in the bottom bar to write your first note."
                },
                style = AurelTheme.typography.bodySmall,
                color = AurelTheme.colors.secondaryMuted
            )
        }
    }
}
