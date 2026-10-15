package com.aurel.notes.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.markdown.FormattingType

data class FormattingAction(
    val label: String,
    val type: FormattingType,
    val icon: ImageVector? = null,
    val isBoldText: Boolean = false
)

@Composable
fun FormattingToolbar(
    onApplyFormat: (FormattingType) -> Unit,
    onOpenTemplates: () -> Unit,
    onAttachClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        FormattingAction("H1", FormattingType.H1, isBoldText = true),
        FormattingAction("H2", FormattingType.H2, isBoldText = true),
        FormattingAction("H3", FormattingType.H3, isBoldText = true),
        FormattingAction("B", FormattingType.BOLD, isBoldText = true),
        FormattingAction("I", FormattingType.ITALIC),
        FormattingAction("~", FormattingType.STRIKETHROUGH),
        FormattingAction("` `", FormattingType.INLINE_CODE),
        FormattingAction("“ ”", FormattingType.QUOTE),
        FormattingAction("• List", FormattingType.BULLET_LIST),
        FormattingAction("1. List", FormattingType.NUMBERED_LIST),
        FormattingAction("☑ Task", FormattingType.CHECKLIST),
        FormattingAction("[[ Link ]]", FormattingType.WIKI_LINK),
        FormattingAction("{ } Code", FormattingType.CODE_BLOCK),
        FormattingAction("— Line", FormattingType.DIVIDER)
    )

    Surface(
        modifier = modifier.fillMaxWidth().height(48.dp),
        color = AurelTheme.colors.cardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Templates Quick Button
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AurelTheme.colors.primaryAccent.copy(alpha = 0.12f))
                        .clickable(onClick = onOpenTemplates)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Templates",
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.primaryAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Attachments Quick Button
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AurelTheme.colors.primaryAccent.copy(alpha = 0.08f))
                        .clickable(onClick = onAttachClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Attach",
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.primaryInk
                    )
                }
            }

            items(actions, key = { it.label }) { action ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (AurelTheme.colors.isDark) AurelTheme.colors.surface else androidx.compose.ui.graphics.Color(0xFFF1EEE8))
                        .clickable { onApplyFormat(action.type) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = action.label,
                        style = AurelTheme.typography.labelSmall.copy(
                            fontWeight = if (action.isBoldText) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (action.type == FormattingType.ITALIC) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                        ),
                        color = AurelTheme.colors.primaryInk
                    )
                }
            }
        }
    }
}
