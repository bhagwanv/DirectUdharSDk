package com.sk.directudhar.ui.success

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SuccessRepository @Inject constructor(private val apiServices: APIServices) {


    suspend fun DisplayDisbursalAmount(leadMasterId:Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.successDisplayDisbursalAmount(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun UpdateLeadSuccess(leadMasterId:Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.successUpdateLeadSuccess(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}