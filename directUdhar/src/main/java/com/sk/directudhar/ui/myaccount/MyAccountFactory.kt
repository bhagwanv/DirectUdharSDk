package com.sk.directudhar.ui.myaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject



class MyAccountFactory @Inject constructor(private val repository: MyAccountRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyAccountViewModel(repository) as T
    }
}