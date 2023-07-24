package com.sk.directudhar.ui.approvalpending

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApprovalPendingRepository @Inject constructor(private val apiServices: APIServices) {


    suspend fun displayDisbursalAmount(leadMasterId:Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.displayDisbursalAmount(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun updateLeadSuccess(leadMasterId:Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.updateLeadSuccess(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}