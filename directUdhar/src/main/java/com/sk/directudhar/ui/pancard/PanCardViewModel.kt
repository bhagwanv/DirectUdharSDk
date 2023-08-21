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
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class PanCardViewModel @Inject constructor(private val repository: PanCardRepository) :
    ViewModel() {

    private val panCardResult = MutableLiveData<String>()

    private var _panCardImageUploadResponse =
        SingleLiveEvent<NetworkResult<PanCardUplodResponseModel>>()
    val panCardImageUploadResponse: SingleLiveEvent<NetworkResult<PanCardUplodResponseModel>> =
        _panCardImageUploadResponse

    private var _updatePanInfoResponse = SingleLiveEvent<NetworkResult<InitiateAccountModel>>()
    val updatePanInfoResponse: SingleLiveEvent<NetworkResult<InitiateAccountModel>> =
        _updatePanInfoResponse

    fun getPanCard(): LiveData<String> = panCardResult
    fun uploadPanCard(leadMasterId: Int, body: MultipartBody.Part) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.uploadPanCard(leadMasterId, body).collect() {
                    _panCardImageUploadResponse.postValue(it)
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

    fun performValidation(panNumber: String,imageUrl:String) {
        if (panNumber.isNullOrEmpty()) {
            panCardResult.value = "Please Enter PanNumber"
        } else if (!Utils.isValidPanCardNo(panNumber)) {
            panCardResult.value = "Please Enter Valid PanNumber"
        } else if (imageUrl.isNullOrEmpty()) {
            panCardResult.value = "Please upload Pan card image"
        }else {
            panCardResult.value = SuccessType
        }
    }

}