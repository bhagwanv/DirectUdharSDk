package com.sk.directudhar.ui.pancard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
@HiltViewModel
class PanCardViewModel @Inject constructor(private val repository: PanCardRepository):ViewModel() {

    private val panCardResult = MutableLiveData<String>()

    private var _panCardResponse = MutableLiveData<NetworkResult<JsonObject>>()
    val panCardResponse: LiveData<NetworkResult<JsonObject>> = _panCardResponse

    private var _updatePanInfoResponse = MutableLiveData<NetworkResult<InitiateAccountModel>>()
    val updatePanInfoResponse: LiveData<NetworkResult<InitiateAccountModel>> = _updatePanInfoResponse

    fun getLogInResult(): LiveData<String> = panCardResult
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

    fun updatePanInfo(model: UpdatePanInfoRequestModel) {

        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.updatePanInfo(model).collect() {
                    _updatePanInfoResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }


    }
    fun performValidation(panCardRequestModel: PanCardRequestModel) {
        if (panCardRequestModel.NameAsPAN.isNullOrEmpty()) {
            panCardResult.value = "Please Enter Name As PAN"
        } else if (panCardRequestModel.EmailID .isNullOrEmpty()) {
            panCardResult.value = "Please Enter Email Id "
        }else if (panCardRequestModel.PanNumber.isNullOrEmpty()){
            panCardResult.value = "Please Enter PanNumber"
        }else if (panCardRequestModel.PanNumber.length<10) {
            panCardResult.value = "Please Enter Valid PanNumber"
        }else if (panCardRequestModel.imageUrl.isNullOrEmpty())
            panCardResult.value = "Please upload Pan Card Image"
        else if (!panCardRequestModel.checked) {
            panCardResult.value = "Please check terms and Condition"
        } else {
            panCardResult.value = SuccessType
        }
    }



}