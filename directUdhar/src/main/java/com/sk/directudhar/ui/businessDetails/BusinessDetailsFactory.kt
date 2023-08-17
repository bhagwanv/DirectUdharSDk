package com.sk.directudhar.ui.businessDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject



class BusinessDetailsFactory @Inject constructor(private val repository: BusinessDetailsRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BusinessDetailsViewModel(repository) as T
    }
}