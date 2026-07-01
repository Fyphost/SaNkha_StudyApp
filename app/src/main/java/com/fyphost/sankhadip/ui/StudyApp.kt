package com.fyphost.sankhadip.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fyphost.sankhadip.data.NodeEntity
import com.fyphost.sankhadip.ui.theme.Bg
import com.fyphost.sankhadip.ui.theme.Bg2
import com.fyphost.sankhadip.ui.theme.NeonBlue
import com.fyphost.sankhadip.ui.theme.NeonBlueBright
import com.fyphost.sankhadip.ui.theme.TextMuted
import kotlinx.coroutines.delay

private sealed interface UiDialog {
    data class NewFolder(val parentId: String?) : UiDialog
    data class Rename(val node: NodeEntity) : UiDialog
    data class Delete(val node: NodeEntity) : UiDialog
}

@Composable
fun StudyApp(vm: StudyViewModel = viewModel()) {
    val context = LocalContext.current
    val nodes by vm.nodes.collectAsStateWithLifecycle()

    var currentId by remember { mutableStateOf<String?>(null) }
    var search by remember { mutableStateOf("") }
    var dialog by remember { mutableStateOf<UiDialog?>(null) }

    // Rotating motivational quote
    var quoteIdx by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            quoteIdx = (quoteIdx + 1) % QUOTES.size
        }
    }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents(),
    ) { uris ->
        val parent = currentId
        if (parent != null && uris.isNotEmpty()) vm.addFiles(parent, uris)
    }

    // Derived tree data
    fun childrenOf(parentId: String?): List<NodeEntity> =
        nodes.filter { it.parentId == parentId }
            .sortedWith(compareByDescending<NodeEntity> { it.isFolder }.thenBy { it.name.lowercase() })

    fun pathTo(id: String?): List<NodeEntity> {
        val path = mutableListOf<NodeEntity>()
        var cur = id?.let { cid -> nodes.find { it.id == cid } }
        while (cur != null) {
            path.add(0, cur)
            val parentId = cur.parentId
            cur = parentId?.let { pid -> nodes.find { it.id == pid } }
        }
        return path
    }

    val path = pathTo(currentId)
    val current = currentId?.let { cid -> nodes.find { it.id == cid } }
    val subjectAccent = path.firstOrNull()?.colorArgb?.let { Color(it) } ?: NeonBlue
    val visible = childrenOf(currentId).let { list ->
        if (search.isBlank()) list
        else list.filter { it.name.contains(search, ignoreCase = true) }
    }

    BackHandler(enabled = currentId != null) {
        currentId = current?.parentId
        search = ""
    }

    Scaffold(containerColor = Bg) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            Header(quote = QUOTES[quoteIdx])

            // Breadcrumbs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Crumb("Home", onClick = { currentId = null; search = "" })
                path.forEach { p ->
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp),
                    )
                    Crumb(p.name, onClick = { currentId = p.id; search = "" })
                }
            }

            // Title + actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = current?.name ?: "Your Subjects",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (currentId != null) {
                    OutlinedButton(
                        onClick = { filePicker.launch("*/*") },
                    ) {
                        Icon(Icons.Default.UploadFile, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Upload")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { dialog = UiDialog.NewFolder(currentId) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    ) {
                        Icon(Icons.Default.CreateNewFolder, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Folder")
                    }
                }
            }

            // Search (inside a folder)
            if (currentId != null) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search in this folder\u2026") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            if (visible.isEmpty()) {
                EmptyState(
                    inFolder = currentId != null,
                    onUpload = { filePicker.launch("*/*") },
                    onNewFolder = { dialog = UiDialog.NewFolder(currentId) },
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 108.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(visible, key = { it.id }) { node ->
                        ItemCard(
                            node = node,
                            accent = accentOf(node, subjectAccent),
                            onOpen = {
                                if (node.isFolder) {
                                    currentId = node.id
                                    search = ""
                                } else {
                                    openFile(context, node)
                                }
                            },
                            onRename = { dialog = UiDialog.Rename(node) },
                            onDelete = { dialog = UiDialog.Delete(node) },
                        )
                    }
                }
            }
        }
    }

    when (val d = dialog) {
        is UiDialog.NewFolder -> InputDialog(
            title = "New folder",
            label = "Folder name",
            confirmText = "Create",
            onConfirm = { name -> vm.addFolder(d.parentId, name); dialog = null },
            onDismiss = { dialog = null },
        )
        is UiDialog.Rename -> InputDialog(
            title = "Rename ${if (d.node.isFolder) "folder" else "file"}",
            label = "New name",
            initial = d.node.name,
            confirmText = "Rename",
            onConfirm = { name -> vm.rename(d.node.id, name); dialog = null },
            onDismiss = { dialog = null },
        )
        is UiDialog.Delete -> ConfirmDeleteDialog(
            node = d.node,
            onConfirm = { vm.delete(d.node.id); dialog = null },
            onDismiss = { dialog = null },
        )
        null -> {}
    }
}

@Composable
private fun Header(quote: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Bg2)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LogoBadge(size = 44)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "SanKhadip",
                color = NeonBlueBright,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = quote,
                color = NeonBlue,
                fontSize = 12.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun Crumb(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
        Text(label, color = TextMuted, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
private fun EmptyState(
    inFolder: Boolean,
    onUpload: () -> Unit,
    onNewFolder: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (inFolder) "\uD83D\uDCED" else "\uD83D\uDCDA", fontSize = 56.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (inFolder) "This folder is empty."
                else "Pick a subject to start studying.",
                color = TextMuted,
                fontSize = 15.sp,
            )
            if (inFolder) {
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onUpload) { Text("Upload files") }
                    Button(
                        onClick = onNewFolder,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    ) { Text("New folder") }
                }
            }
        }
    }
}
