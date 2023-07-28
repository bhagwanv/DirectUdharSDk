package com.sk.directudhar.ui.myaccount

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MyAccountRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun getLoanAccountDetail(leadMasterId: Long) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getLoanAccountDetail(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getUdharStatement(accountId: Long, flag: Int) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getUdharStatement(accountId, flag)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun creditLimitRequest(leadMasterId: Long) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.creditLimitRequest(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}