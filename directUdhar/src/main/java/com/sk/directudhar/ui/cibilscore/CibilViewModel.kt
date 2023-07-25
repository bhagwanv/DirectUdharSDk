package com.sk.directudhar.ui.cibilscore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.applyloan.ApplyLoanRequestModel
import com.sk.directudhar.ui.applyloan.CityModel
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.cibilscore.cibiotp.CiBilOTPResponceModel
import com.sk.directudhar.ui.cibilscore.cibiotp.CiBilResponceModel
import com.sk.directudhar.ui.cibilscore.cibiotp.GenrateOtpModel
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CibilViewModel @Inject constructor(private val repository: CibilRepository) :
    ViewModel() {

    private val cibilResult = MutableLiveData<String>()

    private var _stateResponse = MutableLiveData<NetworkResult<ArrayList<StateModel>>>()
    val stateResponse: LiveData<NetworkResult<ArrayList<StateModel>>> = _stateResponse

    private var _cityResponse = MutableLiveData<NetworkResult<ArrayList<CityModel>>>()
    val cityResponse: LiveData<NetworkResult<ArrayList<CityModel>>> = _cityResponse

    private var _UserInfoResponse = MutableLiveData<NetworkResult<CibilRequestModel>>()
    val userResponse: LiveData<NetworkResult<CibilRequestModel>> = _UserInfoResponse

    private var _postDataResponse = MutableLiveData<NetworkResult<CiBilResponceModel>>()
    val postResponse: LiveData<NetworkResult<CiBilResponceModel>> = _postDataResponse

    private var _genrateOtpResponse = MutableLiveData<NetworkResult<CiBilOTPResponceModel>>()
    val genrateOtpResponse: LiveData<NetworkResult<CiBilOTPResponceModel>> = _genrateOtpResponse

    fun getCiBilResult(): LiveData<String> = cibilResult


    fun performValidation(cibilRequestModel: CibilRequestModel) {
        if (cibilRequestModel.FirstName.isNullOrEmpty()) {
            cibilResult.value = "Please Enter Name"
        } else if (cibilRequestModel.LastName.isNullOrEmpty()) {
            cibilResult.value = "Please Last Name "
        } else if (cibilRequestModel.FlatNo.isNullOrEmpty()) {
            cibilResult.value = "Please Flat No Address"
        } else if (cibilRequestModel.PanNumber.isNullOrEmpty()) {
            cibilResult.value = "Please Pan Number"
        } else if (cibilRequestModel.PanNumber.length < 10) {
            cibilResult.value = "Please Enter Valid Pan number"
        } else if (cibilRequestModel.PinCode.isNullOrEmpty()) {
            cibilResult.value = "Please Enter Pin Code"
        } else if (cibilRequestModel.PinCode.length<6) {
            cibilResult.value = "Please Enter Valid pin number"
        } else if (!cibilRequestModel.IsAcceptConsent) {
            cibilResult.value = "Please Check Term Policy"
        } else {
            cibilResult.value =SuccessType
        }
    }


    fun postFromData(genrateOtpModel: GenrateOtpModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.genrateData(genrateOtpModel).collect() {
                    _genrateOtpResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun postFromData(cibilRequestModel: CibilRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.postData(cibilRequestModel).collect() {
                    _postDataResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
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

    fun callCity(stateId: Int) {
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

    fun callUserInfo(leadMasterID:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getUserInfo(leadMasterID).collect() {
                    _UserInfoResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }
}