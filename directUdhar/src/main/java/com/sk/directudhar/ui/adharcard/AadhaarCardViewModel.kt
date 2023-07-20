package com.sk.directudhar.ui.adharcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sk.directudhar.utils.Utils.Companion.isValidAadhaar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AadhaarCardViewModel @Inject constructor(private val repository: AadhaarCardRepository) :
    ViewModel() {

    private val aadhaarResultResult = MutableLiveData<String>()
    fun getAadhaarResult(): LiveData<String> = aadhaarResultResult

    fun validateAadhaar(aadhaarNumber: String) {
        if (aadhaarNumber.isNullOrEmpty()) {
            aadhaarResultResult.value = "Please Enter Aadhaar Number"
        } else if (aadhaarNumber.length < 12) {
            aadhaarResultResult.value = "Please Enter 12 Digit Aadhaar number"
        } else if (!isValidAadhaar(aadhaarNumber)) {
            aadhaarResultResult.value = "Invalid Aadhaar number"
        } else {
            aadhaarResultResult.value = "Nice Work!!"
        }
    }
}