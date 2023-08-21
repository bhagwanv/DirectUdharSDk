package com.sk.directudhar.ui.adharcard.aadhaarCardOtp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.adharcard.AadhaarUpdateResponseModel
import com.sk.directudhar.ui.adharcard.UpdateAadhaarInfoRequestModel
import com.sk.directudhar.ui.adharcard.aadhaarManullyUpload.AadhaarManuallyUploadResponseModel
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils.Companion.AADHAAR_OTP_VALIDATE_SUCCESSFULLY
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AadhaarOtpViewModel @Inject constructor(private val repository: AadhaarOtpRepository) :
    ViewModel() {

    private val aadhaarResultResult = SingleLiveEvent<String>()
    fun getAadhaarResult(): SingleLiveEvent<String> = aadhaarResultResult

    private var _postDataResponse = SingleLiveEvent<NetworkResult<InitiateAccountModel>>()
    val postResponse: SingleLiveEvent<NetworkResult<InitiateAccountModel>> = _postDataResponse

    private var putUploadImageResponse = SingleLiveEvent<NetworkResult<AadhaarManuallyUploadResponseModel>>()
    val getUploadImageResponse: SingleLiveEvent<NetworkResult<AadhaarManuallyUploadResponseModel>> = putUploadImageResponse

    private var _sendOtpDataResponse = SingleLiveEvent<NetworkResult<AadhaarUpdateResponseModel>>()
    val putOtpResponse: SingleLiveEvent<NetworkResult<AadhaarUpdateResponseModel>> = _sendOtpDataResponse
    fun validateOtp(otp: String) {
        if (otp.isNullOrEmpty()) {
            aadhaarResultResult.value = "Please Enter OTP"
        } else if (otp.length < 6) {
            aadhaarResultResult.value = "Please Enter 6 Digit OTP"
        }  else {
            aadhaarResultResult.value = AADHAAR_OTP_VALIDATE_SUCCESSFULLY
        }
    }

    fun aadharVerification(aadharVerificationRequestModel: AadharVerificationRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.aadharVerification(aadharVerificationRequestModel).collect() {
                    _postDataResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun uploadAadhaarImage(body: MultipartBody.Part,leadMasterId:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.uploadAadhaarImage(body,leadMasterId).collect() {
                    putUploadImageResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun updateAadhaarInfo(updateAadhaarInfoRequestModel: UpdateAadhaarInfoRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.updateAadhaarInfo(updateAadhaarInfoRequestModel).collect() {
                    _sendOtpDataResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}