package com.sk.directudhar.ui.approvalpending

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.applyloan.ApplyLoanRequestModel
import com.sk.directudhar.ui.applyloan.CityModel
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.ui.pancard.UpdatePanInfoRequestModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApprovalPendingViewModel @Inject constructor(private val repository: ApprovalPendingRepository) :
    ViewModel() {

    private val approvalPendingResult = MutableLiveData<String>()

    private var _displayDisbursalAmountResponse = MutableLiveData<NetworkResult<DisplayDisbursalAmountResponse>>()
    val displayDisbursalAmountResponse: LiveData<NetworkResult<DisplayDisbursalAmountResponse>> = _displayDisbursalAmountResponse

    private var _updateLeadSuccessResponse = MutableLiveData<NetworkResult<InitiateAccountModel>>()
    val updateLeadSuccessResponse: LiveData<NetworkResult<InitiateAccountModel>> = _updateLeadSuccessResponse

    fun displayDisbursalAmount(leadMasterId:Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.displayDisbursalAmount(leadMasterId).collect() {
                    _displayDisbursalAmountResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun updateLeadSuccess(leadMasterId:Int) {

        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.updateLeadSuccess(leadMasterId).collect() {
                    _updateLeadSuccessResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }


    }

}