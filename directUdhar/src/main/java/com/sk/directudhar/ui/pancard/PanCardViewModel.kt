package com.sk.directudhar.ui.pancard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
@HiltViewModel
class PanCardViewModel @Inject constructor(private val repository: PanCardRepository):ViewModel() {
    var username: String = ""

    private val logInResult = MutableLiveData<String>()
    private var _panCardResponse = MutableLiveData<NetworkResult<JsonObject>>()
    val panCardResponse: LiveData<NetworkResult<JsonObject>> = _panCardResponse

    fun getLogInResult(): LiveData<String> = logInResult
    fun uploadPanCard(leadMasterId:Int ,body: MultipartBody.Part) {

        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.uploadPanCard(leadMasterId,body).collect() {
                    _panCardResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }


    }

    fun performValidation(panCardRequestModel: PanCardRequestModel) {
        if (panCardRequestModel.NameAsPAN.isNullOrEmpty()) {
            logInResult.value = "Please Enter Name As PAN"
        } else if (panCardRequestModel.EmailID .isNullOrEmpty()) {
            logInResult.value = "Please Enter Email Id "
        }else if (panCardRequestModel.PanNumber.isNullOrEmpty()){
            logInResult.value = "Please Enter PanNumber"
        }else if (panCardRequestModel.RefrralCode.isNullOrEmpty()){
            logInResult.value ="Please Enter RefrralCode"
        } else {
            postFromData(panCardRequestModel)
        }
    }

    private fun postFromData(panCardRequestModel: PanCardRequestModel) {

    }


}