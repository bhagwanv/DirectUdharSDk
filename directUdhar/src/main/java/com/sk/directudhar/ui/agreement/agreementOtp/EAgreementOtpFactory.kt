package com.sk.directudhar.ui.agreement.agreementOtp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class EAgreementOtpFactory @Inject constructor(private val repository: EAgreementOtpRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EAgreementOtpViewModel(repository) as T
    }
}