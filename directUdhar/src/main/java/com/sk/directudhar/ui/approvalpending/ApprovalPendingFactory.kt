package com.sk.directudhar.ui.approvalpending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject



class ApprovalPendingFactory @Inject constructor(private val repository: ApprovalPendingRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApprovalPendingViewModel(repository) as T
    }
}