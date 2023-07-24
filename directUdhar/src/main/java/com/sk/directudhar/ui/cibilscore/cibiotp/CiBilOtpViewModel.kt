package com.sk.directudhar.ui.cibilscore.cibiotp

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewScoped
import javax.inject.Inject

@HiltViewModel
class CiBilOtpViewModel @Inject constructor(val respository: CiBilOtpRespository):ViewModel() {

}