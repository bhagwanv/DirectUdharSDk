package com.sk.directudhar.ui.myaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MyAccountViewModel @Inject constructor(private val repository: MyAccountRepository):ViewModel() {

    private var _myAccountDetailsModelResponse = MutableLiveData<NetworkResult<MyAccountDetailsModel>>()
    val myAccountDetailsModelResponse: LiveData<NetworkResult<MyAccountDetailsModel>> = _myAccountDetailsModelResponse

    private var _getUdharStatementResponse = MutableLiveData<NetworkResult<ArrayList<UdharStatementModel>>>()
    val getUdharStatementResponse: LiveData<NetworkResult<ArrayList<UdharStatementModel>>> = _getUdharStatementResponse

    fun getLoanAccountDetail(leadMasterId:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getLoanAccountDetail(leadMasterId).collect() {
                    _myAccountDetailsModelResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun getUdharStatement(accountId:Int, flag:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getUdharStatement(accountId, flag).collect() {
                    _getUdharStatementResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}