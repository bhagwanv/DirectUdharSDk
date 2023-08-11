package com.sk.directudhar.ui.phoneVerification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject

class PhoneVerificationFactory @Inject constructor(private val repository: PhoneVerificationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhoneVerificationViewModel(repository) as T
    }
}