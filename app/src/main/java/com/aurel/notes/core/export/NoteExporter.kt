package com.aurel.notes.core.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.aurel.notes.core.database.entity.NoteEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Handles exporting single notes to Markdown (.md), Plain Text (.txt), HTML (.html), and PDF (.pdf).
 */
object NoteExporter {

    private val dateFormatter = SimpleDateFormat("MMMM dd, yyyy · HH:mm", Locale.getDefault())

    fun toPlainText(note: NoteEntity): String {
        val dateStr = dateFormatter.format(Date(note.updatedAt))
        val sb = StringBuilder()
        sb.append(note.title.ifBlank { "Untitled Note" }).append("\n")
        sb.append("=".repeat(note.title.length.coerceAtLeast(15))).append("\n")
        sb.append("Last modified: ").append(dateStr).append("\n")
        if (note.tags.isNotEmpty()) {
            sb.append("Tags: ").append(note.tags.joinToString(", ")).append("\n")
        }
        sb.append("\n")
        sb.append(note.body)
        return sb.toString()
    }

    fun toMarkdown(note: NoteEntity): String {
        val dateStr = dateFormatter.format(Date(note.updatedAt))
        val sb = StringBuilder()
        sb.append("# ").append(note.title.ifBlank { "Untitled Note" }).append("\n\n")
        sb.append("> *Modified: ").append(dateStr).append("*\n")
        if (note.tags.isNotEmpty()) {
            sb.append("> *Tags: ").append(note.tags.joinToString(" ") { "#$it" }).append("*\n")
        }
        sb.append("\n---\n\n")
        sb.append(note.body).append("\n")
        return sb.toString()
    }

    fun toHtml(note: NoteEntity): String {
        val title = note.title.ifBlank { "Untitled Note" }
        val dateStr = dateFormatter.format(Date(note.updatedAt))
        val tagsHtml = if (note.tags.isNotEmpty()) {
            note.tags.joinToString(" ") {
                "<span style=\"display:inline-block;background:#EAE5FF;color:#4B3F8A;padding:2px 8px;border-radius:12px;font-size:12px;margin-right:4px;\">#$it</span>"
            }
        } else ""

        // Escape HTML entities and format simple Markdown constructs
        val escapedBody = escapeHtml(note.body)
        val formattedBody = formatBodyToHtml(escapedBody)

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$title</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        line-height: 1.65;
                        color: #25232B;
                        background-color: #F8F6F1;
                        margin: 0;
                        padding: 32px 16px;
                    }
                    .container {
                        max-width: 680px;
                        margin: 0 auto;
                        background: #FFFFFF;
                        padding: 40px;
                        border-radius: 16px;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
                        border: 1px solid #E9E5E0;
                    }
                    h1 {
                        font-family: Georgia, serif;
                        font-size: 28px;
                        margin-top: 0;
                        margin-bottom: 8px;
                        color: #141318;
                    }
                    .metadata {
                        font-size: 13px;
                        color: #777481;
                        margin-bottom: 24px;
                        padding-bottom: 16px;
                        border-bottom: 1px solid #E9E5E0;
                    }
                    .content {
                        font-size: 16px;
                        white-space: pre-wrap;
                        word-break: break-word;
                    }
                    blockquote {
                        border-left: 3px solid #7564D8;
                        margin: 16px 0;
                        padding: 8px 16px;
                        background: #F8F6FD;
                        color: #555;
                    }
                    code {
                        font-family: monospace;
                        background: #F0EDF5;
                        padding: 2px 6px;
                        border-radius: 4px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>$title</h1>
                    <div class="metadata">
                        <div>$dateStr</div>
                        ${if (tagsHtml.isNotEmpty()) "<div style=\"margin-top:8px;\">$tagsHtml</div>" else ""}
                    </div>
                    <div class="content">$formattedBody</div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun formatBodyToHtml(text: String): String {
        return text
            .replace(Regex("""\*\*(.*?)\*\*"""), "<strong>$1</strong>")
            .replace(Regex("""\*(.*?)\*"""), "<em>$1</em>")
            .replace(Regex("""~~(.*?)~~"""), "<del>$1</del>")
            .replace(Regex("""`([^`]+)`"""), "<code>$1</code>")
    }

    /**
     * Renders note as a multi-line formatted PDF using Android's native PdfDocument.
     */
    fun exportToPdf(note: NoteEntity, outputStream: OutputStream) {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 50f
        val contentWidth = pageWidth - (margin * 2)

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(0x25, 0x23, 0x2B)
            textSize = 22f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.rgb(0x77, 0x74, 0x81)
            textSize = 11f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.rgb(0x35, 0x33, 0x3B)
            textSize = 12f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(0xE9, 0xE5, 0xE0)
            strokeWidth = 1f
        }

        var y = margin + 30f

        // Draw Title
        val title = note.title.ifBlank { "Untitled Note" }
        canvas.drawText(title, margin, y, titlePaint)
        y += 24f

        // Draw Date metadata
        val dateStr = dateFormatter.format(Date(note.updatedAt))
        canvas.drawText("Last modified: $dateStr", margin, y, metaPaint)
        y += 18f

        // Draw Divider
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 24f

        // Draw Body Text with simple word wrapping
        val lines = note.body.split("\n")
        for (rawLine in lines) {
            if (y > pageHeight - margin) break // Stop if page bounds reached
            if (rawLine.isBlank()) {
                y += 14f
                continue
            }

            // Word wrap
            val words = rawLine.split(" ")
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val measuredWidth = bodyPaint.measureText(testLine)
                if (measuredWidth > contentWidth) {
                    canvas.drawText(currentLine, margin, y, bodyPaint)
                    y += 18f
                    if (y > pageHeight - margin) break
                    currentLine = word
                } else {
                    currentLine = testLine
                }
            }
            if (currentLine.isNotEmpty() && y <= pageHeight - margin) {
                canvas.drawText(currentLine, margin, y, bodyPaint)
                y += 18f
            }
        }

        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
    }
}
