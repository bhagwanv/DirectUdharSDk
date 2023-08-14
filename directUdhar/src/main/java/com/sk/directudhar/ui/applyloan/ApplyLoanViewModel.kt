package com.sk.directudhar.ui.applyloan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ApplyLoanViewModel @Inject constructor(private val repository: ApplayLoanRepository) :
    ViewModel() {

    private val logInResult = MutableLiveData<String>()
    fun getLogInResult(): LiveData<String> = logInResult

    private var _stateResponse = MutableLiveData<NetworkResult<ArrayList<StateModel>>>()
    val stateResponse: LiveData<NetworkResult<ArrayList<StateModel>>> = _stateResponse

    private var _cityResponse = MutableLiveData<NetworkResult<ArrayList<CityModel>>>()
    val cityResponse: LiveData<NetworkResult<ArrayList<CityModel>>> = _cityResponse

    private var _postDataResponse = MutableLiveData<NetworkResult<InitiateAccountModel>>()
    val postResponse: LiveData<NetworkResult<InitiateAccountModel>> = _postDataResponse

    private var _getPersonalInformationResponse = MutableLiveData<NetworkResult<GetPersonalInformationResponseModel>>()
    val getPersonalInformationResponse: LiveData<NetworkResult<GetPersonalInformationResponseModel>> = _getPersonalInformationResponse

    private var _postCreditBeurauResponse = MutableLiveData<NetworkResult<PostCreditBeurauResponseModel>>()
    val postCreditBeurauResponse: LiveData<NetworkResult<PostCreditBeurauResponseModel>> = _postCreditBeurauResponse


    private var putBusinessDetailsResponse= MutableLiveData<NetworkResult<BusinessDetailsResponseModel>>()
    val getBusinessDetailsResponse: LiveData<NetworkResult<BusinessDetailsResponseModel>> = putBusinessDetailsResponse

    private val businessValidResult = MutableLiveData<String>()
    fun getBusinessValidResult(): LiveData<String> = businessValidResult

    private var _getGSTDetailsResponse = MutableLiveData<NetworkResult<GSTDetailsResponse>>()
    val getGSTDetailsResponse: LiveData<NetworkResult<GSTDetailsResponse>> = _getGSTDetailsResponse

    private var _getBusinessTypeListResponse = MutableLiveData<NetworkResult<BusinessTypeListResponse>>()
    val getBusinessTypeListResponse: LiveData<NetworkResult<BusinessTypeListResponse>> = _getBusinessTypeListResponse

    private var _electricityDocumentUploadResponse = MutableLiveData<NetworkResult<JsonObject>>()
    val electricityDocumentUploadResponse: LiveData<NetworkResult<JsonObject>> = _electricityDocumentUploadResponse

    private var _verifyElectricityBillResponse = MutableLiveData<NetworkResult<BusinessDetailsVerifyElectricityBillResponseModel>>()
    val verifyElectricityBillResponse: LiveData<NetworkResult<BusinessDetailsVerifyElectricityBillResponseModel>> = _verifyElectricityBillResponse


    fun performValidation() {
        /*if (postCreditBeurauRequestModel.FirstName.isNullOrEmpty()) {
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

        logInResult.value =SuccessType
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

    fun addBusinessDetail(businessDetailsRequestModel: BusinessDetailsRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.addBusinessDetail(businessDetailsRequestModel).collect() {
                    putBusinessDetailsResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
    fun getGSTDetails(GSTNo: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getGSTDetails(GSTNo).collect() {
                    _getGSTDetailsResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }



    fun validateBusinessDetails(model:BusinessDetailsRequestModel,isGSTVerify: Boolean, tnCchecked: Boolean) {
        if (model.GSTNo.isNullOrEmpty()){
            businessValidResult.value = "Please enter GST Number"
        }else if(!isGSTVerify){
            businessValidResult.value = "Please verify GST Number"
        }/*else if(etCustomerNumber.isEmpty()){
            businessValidResult.value = "Please Customer Number"
        }*/else{
            businessValidResult.value = Utils.BUSINESS_VALIDATE_SUCCESSFULLY
        }
    }

    fun getBusinessTypeList() {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getBusinessTypeList().collect() {
                    _getBusinessTypeListResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun electricityDocumentUpload(LeadMasterId: Int,body: MultipartBody.Part) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.electricityDocumentUpload(LeadMasterId,body).collect() {
                    _electricityDocumentUploadResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun verifyElectricityBill(model: BusinessDetailsVerifyElectricityBillRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.verifyElectricityBill(model).collect() {
                    _verifyElectricityBillResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}