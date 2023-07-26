package com.sk.directudhar.ui.agreement.agreementOtp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.agreement.AgreementResponseModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.AADHAAR_OTP_VALIDATE_SUCCESSFULLY
import com.sk.directudhar.utils.Utils.Companion.EAGREEMENT_OTP_VALIDATE_SUCCESSFULLY
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EAgreementOtpViewModel @Inject constructor(private val repository: EAgreementOtpRepository) :
    ViewModel() {

    private val eAgreementResult = MutableLiveData<String>()
    fun getAgreementResult(): LiveData<String> = eAgreementResult

    private var _eAgreementOtpVerificationResponse = MutableLiveData<NetworkResult<AgreementResponseModel>>()
    val eAgreementOtpVerificationResponse: LiveData<NetworkResult<AgreementResponseModel>> = _eAgreementOtpVerificationResponse

    private var _sendOtpResponse = MutableLiveData<NetworkResult<AgreementResponseModel>>()
    val sendOtpResponse: LiveData<NetworkResult<AgreementResponseModel>> = _sendOtpResponse

    fun validateOtp(otp: String) {
        if (otp.isNullOrEmpty()) {
            eAgreementResult.value = "Please Enter OTP"
        } else if (otp.length < 4) {
            eAgreementResult.value = "Please Enter 4 Digit OTP"
        }  else {
            eAgreementResult.value = EAGREEMENT_OTP_VALIDATE_SUCCESSFULLY
        }
    }

    fun eAgreementVerification(model: EAgreementOtpResquestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.eAgreementOtpVerification(model).collect() {
                    _eAgreementOtpVerificationResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun sendOtp(mobileNo: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.sendOtp(mobileNo).collect() {
                    _sendOtpResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}