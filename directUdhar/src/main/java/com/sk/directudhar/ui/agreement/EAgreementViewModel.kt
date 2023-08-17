package com.sk.directudhar.ui.agreement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EAgreementViewModel @Inject constructor(private val repository: EAgreementRepository) :
    ViewModel() {

    private var _agreementResponse = MutableLiveData<NetworkResult<AgreementResponseModel>>()
    val agreementResponse: LiveData<NetworkResult<AgreementResponseModel>> = _agreementResponse

    private var _sendOtpResponse = MutableLiveData<NetworkResult<SendOtpResponseModel>>()
    val sendOtpResponse: LiveData<NetworkResult<SendOtpResponseModel>> = _sendOtpResponse

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

}