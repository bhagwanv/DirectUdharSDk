package com.sk.directudhar.ui.adharcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.AADHAAR_VALIDATE_SUCCESSFULLY
import com.sk.directudhar.utils.Utils.Companion.isValidAadhaar
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AadhaarCardViewModel @Inject constructor(private val repository: AadhaarCardRepository) :
    ViewModel() {

    private val aadhaarResultResult = MutableLiveData<String>()
    fun getAadhaarResult(): LiveData<String> = aadhaarResultResult

    private var _postDataResponse = MutableLiveData<NetworkResult<AadhaarUpdateResponseModel>>()
    val postResponse: LiveData<NetworkResult<AadhaarUpdateResponseModel>> = _postDataResponse

    fun validateAadhaar(aadhaarNumber: String, tnCchecked: Boolean) {
        if (aadhaarNumber.isNullOrEmpty()) {
            aadhaarResultResult.value = "Please Enter Aadhaar Number"
        } else if (aadhaarNumber.length < 12) {
            aadhaarResultResult.value = "Please Enter 12 Digit Aadhaar number"
        } else if (!isValidAadhaar(aadhaarNumber)) {
            aadhaarResultResult.value = "Invalid Aadhaar number"
        } else if (!tnCchecked) {
            aadhaarResultResult.value = "Please Check Term and Condition"
        } else {
            aadhaarResultResult.value = AADHAAR_VALIDATE_SUCCESSFULLY
        }
    }

    fun updateAadhaarInfo(updateAadhaarInfoRequestModel: UpdateAadhaarInfoRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.updateAadhaarInfo(updateAadhaarInfoRequestModel).collect() {
                    _postDataResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}