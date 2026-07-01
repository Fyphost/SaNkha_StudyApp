package com.fyphost.sankhadip.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class NodeRepository(
    private val context: Context,
    private val dao: NodeDao,
) {
    val nodes: Flow<List<NodeEntity>> = dao.observeAll()

    private val filesDir: File
        get() = File(context.filesDir, "files").apply { mkdirs() }

    /** Seed any missing subject folders (safe to run on upgrades). */
    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val existingSubjectNames = dao.getAll()
            .filter { it.isSubject }
            .map { it.name }
            .toSet()
        val now = System.currentTimeMillis()
        val missing = SUBJECTS
            .filter { it.name !in existingSubjectNames }
            .map { s ->
                NodeEntity(
                    id = UUID.randomUUID().toString(),
                    parentId = null,
                    name = s.name,
                    isFolder = true,
                    isSubject = true,
                    colorArgb = s.colorArgb,
                    icon = s.icon,
                    createdAt = now,
                    updatedAt = now,
                )
            }
        if (missing.isNotEmpty()) dao.upsertAll(missing)
    }

    suspend fun addFolder(parentId: String?, name: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.upsert(
            NodeEntity(
                id = UUID.randomUUID().toString(),
                parentId = parentId,
                name = name.trim().ifBlank { "New Folder" },
                isFolder = true,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    /** Copy a picked file into internal storage and record it under [parentId]. */
    suspend fun addFile(parentId: String, uri: Uri) = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        var displayName = "file"
        var size = 0L
        resolver.query(uri, null, null, null, null)?.use { c ->
            val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIdx = c.getColumnIndex(OpenableColumns.SIZE)
            if (c.moveToFirst()) {
                if (nameIdx >= 0) displayName = c.getString(nameIdx) ?: displayName
                if (sizeIdx >= 0 && !c.isNull(sizeIdx)) size = c.getLong(sizeIdx)
            }
        }
        val mime = resolver.getType(uri) ?: "application/octet-stream"
        val storedName = "${UUID.randomUUID()}_$displayName"
        val dest = File(filesDir, storedName)
        resolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        }
        if (size == 0L) size = dest.length()
        val now = System.currentTimeMillis()
        dao.upsert(
            NodeEntity(
                id = UUID.randomUUID().toString(),
                parentId = parentId,
                name = displayName,
                isFolder = false,
                filePath = dest.absolutePath,
                mime = mime,
                size = size,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    suspend fun rename(id: String, newName: String) = withContext(Dispatchers.IO) {
        val node = dao.getById(id) ?: return@withContext
        val clean = newName.trim()
        if (clean.isEmpty()) return@withContext
        dao.upsert(node.copy(name = clean, updatedAt = System.currentTimeMillis()))
    }

    /** Recursively delete a node, its descendants, and any backing files. */
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val all = dao.getAll()
        val byParent = all.groupBy { it.parentId }
        val toDelete = mutableListOf<NodeEntity>()
        fun collect(nodeId: String) {
            val node = all.find { it.id == nodeId } ?: return
            toDelete += node
            byParent[nodeId]?.forEach { collect(it.id) }
        }
        collect(id)
        toDelete.forEach { n -> n.filePath?.let { runCatching { File(it).delete() } } }
        dao.deleteByIds(toDelete.map { it.id })
    }
}
