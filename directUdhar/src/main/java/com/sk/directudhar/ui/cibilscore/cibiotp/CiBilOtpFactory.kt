package com.sk.directudhar.ui.cibilscore.cibiotp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject


class CiBilOtpFactory @Inject constructor(private val repository: CiBilOtpRespository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CiBilOtpViewModel(repository) as T
    }
}