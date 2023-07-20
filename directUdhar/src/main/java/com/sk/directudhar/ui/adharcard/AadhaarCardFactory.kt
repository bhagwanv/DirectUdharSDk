package com.sk.directudhar.ui.adharcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class AadhaarCardFactory @Inject constructor(private val repository: AadhaarCardRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AadhaarCardViewModel(repository) as T
    }
}