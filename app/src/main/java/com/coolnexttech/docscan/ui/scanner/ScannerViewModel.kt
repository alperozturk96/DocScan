package com.coolnexttech.docscan.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.docscan.util.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerViewModel: ViewModel() {

    private val _docs: ArrayList<Doc> = arrayListOf()

    private val _filteredDocs: MutableStateFlow<List<Doc>?> = MutableStateFlow(null)
    val filteredDocs: StateFlow<List<Doc>?> = _filteredDocs

    init {
        fetchDocs()
    }

    fun fetchDocs() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Storage.readDocs()

            _docs.addAll(result)

            _filteredDocs.update {
                result
            }
        }
    }

    fun search(text: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (text.isEmpty()) {
                    _docs
                } else {
                    val lowerCaseQuery = text.lowercase()
                    _docs.filter { doc ->
                        doc.filename.lowercase().contains(lowerCaseQuery)
                    }
                }
            }

            _filteredDocs.update {
                result
            }
        }
    }
}
