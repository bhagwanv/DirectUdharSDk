package com.sk.directudhar.ui.mandate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EMandateViewModel @Inject constructor(private val repository: EMandateRepository) :
    ViewModel() {

    private val eMandateResult = SingleLiveEvent<String>()

    private var _bankListResponse = SingleLiveEvent<NetworkResult<BankListResponse>>()
    val bankListResponse: SingleLiveEvent<NetworkResult<BankListResponse>> = _bankListResponse

    private var _eMandateAddResponse = SingleLiveEvent<NetworkResult<EMandateAddResponseModel>>()
    val eMandateAddResponse: SingleLiveEvent<NetworkResult<EMandateAddResponseModel>> =
        _eMandateAddResponse

    private var putEMandateVerificationResponse = SingleLiveEvent<NetworkResult<EMandateVerificationResponse>>()
    val getEMandateVerificationResponse: SingleLiveEvent<NetworkResult<EMandateVerificationResponse>> =
        putEMandateVerificationResponse
    fun getEMandateResult(): SingleLiveEvent<String> = eMandateResult

    fun performValidation(eMandateAddRequestModel: EMandateAddRequestModel) {

        if (eMandateAddRequestModel.BankName.isNullOrEmpty()) {
            eMandateResult.value = "Please enter Bank name"
        } else if (eMandateAddRequestModel.IFSCCode.isNullOrEmpty()) {
            eMandateResult.value = "Please Enter Ifsc Code"
        } else if (eMandateAddRequestModel.AccountType.isNullOrEmpty()) {
            eMandateResult.value = "Please select Account Type"
        }else if (eMandateAddRequestModel.AccountNumber.isNullOrEmpty()) {
            eMandateResult.value = "Please enter Account number"
        } else if (eMandateAddRequestModel.Channel.isNullOrEmpty()) {
            eMandateResult.value = "Please select Channel "
        } else {
            eMandateResult.value = SuccessType
        }
    }

    fun callBankList() {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.bankList().collect() {
                    _bankListResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun callEmandateAdd(model: EMandateAddRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.setUpEMandateAdd(model).collect() {
                    _eMandateAddResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }

    fun eMandateVerification(eMandateVerificationRequestModel: EMandateVerificationRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.eMandateVerification(eMandateVerificationRequestModel).collect() {
                    putEMandateVerificationResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }
}