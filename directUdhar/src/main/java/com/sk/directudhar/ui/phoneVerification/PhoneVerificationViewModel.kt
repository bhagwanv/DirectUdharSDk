package com.sk.directudhar.ui.phoneVerification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneVerificationViewModel @Inject constructor(private val repository: PhoneVerificationRepository) :
    ViewModel() {

    private var putGenOptResponse =
        MutableLiveData<NetworkResult<GenOtpResponseModel>>()
    val getGenOptResponse: LiveData<NetworkResult<GenOtpResponseModel>> =
        putGenOptResponse

    private var putOptVerifyResponse =
        MutableLiveData<NetworkResult<GenOtpResponseModel>>()
    val getOptVerifyResponse: LiveData<NetworkResult<GenOtpResponseModel>> =
        putOptVerifyResponse


    fun callGenOtp(mobile: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getOtp(mobile).collect {
                    putGenOptResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun callOtpVerify(mobile: String,otp: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getOtpVerify(mobile, otp).collect {
                    putOptVerifyResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}