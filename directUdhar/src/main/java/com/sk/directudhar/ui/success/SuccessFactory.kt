package com.sk.directudhar.ui.success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.ui.applyloan.ApplayLoanRepository
import com.sk.directudhar.ui.applyloan.ApplyLoanViewModel
import com.sk.directudhar.ui.approvalpending.ApprovalPendingRepository
import com.sk.directudhar.ui.approvalpending.ApprovalPendingViewModel

import javax.inject.Inject



class SuccessFactory @Inject constructor(private val repository: SuccessRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SuccessViewModel(repository) as T
    }
}