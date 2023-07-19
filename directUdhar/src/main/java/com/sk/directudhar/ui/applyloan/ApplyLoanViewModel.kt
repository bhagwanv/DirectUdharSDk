package com.sk.directudhar.ui.applyloan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ApplyLoanViewModel @Inject constructor(private val repository: ApplayLoanRepository):ViewModel() {
    var username: String = ""

    private val logInResult = MutableLiveData<String>()
    fun getLogInResult(): LiveData<String> = logInResult

    fun performValidation() {

        if (username.isBlank()) {
            logInResult.value = "Invalid username"
            return
        }

        /*if (password.isBlank()) {
            logInResult.value = "Invalid password"
            return
        }*/

        logInResult.value = "Valid credentials :)"
    }
}