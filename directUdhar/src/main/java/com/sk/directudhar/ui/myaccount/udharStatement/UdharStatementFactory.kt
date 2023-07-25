package com.sk.directudhar.ui.myaccount.udharStatement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class UdharStatementFactory @Inject constructor(private val repository: UdharStatementRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UdharStatementViewModel(repository) as T
    }
}