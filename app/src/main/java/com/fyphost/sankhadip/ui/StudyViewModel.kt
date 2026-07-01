package com.fyphost.sankhadip.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fyphost.sankhadip.SanKhadipApp
import com.fyphost.sankhadip.data.NodeEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as SanKhadipApp).repository

    val nodes: StateFlow<List<NodeEntity>> = repo.nodes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        viewModelScope.launch { repo.seedIfNeeded() }
    }

    fun addFolder(parentId: String?, name: String) = viewModelScope.launch {
        repo.addFolder(parentId, name)
    }

    fun addFiles(parentId: String, uris: List<Uri>) = viewModelScope.launch {
        uris.forEach { repo.addFile(parentId, it) }
    }

    fun rename(id: String, newName: String) = viewModelScope.launch {
        repo.rename(id, newName)
    }

    fun delete(id: String) = viewModelScope.launch {
        repo.delete(id)
    }
}
