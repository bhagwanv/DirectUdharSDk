package com.sk.directudhar.ui.pancard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject



class PanCardFactory @Inject constructor(private val repository: PanCardRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PanCardViewModel(repository) as T
    }
}