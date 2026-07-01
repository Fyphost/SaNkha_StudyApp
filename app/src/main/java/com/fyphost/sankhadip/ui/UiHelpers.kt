package com.fyphost.sankhadip.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.fyphost.sankhadip.data.NodeEntity
import java.io.File
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

val QUOTES = listOf(
    "It's not over until I win.",
    "Stay Focused \u2014 Keep Growing.",
    "Dream. Plan. Achieve.",
    "Believe in Yourself.",
    "Good Vibes, Good Life.",
    "Discipline beats motivation.",
    "One page at a time.",
)

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digit = (log10(bytes.toDouble()) / log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    val value = bytes / 1024.0.pow(digit.toDouble())
    return if (digit == 0) "$bytes B"
    else String.format(Locale.US, "%.1f %s", value, units[digit])
}

fun fileEmoji(mime: String?, name: String): String {
    val ext = name.substringAfterLast('.', "").lowercase(Locale.US)
    val m = mime ?: ""
    return when {
        m.startsWith("image/") -> "\uD83D\uDDBC\uFE0F"
        m.startsWith("video/") -> "\uD83C\uDFAC"
        m.startsWith("audio/") -> "\uD83C\uDFB5"
        m == "application/pdf" || ext == "pdf" -> "\uD83D\uDCC4"
        ext in listOf("doc", "docx") -> "\uD83D\uDCDD"
        ext in listOf("xls", "xlsx", "csv") -> "\uD83D\uDCCA"
        ext in listOf("ppt", "pptx") -> "\uD83D\uDCFD\uFE0F"
        ext in listOf("zip", "rar", "7z") -> "\uD83D\uDDDC\uFE0F"
        m.startsWith("text/") || ext in listOf("txt", "md") -> "\uD83D\uDCC3"
        else -> "\uD83D\uDCCE"
    }
}

/** Open a stored file with an external viewer via a FileProvider content URI. */
fun openFile(context: Context, node: NodeEntity) {
    val path = node.filePath ?: return
    val file = File(path)
    if (!file.exists()) {
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        return
    }
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, node.mime ?: "*/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(intent, "Open ${node.name}")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooser)
    } else {
        Toast.makeText(context, "No app can open this file", Toast.LENGTH_SHORT).show()
    }
}
