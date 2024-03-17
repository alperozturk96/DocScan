package com.coolnexttech.docscan.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.docscan.appContext
import com.coolnexttech.docscan.util.Storage
import com.coolnexttech.docscan.util.extensions.getFileName
import com.coolnexttech.docscan.util.extensions.toImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel: ViewModel() {

    private val _docs: MutableStateFlow<List<Doc>?> = MutableStateFlow(null)
    val docs: StateFlow<List<Doc>?> = _docs

    init {
        fetchDocs()
    }

    fun fetchDocs() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = arrayListOf<Doc>()
            val docUris = Storage.readDocs()

            docUris.forEach { uri ->
                val context = appContext.get()

                context?.let {
                    val fileName = context.getFileName(uri) ?: ""
                    val imageBitmap = uri.toImageBitmap(context)
                    result.add(Doc(fileName, imageBitmap, uri))
                }
            }

            _docs.update {
                result
            }
        }
    }

}