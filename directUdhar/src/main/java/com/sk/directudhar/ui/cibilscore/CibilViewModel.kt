package com.sk.directudhar.ui.cibilscore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.SingleLiveEvent
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CibilViewModel @Inject constructor(private val repository: CibilRepository) :
    ViewModel() {



    private var putCibilResponse = SingleLiveEvent<NetworkResult<CibilResponseModel>>()
    val getCibilResponse: SingleLiveEvent<NetworkResult<CibilResponseModel>> = putCibilResponse

    private var _cibilActivityComplete = SingleLiveEvent<NetworkResult<CibilActivityCompleteResponseModel>>()
    val cibilActivityCompleteResponse: SingleLiveEvent<NetworkResult<CibilActivityCompleteResponseModel>> = _cibilActivityComplete

    fun callUserCreditInfo(leadMasterID:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getUserCreditInfo(leadMasterID).collect() {
                    putCibilResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }

    fun cibilActivityComplete(leadMasterID:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.cibilActivityComplete(leadMasterID).collect() {
                    _cibilActivityComplete.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }

    }
}