package com.sk.directudhar.ui.agreement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject



class EAgreementFactory @Inject constructor(private val repository: EAgreementRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EAgreementViewModel(repository) as T
    }
}