package com.aurel.notes.ui.notebooks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.database.dao.NotebookWithCount
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.CardColorTokens
import com.aurel.notes.core.designsystem.components.AurelCard

@Composable
fun NotebooksScreen(
    onNotebookSelected: (Long) -> Unit = {},
    viewModel: NotebooksViewModel = viewModel()
) {
    val notebooks by viewModel.notebooksWithCount.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AurelTheme.spacing.screenHorizontal)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Notebooks",
                        style = AurelTheme.typography.headlineLarge,
                        color = AurelTheme.colors.primaryInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Curated spaces for ideas & projects",
                        style = AurelTheme.typography.bodySmall,
                        color = AurelTheme.colors.secondaryMuted
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Notebooks Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notebooks, key = { it.id }) { notebook ->
                    NotebookCard(
                        notebook = notebook,
                        onClick = { onNotebookSelected(notebook.id) },
                        onDeleteClick = { viewModel.deleteNotebook(notebook.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateNotebookDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, colorHex, iconName ->
                viewModel.createNotebook(name, colorHex, iconName)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun NotebookCard(
    notebook: NotebookWithCount,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(notebook.colorHex))
    } catch (_: Exception) {
        AurelTheme.colors.primaryAccent
    }

    val iconVector = when (notebook.iconName.lowercase()) {
        "work" -> Icons.Filled.Work
        "lightbulb", "ideas" -> Icons.Filled.Lightbulb
        "school", "learning" -> Icons.Filled.School
        "favorite", "personal" -> Icons.Filled.Favorite
        "star" -> Icons.Filled.Star
        else -> Icons.Filled.Book
    }

    AurelCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (!notebook.isDefault) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Notebook",
                            tint = AurelTheme.colors.secondaryMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = notebook.name,
                style = AurelTheme.typography.titleMedium,
                color = AurelTheme.colors.primaryInk,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${notebook.noteCount} ${if (notebook.noteCount == 1) "note" else "notes"}",
                style = AurelTheme.typography.labelSmall,
                color = AurelTheme.colors.secondaryMuted
            )
        }
    }
}

@Composable
private fun CreateNotebookDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#7564D8") }
    var selectedIcon by remember { mutableStateOf("book") }

    val colors = listOf(
        "#7564D8", "#4A9E68", "#E08D3C", "#3C82E0", "#D94848", "#8B5CF6"
    )

    val icons = listOf(
        "book" to Icons.Filled.Book,
        "work" to Icons.Filled.Work,
        "ideas" to Icons.Filled.Lightbulb,
        "learning" to Icons.Filled.School,
        "personal" to Icons.Filled.Favorite,
        "star" to Icons.Filled.Star
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Notebook") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Notebook Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Color", style = AurelTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = hex }
                                .then(
                                    if (selectedColor == hex) Modifier.border(2.dp, AurelTheme.colors.primaryInk, CircleShape) else Modifier
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Icon", style = AurelTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    icons.forEach { (key, vector) ->
                        IconButton(
                            onClick = { selectedIcon = key },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedIcon == key) AurelTheme.colors.primaryAccent.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                        ) {
                            Icon(
                                imageVector = vector,
                                contentDescription = key,
                                tint = if (selectedIcon == key) AurelTheme.colors.primaryAccent else AurelTheme.colors.secondaryMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name, selectedColor, selectedIcon) },
                colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent),
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
