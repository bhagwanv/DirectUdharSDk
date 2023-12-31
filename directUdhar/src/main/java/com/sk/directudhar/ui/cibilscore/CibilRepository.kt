package com.sk.directudhar.ui.cibilscore

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CibilRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun getState()  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.stateMaster()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getCity(statId: Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.stateMaster(statId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getUserCreditInfo(leadMasterID: Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getUserCreditInfo(leadMasterID)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
    suspend fun cibilActivityComplete(leadMasterID: Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.cibilActivityComplete(leadMasterID)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }


}