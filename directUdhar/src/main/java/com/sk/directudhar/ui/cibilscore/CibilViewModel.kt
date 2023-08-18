package com.sk.directudhar.ui.cibilscore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.agreement.SignSessionRequestModel
import com.sk.directudhar.ui.applyloan.CityModel
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.cibilscore.cibiotp.CiBilOTPResponceModel
import com.sk.directudhar.ui.cibilscore.cibiotp.CiBilResponceModel
import com.sk.directudhar.ui.cibilscore.cibiotp.GenrateOtpModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
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

    private var putCibilResponse = SingleLiveEvent<NetworkResult<CibilResponseModel>>()
    val getCibilResponse: SingleLiveEvent<NetworkResult<CibilResponseModel>> = putCibilResponse

    private var _postDataResponse = MutableLiveData<NetworkResult<CiBilResponceModel>>()
    val postResponse: LiveData<NetworkResult<CiBilResponceModel>> = _postDataResponse

    private var _genrateOtpResponse = MutableLiveData<NetworkResult<CiBilOTPResponceModel>>()
    val genrateOtpResponse: LiveData<NetworkResult<CiBilOTPResponceModel>> = _genrateOtpResponse

    fun getCiBilResult(): LiveData<String> = cibilResult

    private var _cibilActivityComplete = SingleLiveEvent<NetworkResult<CibilActivityCompleteResponseModel>>()
    val cibilActivityCompleteResponse: SingleLiveEvent<NetworkResult<CibilActivityCompleteResponseModel>> = _cibilActivityComplete


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

    fun postFromData(cibilRequestModel: CibilResponseModel) {
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

    fun callUserCreditInfo(leadMasterID:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getUserCreditInfo(leadMasterID).collect() {
                    putCibilResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }

    fun cibilActivityComplete(leadMasterID:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.cibilActivityComplete(leadMasterID).collect() {
                    _cibilActivityComplete.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }
}