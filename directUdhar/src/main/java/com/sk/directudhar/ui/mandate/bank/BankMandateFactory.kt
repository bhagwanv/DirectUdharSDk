package com.sk.directudhar.ui.mandate.bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class BankMandateFactory @Inject constructor(private val repository: BankMandateRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BankMandateViewModel(repository) as T
    }
}