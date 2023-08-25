package com.sk.directudhar.ui.agreement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EAgreementViewModel @Inject constructor(private val repository: EAgreementRepository) :
    ViewModel() {

    private var _agreementResponse = SingleLiveEvent<NetworkResult<AgreementResponseModel>>()
    val agreementResponse: SingleLiveEvent<NetworkResult<AgreementResponseModel>> = _agreementResponse

    private var _sendOtpResponse = SingleLiveEvent<NetworkResult<SendOtpResponseModel>>()
    val sendOtpResponse: SingleLiveEvent<NetworkResult<SendOtpResponseModel>> = _sendOtpResponse

    private var _signSessionResponse = SingleLiveEvent<NetworkResult<SignSessionResponseModel>>()
    val signSessionResponse: SingleLiveEvent<NetworkResult<SignSessionResponseModel>> = _signSessionResponse

    private var putEsignOrAgreementWithOtpOptionResponse = SingleLiveEvent<NetworkResult<EsignOrAgreementWithOtpOptionResponse>>()
    val getEsignOrAgreementWithOtpOptionResponse: SingleLiveEvent<NetworkResult<EsignOrAgreementWithOtpOptionResponse>> = putEsignOrAgreementWithOtpOptionResponse

    private var putESignDocumentsAsyncResponse = SingleLiveEvent<NetworkResult<JsonObject>>()
    val getESignDocumentsAsyncResponse: SingleLiveEvent<NetworkResult<JsonObject>> = putESignDocumentsAsyncResponse

    fun getAgreement(leadMasterId:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getAgreement(leadMasterId).collect() {
                    _agreementResponse.postValue(it)
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

    fun eSignSessionAsync(signSessionRequestModel: SignSessionRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.eSignSessionAsync(signSessionRequestModel).collect() {
                    _signSessionResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun isEsignOrAgreementWithOtp(leadMasterId:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.isEsignOrAgreementWithOtp(leadMasterId).collect() {
                    putEsignOrAgreementWithOtpOptionResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun eSignDocumentsAsync(jsonObject: JsonObject) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.eSignDocumentsAsync(jsonObject).collect() {
                    putESignDocumentsAsyncResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}