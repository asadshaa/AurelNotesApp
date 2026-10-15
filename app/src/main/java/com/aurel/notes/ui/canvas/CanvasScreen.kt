package com.aurel.notes.ui.canvas

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme
import java.io.File
import java.io.FileOutputStream

data class DrawingPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun CanvasScreen(
    onNavigateBack: () -> Unit,
    onSavedDrawing: (String) -> Unit
) {
    val context = LocalContext.current
    val paths = remember { mutableStateListOf<DrawingPath>() }
    val undonePaths = remember { mutableStateListOf<DrawingPath>() }

    var currentPath by remember { mutableStateOf<Path?>(null) }
    var selectedColor by remember { mutableStateOf(Color(0xFF25232B)) }
    var selectedStrokeWidth by remember { mutableStateOf(8f) }

    val palette = listOf(
        Color(0xFF25232B), // Ink
        Color(0xFF7564D8), // Violet
        Color(0xFF439C5E), // Sage
        Color(0xFFE08D3C), // Peach
        Color(0xFF4A8FD9), // Mist Blue
        Color(0xFFFFFFFF)  // Eraser
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF9F5))
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurelTheme.colors.primaryInk)
            }

            Text(
                text = "Sketch Canvas",
                style = AurelTheme.typography.titleMedium,
                color = AurelTheme.colors.primaryInk
            )

            Button(
                onClick = {
                    val savedFile = saveCanvasToPng(context, paths)
                    if (savedFile != null) {
                        onSavedDrawing(savedFile.absolutePath)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save", style = AurelTheme.typography.labelMedium)
            }
        }

        // Main Drawing Surface
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, AurelTheme.colors.divider, RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedColor, selectedStrokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val newPath = Path().apply { moveTo(offset.x, offset.y) }
                                currentPath = newPath
                            },
                            onDrag = { change, _ ->
                                currentPath?.lineTo(change.position.x, change.position.y)
                                // Trigger recomposition
                                currentPath = Path().apply {
                                    currentPath?.let { addPath(it) }
                                }
                            },
                            onDragEnd = {
                                currentPath?.let {
                                    paths.add(DrawingPath(it, selectedColor, selectedStrokeWidth))
                                    undonePaths.clear()
                                }
                                currentPath = null
                            },
                            onDragCancel = {
                                currentPath = null
                            }
                        )
                    }
            ) {
                // Render completed paths
                for (drawingPath in paths) {
                    drawPath(
                        path = drawingPath.path,
                        color = drawingPath.color,
                        style = Stroke(
                            width = drawingPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Render path currently being drawn
                currentPath?.let {
                    drawPath(
                        path = it,
                        color = selectedColor,
                        style = Stroke(
                            width = selectedStrokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        // Bottom Controls Bar (Color Palette, Stroke Width, Undo/Redo/Clear)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(18.dp),
            color = AurelTheme.colors.cardSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Color Palette row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        palette.forEach { color ->
                            val isSelected = selectedColor == color
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.divider,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = color }
                            )
                        }
                    }

                    // Undo, Redo, Clear
                    Row {
                        IconButton(
                            onClick = {
                                if (paths.isNotEmpty()) {
                                    undonePaths.add(paths.removeLast())
                                }
                            },
                            enabled = paths.isNotEmpty(),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                        }
                        IconButton(
                            onClick = {
                                if (undonePaths.isNotEmpty()) {
                                    paths.add(undonePaths.removeLast())
                                }
                            },
                            enabled = undonePaths.isNotEmpty(),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                        }
                        IconButton(
                            onClick = {
                                paths.clear()
                                undonePaths.clear()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Clear", tint = Color(0xFFD94848))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stroke Width Row (Fine, Medium, Broad)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stroke:", style = AurelTheme.typography.labelSmall, color = AurelTheme.colors.secondaryMuted)
                    StrokeWidthSelector(
                        label = "Fine",
                        width = 4f,
                        isSelected = selectedStrokeWidth == 4f,
                        onSelect = { selectedStrokeWidth = 4f }
                    )
                    StrokeWidthSelector(
                        label = "Medium",
                        width = 10f,
                        isSelected = selectedStrokeWidth == 10f,
                        onSelect = { selectedStrokeWidth = 10f }
                    )
                    StrokeWidthSelector(
                        label = "Broad",
                        width = 24f,
                        isSelected = selectedStrokeWidth == 24f,
                        onSelect = { selectedStrokeWidth = 24f }
                    )
                }
            }
        }
    }
}

@Composable
private fun StrokeWidthSelector(
    label: String,
    width: Float,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect),
        color = if (isSelected) AurelTheme.colors.primaryAccent.copy(alpha = 0.12f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.divider
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size((width / 2).coerceIn(4f, 16f).dp)
                    .clip(CircleShape)
                    .background(AurelTheme.colors.primaryInk)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = AurelTheme.typography.labelSmall,
                color = if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.primaryInk
            )
        }
    }
}

private fun saveCanvasToPng(context: Context, paths: List<DrawingPath>): File? {
    if (paths.isEmpty()) return null
    return try {
        val bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
        }

        for (item in paths) {
            paint.color = item.color.toArgb()
            paint.strokeWidth = item.strokeWidth * 1.5f
            canvas.drawPath(item.path.asAndroidPath(), paint)
        }

        val attachmentsDir = File(context.filesDir, "attachments").apply { mkdirs() }
        val file = File(attachmentsDir, "drawing_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream)
        stream.flush()
        stream.close()
        file
    } catch (_: Exception) {
        null
    }
}
