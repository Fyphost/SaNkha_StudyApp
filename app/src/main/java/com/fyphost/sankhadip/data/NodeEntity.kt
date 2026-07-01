package com.fyphost.sankhadip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single node in the study tree. A node is either a folder or a file.
 * The five subject folders have [isSubject] = true and cannot be deleted or renamed.
 */
@Entity(tableName = "nodes")
data class NodeEntity(
    @PrimaryKey val id: String,
    val parentId: String?,
    val name: String,
    val isFolder: Boolean,
    val isSubject: Boolean = false,
    val colorArgb: Long? = null,
    val icon: String? = null,
    val filePath: String? = null,
    val mime: String? = null,
    val size: Long = 0,
    val createdAt: Long,
    val updatedAt: Long,
)
