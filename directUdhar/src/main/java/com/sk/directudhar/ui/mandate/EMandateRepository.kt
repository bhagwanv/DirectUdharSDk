package com.sk.directudhar.ui.mandate

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EMandateRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun bankList()  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.bankList()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun setUpEMandateAdd(model: EMandateAddRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.setUpEMandateAdd(model)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}