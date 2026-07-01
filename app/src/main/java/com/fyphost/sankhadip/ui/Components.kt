package com.fyphost.sankhadip.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fyphost.sankhadip.R
import com.fyphost.sankhadip.data.NodeEntity
import com.fyphost.sankhadip.ui.theme.BorderBlue
import com.fyphost.sankhadip.ui.theme.NeonBlue
import com.fyphost.sankhadip.ui.theme.Panel
import com.fyphost.sankhadip.ui.theme.TextMuted

fun accentOf(node: NodeEntity, fallback: Color): Color =
    node.colorArgb?.let { Color(it) } ?: fallback

@Composable
fun LogoBadge(size: Int = 44) {
    Image(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = "SanKhadip logo",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(1.5.dp, NeonBlue, CircleShape),
    )
}

@Composable
fun ItemCard(
    node: NodeEntity,
    accent: Color,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    val locked = node.isFolder && node.isSubject
    var menuOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderBlue),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accent),
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val icon = if (node.isFolder) node.icon ?: "\uD83D\uDCC1"
                    else fileEmoji(node.mime, node.name)
                    Text(text = icon, fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = node.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (node.isFolder) "Folder" else formatBytes(node.size),
                        color = TextMuted,
                        fontSize = 11.sp,
                    )
                }
                if (!locked) {
                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Actions",
                                tint = TextMuted,
                            )
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                        ) {
                            if (!node.isFolder) {
                                DropdownMenuItem(
                                    text = { Text("Open") },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, null) },
                                    onClick = { menuOpen = false; onOpen() },
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Rename") },
                                leadingIcon = { Icon(Icons.Default.Edit, null) },
                                onClick = { menuOpen = false; onRename() },
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = { Icon(Icons.Default.Delete, null) },
                                onClick = { menuOpen = false; onDelete() },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputDialog(
    title: String,
    label: String,
    initial: String = "",
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(label) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (value.isNotBlank()) onConfirm(value.trim()) },
                enabled = value.isNotBlank(),
            ) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
fun ConfirmDeleteDialog(
    node: NodeEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Folder, null) },
        title = { Text("Delete ${if (node.isFolder) "folder" else "file"}?") },
        text = {
            Text(
                "Delete \"${node.name}\"" +
                    if (node.isFolder) " and everything inside it? This cannot be undone."
                    else "? This cannot be undone.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
