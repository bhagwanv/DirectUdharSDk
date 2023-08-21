package com.sk.directudhar.ui.success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class SuccessFactory @Inject constructor(private val repository: SuccessRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SuccessViewModel(repository) as T
    }
}