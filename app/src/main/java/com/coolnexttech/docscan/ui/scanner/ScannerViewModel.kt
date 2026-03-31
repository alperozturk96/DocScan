package com.coolnexttech.docscan.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.docscan.ui.scanner.model.Doc
import com.coolnexttech.docscan.ui.scanner.model.SortOptions
import com.coolnexttech.docscan.util.Storage
import com.coolnexttech.docscan.util.extensions.sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerViewModel: ViewModel() {

    private var _docs: List<Doc> = listOf()

    private var searchJob: Job? = null

    private val _filteredDocs: MutableStateFlow<List<Doc>?> = MutableStateFlow(null)
    val filteredDocs: StateFlow<List<Doc>?> = _filteredDocs

    fun readDocs(onCompleted: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Storage.readDocs()
            _docs = result
            _filteredDocs.update {
                result
            }

            launch(Dispatchers.Main) {
                onCompleted()
            }
        }
    }

    fun sort(sortOption: SortOptions) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = withContext(Dispatchers.IO) {
                _filteredDocs.value?.sort(sortOption)
            }

            if (result?.isNotEmpty() == true) {
                _filteredDocs.update {
                    result
                }
            }
        }
    }

    fun search(text: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                if (text.isBlank()) {
                    _docs
                } else {
                    val query = text.lowercase().trim()
                    _docs.filter { it.filename.lowercase().contains(query) }
                }
            }
            _filteredDocs.update {
                result
            }
        }
    }
}
