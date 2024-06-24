package com.coolnexttech.docscan.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.docscan.ui.scanner.model.Doc
import com.coolnexttech.docscan.ui.scanner.model.SortOptions
import com.coolnexttech.docscan.util.Storage
import com.coolnexttech.docscan.util.extensions.sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerViewModel: ViewModel() {

    private var _docs: List<Doc> = listOf()

    private val _filteredDocs: MutableStateFlow<List<Doc>?> = MutableStateFlow(null)
    val filteredDocs: StateFlow<List<Doc>?> = _filteredDocs

    private val _loading: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val loading: StateFlow<Boolean?> = _loading

    fun readDocs(onCompleted: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.update {
                true
            }

            val result = Storage.readDocs()
            _docs = result
            _filteredDocs.update {
                result
            }

            launch(Dispatchers.Main) {
                onCompleted()
                _loading.update {
                    false
                }
            }
        }
    }

    fun hideLoading() {
        _loading.update {
            false
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
        viewModelScope.launch(Dispatchers.IO) {
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
