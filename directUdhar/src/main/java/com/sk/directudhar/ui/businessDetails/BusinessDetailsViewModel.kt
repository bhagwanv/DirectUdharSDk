package com.sk.directudhar.ui.businessDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
@HiltViewModel
class BusinessDetailsViewModel @Inject constructor(private val repository: BusinessDetailsRepository):ViewModel() {

    private var putBusinessDetailsResponse= MutableLiveData<NetworkResult<BusinessDetailsResponseModel>>()
    val getBusinessDetailsResponse: LiveData<NetworkResult<BusinessDetailsResponseModel>> = putBusinessDetailsResponse

    private val businessValidResult = MutableLiveData<String>()
    fun getBusinessValidResult(): LiveData<String> = businessValidResult

    private var _getGSTDetailsResponse = MutableLiveData<NetworkResult<GSTDetailsResponse>>()
    val getGSTDetailsResponse: LiveData<NetworkResult<GSTDetailsResponse>> = _getGSTDetailsResponse

    private var _getBusinessTypeListResponse = MutableLiveData<NetworkResult<BusinessTypeListResponse>>()
    val getBusinessTypeListResponse: LiveData<NetworkResult<BusinessTypeListResponse>> = _getBusinessTypeListResponse

    private var _bankPassBookUploadResponse = MutableLiveData<NetworkResult<StatementFileResponse>>()
    val bankPassBookUploadResponse: LiveData<NetworkResult<StatementFileResponse>> = _bankPassBookUploadResponse

    private var _verifyElectricityBillResponse = MutableLiveData<NetworkResult<BusinessDetailsVerifyElectricityBillResponseModel>>()
    val verifyElectricityBillResponse: LiveData<NetworkResult<BusinessDetailsVerifyElectricityBillResponseModel>> = _verifyElectricityBillResponse


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

    fun validateBusinessDetails(
        model: BusinessDetailsRequestModel,
        isGSTVerify: Boolean,
        tnCchecked: Boolean,
        etCustomerNumber: String,
        isVerifyElectricityBill: Boolean
    ) {
        if (model.GSTNo.isNullOrEmpty()){
            businessValidResult.value = "Please enter GST Number"
        }else if(!isGSTVerify){
            businessValidResult.value = "Please verify GST Number"
        }else if(model.BusinessName.isNullOrEmpty()){
            businessValidResult.value = "Please enter business Name"
        }else if(model.BusinessTurnOver.isNullOrEmpty()){
            businessValidResult.value = "Please enter business turn over"
        }else if(model.BusinessIncorporationDate.isNullOrEmpty()){
            businessValidResult.value = "Please enter business incorporation date"
        }else if(model.IncomSlab.isNullOrEmpty()){
            businessValidResult.value = "Please enter income slab"
        }/*else if(etCustomerNumber.isNullOrEmpty()){
            businessValidResult.value = "Please Customer Number"
        }else if(!isVerifyElectricityBill){
            businessValidResult.value = "Please verify Customer Number"
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

    fun bankPassBookUpload(LeadMasterId: Int,body: MultipartBody.Part) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.bankPassBookUpload(LeadMasterId,body).collect() {
                    _bankPassBookUploadResponse.postValue(it)
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