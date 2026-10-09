package com.aurel.notes.core.designsystem.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme

@Composable
fun FilterChipGroup(
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            FilterChip(
                selected = isSelected,
                onClick = { onItemSelected(if (isSelected) null else item) },
                label = { Text(item, style = AurelTheme.typography.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AurelTheme.colors.primaryAccent.copy(alpha = 0.15f),
                    selectedLabelColor = AurelTheme.colors.primaryAccent,
                    containerColor = AurelTheme.colors.cardSurface,
                    labelColor = AurelTheme.colors.secondaryMuted
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = AurelTheme.colors.divider,
                    selectedBorderColor = AurelTheme.colors.primaryAccent
                )
            )
        }
    }
}
