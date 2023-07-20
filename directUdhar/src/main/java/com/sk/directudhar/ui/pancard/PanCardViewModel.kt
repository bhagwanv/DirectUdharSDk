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


    private var _panCardResponse = MutableLiveData<NetworkResult<JsonObject>>()
    val panCardResponse: LiveData<NetworkResult<JsonObject>> = _panCardResponse
    fun uploadPanCard(body: MultipartBody.Part) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.uploadPanCard(body).collect() {
                    _panCardResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }


    }

}