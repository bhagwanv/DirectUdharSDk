package com.sk.directudhar.ui.cibilscore.cibiotp

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
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CiBilOtpViewModel @Inject constructor(val respository: CiBilOtpRespository):ViewModel() {

    private val cibilResultResult = MutableLiveData<String>()
    fun getCiBilResult(): LiveData<String> = cibilResultResult

    private var _postOTPResponse = MutableLiveData<NetworkResult<InitiateAccountModel>>()
    val postOtpResponse: LiveData<NetworkResult<InitiateAccountModel>> = _postOTPResponse

    fun validateOtp(otp: String) {
        if (otp.isNullOrEmpty()) {
            cibilResultResult.value = "Please Enter OTP"
        } else {
            cibilResultResult.value = Utils.AADHAAR_OTP_VALIDATE_SUCCESSFULLY
        }
    }


    fun postOtpData(postOTPRequestModel: PostOTPRequestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                respository.postOtpData(postOTPRequestModel).collect() {
                    _postOTPResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}