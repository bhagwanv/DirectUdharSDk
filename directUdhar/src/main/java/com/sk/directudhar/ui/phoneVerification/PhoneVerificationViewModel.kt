package com.sk.directudhar.ui.phoneVerification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneVerificationViewModel @Inject constructor(private val repository: PhoneVerificationRepository) :
    ViewModel() {

    private var putGenOptResponse =
        SingleLiveEvent<NetworkResult<PhoneVerifyResponseModel>>()
    val getGenOptResponse: SingleLiveEvent<NetworkResult<PhoneVerifyResponseModel>> =
        putGenOptResponse

    private var putOptVerifyResponse =
        SingleLiveEvent<NetworkResult<OtpVerifyResponseModel>>()
    val getOptVerifyResponse: SingleLiveEvent<NetworkResult<OtpVerifyResponseModel>> =
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

    fun callOtpVerify(mobile: String,otp: String,txnNo: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getOtpVerify(mobile, otp,txnNo).collect {
                    putOptVerifyResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}