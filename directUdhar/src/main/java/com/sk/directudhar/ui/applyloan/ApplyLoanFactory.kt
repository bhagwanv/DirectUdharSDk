package com.sk.directudhar.ui.applyloan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject



class ApplyLoanFactory @Inject constructor(private val repository: ApplayLoanRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApplyLoanViewModel(repository) as T
    }
}