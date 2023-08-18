package com.sk.directudhar.ui.cibilOtpValidate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.phoneVerification.OtpVerifyResponseModel
import com.sk.directudhar.ui.phoneVerification.PhoneVerifyResponseModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CibilPhoneVerificationViewModel @Inject constructor(private val repository: CibilPhoneVerificationRepository) :
    ViewModel() {

    private var putGenOptResponse =
        SingleLiveEvent<NetworkResult<PhoneVerifyResponseModel>>()
    val getGenOptResponse: SingleLiveEvent<NetworkResult<PhoneVerifyResponseModel>> =
        putGenOptResponse

    private var putOptVerifyResponse =
        SingleLiveEvent<NetworkResult<OtpVerifyResponseModel>>()
    val getOptVerifyResponse: SingleLiveEvent<NetworkResult<OtpVerifyResponseModel>> =
        putOptVerifyResponse


    fun callGenOtp(cibilGetOTPRequestModel:CibilGetOTPRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getOtp(cibilGetOTPRequestModel).collect {
                    putGenOptResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun callOtpVerify(cibilOTPVerifyRequestModel : CibilOTPVerifyRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getOtpVerify(cibilOTPVerifyRequestModel).collect {
                    putOptVerifyResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}