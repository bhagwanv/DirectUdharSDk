package com.sk.directudhar.ui.personalDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyLoanViewModel @Inject constructor(private val repository: ApplayLoanRepository) :
    ViewModel() {

    private val putValidResult = SingleLiveEvent<String>()
    fun getValidResult(): SingleLiveEvent<String> = putValidResult

    private var _stateResponse = SingleLiveEvent<NetworkResult<ArrayList<StateModel>>>()
    val stateResponse: SingleLiveEvent<NetworkResult<ArrayList<StateModel>>> = _stateResponse

    private var _cityResponse = SingleLiveEvent<NetworkResult<ArrayList<CityModel>>>()
    val cityResponse: SingleLiveEvent<NetworkResult<ArrayList<CityModel>>> = _cityResponse

    private var _postDataResponse = SingleLiveEvent<NetworkResult<InitiateAccountModel>>()
    val postResponse: SingleLiveEvent<NetworkResult<InitiateAccountModel>> = _postDataResponse

    private var _getPersonalInformationResponse = SingleLiveEvent<NetworkResult<GetPersonalInformationResponseModel>>()
    val getPersonalInformationResponse: SingleLiveEvent<NetworkResult<GetPersonalInformationResponseModel>> = _getPersonalInformationResponse

    private var _postCreditBeurauResponse = SingleLiveEvent<NetworkResult<PostCreditBeurauResponseModel>>()
    val postCreditBeurauResponse: SingleLiveEvent<NetworkResult<PostCreditBeurauResponseModel>> = _postCreditBeurauResponse




    fun performValidation() {
       /* if (postCreditBeurauRequestModel.FirstName.isNullOrEmpty()) {
            logInResult.value = "Please Enter Name"
        } else if (applyLoanRequestModel.FirmName.isNullOrEmpty()) {
            logInResult.value = "Please Business Name "
        } else if (applyLoanRequestModel.Address.isNullOrEmpty()) {
            logInResult.value = "Please Business Address"
        } else if (applyLoanRequestModel.MobileNo.isNullOrEmpty()) {
            logInResult.value = "Please Enter Mobile Number"
        } else if (applyLoanRequestModel.MobileNo.length < 10) {
            logInResult.value = "Please Enter Valid Mobile number"
        } else if (applyLoanRequestModel.EmailId.isNullOrEmpty()) {
            logInResult.value = "Please Enter Email id"
        } else if (applyLoanRequestModel.BusinessTurnOver.isNullOrEmpty()) {
            logInResult.value = "Please Enter Your company turn over"
        } else if (!applyLoanRequestModel.IsAgreementAccept) {
            logInResult.value = "Please Check Term Policy"
        } else {
            logInResult.value =SuccessType
        }*/

        putValidResult.value =SuccessType
    }

    fun postFromData(applyLoanRequestModel: ApplyLoanRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.postData(applyLoanRequestModel).collect() {
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

    fun getPersonalInformation(leadMasterId: Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getPersonalInformation(leadMasterId).collect() {
                    _getPersonalInformationResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }

    fun postCreditBeurau(postCreditBeurauRequestModel:PostCreditBeurauRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.postCreditBeurau(postCreditBeurauRequestModel).collect() {
                    _postCreditBeurauResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }


}