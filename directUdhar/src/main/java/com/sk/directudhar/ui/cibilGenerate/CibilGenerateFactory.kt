package com.sk.directudhar.ui.cibilGenerate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject

class CibilGenerateFactory @Inject constructor(private val repository: CibilGenerateRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CibilGenerateViewModel(repository) as T
    }
}