package com.sk.directudhar.ui.cibilscore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class CibilViewFactory @Inject constructor(private val repository: CibilRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CibilViewModel(repository) as T
    }
}