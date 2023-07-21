package com.sk.directudhar.ui.adharcard.aadhaarCardOtp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class AadhaarOtpFactory @Inject constructor(private val repository: AadhaarOtpRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AadhaarOtpViewModel(repository) as T
    }
}