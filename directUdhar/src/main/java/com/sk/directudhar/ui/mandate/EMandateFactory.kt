package com.sk.directudhar.ui.mandate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class EMandateFactory@Inject constructor(private val repository: EMandateRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EMandateViewModel(repository) as T
    }
}