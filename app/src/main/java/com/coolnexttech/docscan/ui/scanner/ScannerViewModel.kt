package com.coolnexttech.docscan.ui.scanner

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.docscan.util.Storage
import com.coolnexttech.docscan.util.extensions.toImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel: ViewModel() {

    private val _docs: MutableStateFlow<List<ImageBitmap>?> = MutableStateFlow(null)
    val docs: StateFlow<List<ImageBitmap>?> = _docs

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Storage.readDocs().toImageBitmap()

            _docs.update {
                result
            }
        }
    }

}