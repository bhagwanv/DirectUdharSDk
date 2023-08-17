package com.sk.directudhar.ui.mandate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.applyloan.ApplayLoanRepository
import com.sk.directudhar.ui.applyloan.ApplyLoanRequestModel
import com.sk.directudhar.ui.applyloan.CityModel
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EMandateViewModel @Inject constructor(private val repository: EMandateRepository) :
    ViewModel() {

    private val eMandateResult = MutableLiveData<String>()

    private var _bankListResponse = MutableLiveData<NetworkResult<BankListResponse>>()
    val bankListResponse: LiveData<NetworkResult<BankListResponse>> = _bankListResponse

    private var _eMandateAddResponse = MutableLiveData<NetworkResult<EMandateAddResponseModel>>()
    val eMandateAddResponse: LiveData<NetworkResult<EMandateAddResponseModel>> =
        _eMandateAddResponse


    fun getEMandateResult(): LiveData<String> = eMandateResult

    fun performValidation(eMandateAddRequestModel: EMandateAddRequestModel) {

        if (eMandateAddRequestModel.BankName.isNullOrEmpty()) {
            eMandateResult.value = "Please enter Bank name"
        } else if (eMandateAddRequestModel.IFSCCode.isNullOrEmpty()) {
            eMandateResult.value = "Please Enter Ifsc Code"
        } else if (eMandateAddRequestModel.AccountType.isNullOrEmpty()) {
            eMandateResult.value = "Please select Account Type"
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
}