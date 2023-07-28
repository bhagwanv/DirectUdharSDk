package com.sk.directudhar.ui.myaccount.udharStatement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.directudhar.MyApplication
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.ui.myaccount.UdharStatementModel
import com.sk.directudhar.utils.Network
import com.sk.directudhar.utils.Utils.Companion.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UdharStatementViewModel @Inject constructor(private val repository: UdharStatementRepository) :
    ViewModel() {

    private var _getUdharStatementResponse =
        MutableLiveData<NetworkResult<ArrayList<UdharStatementModel>>>()
    val getUdharStatementResponse: LiveData<NetworkResult<ArrayList<UdharStatementModel>>> =
        _getUdharStatementResponse

    private var _downloadReportResponse =
        MutableLiveData<NetworkResult<LedgerReportResponseModel>>()
    val downloadReportResponse: LiveData<NetworkResult<LedgerReportResponseModel>> =
        _downloadReportResponse

    private var _transactionDetailResponse =
        MutableLiveData<NetworkResult<ArrayList<TransactionDetailResponseModel>>>()
    val transactionDetailResponse: LiveData<NetworkResult<ArrayList<TransactionDetailResponseModel>>> =
        _transactionDetailResponse

    fun getUdharStatement(accountId: Long, flag: Int) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getUdharStatement(accountId, flag).collect {
                    _getUdharStatementResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun downloadReport(downloadLedgerReportResquestModel: DownloadLedgerReportResquestModel) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.downloadReport(downloadLedgerReportResquestModel).collect {
                    _downloadReportResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }

    fun getTransactionDetail(transactionId: String) {
        if (Network.checkConnectivity(MyApplication.context!!)) {
            viewModelScope.launch {
                repository.getTransactionDetail(transactionId).collect {
                    _transactionDetailResponse.postValue(it)
                }
            }
        } else {
            (MyApplication.context)!!.toast("No internet connectivity")
        }
    }
}