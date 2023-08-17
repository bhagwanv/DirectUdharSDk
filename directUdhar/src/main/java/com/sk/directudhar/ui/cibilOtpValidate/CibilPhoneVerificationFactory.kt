package com.sk.directudhar.ui.cibilOtpValidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject

class CibilPhoneVerificationFactory @Inject constructor(private val repository: CibilPhoneVerificationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CibilPhoneVerificationViewModel(repository) as T
    }
}