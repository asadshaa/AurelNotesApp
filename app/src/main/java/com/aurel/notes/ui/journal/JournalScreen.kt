package com.aurel.notes.ui.journal

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.CardColorTokens
import com.aurel.notes.core.designsystem.components.AurelCard

@Composable
fun JournalScreen(
    onNavigateBack: () -> Unit,
    onSavedOpenEditor: (Long) -> Unit,
    viewModel: JournalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background),
        contentPadding = PaddingValues(bottom = 64.dp)
    ) {
        // Top Toolbar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurelTheme.colors.primaryInk)
                }

                Button(
                    onClick = { viewModel.saveJournal { id -> onSavedOpenEditor(id) } },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Entry", style = AurelTheme.typography.labelMedium)
                }
            }
        }

        // Header Title
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AurelTheme.spacing.screenHorizontal)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "DAILY REFLECTION",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted
                )
                Text(
                    text = "Evening Journal",
                    style = AurelTheme.typography.displayMedium,
                    color = AurelTheme.colors.primaryInk
                )
            }
        }

        // 1. Mood Selector
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp)
            ) {
                Text(
                    text = "How does your inner world feel right now?",
                    style = AurelTheme.typography.titleMedium,
                    color = AurelTheme.colors.primaryInk
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    JournalMood.values().forEach { mood ->
                        val isSelected = uiState.selectedMood == mood
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectMood(mood) }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.divider,
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            color = if (isSelected) AurelTheme.colors.primaryAccent.copy(alpha = 0.12f) else AurelTheme.colors.cardSurface
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = mood.emoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mood.label,
                                    style = AurelTheme.typography.labelSmall,
                                    color = if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.secondaryMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Gratitude Prompt
        item {
            AurelCard(
                modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 12.dp),
                colorHex = CardColorTokens.PEACH
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Three Small Moments of Gratitude 🙏",
                        style = AurelTheme.typography.titleMedium,
                        color = AurelTheme.colors.primaryInk
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = uiState.gratitude1,
                        onValueChange = { viewModel.setGratitude1(it) },
                        placeholder = { Text("1. A peaceful cup of coffee...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                            focusedBorderColor = AurelTheme.colors.primaryAccent,
                            unfocusedBorderColor = AurelTheme.colors.divider
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.gratitude2,
                        onValueChange = { viewModel.setGratitude2(it) },
                        placeholder = { Text("2. Progress made on key work...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                            focusedBorderColor = AurelTheme.colors.primaryAccent,
                            unfocusedBorderColor = AurelTheme.colors.divider
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.gratitude3,
                        onValueChange = { viewModel.setGratitude3(it) },
                        placeholder = { Text("3. A kind gesture or moment of rest...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                            focusedBorderColor = AurelTheme.colors.primaryAccent,
                            unfocusedBorderColor = AurelTheme.colors.divider
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // 3. Rotating Question Bank & Reflection Body
        item {
            AurelCard(
                modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp),
                colorHex = CardColorTokens.SAGE
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AurelTheme.colors.primaryAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REFLECTION PROMPT",
                                style = AurelTheme.typography.labelSmall,
                                color = AurelTheme.colors.primaryAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { viewModel.shufflePrompt() }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Shuffle, contentDescription = "Shuffle Prompt", tint = AurelTheme.colors.primaryAccent, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"${uiState.reflectionPrompt}\"",
                        style = AurelTheme.typography.titleMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = AurelTheme.colors.primaryInk
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = uiState.reflectionBody,
                        onValueChange = { viewModel.setReflectionBody(it) },
                        placeholder = { Text("Write freely without judgment...") },
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                            focusedBorderColor = AurelTheme.colors.primaryAccent,
                            unfocusedBorderColor = AurelTheme.colors.divider
                        )
                    )
                }
            }
        }
    }
}
