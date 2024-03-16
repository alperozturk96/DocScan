package com.coolnexttech.docscan.ui.scanner

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.docscan.util.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel: ViewModel() {

    private val _docs: MutableStateFlow<List<Uri>?> = MutableStateFlow(null)
    val docs: StateFlow<List<Uri>?> = _docs

    init {
        fetchDocs()
    }

    fun fetchDocs() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Storage.readDocs()

            _docs.update {
                result
            }
        }
    }

}