package com.sk.directudhar.ui.applyloan

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.data.TokenResponse
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyLoanViewModel @Inject constructor(private val repository: ApplayLoanRepository) : ViewModel() {

    private val logInResult = MutableLiveData<String>()

    private var _stateResponse = MutableLiveData<NetworkResult<ArrayList<StateModel>>>()
    val stateResponse: LiveData<NetworkResult<ArrayList<StateModel>>> = _stateResponse

    private var _cityResponse = MutableLiveData<NetworkResult<ArrayList<CityModel>>>()
    val cityResponse: LiveData<NetworkResult<ArrayList<CityModel>>> = _cityResponse

    fun getLogInResult(): LiveData<String> = logInResult

    fun performValidation(applyLoanRequestModel: ApplyLoanRequestModel) {
        if (applyLoanRequestModel.Name.isNullOrEmpty()) {
            logInResult.value = "Please Enter Name"
        } else if (applyLoanRequestModel.FirmName.isNullOrEmpty()) {
            logInResult.value = "Please Business Name "
        }else if (applyLoanRequestModel.Address.isNullOrEmpty()){
            logInResult.value = "Please Business Address"
        }else if (applyLoanRequestModel.MobileNo.isNullOrEmpty()){
            logInResult.value ="Please Enter Mobile Number"
        }else if (applyLoanRequestModel.MobileNo.length<10){
            logInResult.value ="Please Enter Valid Mobile number"
        }else if (applyLoanRequestModel.EmailId.isNullOrEmpty()){
            logInResult.value ="Please Enter Email id"
        }else if (applyLoanRequestModel.BusinessTurnOver.isNullOrEmpty()){
            logInResult.value="Please Enter Your company turn over"
        }else {
          postFromData(applyLoanRequestModel)
        }
        }

    private fun postFromData(applyLoanRequestModel: ApplyLoanRequestModel) {

    }

    fun callState() {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getState().collect() {
                    _stateResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }
    fun callCity(stateId:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getCity(stateId).collect() {
                    _cityResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }
}