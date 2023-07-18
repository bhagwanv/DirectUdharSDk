package com.sk.directudhar.ui.mainhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.data.TokenResponse
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) :
    ViewModel() {

    private var _tokenResponse = MutableLiveData<NetworkResult<TokenResponse>>()
    val tokenResponse: LiveData<NetworkResult<TokenResponse>> = _tokenResponse

    private var _initiateResponse = MutableLiveData<NetworkResult<JsonObject>>()
    val accountInitiateResponse: LiveData<NetworkResult<JsonObject>> = _initiateResponse

    fun callToken(password: String, secretkey: String, apikey: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getToken(password, secretkey, apikey).collect() {
                    _tokenResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }

    fun getAccountInitiateResponse(mobilNumber: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getAccountInitiate(mobilNumber).collect() {
                    _initiateResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }


    }


}