package com.sk.directudhar.ui.success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.approvalpending.DisplayDisbursalAmountResponse
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuccessViewModel @Inject constructor(private val repository: SuccessRepository) :
    ViewModel() {

    private val approvalPendingResult = MutableLiveData<String>()

    private var _DisplayDisbursalAmountResponse = MutableLiveData<NetworkResult<SuccessDisplayDisbursalAmountResponse>>()
    val displayDisbursalAmountResponse: LiveData<NetworkResult<SuccessDisplayDisbursalAmountResponse>> = _DisplayDisbursalAmountResponse

    private var _UpdateLeadSuccessResponse = MutableLiveData<NetworkResult<InitiateAccountModel>>()
    val updateLeadSuccessResponse: LiveData<NetworkResult<InitiateAccountModel>> = _UpdateLeadSuccessResponse

   /* fun getLogInResult(): LiveData<String> = approvalPendingResult
    fun getSuccessResualt(): LiveData<String> = approvalPendingResult

    fun performValidation(applyLoanRequestModel: ApplyLoanRequestModel) {
        if (applyLoanRequestModel.Name.isNullOrEmpty()) {
            approvalPendingResult.value = "Please Enter Name"
        } else if (applyLoanRequestModel.FirmName.isNullOrEmpty()) {
            approvalPendingResult.value = "Please Business Name "
        } else if (applyLoanRequestModel.Address.isNullOrEmpty()) {
            approvalPendingResult.value = "Please Business Address"
        } else {
            approvalPendingResult.value =SuccessType
        }
    }*/


    fun displayDisbursalAmount(leadMasterId:Int) {

        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.DisplayDisbursalAmount(leadMasterId).collect() {
                    _DisplayDisbursalAmountResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }


    }

    fun updateLeadSuccess(leadMasterId:Int) {

        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.UpdateLeadSuccess(leadMasterId).collect() {
                    _UpdateLeadSuccessResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }


    }

}