package com.sk.directudhar.ui.myaccount.udharStatement

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UdharStatementRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun getUdharStatement(accountId:Long, flag:Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getUdharStatement(accountId, flag)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun downloadReport(downloadLedgerReportResquestModel: DownloadLedgerReportResquestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.downloadReport(downloadLedgerReportResquestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getTransactionDetail(transactionId: String)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getTransactionDetail(transactionId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

}